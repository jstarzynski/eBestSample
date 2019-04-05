package com.elstatgroup.elstat.ebest.sample

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.elstatgroup.elstat.sdk.api.*
import com.elstatgroup.elstat.sdk.api.NexoVerificationResult.NexoVerificationStatus.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val executor = Executors.newFixedThreadPool(10)

    private val mBluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        authAsynchronously()
    }

    private fun authAsynchronously() {
        NexoSync.getInstance().authorize(applicationContext, object: NexoAuthorizationListener {
            override fun onAuthorizationSuccessful() {
                Log.v("eBestSample", "SDK authorized successfully")
                startScanning()
            }

            override fun onError(nexoId: String?, error: NexoError) {
                Log.v("eBestSample", "error: ${error.errorType.name}")
            }
        })
    }

    private fun authSynchronously() {
        executor.execute {
            val result = NexoSync.getInstance().authorize(applicationContext)
            if (result.isSuccess)
                Log.v("eBestSample", "SDK authorized successfully")
            else
                Log.v("eBestSample", "error: ${result.nexoError?.errorType?.name}")
        }
    }

    private fun startScanning() {
        mBluetoothAdapter?.bluetoothLeScanner?.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                executor.execute {
                    result?.device?.let {
                        val verificationResult = NexoSync.getInstance().verifyBeacon(
                            applicationContext, it, result.rssi)
                        handleVerificationResult(it, verificationResult)
                    }
                }
            }

        })
    }

    private fun handleVerificationResult(device: BluetoothDevice, result: NexoVerificationResult) {
        when(result.status) {
            NOT_AUTHORIZED -> Log.v("eBestSample", "unauthorized: ${device.name ?: device.address}")
            AUTHORIZED -> result.nexoId?.let { nexoId ->
                Log.v("eBestSample", "authorized: $nexoId")
                NexoSync.getInstance().syncCooler(applicationContext, nexoId, 3, syncListener)
            }
            ERROR_DURING_VERIFICATION -> result.error?.let { error ->
                Log.v("eBestSample", "error: ${error.errorType.name}")
            }
        }
    }

    private val syncListener = object: NexoSyncListener {

        override fun onSuccess(nexoId: String?, result: String?) {
            Log.v("eBestSample", "$nexoId -> success: $result")
        }

        override fun onError(nexoId: String?, error: NexoError) {
            Log.v("eBestSample", "$nexoId -> error: ${error.errorType.name}")
        }

        override fun onCoolerProgress(nexoId: String?, progress: Float) {
            Log.v("eBestSample", "$nexoId -> progress: ${progress * 100}%")
        }
    }

}



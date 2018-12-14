package com.elstatgroup.elstat.ebest.sample

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.elstatgroup.elstat.sdk.api.NexoError
import com.elstatgroup.elstat.sdk.api.NexoVerificationListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mBluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mBluetoothAdapter?.bluetoothLeScanner?.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.
            }
        })
    }

    private val verificationListener = object : NexoVerificationListener {

        override fun onAuthorized(deviceName: String?, nexoId: String?) {
            Toast.makeText(applicationContext, "Authorized: $deviceName", Toast.LENGTH_SHORT).show()
        }

        override fun onUnauthorized(deviceName: String?) {
            Toast.makeText(applicationContext, "Unauthorized: $deviceName", Toast.LENGTH_SHORT).show()
        }

        override fun onError(error: NexoError) {
            Toast.makeText(applicationContext, error.errorType.name, Toast.LENGTH_SHORT).show()
        }

    }

}

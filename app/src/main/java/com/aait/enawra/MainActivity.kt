package com.aait.enawra

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private val DEVICE_ADDRESS = "98:D3:51:FE:00:CA"
    private val PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") //Serial Port Service ID
    private var device: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun BTinit(): Boolean {
        var found = false
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show()
        }
        if (!bluetoothAdapter!!.isEnabled) {
            val enableAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableAdapter, 0)
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        val bondedDevices = bluetoothAdapter.bondedDevices
        if (bondedDevices.isEmpty()) {
            Toast.makeText(applicationContext, "Please Pair the Device first", Toast.LENGTH_SHORT).show()
        } else {
            for (iterator in bondedDevices) {
                if (iterator.address == DEVICE_ADDRESS) {
                    device = iterator
                    found = true
                    break
                }
            }
        }
        return found
    }

    fun BTconnect(): Boolean {
        var connected = true
        try {
            socket = device!!.createRfcommSocketToServiceRecord(PORT_UUID)
            socket!!.connect()
        } catch (e: IOException) {
            e.printStackTrace()
            connected = false
        }
        if (connected) {
            try {
                outputStream = socket!!.outputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                inputStream = socket!!.inputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return connected
    }
    fun BTStart(){
        if(BTconnect()){
            try {
                inputStream = socket!!.inputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
}
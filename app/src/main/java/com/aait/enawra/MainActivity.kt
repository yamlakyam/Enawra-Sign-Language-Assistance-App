package com.aait.enawra

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private val DEVICE_ADDRESS = "98:D3:51:FE:00:CA"
    private val PORT_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") //Serial Port Service ID
    private var device: BluetoothDevice? = null
    private var socket: BluetoothSocket? = null

    //private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    var buffer: ByteArray = ByteArray(1)
    var stopThread = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn.isEnabled = false
        stopBtn.isEnabled = false

        connect_btn.setOnClickListener {
            if (BTinit()) {
                if (BTconnect()) {
                    Toast.makeText(this, "Connection opened", Toast.LENGTH_LONG).show()
                    startBtn.isEnabled = true
                    stopBtn.isEnabled = true

                }
            }
        }
        startBtn.setOnClickListener {
            BTStart()
            beginListenForData()
        }

        stopBtn.setOnClickListener {
            inputStream!!.close()
            socket!!.close()
            incomingtxt.text = ""
            Toast.makeText(this, "connection closed", Toast.LENGTH_LONG).show()

            //connect_btn.isEnabled=false
            startBtn.isEnabled = false
        }
    }

    fun BTinit(): Boolean {
        var found = false
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(
                applicationContext,
                "Device doesnt Support Bluetooth",
                Toast.LENGTH_SHORT
            ).show()
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
            Toast.makeText(applicationContext, "Please Pair the Device first", Toast.LENGTH_SHORT)
                .show()
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
            /*try {
                outputStream = socket!!.outputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }*/
            try {
                inputStream = socket!!.inputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return connected
    }

    fun BTStart() {
        if (BTconnect()) {
            try {
                inputStream = socket!!.inputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun beginListenForData() {
        val handler = Handler()
        stopThread = false
        buffer = ByteArray(1024)
        val thread = Thread(Runnable {
            while (!Thread.currentThread().isInterrupted && !stopThread) {
                try {
                    //==========================================================================
                    val sb = StringBuilder()
                    var line: String?

                    val br = BufferedReader(inputStream!!.bufferedReader(Charsets.UTF_8))
                    line = br.readLine()
                    while (line != null) {
                        var string = sb.append(line)
                        //handler.post { textView!!.append(string) }
                        //incomingtxt.text=line
                        handler.post { incomingtxt!!.text = line }
                        var i = 0
                        line = br.readLine().substring(i, i + 3)
                        if (line == "ha ") {
                            var ha: MediaPlayer = MediaPlayer.create(this, R.raw.ha)
                            ha.start()
                            handler.post{soundAnim.visibility=View.VISIBLE}
                            ha.setOnCompletionListener {
                                ha.release()
                                soundAnim.visibility= View.INVISIBLE
                            }

                        }
                        else if (line == "hu ") {
                            var hu: MediaPlayer = MediaPlayer.create(this, R.raw.hu)
                            hu.start()
                            handler.post{soundAnim.visibility=View.VISIBLE}
                            hu.setOnCompletionListener {
                                hu.release()
                                soundAnim.visibility=View.INVISIBLE
                            }

                        } else if (line == "he ") {
                            var he: MediaPlayer = MediaPlayer.create(this, R.raw.he)
                            he.start()
                            handler.post{soundAnim.visibility=View.VISIBLE}
                            he.setOnCompletionListener {
                                he.release()
                                soundAnim.visibility=View.INVISIBLE
                            }

                        } else if (line == "haa") {
                            var haa: MediaPlayer = MediaPlayer.create(this, R.raw.ha)
                            haa.start()
                            handler.post{soundAnim.visibility=View.VISIBLE}
                            haa.setOnCompletionListener {
                                haa.release()
                                soundAnim.visibility=View.INVISIBLE
                            }


                        } else if (line == "hai") {
                            var hai: MediaPlayer = MediaPlayer.create(this, R.raw.hai)
                            hai.start()
                            handler.post{soundAnim.visibility=View.VISIBLE}
                            hai.setOnCompletionListener {
                                hai.release()
                                soundAnim.visibility=View.INVISIBLE
                            }


                        } else if (line == "hee") {
                            var heee: MediaPlayer = MediaPlayer.create(this, R.raw.heee)
                            heee.start()
                            handler.post{soundAnim.visibility=View.VISIBLE}
                            heee.setOnCompletionListener {
                                heee.release()
                                soundAnim.visibility=View.INVISIBLE
                            }


                        } else {
                            var ho: MediaPlayer = MediaPlayer.create(this, R.raw.ho)
                            ho.start()
                            handler.post{soundAnim.visibility=View.VISIBLE }
                            ho.setOnCompletionListener {
                                ho.release()
                                soundAnim.visibility=View.INVISIBLE
                            }

                        }
                    }
                    br.close()
                    //==========================================================================

                } catch (ex: IOException) {
                    stopThread = true
                }
            }
        })
        thread.start()
    }
}
package com.autozen.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import com.autozen.dashboard.model.VehicleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Standard SPP UUID for ELM327
private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

@Singleton
class BluetoothObdDataSource @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?
) : ObdDataSource {

    private var socket: BluetoothSocket? = null
    private var reader: BufferedReader? = null
    private var writer: OutputStream? = null

    @SuppressLint("MissingPermission")
    override val dataFlow: Flow<VehicleData> = flow {
        val adapter = bluetoothAdapter ?: run {
            emit(VehicleData()); return@flow
        }
        // Find ELM327 device (named "OBDII" or "ELM327" or "V-LINK")
        val device = adapter.bondedDevices.firstOrNull { d ->
            d.name?.contains("OBD", ignoreCase = true) == true ||
            d.name?.contains("ELM", ignoreCase = true) == true ||
            d.name?.contains("LINK", ignoreCase = true) == true
        } ?: run {
            emit(VehicleData()); return@flow
        }
        try {
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            socket!!.connect()
            writer = socket!!.outputStream
            reader = BufferedReader(InputStreamReader(socket!!.inputStream))
            initElm327()
            while (true) {
                val speed = sendCmd("010D")?.parseObdInt() ?: 0  // km/h
                val rpm   = sendCmd("010C")?.parseObdInt()?.div(4) ?: 0  // RPM = (A*256+B)/4
                val fuel  = sendCmd("012F")?.parseObdInt()?.times(100f / 255f) ?: 80f
                val temp  = sendCmd("0105")?.parseObdInt()?.minus(40) ?: 90  // °C = A-40
                emit(
                    VehicleData(
                        speedKmh = speed.toFloat(),
                        rpm = rpm.toFloat(),
                        fuelPercent = fuel,
                        coolantTempC = temp.toFloat(),
                        isEngineOn = rpm > 0
                    )
                )
                delay(100)
            }
        } catch (e: Exception) {
            emit(VehicleData())
        } finally {
            runCatching { socket?.close() }
        }
    }.flowOn(Dispatchers.IO)

    private fun initElm327() {
        sendCmd("ATZ")   // Reset
        sendCmd("ATE0")  // Echo off
        sendCmd("ATL0")  // Linefeeds off
        sendCmd("ATH0")  // Headers off
        sendCmd("ATSP0") // Auto protocol
    }

    private fun sendCmd(cmd: String): String? {
        return try {
            writer?.write("$cmd\r".toByteArray())
            writer?.flush()
            Thread.sleep(50)
            reader?.readLine()?.trim()
        } catch (e: Exception) { null }
    }

    // Parse OBD hex response like "41 0D 3C" → last byte as int
    private fun String.parseObdInt(): Int? {
        val parts = trim().split(" ").filter { it.length == 2 }
        return if (parts.size >= 3) parts.last().toIntOrNull(16) else null
    }
}

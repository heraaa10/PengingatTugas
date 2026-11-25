package com.example.pengingattugas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Dipanggil saat token baru dibuat/diperbarui. Simpan ke database server.
        Log.d("FCM", "Token baru: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Mengambil payload Data Message: judul, isi, dan tugas_id
        val judul = message.data["judul"] ?: "Pengingat"
        val isi = message.data["isi"] ?: ""
        val tugasId = message.data["tugas_id"]

        tampilkanNotifikasi(judul, isi, tugasId)
    }

    private fun tampilkanNotifikasi(judul: String, isi: String, tugasId: String?) {
        val channelId = "pengingat_tugas"
        val manager = getSystemService(NotificationManager::class.java)

        // Wajib membuat NotificationChannel untuk Android 8.0 (API 26) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Pengingat Tugas",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager.createNotificationChannel(channel)
            }
        }

        // Menyiapkan Intent agar saat notifikasi diketuk, MainActivity terbuka membawa tugas_id
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("tugas_id", tugasId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notifikasi = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifikasi)
            .setContentTitle(judul)
            .setContentText(isi)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // ID unik menggunakan timestamp agar notifikasi baru tidak menimpa notifikasi lama
        manager.notify(System.currentTimeMillis().toInt(), notifikasi)
    }
}
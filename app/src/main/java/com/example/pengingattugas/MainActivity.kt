package com.example.pengingattugas

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var txtStatus: TextView

    // Launcher izin notifikasi wajib dideklarasikan sebagai properti kelas
    private val mintaIzin = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { diberikan ->
        if (!diberikan) {
            Toast.makeText(this, "Notifikasi dimatikan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtStatus = findViewById(R.id.txtStatus)

        // 1. Minta izin notifikasi pada Android 13 (API 33) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mintaIzin.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 2. Ambil Token FCM perangkat dan cetak di Logcat
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Token: $token")
            } else {
                Log.e("FCM", "Gagal mengambil token", task.exception)
            }
        }

        // (Opsional) Berlangganan topik untuk pengiriman massal
        FirebaseMessaging.getInstance().subscribeToTopic("tugas-kuliah")

        // 3. Cek apakah Activity dibuka dari ketukan notifikasi
        tampilkanStatusTugas(intent)
    }

    // Dipanggil jika MainActivity sudah terbuka dan pengguna mengetuk notifikasi baru
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        tampilkanStatusTugas(intent)
    }

    private fun tampilkanStatusTugas(intent: Intent?) {
        val tugasId = intent?.getStringExtra("tugas_id")
        txtStatus.text = if (tugasId != null) {
            "Buka tugas $tugasId"
        } else {
            "Menunggu pengingat tugas"
        }
    }
}
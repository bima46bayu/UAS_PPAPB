package com.example.uas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.uas.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val channelId = "KaloriChannel"
    private val notificationId = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Panggil metode untuk menghitung dan menampilkan kalori harian
        hitungDanTampilkanKaloriHarian()
    }

    private fun hitungDanTampilkanKaloriHarian() {
        // Mendapatkan ID pengguna yang sedang masuk
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid

        if (userUid != null) {
            // Mendapatkan data Kalori dari Firestore berdasarkan ID pengguna
            db.collection("kalori")
                .whereEqualTo("userId", userUid)
                .get()
                .addOnSuccessListener { documents ->
                    val kaloriList = mutableListOf<Kalori>()
                    for (document in documents) {
                        val kalori = document.toObject(Kalori::class.java)
                        kaloriList.add(kalori)
                    }

                    // Hitung total kalori untuk hari itu
                    var totalKalori = 0
                    var sisaKalori = 0
                    for (kalori in kaloriList) {
                        // Konversi jumlah_kalori menjadi integer dan tambahkan ke total
                        totalKalori += kalori.jumlah_kalori.toInt()
                        sisaKalori = 2000 - totalKalori
                    }

                    // Tampilkan total kalori di dalam TextView
                    val textViewTotalKalori: TextView = binding.textViewKalori
                    textViewTotalKalori.text = "$totalKalori"

                    val textViewSisaKalori: TextView = binding.textViewSisaKalori
                    textViewSisaKalori.text = "$sisaKalori"

                    // Tampilkan notifikasi berdasarkan kondisi sisa kalori
                    showNotification(sisaKalori)
                }
                .addOnFailureListener { exception ->
                    // Penanganan kesalahan, misalnya, tampilkan pesan kesalahan
                    showToast("Error getting documents: $exception")
                }
        } else {
            showToast("User not logged in")
        }
    }

    private fun showNotification(sisaKalori: Int) {
        Log.d("Notification", "Trying to send notification")

        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.calorie_tracker)
            .setContentTitle("Status Kalori Harian")
            .setContentText(getNotificationText(sisaKalori))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(channelId, "Notifku", NotificationManager.IMPORTANCE_DEFAULT)
            with(notificationManager) {
                createNotificationChannel(notifChannel)
                notify(notificationId, builder.build())
            }
        } else {
            notificationManager.notify(notificationId, builder.build())
        }
    }

    private fun getNotificationText(sisaKalori: Int): String {
        return when {
            sisaKalori == 0 -> "Kalori harian terpenuhi!"
            sisaKalori < 0 -> "Kalori harian terlalu banyak!"
            else -> "Kalori harian belum terpenuhi. Sisa: $sisaKalori kalori"
        }
    }

    private fun showToast(message: String) {
        // Implementasi showToast sesuai kebutuhan aplikasi Anda
    }
}

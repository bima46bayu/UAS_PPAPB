package com.example.uas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.uas.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val db = FirebaseFirestore.getInstance()

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
        // Mendapatkan data Kalori dari Firestore
        db.collection("kalori")
            .get()
            .addOnSuccessListener { documents ->
                val kaloriList = mutableListOf<Kalori>()
                for (document in documents) {
                    val kalori = document.toObject(Kalori::class.java)
                    kaloriList.add(kalori)
                }

                // Sekarang, Anda memiliki daftar Kalori dari koleksi "kalori"
                // Selanjutnya, Anda dapat menggunakan daftar tersebut untuk perhitungan atau tampilan

                // Hitung total kalori untuk hari itu
                var totalKalori = 0
                for (kalori in kaloriList) {
                    // Konversi jumlah_kalori menjadi integer dan tambahkan ke total
                    totalKalori += kalori.jumlah_kalori.toInt()
                }

                // Tampilkan total kalori di dalam TextView
                val textViewTotalKalori: TextView = binding.textViewKalori
                textViewTotalKalori.text = "$totalKalori"
            }
            .addOnFailureListener { exception ->
                // Penanganan kesalahan, misalnya, tampilkan pesan kesalahan
                showToast("Error getting documents: $exception")
            }
    }

    private fun showToast(message: String) {
        // Implementasi showToast sesuai kebutuhan aplikasi Anda
    }
}

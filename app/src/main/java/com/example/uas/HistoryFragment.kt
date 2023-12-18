package com.example.uas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.databinding.FragmentHistoryBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var kaloriAdapter: KaloriAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        val recyclerView = binding.recyclerViewKalori
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        kaloriAdapter = KaloriAdapter()
        recyclerView.adapter = kaloriAdapter

        // Fetch and observe makanan data from Firestore
        fetchDataAndObserve()

        binding.btnAddMakanan.setOnClickListener {
            navigateToAddMakanan()
        }

        // Panggil metode reset saat fragment dibuat atau diperbarui
        resetDataKaloriJikaPerlu()
    }

    private fun navigateToAddMakanan() {
        val intent = Intent(requireContext(), AddMakananActivity::class.java)
        startActivity(intent)
    }

    private fun fetchDataAndObserve() {
        try {
            val kaloriCollection = firestore.collection("kalori")
            // Observe Firestore changes
            kaloriCollection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    showToast("Error fetching data from Firestore")
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val kaloris = mutableListOf<Kalori>()
                    for (document in documents) {
                        val kaloriId = document.id
                        val kalori = document.toObject(Kalori::class.java).copy(id = kaloriId)
                        kaloris.add(kalori)
                    }

                    // Update the UI with the Firestore data
                    kaloriAdapter.setKalori(kaloris)
                }
            }
        } catch (e: Exception) {
            showToast(e.toString())
            Log.d("ERRORKU", e.toString())
        }
    }

    private fun resetDataKaloriJikaPerlu() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        // Dapatkan tanggal terakhir kali reset dari SharedPreferences
        val lastResetDate = sharedPreferences.getString(LAST_RESET_DATE_KEY, "") ?: ""

        // Jika tanggal terakhir kali reset tidak sama dengan tanggal hari ini, reset data
        if (lastResetDate != currentDate) {
            resetDataKalori()
            // Simpan tanggal terakhir kali reset ke SharedPreferences
            with(sharedPreferences.edit()) {
                putString(LAST_RESET_DATE_KEY, currentDate)
                apply()
            }
        }
    }

    private fun resetDataKalori() {
        // Reset atau hapus semua data kalori dari hari sebelumnya
        // Implementasikan sesuai kebutuhan aplikasi Anda
        // ...
    }

    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    companion object {
        private const val LAST_RESET_DATE_KEY = "last_reset_date"
    }
}



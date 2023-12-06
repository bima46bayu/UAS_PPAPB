package com.example.uas

import android.content.Intent
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

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var kaloriAdapter: KaloriAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
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

    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }
}


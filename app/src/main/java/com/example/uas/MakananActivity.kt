package com.example.uas

import MakananAdapter
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.databinding.ActivityMakananBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.core.View

class MakananActivity : AppCompatActivity() {

    private  lateinit var binding : ActivityMakananBinding
    private lateinit var makananAdapter: MakananAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val recyclerView = binding.recyclerViewMakanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        makananAdapter = MakananAdapter()
        recyclerView.adapter = makananAdapter

        // Fetch and observe buku data from Firestore
        fetchDataAndObserve()

        with(binding) {
            btnCustomMakanan.setOnClickListener {
                val intent = Intent(this@MakananActivity, AddMakananActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun fetchDataAndObserve() {

        try {
            val makananCollection = firestore.collection("makanan")
            // Observe Firestore changes
            makananCollection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    showToast(this@MakananActivity, "Error fetching data from Firestore")
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val makanans = mutableListOf<Makanan>()
                    for (document in documents) {
                        val makananId = document.id
                        val makanan = document.toObject(Makanan::class.java).copy(id = makananId)
                        makanans.add(makanan)
                    }

                    // Update the UI with the Firestore data
                    makananAdapter.setMakanan(makanans)
                }
            }
        }catch (e: Exception){
            showToast(this@MakananActivity, e.toString())
            Log.d("ERRORKU", e.toString())
        }

    }

    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

}
package com.example.uas

import MakananAdapter
import MakananAdapterRoom
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.databinding.ActivityMakananBinding
import com.google.firebase.firestore.FirebaseFirestore

class MakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakananBinding
    private lateinit var makananAdapter: MakananAdapter
    private lateinit var firestore: FirebaseFirestore

    private lateinit var makananDao: MakananDao
    private var originalMakanan: List<Makanan> = listOf()
    private lateinit var makananAdapterRoom: MakananAdapterRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val database = AppDatabase.getDatabase(this@MakananActivity)
        makananDao = database!!.makananDao()!!

        makananAdapterRoom = MakananAdapterRoom(this@MakananActivity)

        val recyclerView = binding.recyclerViewMakanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        makananAdapter = MakananAdapter(originalMakanan)
        recyclerView.adapter = makananAdapter

        // Fetch and observe buku data from Firestore
        fetchDataAndObserve()
        fetchRoomDataAndObserve()

        with(binding) {
            btnCustomMakanan.setOnClickListener {
                val intent = Intent(this@MakananActivity, AddMakananActivity::class.java)
                startActivity(intent)
            }

            searchMakanan.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    makananAdapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })
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
                    // Simpan dataset asli
                    originalMakanan = makanans.toList()
                    // Update the UI with the Firestore data
                    makananAdapter.setMakanan(makanans)
                }
            }
        } catch (e: Exception) {
            showToast(this@MakananActivity, e.toString())
            e.printStackTrace()
        }
    }

    private fun fetchRoomDataAndObserve() {
        try {
            // Observe LiveData changes from Room Database
            var dataList = mutableListOf<MakananEntity>()
            makananDao.getAllMakanan.observe(this, Observer { makananList ->

              for (data in makananList){
                  dataList.add(data)
              }

            })
                makananAdapterRoom.setMakanans(dataList)
        } catch (e: Exception) {
            showToast(this@MakananActivity, "Error fetching data from Room Database")
            e.printStackTrace()
        }
    }


    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
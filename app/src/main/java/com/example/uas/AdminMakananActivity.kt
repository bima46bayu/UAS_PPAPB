package com.example.uas

import MakananAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.databinding.ActivityAdminMakananBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMakananBinding
    private lateinit var makananAdapter: MakananAdapter
    private lateinit var firestore: FirebaseFirestore
    private var originalMakanan: List<Makanan> = listOf()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menggunakan layout binding untuk ActivityAdminMakanan
        binding = ActivityAdminMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        checkAdminAccess()

        val recyclerView = binding.recyclerViewMakanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        makananAdapter = MakananAdapter(originalMakanan)
        recyclerView.adapter = makananAdapter

        // Ambil dan amati data makanan dari Firestore
        fetchDataAndObserve()

        with(binding) {
            btnCustomMakanan.setOnClickListener {
                val intent = Intent(this@AdminMakananActivity, AddMakananActivity::class.java)
                startActivity(intent)
            }

            searchMakanan.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    makananAdapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })

            sessionManager = SessionManager(this@AdminMakananActivity)

            // Implementasi logout
            btnLogoutAdmin.setOnClickListener {
                // Lakukan logout
                sessionManager.logout()

                // Arahkan ke halaman login
                val intent = Intent(this@AdminMakananActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun fetchDataAndObserve() {
        try {
            val makananCollection = firestore.collection("makanan")
            // Amati perubahan Firestore
            makananCollection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    showToast(this@AdminMakananActivity, "Error mengambil data dari Firestore")
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
                    // Perbarui UI dengan data Firestore
                    makananAdapter.setMakanan(makanans)
                }
            }
        } catch (e: Exception) {
            showToast(this@AdminMakananActivity, e.toString())
            e.printStackTrace()
        }
    }

    private fun checkAdminAccess() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Ambil informasi peran pengguna dari Firebase
            val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role")

                        if (role == "admin") {
                            // Pengguna adalah admin, izinkan akses ke halaman
                            setupAdminAccess()
                        } else {
                            // Pengguna bukan admin, tampilkan pesan atau lakukan sesuatu yang sesuai
                            showToast(this@AdminMakananActivity, "Anda bukan admin")
                            finish() // Tidak izinkan akses, kembali ke halaman sebelumnya atau keluar dari halaman ini
                        }
                    }
                }
                .addOnFailureListener { e ->
                    showToast(this@AdminMakananActivity, "Gagal memeriksa peran admin")
                    e.printStackTrace()
                }
        } else {
            // Pengguna belum masuk, redirect atau lakukan sesuatu yang sesuai
            showToast(this@AdminMakananActivity, "Anda belum masuk")
            finish() // Tidak izinkan akses, kembali ke halaman sebelumnya atau keluar dari halaman ini
        }
    }

    private fun setupAdminAccess() {
        // Lakukan aksi untuk menyiapkan UI atau akses admin
        binding.btnCustomMakanan.visibility = View.VISIBLE
    }

    private fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}

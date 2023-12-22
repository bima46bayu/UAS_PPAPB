package com.example.uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.uas.databinding.ActivityAddMakananAdminBinding
import com.example.uas.databinding.ActivityAdminMakananBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddMakananAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMakananAdminBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val makananCollectionRef = firestore.collection("makanan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMakananAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            addBTTambah.setOnClickListener {
                var makanan = addETMakanan.text.toString()
                var kalori = addETKalori.text.toString()

                if (makanan == "" || kalori == "") {
                    showToast("Data tidak boleh kosong!")
                } else {
                    try {
                        tambahkanMakanan(
                            Makanan(
                                makanan = makanan,
                                kalori = kalori
                            )
                        )
                        showToast("Makanan berhasil ditambahkan!")

                        val intent = Intent(this@AddMakananAdminActivity, AdminMakananActivity::class.java)
                        startActivity(intent)
                    } catch (e: NumberFormatException) {
                        showToast("Kalori harus berupa angka yang valid!")
                    }
                }
            }
        }
    }



    private fun tambahkanMakanan(makanan: Makanan) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val task = makananCollectionRef.add(makanan).await()
                Log.d("AddMakananAdminActivity", "Berhasil menambahkan makanan dengan ID: ${task.id}")

            } catch (e: Exception) {
                Log.e("AddMakananAdminActivity", "Gagal menambahkan makanan", e)
                showToast("Gagal menambahkan makanan!")
            }
        }
    }



//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@AddMakananAdminActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}

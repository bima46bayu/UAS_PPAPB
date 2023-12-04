package com.example.uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.uas.databinding.ActivityAddMakananBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddMakananActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAddMakananBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val makananCollection  = firestore.collection("makanan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            addBTTambah.setOnClickListener{
                var makanan = addETMakanan.text.toString()
                var kalori = addETKalori.text.toString()


                if (makanan == "" || kalori == "" ){
                    showToast("Cant Empty Data!")
                }
                else{
                    add(
                        Makanan(
                            makanan = makanan,
                            kalori = kalori
                        )
                    )
                    showToast("INSERTED!")

                    val intent = Intent(this@AddMakananActivity, MakananActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun add(makanan: Makanan){
        makananCollection.add(makanan).addOnFailureListener { e ->
            Log.d("AddMakananActivity", "Error adding makanan", e)
            showToast(e.toString())
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
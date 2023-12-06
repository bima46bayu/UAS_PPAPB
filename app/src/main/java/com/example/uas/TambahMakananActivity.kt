package com.example.uas

import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas.databinding.ActivityTambahMakananBinding
import com.google.firebase.firestore.FirebaseFirestore

class TambahMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahMakananBinding
    private val db = FirebaseFirestore.getInstance()

    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan nilai dari intent
        val namaMakanan = intent.getStringExtra("nama_makanan")
        val jumlahKalori = intent.getStringExtra("jumlah_kalori")

        // Menetapkan nilai ke dalam EditText
        binding.addETMakanan.setText(namaMakanan)
        binding.addETKalori.setText(jumlahKalori)

        // Setting time listener
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            // Handle waktu yang dipilih di sini
            selectedTime = "$hourOfDay:$minute"
            binding.addTVWaktu.text = selectedTime
        }

        val c = Calendar.getInstance()
        val currentHour = c.get(Calendar.HOUR_OF_DAY)
        val currentMinute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            timeSetListener,
            currentHour,
            currentMinute,
            false
        )
        timePickerDialog.show()

        binding.addBTTambah.setOnClickListener {
            // Simpan data ke Firestore
            simpanDataFirestore(namaMakanan, jumlahKalori, selectedTime)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun simpanDataFirestore(namaMakanan: String?, jumlahKalori: String?, waktu: String) {
        // Mendapatkan referensi ke koleksi "kalori"
        val collectionRef = db.collection("kalori")

        // Menyimpan data ke dalam Firestore
        val dataMakanan = hashMapOf(
            "nama_makanan" to namaMakanan,
            "jumlah_kalori" to jumlahKalori,
            "waktu" to waktu
        )

        collectionRef
            .add(dataMakanan)
            .addOnSuccessListener { documentReference ->
                // Berhasil menyimpan data
                val pesan = "Data berhasil disimpan dengan ID: ${documentReference.id}"
                showToast(pesan)
            }
            .addOnFailureListener { e ->
                // Gagal menyimpan data
                val pesan = "Data gagal disimpan"
                showToast(pesan)
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

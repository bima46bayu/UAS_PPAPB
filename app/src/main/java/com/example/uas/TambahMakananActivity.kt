package com.example.uas

import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas.databinding.ActivityTambahMakananBinding

class TambahMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahMakananBinding

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
            val selectedTime = "$hourOfDay:$minute"
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

    }
}
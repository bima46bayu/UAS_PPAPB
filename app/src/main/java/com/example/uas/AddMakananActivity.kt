package com.example.uas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.uas.AppDatabase
import com.example.uas.MakananActivity
import com.example.uas.MakananDao
import com.example.uas.MakananEntity
import com.example.uas.databinding.ActivityAddMakananBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class AddMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMakananBinding
    private lateinit var makananDao: MakananDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this@AddMakananActivity)
        makananDao = database!!.makananDao()!!

        with(binding) {
            addBTTambah.setOnClickListener {
                val makanan = addETMakanan.text.toString()
                val kalori = addETKalori.text.toString()

                if (makanan.isEmpty() || kalori.isEmpty()) {
                    showToast("Can't Empty Data!")
                } else {
                    val makananEntity = MakananEntity(id = 0, makanan = makanan, kalori = kalori)
                    insertMakanan(makananEntity)
                    showToast("INSERTED!")

                    val intent = Intent(this@AddMakananActivity, MakananActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun insertMakanan(makananEntity: MakananEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            makananDao.insert(makananEntity)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

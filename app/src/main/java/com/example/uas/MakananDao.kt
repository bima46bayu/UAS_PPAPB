package com.example.uas

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MakananDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(makanan: MakananEntity)

    @Update
    fun update(makanan: MakananEntity)

    @Delete
    fun delete(makanan: MakananEntity) // Ganti dengan tipe yang sesuai, sesuaikan dengan Entity yang benar

    @get:Query("SELECT * FROM makanan_table ORDER BY id ASC")
    val getAllMakanan: LiveData<List<MakananEntity>>
}



package com.example.uas

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "makanan_table")
data class MakananEntity (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id:Int = 0,

    @ColumnInfo(name = "makanan")
    val makanan: String,

    @ColumnInfo(name = "kalori")
    val kalori: String
)


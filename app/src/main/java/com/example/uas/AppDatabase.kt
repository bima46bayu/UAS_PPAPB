package com.example.uas

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [MakananEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun makananDao() : MakananDao?

    companion object {
        @Volatile
        private var INSTANCE : AppDatabase ? = null
        fun getDatabase(context: Context) : AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "makanan_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}

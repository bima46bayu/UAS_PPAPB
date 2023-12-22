package com.example.uas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class NotifReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val msg = intent?.getStringExtra("MESSAGE")
        if (msg != null) {
            Log.d("Notiif-Receiver", "baca notif click")
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
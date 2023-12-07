package com.example.uas

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.Kalori
import com.example.uas.Makanan
import com.example.uas.MakananActivity
import com.example.uas.R
import com.example.uas.TambahMakananActivity
import com.google.firebase.firestore.FirebaseFirestore

class KaloriAdapter : RecyclerView.Adapter<KaloriAdapter.KaloriViewHolder>() {

    private var kalori: List<Kalori> = listOf()
    private val firestore = FirebaseFirestore.getInstance()
    private val kaloriCollection = firestore.collection("kalori")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KaloriViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.kalori_adapter, parent, false)
        return KaloriViewHolder(view)
    }

    override fun onBindViewHolder(holder: KaloriViewHolder, position: Int) {
        val currentKalori = kalori[position]

        holder.textViewMakanan.text = currentKalori.nama_makanan
        holder.textViewKalori.text = currentKalori.jumlah_kalori
        holder.textViewWaktu.text = currentKalori.waktu

        holder.btDelete.setOnClickListener {
            showYesNoAlertDialog(
                holder.itemView.context,
                "Apakah anda yakin akan menghapus ${currentKalori.jumlah_kalori}",
                DialogInterface.OnClickListener { _, _ ->
                    deleteMakanan(currentKalori.id,holder)
                }
            )
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TambahMakananActivity::class.java)
            intent.putExtra("id_makanan", currentKalori.id)
            intent.putExtra("nama_makanan", currentKalori.nama_makanan)
            intent.putExtra("jumlah_kalori", currentKalori.jumlah_kalori)
            intent.putExtra("waktu_makan", currentKalori.waktu)
            holder.itemView.context.startActivity(intent)
        }

        // ... (kode lainnya)
    }


    private fun deleteMakanan(id: String, holder: KaloriViewHolder) {
        kaloriCollection.document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Kalori harian berhasil dihapus",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    holder.itemView.context,
                    "Error deleting document: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun getItemCount(): Int {
        return kalori.size
    }

    fun setKalori(kalori: List<Kalori>) {
        this.kalori = kalori
        notifyDataSetChanged()
    }

    inner class KaloriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMakanan: TextView = itemView.findViewById(R.id.makananTextView)
        val textViewKalori: TextView = itemView.findViewById(R.id.kaloriTextView)
        val textViewWaktu: TextView = itemView.findViewById(R.id.waktuTextView)
        val btDelete: Button = itemView.findViewById(R.id.itemBtDelete)
    }

    fun showYesNoAlertDialog(
        context: Context,
        message: String,
        onYesClickListener: DialogInterface.OnClickListener
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes", onYesClickListener)
        alertDialogBuilder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



    private fun showToast(message: String, holder: KaloriViewHolder) {
        Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
    }

}

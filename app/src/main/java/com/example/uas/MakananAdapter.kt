import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.Makanan
import com.example.uas.R
import com.example.uas.TambahMakananActivity
import com.google.firebase.firestore.FirebaseFirestore

class MakananAdapter(originalMakanan: List<Makanan>) : RecyclerView.Adapter<MakananAdapter.MakananViewHolder>(), Filterable {

    private var makanan: List<Makanan> = listOf()
    private val firestore = FirebaseFirestore.getInstance()
    private val makananCollection = firestore.collection("makanan")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_makanan, parent, false)
        return MakananViewHolder(view)
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        val currentMakanan = makanan[position]

        holder.textViewMakanan.text = currentMakanan.makanan
        holder.textViewKalori.text = currentMakanan.kalori

        holder.btDelete.setOnClickListener {
            showYesNoAlertDialog(
                holder.itemView.context,
                "Apakah anda yakin akan menghapus ${currentMakanan.makanan}",
                DialogInterface.OnClickListener { _, _ ->
                    deleteMakanan(currentMakanan.id,holder)
                }
            )
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TambahMakananActivity::class.java)
            intent.putExtra("nama_makanan", currentMakanan.makanan)
            intent.putExtra("jumlah_kalori", currentMakanan.kalori)
            holder.itemView.context.startActivity(intent)
        }

        // ... (kode lainnya)
    }


    private fun deleteMakanan(id: String, holder: MakananViewHolder) {
        makananCollection.document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Makanan berhasil dihapus",
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
        return makanan.size
    }

    fun setMakanan(makanan: MutableList<Makanan>) {
        this.makanan = makanan
        notifyDataSetChanged()
    }

    inner class MakananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMakanan: TextView = itemView.findViewById(R.id.makananTextView)
        val textViewKalori: TextView = itemView.findViewById(R.id.kaloriTextView)
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

    fun updateData(makanans: List<Makanan>) {
        this.makanan = makanans.toMutableList()
        notifyDataSetChanged()
    }



    private fun showToast(message: String, holder: MakananViewHolder) {
        Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Makanan>()

                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(makanan)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()

                    for (item in makanan) {
                        if (item.makanan.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                makanan = results?.values as MutableList<Makanan>
                notifyDataSetChanged()
            }
        }
    }


}

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.uas.AppDatabase
import com.example.uas.MakananDao
import com.example.uas.MakananEntity
import com.example.uas.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MakananAdapterRoom(private val context: Context) : RecyclerView.Adapter<MakananAdapterRoom.BukuViewHolder>() {

    private var makanans: List<MakananEntity> = listOf()
    private lateinit var makananDao: MakananDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_makanan, parent, false) // Ganti dengan file layout yang benar
        return BukuViewHolder(view)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        val db = AppDatabase.getDatabase(context)
        makananDao = db!!.makananDao()!!

        val currentMakanan = makanans[position]
        holder.textViewMakanan.text = currentMakanan.makanan
        holder.textViewKalori.text = currentMakanan.kalori

        holder.btDelete.setOnClickListener {
            showYesNoAlertDialog(
                "Apakah anda yakin akan menghapus " + currentMakanan.makanan,
                DialogInterface.OnClickListener { _, _ ->
                    Toast.makeText(context, "del" + currentMakanan.id.toString(), Toast.LENGTH_SHORT).show()

                    executorService.execute {
                        try {
                            makananDao.delete(currentMakanan)
                        } catch (e: Exception) {
                            // Handle exceptions
                        }
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return makanans.size
    }

    fun setMakanans(makanans: MutableList<MakananEntity>) {

        this.makanans = makanans.toList()
        notifyDataSetChanged()

    }

    inner class BukuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMakanan: TextView = itemView.findViewById(R.id.makananTextView)
        val textViewKalori: TextView = itemView.findViewById(R.id.kaloriTextView)
        val btDelete: Button = itemView.findViewById(R.id.itemBtDelete)

        // Add other views if needed
    }

    private fun showYesNoAlertDialog(message: String, onYesClickListener: DialogInterface.OnClickListener) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton("Yes", onYesClickListener)
        alertDialogBuilder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}

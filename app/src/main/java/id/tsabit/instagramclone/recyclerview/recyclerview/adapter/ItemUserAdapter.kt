package id.tsabit.instagramclone.recyclerview.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import id.tsabit.instagramclone.databinding.ItemUserBinding
import id.tsabit.instagramclone.model.User
import id.tsabit.instagramclone.recyclerview.recyclerview.viewholder.ItemUserVH

class ItemUserAdapter : RecyclerView.Adapter<ItemUserVH>() {
    // Buat variabel wadah data yang akan ditampilkan di RecyclerView
    private val listData = arrayListOf<User>()

    // viewBinding untuk item_user.xml
    private lateinit var binding: ItemUserBinding

    // Buat fungsi untuk menambahkan data ke dalam listData
    fun addData(userData: List<User>) {
        // Hapus isi yang ada di dalam listData
        listData.clear()
        // Tambahkan data terbaru ke dalam listData
        listData.addAll(userData)
        // notify RecyclerView bahwa ada penambahan data
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemUserVH {
        // viewBinding untuk item_user.xml
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemUserBinding.inflate(inflater)
        return ItemUserVH(binding)
    }

    // Buat fungsi untuk mengkonversi dari dp ke px
    private fun dpToPx(dp: Int): Int {
        val px = dp * binding.root.resources.displayMetrics.density
        return px.toInt()
    }

    override fun onBindViewHolder(holder: ItemUserVH, position: Int) {
        // Margin untuk layout
        val marginLeft = dpToPx(4)
        val marginTop = dpToPx(8)
        val marginRight = dpToPx(4)
        val marginBottom = dpToPx(8)
        // Buat layout parameter baru untuk item_user
        val newLayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // Set margin parameter terbaru
        newLayoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        // Masukkan layout paramater baru ke dalam ViewHolder
        holder.itemView.layoutParams = newLayoutParams
        // Ambil data sesuai dengan posisinya
        val data = listData[position]
        // Masukkan ke dalam ViewHolder
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        // return ukuran data yang dimasukkan
        // agar jumlah item yang ada di RecyclerView sesuai dengan jumlah data
        return listData.size
    }
}
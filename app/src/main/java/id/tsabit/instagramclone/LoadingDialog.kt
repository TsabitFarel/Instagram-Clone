package id.tsabit.instagramclone

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import id.tsabit.instagramclone.databinding.CustomBarBinding

class LoadingDialog(private val activity: Activity) { // Tambahkan constructor atau variabel activity

    // Gunakan viewbinding untuk mengakses custom_bar.xml
    private val inflater = LayoutInflater.from(activity)
    private val binding = CustomBarBinding.inflate(inflater)

    // Buat variabel dialog tipe data AlertDialog
    private lateinit var  dialog : AlertDialog

    // Buat fungsi startLoadDialog untuk memulai loading dialog
    fun startLoadingDialog(){
        if (binding.root.parent == null){
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setView(binding.root)
            alertDialog.setCancelable(false)
            dialog = alertDialog.create()
        }
        dialog.show()
    }

    // Buat fungsi dismissDialog untuk menutup loading dialog
    fun dismissDialog() {
        dialog.dismiss()
    }

}
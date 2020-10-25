package id.tsabit.instagramclone

import android.text.TextUtils

// Fungsi ini untuk berlaku ke semua tipe data String
fun String.isEmailValid():Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
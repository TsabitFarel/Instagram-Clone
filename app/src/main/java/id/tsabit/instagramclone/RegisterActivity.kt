package id.tsabit.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import id.tsabit.instagramclone.databinding.ActivityRegisterBinding
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    // Buat variabel dialog
    private lateinit var dialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = layoutInflater
        binding = ActivityRegisterBinding.inflate(inflater)
        setContentView(binding.root)

        // Buat dulu progressDialog sebagai efek loading
        dialog = LoadingDialog(this@RegisterActivity)

        binding.run {
            // Tambahkan setOnClickListener pada btnSignin
            btnSignin.setOnClickListener {
                // Finish digunakan untuk mengakhiri sebuah activity
                // Sehingga activity sebelumnya akan terbuka
                finish()
            }

            // Tambahkan setOnClickListener pada tombol register
            btnRegister.setOnClickListener {
                // Jalankan fungsi createAccount(), Jika merah maka buat fungsi baru bernama createAccount()
                createAccount()
            }
        }
    }
    // Buat fungsi bernama showToast()
    private fun showToast(pesan: String) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
    }

    // Buat fungsi private createAccount()
    private fun createAccount() {
        // Gunakan binding.run karena kita akses view di layout
        binding.run {
            // Ambil nilai yang dimasukkan ke dalam masing-masing editText
            val fullName = inputFullname.text.toString()
            val emailUser = inputEmail.text.toString()
            val userName = inputUsername.text.toString()
            val passWord = inputPassword.text.toString()

            // Cek semua input, Jika kosong tampilkan toast
            // Return digunakan untuk mengakhiri jalannya fungsi
            if (fullName.isEmpty()) {
                showToast("Fullname harus di isi !")
                // return digunakan untuk menyelesaikan fungsi createaccount() lebih awal
                return
            }
            if (emailUser.isEmpty()) {
                showToast("Email harus di isi !")
                // return digunakan untuk menyelesaikan fungsi createaccount() lebih awal
                return
            }
            if (userName.isEmpty()) {
                showToast("Username harus di isi !")
                // return digunakan untuk menyelesaikan fungsi createaccount() lebih awal
                return
            }
            if (passWord.isEmpty()) {
                showToast("Password harus diisi !")
                // return digunakan untuk menyelesaikan fungsi createaccount() lebih awal
                return
            }
            if (!emailUser.isEmailValid()) { // tanda seru ! digunakan untuk hasil yang berlawanan
                // Semisal hasil isEmailValid itu true
                // maka di if ini nilainya false
                showToast("Email Tidak Valid !")
                return
            }
            if (passWord.count() < 8) { // Jika password ukurannya kurang dari 8 digit
                showToast("Password minimal terdiri dari 8 karakter!")
                return
            }

            // Munculkan loading sebelum menyimpan data ke Firebase
            dialog.startLoadingDialog()

            // Sambungkan Firebase auth
            val mAuth = FirebaseAuth.getInstance()
            mAuth.createUserWithEmailAndPassword(emailUser, passWord)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Panggil fungsi saveUserInfo
                        // Untuk menyimpan data user seperti fullname dan username
                        saveUserInfo(fullName, userName, emailUser)
                    } else {
                        // Jika gagal membuat user maka tampilkan toast beserta errornya
                        val message = task.exception
                        showToast(message.toString())
                        mAuth.signOut()
                        // Jika gagal, loading ditutup menggunakan dismissDialog()
                        dialog.dismissDialog()
                    }
                }
        }
    }

    // Buat fungsi saveUserInfo
    private fun saveUserInfo(fullName: String, userName: String, emailUser: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId.toString()
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["email"] = emailUser
        userMap["bio"] = ""
        userMap["image"] = ""

        // Fungsi di bawah untuk memasukkan data ke dalam database Firebase
        userRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { // Jika berhasil update Firebase
                    // Jika sukses tutup dialog
                    showToast("Akun Sudah Dibuat")
                    // Buat intent menuju mainActivtiy
                    val intent = Intent(this, MainActivity::class.java)
                    // Tambahkan flag activity clear task untuk menonaktifkan tombol back
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    // Jika gagal tutup dialog
                    val message = task.exception.toString()
                    showToast(message)
                    FirebaseAuth.getInstance().signOut()
                }
            }
    }

}
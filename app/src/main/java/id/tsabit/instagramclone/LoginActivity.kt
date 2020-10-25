package id.tsabit.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import id.tsabit.instagramclone.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    // viewBinding untuk Activity_Login.xml
    private lateinit var binding: ActivityLoginBinding

    // variabel LoginDialog
    private lateinit var loginDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Deklarasi viewBinding dimulai
        val inflater = layoutInflater
        binding = ActivityLoginBinding.inflate(inflater)
        setContentView(binding.root)
        // Deklarasi viewBinding selesai

        // Deklarasikan Loading Dialog
        loginDialog = LoadingDialog(this)

        binding.run {
            // Menambahkan setOnClickListener pada btnSignup
            btnSignup.setOnClickListener {
                // Buat Intent menuju ke ActivityRegister
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                // Mulai activity intent
                startActivity(intent)
            }

            btnLogin.setOnClickListener {
                // Jalankan fungsi loginUser()
                loginUser()
            }
        }
    }

    // Buat fungsi loginUser()
    private fun loginUser() {
        // Buat variabel berisi Email dan Password dari editText
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPass.text.toString()

        // Cek kalau Email dan Password itu ada
        if (email.isEmpty()) {
            Toast.makeText(this, "Email harus diisi!", Toast.LENGTH_SHORT).show()
            // return agar fungsi berakhir
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 8) { // Jika Password kurang dari 8 karakter
            Toast.makeText(this, "Password minimal 8 karakter!", Toast.LENGTH_SHORT).show()
            return
        }

        // Jika Email dan Password sudah sesuai
        // Buat variabel untuk menghubungkan ke Firebase
        val mAuth = FirebaseAuth.getInstance()

        // Tampilkan loading terlebih dahulu sebelum ke Firebase
        loginDialog.startLoadingDialog()

        // Masuk ke Firebase dengan Email dan Password
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Jika berhasil Login
                if (task.isSuccessful) {
                    // Tutup loading
                    loginDialog.dismissDialog()
                    // Lanjut ke MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    // addflag menambahkan opsi pada Intent
                    // FLAG_ACTIVITY_CLEAR_TASK digunakan untuk menonaktifkan tombol back
                    // FLAG_ACTIVITY_NEW_TASK digunakan untuk membuat activity baru
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    // Mulai Intent
                    startActivity(intent)
                    // Tutup activity login dengan finsih()
                    finish()
                } else { // Jika tidak berhasil Login
                    // Tampilkan pesan error Login
                    val message = task.exception.toString()
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    // Signout dari Firebase
                    mAuth.signOut()
                    // Tutup loading
                    loginDialog.dismissDialog()
                }
            }
    }

    // Fungsi onStart dijalankan setelah onCreate
    override fun onStart() {
        super.onStart()

        // Jika user tidak null atau ada maka lanjut ke MainActivity
        if (FirebaseAuth.getInstance().currentUser !=null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
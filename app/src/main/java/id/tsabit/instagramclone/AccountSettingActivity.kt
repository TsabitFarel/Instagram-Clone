package id.tsabit.instagramclone

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import id.tsabit.instagramclone.databinding.ActivityAccountSettingBinding
import id.tsabit.instagramclone.model.User

class AccountSettingActivity : AppCompatActivity() {
    // viewBinding untuk activity_account_setting.xml
    private lateinit var binding: ActivityAccountSettingBinding

    // Buat variabel userInfo berisi database reference
    private lateinit var userInfo: DatabaseReference

    // Buat variabel user yang berisi model dari struktur data di Firebase
    private lateinit var user: User

    // Buat variabel untuk mengakses Firebase storage
    private lateinit var firebaseStorage: StorageReference

    // Buat variabel dialog
    private lateinit var dialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pengaturan viewBinding dimulai
        val inflater = layoutInflater
        binding = ActivityAccountSettingBinding.inflate(inflater)
        setContentView(binding.root)
        // Pengaturan viewBinding selesai

        // Inialisasi dialog
        dialog = LoadingDialog(this)

        // Setting agar button Logout bisa Logout dari Firebase
        // Dan kembali ke ActivityLogin
        binding.btnLogout.setOnClickListener {
            // Signout / Logout dari Firebase
            FirebaseAuth.getInstance().signOut()
            // buat Intent menuju Activity Login
            val intent = Intent(this, LoginActivity::class.java)
            // addFlags agar tombol back tidak bisa diklik
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            // mulai Activity Intent
            startActivity(intent)
            // finish (hapus) activity account setting activity
            finish()
        }

        // Tombol close diberi setOnclickListener finish untuk menutup finish untuk menutup activity
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Tombol centang diberi setOnClick untuk mengupdate info
        binding.btnAccept.setOnClickListener {
            updateUserInfo()
        }

        // Jika ada user yang login
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            // Dapatkan UID dari User yang login
            val uidUser = currentUser.uid
            userInfo = FirebaseDatabase.getInstance()
                .reference
                .child("users")
                .child(uidUser)

            // Aktifkan Firebase Storage, Buat folder bernama ProfilePict
            firebaseStorage = FirebaseStorage.getInstance().reference.child("ProfilePict")

            // Mengaktifkan tombol ganti gambar agar membuka Crop gambar
            binding.btnChange.setOnClickListener {
                CropImage.activity().setAspectRatio(1, 1).start(this)
            }

            // Jika ada user yang Login maka tombol Delete Account bisa di klik
            // Tombol Delete Account setOnClick untuk menghapus akun
            binding.btnDelete.setOnClickListener {

                // Buat credential berisi Email dan Password dari User
                val password = binding.inputPassword.text.toString()
                val emailUser = currentUser.email.toString()

                if (password.isEmpty()) {
                    Toast.makeText(this, "Masukkan Ulang Password", Toast.LENGTH_SHORT).show()
                    binding.inputPassword.visibility = View.VISIBLE
                } else {
                    binding.inputPassword.visibility = View.GONE

                    val credential = EmailAuthProvider.getCredential(emailUser, password)
                    // reauthenthicate untuk login ulang dari sistem
                    currentUser.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) { // Jika berhasil Login
                            // Hapus user saat ini yang ada di authenticate Firebase
                            currentUser.delete()
                            // Hapus user infonya juga yang ada di realtime Database Firebase
                            userInfo.removeValue()
                            // Hapus user selesai
                            // Logout dari Firebase
                            FirebaseAuth.getInstance().signOut()
                            // Intent menuju Activity Login
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            // finish() hapus Activity Account Setting Activity
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Error :" + task.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.inputPassword.visibility = View.VISIBLE
                            binding.inputPassword.text.clear()
                        }
                    }
                }
            }

            // Ambil data dari userInfo
            userInfo.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Jadikan data dari Firebase menjadi data class user
                            user = snapshot.getValue(User::class.java) as User
                            binding.run {
                                // Masukkan data name, Username, dan Bio ke dalam EditText
                                inputName.text = SpannableStringBuilder(user.fullname)
                                inputUsername.text = SpannableStringBuilder(user.username)
                                inputBio.text = SpannableStringBuilder(user.bio)
                                // Tambahkan Glide untuk menambahkan gambar
                                // Cek jika user memiliki gambar
                                var urlImage = user.image
                                // Jika url gambar kosong maka ganti dengan gambar standar
                                if (urlImage.isEmpty()) urlImage =
                                    "https://tanjungpinangkota.bawaslu.go.id/wp-content/uploads/2020/05/default-1.jpg"
                                // Masukkan gambar ke dalam ImageView
                                Glide.with(this@AccountSettingActivity)
                                    .load(urlImage)
                                    .circleCrop()
                                    .into(imgProfile)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            )
        }
    }

    // onActivityResult digunakan untuk menerima data dari Actity lain
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Karena banyak jenis request Code
        // Maka untuk mengambil data dari Activity CropImage kita gunakan request code CropImage
        // Jika sukses mengambil gambar dari galeri + crop
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            // Jika TIDAK ada error maupun cancel dari user saat crop image
            // Maka fungsi di dalam ini akan dijalankan

            // Ambil gambar dari CropImage
            val resultUriImage =CropImage.getActivityResult(data).uri
            // Mulai Loading Dialog
            dialog.startLoadingDialog()
            // Buat URL gambar di Firebase
            val fileRef = firebaseStorage.child(user.uid + ".jpg")
            // Upload gambar
            val uploadImage = fileRef.putFile(resultUriImage)
            // https://firebase.google.com/docs/storage/android/upload-files?hl=id#get_a_download_url
            uploadImage.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Jika upload sukses
                    // Dapatkan URL Download dari foto profile
                    val downloadUri = task.result
                    user.image = task.result.toString()
                    // Masukkan foto profile baru menggunakan Glide
                    Glide.with(this)
                        .load(user.image)
                        .circleCrop()
                        .into(binding.imgProfile)
                    // Update user info
                    updateUserInfo()
                    // Hilangkan loading bar
                    dialog.dismissDialog()
                    Toast.makeText(this, "Sukses Upload Foto Profile", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failures
                    dialog.dismissDialog()
                    Toast.makeText(this,"Gagal Upload Foto Profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Buat private fungsi updateUserInfo() untuk menyimpan info user ke dalam database
    // private fun itu fungsi yang hanya bisa di akses oleh fungsi lain di dalam kelas yang sama
    private fun updateUserInfo() {
        // Akses smeua input text yang ada di account setting activity
        binding.run {
            val fullName = inputName.text.toString()
            val userName = inputUsername.text.toString()
            val userBio = inputBio.text.toString()

            // Cek apakah semua data telah terisi
            if (fullName.isEmpty()) {
                Toast.makeText(this@AccountSettingActivity, "Nama Harus diisi!", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            if (userName.isEmpty()) {
                Toast.makeText(
                    this@AccountSettingActivity,
                    "UserName Harus diisi!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Buat userMap yang menyimpan data terupdate
            // Nama variabel dalam userMap harus persis sama dengan yang ada di Firebase
            val userMap = HashMap<String, Any>()
            userMap["bio"] = userBio
            userMap["email"] = user.email
            userMap["fullname"] = fullName
            userMap["image"] = user.image
            userMap["uid"] = user.uid
            userMap["username"] = userName

            // Update data yang ada pada Firebase
            userInfo.updateChildren(userMap)
            Toast.makeText(this@AccountSettingActivity, "User Telah Diupdate", Toast.LENGTH_SHORT)
                .show()

            // finish() untuk keluar dari AccountSetting
            finish()
        }
    }
}
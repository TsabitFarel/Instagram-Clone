package id.tsabit.instagramclone.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import id.tsabit.instagramclone.AccountSettingActivity
import id.tsabit.instagramclone.databinding.FragmentProfileBinding
import id.tsabit.instagramclone.model.User

class ProfileFragment : Fragment() {

    // viewBinding Fragment untuk fragment_profile.xml
    private lateinit var binding: FragmentProfileBinding

    // Buat variabel userInfo berisi database reference
    // DatabaseReference adalah bagian dari realtime database dari Firebase
    private lateinit var databaseReference: DatabaseReference

    // Buat variabel user yang berisi model user dari struktur data di Firebase
    private lateinit var user: User

    // onCreateView dipakai untuk menginisialisasi view yang ada pada layout fragment_profile
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inisialisasi viewBinding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    // onViewCreated untuk memberi fungsi pada view di dalam layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Beri fungsi setOnClickListener pada tombol Edit Profile
        binding.btnEditProfile.setOnClickListener {
            // Buat Intent menuju AccountSettingActivity
            val intent = Intent(view.context, AccountSettingActivity::class.java)
            startActivity(intent)
        }

        // Cek adanya user login saat ini
        FirebaseAuth.getInstance().currentUser?.let {currentUser ->
            // Dapatkan UID dari User yang login
            val uidUser = currentUser.uid
            // Dapatkan database reference berdasarkan UID
            databaseReference = FirebaseDatabase.getInstance()
                .reference
                // child users berisi nama folder dari user yang ada di Firebase realtime database
                // nama ini harus persis
                .child("users")
                .child(uidUser)

            // Ambil data dari User saat ini dari databaseReference
            databaseReference.addValueEventListener(
                object  : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Ini cara mengambil data dari Firebase jadi variabel user
                            user = snapshot.getValue(User::class.java) as User
                            // Menggunakan binding agar cepat memasukkan data kedalaam UI
                            binding.run {
                                titleProfile.text = user.username
                                textBio.text = user.bio
                                textName.text = user.fullname

                                // Jika user tidak memiliki foto maka isi dengan foto profile default
                                if (user.image.isEmpty()) user.image =
                                    "https://tanjungpinangkota.bawaslu.go.id/wp-content/uploads/2020/05/default-1.jpg"
                                // Masukkan foto profile menggunakan Glide, circleCrop agar jadi lingkaran
                                Glide.with(this@ProfileFragment).load(user.image)
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
}
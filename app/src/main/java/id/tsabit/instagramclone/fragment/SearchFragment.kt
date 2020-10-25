package id.tsabit.instagramclone.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*

import id.tsabit.instagramclone.databinding.FragmentSearchBinding
import id.tsabit.instagramclone.model.User
import id.tsabit.instagramclone.recyclerview.recyclerview.adapter.ItemUserAdapter

class SearchFragment : Fragment() {

    // viewBinding untuk fragment_search.xml
    private lateinit var binding: FragmentSearchBinding

    // variabel adapterRV berisi ItemUserAdapter
    private lateinit var adapterRV : ItemUserAdapter

    // Fragment binding diawali di onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Inialisasi adapterRV terlebih dahulu
        adapterRV = ItemUserAdapter()
        // Setting RecyclerView di onCreateView
        binding.rvSearch.run {
            setHasFixedSize(true)
            // Gunakan GridLayoutManager agar Grid terbagi menjadi 2
            layoutManager = GridLayoutManager(context, 2)
            adapter = adapterRV
        }
        return binding.root
    }

    // onViewCreated itu artinya setelah tampilan dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Letakkan getUsers di onViewCreated agar fungsi ini berjalan lebih dahulu
        // Sehingga saat SearchFragment dibuka yang akan pertama kali muncul adalah semua users yang ada
        getUsers()

        // Mendeteksi perubahan keyword pada SearchView
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Jika keyword dirubah dan tombol enter ditekan maka fungsi ini berjalan
                    Toast.makeText(view.context, query.toString(), Toast.LENGTH_SHORT).show()
                    // panggil funsi getUsers() jika tidak ada keyword yang dimasukkan
                    if (query.isNullOrBlank()){
                        // Fungsi dari query.isNullOrEmpty() itu untuk mengecek kalu variabel query tidak null atau kosong
                        getUsers()
                    } else {
                        // Jalanka fungsi searchUser() untuk mencari user sesuai keyword
                        searchUser(query)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Fungsi ini akan berjalan setiap kali ada perubahan keyword
                    // Cek jika keyword tidak berisi kata-kata maka tampilkan semua user
                    if(newText.isNullOrBlank()) getUsers()
                    return false
                }

            }
        )
    }

    // Buat fungsi untuk mendapatkan semua user dari Firebase realtime database
    private fun getUsers() {
        // Tentukan tabel yang akan diambil datanya
        val userDB = FirebaseDatabase.getInstance()
            .reference
            // Ambil data dari tabel users
            .child("users")
            // Urutkan berdasarkan fullname
            .orderByChild("fullname")
            // Batasi 10 data pertama
            .limitToFirst(10)

        userDB.addValueEventListener(
            object : ValueEventListener {
                // Fungsi onDataChange berjalan jika sukses
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Pastikan snapshot exists(ada), snapshot adalah data hasil pengambilan dari database
                    if (snapshot.exists()) {
                        Log.e("SnapshotExist", "Yes")
                        Log.e("SnapshotSum", snapshot.childrenCount.toString())
                        // Buat array kosong yang hanya bisa diisi oleh model User
                        val listUser = arrayListOf<User>()
                        // Buat variabel berisi ukuran data yang didapatkan
                        val dataSize = snapshot.childrenCount
                        // Jika ukuran data lebih besar dari 0, Maka lakukan pengambilan data
                        if (dataSize > 0) {
                            // Lakukan perulangan masing-masing data
                            for (data in snapshot.children) {
                                val user = data.getValue(User::class.java) as User
                                // Tambahkan data user ke dalam array
                                listUser.add(user)
                            }
                            // Masukkan kumpulan data users ke dalam adapter ReyclerView
                            adapterRV.addData(listUser)
                        }
                    }
                }

                // Fungsi onCancelled berjalan ketika ada error
                override fun onCancelled(error: DatabaseError) {
                    Log.e("SnapshotError", error.details)
                }
            }
        )
    }
    // Buat fungsi searchUser() berdasarkan keyword fullname
    private fun searchUser(keyword : String) {
        // Tentukan tabel yang akan diambil datanya
        val userDB = FirebaseDatabase.getInstance()
            .reference
            // Ambil data dari tabel users
            .child("users")
            // Urutkan berdasarkan fullname
            .orderByChild("fullname")
            .startAt(keyword)
            .endAt("$keyword \uf8ff")

        userDB.addValueEventListener(
            object : ValueEventListener {
                // Fungsi onDataChange berjalan jika sukses
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Pastikan snapshot exists(ada), snapshot adalah data hasil pengambilan dari database
                    if (snapshot.exists()) {
                        Log.e("SnapshotExist", "Yes")
                        Log.e("SnapshotSum", snapshot.childrenCount.toString())
                        // Buat array kosong yang hanya bisa diisi oleh model User
                        val listUser = arrayListOf<User>()
                        // Buat variabel berisi ukuran data yang didapatkan
                        val dataSize = snapshot.childrenCount
                        // Jika ukuran data lebih besar dari 0, Maka lakukan pengambilan data
                        if (dataSize > 0) {
                            // Lakukan perulangan masing-masing data
                            for (data in snapshot.children) {
                                val user = data.getValue(User::class.java) as User
                                // Tambahkan data user ke dalam array
                                listUser.add(user)
                            }
                            // Masukkan kumpulan data users ke dalam adapter ReyclerView
                            adapterRV.addData(listUser)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
        )
    }
}
package id.tsabit.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import id.tsabit.instagramclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Deklarasikan binding viewBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewBinding inflater
        val inflater = layoutInflater
        // set viewBinding untuk activitymain.xml
        binding = ActivityMainBinding.inflate(inflater)
        // setContentView diisi dengan binding.root
        setContentView(binding.root)

        // Definisikan NavHostFragment yang ada di Activity_Main.xml
        // id nya itu fragment_container
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment? ?: return
        val navController = host.navController

        // NavView itu adalah id dari bottomNavigation
        binding.navView.setupWithNavController(navController)
    }
}
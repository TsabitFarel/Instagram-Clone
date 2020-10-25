package id.tsabit.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.tsabit.instagramclone.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {
    // variabel Binding
    private lateinit var binding: ActivityUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // viewBinding Init start
        val inflater = layoutInflater
        binding = ActivityUploadBinding.inflate(inflater)
        setContentView(binding.root)
        // viewBinding Init end


    }
}
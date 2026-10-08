package ru.mtuci.drivenext.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityPlaceholderBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.setText(R.string.main_title)
        binding.subtitle.text = ""
    }
}

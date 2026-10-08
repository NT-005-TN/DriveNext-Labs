package ru.mtuci.drivenext.presentation.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ru.mtuci.drivenext.databinding.ActivityProfileBinding
import ru.mtuci.drivenext.presentation.auth.LoginActivity

class ProfileActivity : AppCompatActivity() {
 private val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { binding.avatar.setImageURI(it) } }
 private lateinit var binding: ActivityProfileBinding
 override fun onCreate(state: Bundle?) { super.onCreate(state); binding = ActivityProfileBinding.inflate(layoutInflater); setContentView(binding.root); binding.avatar.setOnClickListener { picker.launch("image/*") }; binding.logoutButton.setOnClickListener { getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply(); startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)) } }
}

package com.example.retocolaborativo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_perfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        val tvDatos = findViewById<TextView>(R.id.tvDatos)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        val firstName = intent.getStringExtra("FIRST_NAME") ?: "Usuario"
        val email = intent.getStringExtra("EMAIL") ?: ""

        tvSaludo.text = getString(R.string.welcome_message, firstName)
        tvDatos.text = getString(R.string.user_email, email)

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

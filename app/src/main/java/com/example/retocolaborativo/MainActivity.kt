package com.example.retocolaborativo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var token: String? = null

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvResultado = findViewById(R.id.tvResultado)

        // Prellenar con credenciales de prueba por comodidad
        etUsuario.setText("emilys")
        etPassword.setText("emilyspass")

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString()

            if (usuario.isEmpty() || password.isEmpty()) {
                tvResultado.text = "Por favor ingresa usuario y contraseña"
                return@setOnClickListener
            }

            tvResultado.text = "Iniciando sesión..."
            hacerLogin(usuario, password)
        }
    }

    private fun hacerLogin(usuario: String, clave: String) {
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.api.login(
                    LoginRequest(usuario, clave)
                )
                if (resp.isSuccessful) {
                    token = resp.body()?.accessToken
                    Log.d("API", "Token recibido: $token")
                    obtenerUsuario()
                } else {
                    tvResultado.text = "Error de autenticación: ${resp.code()}"
                    Log.e("API", "Login falló: ${resp.code()}")
                }
            } catch (e: Exception) {
                tvResultado.text = "Error de red: ${e.message}"
                Log.e("API", "Error de red: ${e.message}")
            }
        }
    }

    private fun obtenerUsuario() {
        val t = token ?: return
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.api.getCurrentUser("Bearer $t")
                if (resp.isSuccessful) {
                    val user = resp.body()
                    Log.d("API", "Hola ${user?.firstName} - ${user?.email}")

                    val intent = Intent(this@MainActivity, activity_perfil::class.java).apply {
                        putExtra("TOKEN", t)
                        putExtra("FIRST_NAME", user?.firstName ?: "")
                        putExtra("EMAIL", user?.email ?: "")
                    }
                    startActivity(intent)
                    finish()
                } else {
                    tvResultado.text = "No se pudo obtener el usuario"
                }
            } catch (e: Exception) {
                tvResultado.text = "Error al obtener usuario: ${e.message}"
                Log.e("API", "Error: ${e.message}")
            }
        }
    }
}

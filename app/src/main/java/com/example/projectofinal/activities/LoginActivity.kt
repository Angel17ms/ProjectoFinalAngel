package com.example.projectofinal.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectofinal.activities.MainActivity
import com.example.projectofinal.activities.SignUpActivity
import com.example.projectofinal.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        if (isLoggedIn()) {
            // Si ya hay una sesión iniciada, redirige al MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.login.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.pass.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Por favor, introduce un correo electrónico válido y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registro.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                    // Guardar las credenciales en las preferencias
                    saveLogin(email, password)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Inicio de sesión fallido. Por favor, comprueba tus credenciales", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveLogin(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun isLoggedIn(): Boolean {
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        return !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }
}

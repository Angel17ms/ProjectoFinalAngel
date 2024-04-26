package com.example.projectofinal.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.projectofinal.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        binding.button2.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.pass.text.toString()
            val password2 = binding.pass2.text.toString()
            val nombre = binding.nombre.text.toString()

            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && isValidPassword(password) && password == password2) {
                signUp(email, password, nombre)
            } else {
                Toast.makeText(this, "Por favor, introduce un correo electrónico válido y contraseñas coincidentes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern = "(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}".toRegex()
        return pattern.matches(password)
    }

    private fun signUp(email: String, password: String, nombre: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    userId?.let { uid ->
                        val user = hashMapOf(
                            "nombre" to nombre
                        )
                        firestore.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al registrar al usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}

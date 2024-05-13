package com.example.projectofinal.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectofinal.R
import com.example.projectofinal.activities.LoginActivity
import com.example.projectofinal.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FragmentProfile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Obtener SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)


        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(user.uid)
            userRef.get()
                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val nombre = documentSnapshot.getString("nombre")
                        binding.editTextNombre.setText(nombre)
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar errores
                }
        }

        // Obtener el correo electrónico y la contraseña desde SharedPreferences
        val email = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")

        // Rellenar campos
        binding.editTextCorreo.setText(email)
        binding.etPassword.setText(password)

        binding.botonCerrarSesion.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun signOut() {
        sharedPreferences.edit().clear().apply()

        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

}
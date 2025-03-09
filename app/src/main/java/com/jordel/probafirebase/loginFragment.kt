package com.jordel.probafirebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jordel.probafirebase.databinding.FragmentLoginBinding

class loginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val vmUser: VMuser by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        FirebaseApp.initializeApp(requireContext())

        firebaseAuth = Firebase.auth

        binding.button.setOnClickListener {
            val correo = binding.editTextUsuario.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (correo.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            Toast.makeText(requireContext(), "Usuario corecto", Toast.LENGTH_SHORT).show()
                            if (user != null) {
                                vmUser.setUser(user.uid)
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        binding.button2.setOnClickListener {
            val correo = binding.editTextUsuario.text.toString()
            var password = binding.editTextPassword.text.toString()
            if (correo.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(correo, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            Toast.makeText(requireContext(), "Usuario registrado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        return binding.root
    }
}
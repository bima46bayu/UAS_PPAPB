package com.example.uas

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.uas.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        with(binding) {
            signupBtSignup.setOnClickListener {
                val email = signupEtEmail.text.toString()
                val password = signupEtPassword.text.toString()
                insert(email, password)
            }

            signupBtToLogin.setOnClickListener {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun insert(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    showToast("Account Created Successfully!")

                    // Ambil UID pengguna yang baru dibuat
                    val userUid = auth.currentUser?.uid

                    // Tambahkan data pengguna ke Firestore
                    if (userUid != null) {
                        addUserToFirestore(userUid, email)
                    }

                    val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    showToast("Failed to Create Account!")
                }
            }
    }

    private fun addUserToFirestore(userUid: String, userEmail: String) {
        val userCollection = FirebaseFirestore.getInstance().collection("users")

        val userData = hashMapOf(
            "email" to userEmail,
            "role" to "user"
        )

        userCollection.document(userUid).set(userData)
            .addOnSuccessListener {
                // Penambahan data berhasil
                Log.d(TAG, "User berhasil ditambahkan ke Firestore")
            }
            .addOnFailureListener { e ->
                // Penambahan data gagal
                Log.w(TAG, "Gagal menambahkan user ke Firestore", e)
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

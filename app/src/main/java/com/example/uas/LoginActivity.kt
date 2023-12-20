package com.example.uas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // If already logged in, redirect to the main page
        if (sessionManager.isLoggedIn()) {
            redirectToAppropriatePage()
        }

        // Implement login
        binding.loginBtLogin.setOnClickListener {
            val email = binding.loginEtEmail.text.toString()
            val password = binding.loginEtPassword.text.toString()
            loginUser(email, password)
        }

        binding.loginBtToSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        showToast("signInWithEmail: success")
                        onLoginSuccess(email, password)
                    } else {
                        // If sign in fails, display a message to the user.
                        showToast("signInWithEmail: failure ${task.exception?.message}")
                    }
                }
        } else {
            showToast("Email or password is empty")
        }
    }

    private fun onLoginSuccess(email: String, password: String) {
        // Save user data to SharedPreferences
        sessionManager.setLogin(true)
        sessionManager.saveUserData(email, password)

        // Redirect to the appropriate page
        redirectToAppropriatePage()
    }

    private fun redirectToAppropriatePage() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            checkAdminAccess(currentUser.uid)
        } else {
            showToast("Gagal mendapatkan informasi pengguna")
        }
    }

    private fun checkAdminAccess(uid: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")

                    if (role == "admin") {
                        // Pengguna adalah admin, arahkan ke AdminMakananActivity
                        val intent = Intent(this@LoginActivity, AdminMakananActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Pengguna bukan admin, arahkan ke MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    showToast("Gagal memeriksa peran admin")
                }
            }
            .addOnFailureListener { e ->
                showToast("Gagal memeriksa peran admin")
                e.printStackTrace()
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

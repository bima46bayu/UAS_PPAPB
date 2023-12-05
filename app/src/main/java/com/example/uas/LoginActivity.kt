package com.example.uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.uas.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        with(binding){
            loginBtLogin.setOnClickListener{

                val email = loginEtEmail.text.toString()
                val password = loginEtPassword.text.toString()
                loginUser(email, password)
            }

            loginBtToSignup.setOnClickListener{
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }

        sessionManager = SessionManager(this)

        // Jika sudah login, arahkan ke halaman utama
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Implementasi login
        binding.loginBtLogin.setOnClickListener {
            // Lakukan login
            val email = binding.loginEtEmail.text.toString()
            val password = binding.loginEtPassword.text.toString()

            onLoginSuccess(email, password)
        }

    }

    private fun loginUser(email: String, password: String) {

        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        showToast("signInWithEmail:success")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        showToast("signInWithEmail:failure")
                    }
                }
        } else {
            showToast("Email or password is empty")
        }
    }

    private fun onLoginSuccess(username: String, email: String) {
        // Simpan data pengguna ke SharedPreferences
        sessionManager.setLogin(true)
        sessionManager.saveUserData(username, email)

        // Arahkan ke halaman utama
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
package com.example.foodfolio

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.foodfolio.R
import com.example.foodfolio.activities.MainActivity
import com.google.android.material.textfield.TextInputLayout

class LogIn : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        databaseHelper = DatabaseHelper(this)

        val user: TextInputLayout = findViewById(R.id.user)
        val pass: TextInputLayout = findViewById(R.id.password)
        val btnLogIn: Button = findViewById(R.id.btnLogIn)

        btnLogIn.setOnClickListener {
            val logUsername: String = user.editText?.text.toString()
            val logPassword: String = pass.editText?.text.toString()
            
            if(logUsername.trim()!="" && logPassword.trim()!="")
                loginDatabase(logUsername, logPassword)
            else
                Toast.makeText(this, "Please answer all fields to log in.", Toast.LENGTH_LONG).show()
        }
    }

    private fun loginDatabase(username: String, password: String) {
        val user = databaseHelper.readUserByUsername(username)

        if (user != null && user.password == password) {
            Toast.makeText(this, "Log In Successful", Toast.LENGTH_LONG).show()
            val homepage = Intent(this, MainActivity::class.java)

            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("USER_ID", user.id)
            editor.putString("USER_NAME", user.name)
            editor.apply()

            startActivity(homepage)
            finish()
        } else {
            Toast.makeText(this, "Log In Failed", Toast.LENGTH_LONG).show()
        }
    }

    fun toMainActivity(view: View) {
        val i = Intent(this, SecondMainActivity::class.java)
        startActivity(i)
    }

    fun toSignUp(view: View) {
        val i = Intent(this, SignUp::class.java)
        startActivity(i)
    }
}
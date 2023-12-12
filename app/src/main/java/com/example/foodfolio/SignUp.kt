package com.example.foodfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.foodfolio.R
import com.google.android.material.textfield.TextInputLayout

class SignUp : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        databaseHelper = DatabaseHelper(this)

        val nameLayout: TextInputLayout = findViewById(R.id.name)
        val usernameLayout: TextInputLayout = findViewById(R.id.username)
        val passwordLayout: TextInputLayout = findViewById(R.id.password)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val signName: String = nameLayout.editText?.text.toString()
            val signUsername: String = usernameLayout.editText?.text.toString()
            val signPassword: String = passwordLayout.editText?.text.toString()

            if(signName.trim()!="" && signUsername.trim()!="" && signPassword.trim()!="")
                signUpDatabase(signName, signUsername, signPassword)
            else
                Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun signUpDatabase(name: String, username: String, password: String) {
        val insertedRowId = databaseHelper.insertUser(User(0, name, username, password))
        if(insertedRowId != -1L){
            Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_LONG).show()
            val login = Intent(this, LogIn::class.java)
            startActivity(login)
            finish()
        }
        else {
            Toast.makeText(this, "Username Already Exists", Toast.LENGTH_LONG).show()
        }
    }

    fun toMainActivity(view: View) {
        val i = Intent(this, SecondMainActivity::class.java)
        startActivity(i)

    }

    fun toLogIn(view: View) {
        val i = Intent(this, LogIn::class.java)
        startActivity(i)
    }
}
package com.example.foodfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.foodfolio.R

class SecondMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_main)
 



    }

    // to Sign Up
    fun toSignUp(view: View) {
        val i = Intent(this, SignUp::class.java)
        startActivity(i)
    }

    //to Log In
    fun toLogIn(view: View) {
        val i = Intent(this, LogIn::class.java)
        startActivity(i)
    }
}
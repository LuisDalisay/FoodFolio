package com.example.foodfolio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.foodfolio.R

class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val iv_note: ImageView = findViewById(R.id.iv_note)

        iv_note.alpha = 0f
        iv_note.animate().setDuration(2500).alpha(1f).withEndAction {
            val i = Intent(this, SecondMainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
    }
}
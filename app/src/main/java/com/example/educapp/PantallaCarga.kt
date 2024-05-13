package com.example.educapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class PantallaCarga : AppCompatActivity() {
    private val tiempo: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_carga)

        Handler().postDelayed({
            startActivity(Intent(this@PantallaCarga, MainActivity::class.java))
            finish()
        }, tiempo)
    }



    }

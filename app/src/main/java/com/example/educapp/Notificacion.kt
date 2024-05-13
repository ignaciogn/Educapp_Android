package com.example.educapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.educapp.databinding.ActivityNotificacionBinding

class Notificacion : AppCompatActivity() {
    lateinit var binding: ActivityNotificacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
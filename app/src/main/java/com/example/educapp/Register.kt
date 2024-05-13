package com.example.educapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityRegisterBinding
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Register : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.btLogin.setOnClickListener {
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.btRegister.setOnClickListener {
            // Obtener datos de los EditText
            val nombre = binding.nombreTexto.text.toString()
            val correo = binding.correoText.text.toString()
            val apellidos = binding.apellidosTexto.text.toString()
            val contrasena = binding.passTexto.text.toString()
            val numTelefono = binding.numTexto.text.toString()

            if (nombre.isBlank() || correo.isBlank() || apellidos.isBlank() || contrasena.isBlank() || numTelefono.isBlank()) {                // Crear una instancia de Estudiante con los datos
                Toast.makeText(this@Register, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }else{
                // Verificar si ya existe un estudiante con el mismo correo
                lifecycleScope.launch(Dispatchers.IO) {
                    val existingStudent = estudianteDAO.getStudentByEmail(correo)

                    if (existingStudent == null) {
                        // No hay un estudiante con el mismo correo, procede con el registro
                        val estudiante = Estudiante(
                            id = 0,
                            nombre = nombre,
                            apellidos = apellidos,
                            correo = correo,
                            telefono = numTelefono,
                            contrasena = contrasena,
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Register, "¡Se ha registrado con exito!", Toast.LENGTH_SHORT).show()
                            estudianteDAO.register(estudiante)

                            val intent = Intent(this@Register, Login::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // Ya hay un estudiante con el mismo correo, mostrar mensaje de error
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@Register,
                                "Ya existe un usuario con ese correo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        }
                }
            }
        }
    }

    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }

}
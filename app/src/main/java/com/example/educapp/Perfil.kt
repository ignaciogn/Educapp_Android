package com.example.educapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityPerfilBinding
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Perfil : AppCompatActivity() {
    lateinit var binding: ActivityPerfilBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)

        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.nombreTexto.setText(estudianteActu.nombre)
        binding.correoText.setText(estudianteActu.correo)
        binding.apellidosTexto.setText(estudianteActu.apellidos)
        binding.passTexto.setText(estudianteActu.contrasena)
        binding.numTexto.setText(estudianteActu.telefono)


        binding.correoText.setOnClickListener{
            Toast.makeText(
                this@Perfil,
                "¡No se puede modificar el correo!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btPrincipal.setOnClickListener{
            val intent = Intent(this@Perfil, Principal::class.java)
            intent.putExtra("ESTUDIANTE", estudianteActu)
            startActivity(intent)
            finish()
        }

        binding.btEliminar.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                estudianteDAO.borra(estudianteActu)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Perfil,
                        "¡Se ha eliminado con exito!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(this@Perfil, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btEditar.setOnClickListener {
            val nombre = binding.nombreTexto.text.toString()
            val correo = binding.correoText.text.toString()
            val apellidos = binding.apellidosTexto.text.toString()
            val contrasena = binding.passTexto.text.toString()
            val numTelefono = binding.numTexto.text.toString()

            if (nombre.isBlank() || correo.isBlank() || apellidos.isBlank() || contrasena.isBlank() || numTelefono.isBlank()) {                // Crear una instancia de Estudiante con los datos
                Toast.makeText(this@Perfil, "Todos los campos son obligatorios", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val estudiante = Estudiante(
                    id = estudianteActu.id,
                    nombre = nombre,
                    apellidos = apellidos,
                    correo = correo,
                    telefono = numTelefono,
                    contrasena = contrasena,
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Perfil,
                            "¡Se ha actualizado con exito!",
                            Toast.LENGTH_SHORT
                        ).show()
                        estudianteDAO.actualiza(estudiante)
                        estudianteActu = estudianteDAO.getStudentById(estudianteActu.id)!!
                        val intent = Intent(this@Perfil, Principal::class.java)
                        intent.putExtra("ESTUDIANTE", estudianteActu)
                        startActivity(intent)
                        finish()
                    }

                }

            }
        }
    }
}
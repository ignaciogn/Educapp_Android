package com.example.educapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityPrincipalBinding
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Principal : AppCompatActivity() {
    lateinit var binding: ActivityPrincipalBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        estudianteActu = intent.getSerializableExtra("ESTUDIANTE") as Estudiante

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.twNombre.setText(estudianteActu.nombre + " " + estudianteActu.apellidos)
        binding.twCorreo.setText(estudianteActu.correo)

        binding.btPerfil.setOnClickListener{
            val intent = Intent(this@Principal, Perfil::class.java)
            intent.putExtra("ESTUDIANTE", estudianteActu)
            startActivity(intent)
        }

        binding.cwLogOut.setOnClickListener {
            val intent = Intent(this@Principal, MainActivity::class.java)
            Toast.makeText(
                this@Principal,
                "Has cerrado sesión",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(intent)

        }

        binding.cwHoy.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (estudianteDAO.getNumeroDeAsignaturasParaEstudiante(estudianteActu.id) > 0) {
                    val intent = Intent(this@Principal, Hoy::class.java)
                    intent.putExtra("ESTUDIANTE", estudianteActu)
                    startActivity(intent)
                }else{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Principal,
                            "NO HAY NINGUN ASIGNATURA REGISTRADA",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.cwExamenes.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (estudianteDAO.getNumeroDeAsignaturasParaEstudiante(estudianteActu.id) > 0) {
                    val intent = Intent(this@Principal, Examenes::class.java)
                    intent.putExtra("ESTUDIANTE", estudianteActu)
                    startActivity(intent)
                }else{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Principal,
                            "NO HAY NINGUN ASIGNATURA REGISTRADA",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding.cwTareas.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (estudianteDAO.getNumeroDeAsignaturasParaEstudiante(estudianteActu.id) > 0) {
                    val intent = Intent(this@Principal, Tareas::class.java)
                    intent.putExtra("ESTUDIANTE", estudianteActu)
                    startActivity(intent)
                }else{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Principal,
                            "NO HAY NINGUN ASIGNATURA REGISTRADA",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.cwCursos.setOnClickListener {
            val intent = Intent(this@Principal, CursosAcademicos::class.java)
            intent.putExtra("ESTUDIANTE", estudianteActu)
            startActivity(intent)
        }
        binding.cwAsignaturas.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (estudianteDAO.getNumeroDeCursosParaEstudiante(estudianteActu.id) > 0) {
                    val intent = Intent(this@Principal, Asignturas::class.java)
                    intent.putExtra("ESTUDIANTE", estudianteActu)
                    startActivity(intent)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Principal,
                            "NO HAY NINGUN CURSO REGISTRADO",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    }
}
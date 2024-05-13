package com.example.educapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityExamenesBinding
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.Examen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Examenes : AppCompatActivity() {

    lateinit var binding: ActivityExamenesBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var rvAdapter: ReciclerViewAdapter3
    private lateinit var examenes: List<Examen>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamenesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        binding.btAdd.setOnClickListener{
            val intent = Intent(this@Examenes, AddExamen::class.java)
            intent.putExtra("ESTUDIANTE", estudianteActu)
            startActivity(intent)
            finish()
        }

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        lifecycleScope.launch(Dispatchers.IO) {
            try {

                examenes = estudianteDAO.getExamenesPorEstudianteId(estudianteActu.id)

                withContext(Dispatchers.Main) {
                    val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@Examenes)

                    binding.lista.layoutManager = layoutManager

                    rvAdapter = ReciclerViewAdapter3(examenes)

                    binding.lista.adapter = rvAdapter

                    rvAdapter.onItemClick = {
                        val intent = Intent(this@Examenes, ExamenIndividual::class.java)
                        intent.putExtra("ESTUDIANTE", estudianteActu)
                        intent.putExtra("EXAMEN",it)
                        startActivity(intent)
                        finish()
                    }

                }
            } catch (e: Exception) {
                Log.e("HOY", "Error al obtener cursos del estudiante: ${e.message}")
            }
        }


    }
    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }
}
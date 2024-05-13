package com.example.educapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityCursosBinding
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CursosAcademicos: AppCompatActivity() {
    lateinit var binding: ActivityCursosBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var rvAdapter: ReciclerViewAdapter
    private lateinit var cursos: List<CursoAcademico>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCursosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        binding.btAdd.setOnClickListener{
            val intent = Intent(this@CursosAcademicos, AddCursos::class.java)
            intent.putExtra("ESTUDIANTE", estudianteActu)
            startActivity(intent)
            finish()
        }

        estudianteDAO = bd.estudianteDAO()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Obtén el estudiante con cursos usando tu DAO
                val estudianteConCursos = estudianteDAO.getEstudianteConCursos(estudianteActu.id)
                // Accede a la lista de cursos en el objeto EstudianteConCurso
                cursos = estudianteConCursos.cursosAcademicos

                withContext(Dispatchers.Main) {
                    val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@CursosAcademicos)

                    binding.lista.layoutManager = layoutManager

                    rvAdapter = ReciclerViewAdapter(cursos)

                    binding.lista.adapter = rvAdapter

                    rvAdapter.onItemClick = {
                        val intent = Intent(this@CursosAcademicos, CursoIndividual::class.java)
                        intent.putExtra("ESTUDIANTE", estudianteActu)
                        intent.putExtra("CURSO",it)
                        startActivity(intent)
                        finish()
                    }

                }
                // Imprime los cursos
                for (curso in cursos) {
                    Log.i(
                        "HOY",
                        "Curso: ${curso.nombre}, Inicio: ${curso.fechaInicio}, Fin: ${curso.fechaFin}, estudiante: ${curso.estudianteId}"
                    )
                }

            } catch (e: Exception) {
                Log.e("HOY", "Error al obtener cursos del estudiante: ${e.message}")
            }
        }
    }
}
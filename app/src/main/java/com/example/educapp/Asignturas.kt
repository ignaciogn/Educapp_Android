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
import com.example.educapp.databinding.ActivityAsignturasBinding
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Asignturas : AppCompatActivity() {
    lateinit var binding: ActivityAsignturasBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var rvAdapter: ReciclerViewAdapter2
    private lateinit var asignaturas: List<Asignatura>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsignturasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        binding.btAdd.setOnClickListener{
            val intent = Intent(this@Asignturas, AddAsignatura::class.java)
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
                // Obtén el estudiante con cursos usando tu DAO
                val asignaturasConCursos = estudianteDAO.getAsignaturasPorEstudiante(estudianteActu.id)
                // Accede a la lista de cursos en el objeto EstudianteConCurso
                asignaturas = asignaturasConCursos


                withContext(Dispatchers.Main) {
                    val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@Asignturas)

                    binding.lista.layoutManager = layoutManager

                    rvAdapter = ReciclerViewAdapter2(asignaturas)

                    binding.lista.adapter = rvAdapter

                    rvAdapter.onItemClick = {
                        val intent = Intent(this@Asignturas, AsignaturaIndividual::class.java)
                        intent.putExtra("ESTUDIANTE", estudianteActu)
                        intent.putExtra("ASIGNATURA",it)
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
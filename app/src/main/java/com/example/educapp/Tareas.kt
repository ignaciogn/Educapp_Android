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
import com.example.educapp.databinding.ActivityTareasBinding
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.Tarea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tareas : AppCompatActivity() {
    lateinit var binding: ActivityTareasBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var rvAdapter: ReciclerViewAdapter4
    private lateinit var tareas: List<Tarea>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTareasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!



        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.btAdd.setOnClickListener{
            val intent = Intent(this@Tareas, AddTarea::class.java)
            intent.putExtra("ESTUDIANTE", estudianteActu)
            startActivity(intent)
            finish()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {

                tareas = estudianteDAO.getTareasPorEstudianteId(estudianteActu.id)

                withContext(Dispatchers.Main) {
                    val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this@Tareas)

                    binding.lista.layoutManager = layoutManager

                    rvAdapter = ReciclerViewAdapter4(tareas)

                    binding.lista.adapter = rvAdapter

                    rvAdapter.onItemClick = {
                        val intent = Intent(this@Tareas, TareaIndividual::class.java)
                        intent.putExtra("ESTUDIANTE", estudianteActu)
                        intent.putExtra("TAREA",it)
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
package com.example.educapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityAddTareaBinding
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.Tarea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTarea : AppCompatActivity() {
    lateinit var binding: ActivityAddTareaBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var opciones: List<Asignatura>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTareaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        lifecycleScope.launch(Dispatchers.IO) {
            opciones = estudianteDAO.obtenerAsignaturasPorEstudiante(estudianteActu.id)
            val adapter = ArrayAdapter(this@AddTarea, android.R.layout.simple_spinner_item, opciones)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinneAsig.adapter = adapter
        }

        binding.btFechaHora.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btGuarda.setOnClickListener {
            val nombre = binding.nombreTexto.text.toString()
            val fecha = binding.textoFecha.text.toString()
            val descripcion = binding.textoDescripcion.text.toString()
            val asigSelec = binding.spinneAsig.selectedItem as Asignatura
            val asiganaturaID  = asigSelec.id

            if (nombre.isBlank() || fecha.isBlank()) {
                Toast.makeText(
                    this@AddTarea,
                    "Todos los campos son obligatorios(excepto Descripcion)",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                val tarea = Tarea(0,nombre,fecha,descripcion,entregado = false,-1,asiganaturaID)
                    estudianteDAO.insertarTarea(tarea)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AddTarea,
                            "¡Se ha añadido la tarea con exito!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val intent = Intent(this@AddTarea, Tareas::class.java)
                    intent.putExtra("ESTUDIANTE",estudianteActu)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDaySelected(day,month,year) }
        datePicker.show(supportFragmentManager,"datePicker")
    }

    private fun onDaySelected(dia: Int, mes: Int, anio:Int){
        binding.textoFecha.setText("${dia}/${mes+1}/${anio}")
    }
    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }
}
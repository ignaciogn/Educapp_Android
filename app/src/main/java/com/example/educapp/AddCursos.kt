package com.example.educapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityAddCursosBinding
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddCursos : AppCompatActivity() {
    lateinit var binding: ActivityAddCursosBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCursosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.btFechaInicio.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btFechaFin.setOnClickListener {
            showDatePickerDialog2()
        }

        binding.btGuarda.setOnClickListener {
            val nombre = binding.nombreTexto.text.toString()
            val fechaInicio = binding.textoFechaInicio.text.toString()
            val fechaFin = binding.textoFechaFin.text.toString()
            val descripcion = binding.textoDescripcion.text.toString()


            if (nombre.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
                Toast.makeText(this@AddCursos, "Todos los campos son obligatorios(excepto Descripcion)", Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch(Dispatchers.IO) {
                    val existingCurso = estudianteDAO.getCursoPorEstudianteYNombre(estudianteActu.id,nombre)

                    if (existingCurso == null) {
                        val curso = CursoAcademico(0,nombre,fechaInicio,fechaFin,estudianteActu.id,descripcion)
                        estudianteDAO.insertCurso(curso)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddCursos,
                                "¡Se ha añadido el curso con exito!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val intent = Intent(this@AddCursos, CursosAcademicos::class.java)
                        intent.putExtra("ESTUDIANTE",estudianteActu)
                        startActivity(intent)
                        finish()
                    }else{
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddCursos,
                                "Ya existe un curso con este nombre",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
             }

        }

    }

    private fun showDatePickerDialog2() {
        val datePicker = DatePickerFragment { day, month, year -> onDaySelected2(day,month,year) }
        datePicker.show(supportFragmentManager,"datePicker")    }

    private fun onDaySelected2(dia: Int, mes: Int, anio:Int) {
        binding.textoFechaFin.setText("${dia}/${mes+1}/${anio}")
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDaySelected(day,month,year) }
        datePicker.show(supportFragmentManager,"datePicker")
    }

    private fun onDaySelected(dia: Int, mes: Int, anio:Int){
        binding.textoFechaInicio.setText("${dia}/${mes+1}/${anio}")
    }
    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }
}
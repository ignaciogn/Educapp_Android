package com.example.educapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityCursoIndividualBinding
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CursoIndividual : AppCompatActivity() {
    lateinit var binding: ActivityCursoIndividualBinding
    lateinit var estudianteActu: Estudiante
    lateinit var cursoActu: CursoAcademico
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCursoIndividualBinding.inflate(layoutInflater)

        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!
        cursoActu = (intent.getSerializableExtra("CURSO") as? CursoAcademico)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.btFechaInicio.setOnClickListener{
            showDatePickerDialog2()
        }

        binding.btFechaFin.setOnClickListener {
            showDatePickerDialog()
        }

        binding.nombreTexto.setText(cursoActu.nombre)
        binding.textoFechaInicio.setText(cursoActu.fechaInicio)
        binding.textoFechaFin.setText(cursoActu.fechaFin)
        binding.textoDescripcion.setText(cursoActu.descripcion)

        binding.nombreTexto.setOnClickListener{
            Toast.makeText(
                this@CursoIndividual,
                "¡No se puede modificar el nombre!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btEditar.setOnClickListener {
            val nombre = binding.nombreTexto.text.toString()
            val fechaInicio = binding.textoFechaInicio.text.toString()
            val fechaFin = binding.textoFechaFin.text.toString()
            val descripcion = binding.textoDescripcion.text.toString()


            if (nombre.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
                Toast.makeText(this@CursoIndividual, "Todos los campos son obligatorios(excepto Descripcion)", Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch(Dispatchers.IO) {
                        val curso = CursoAcademico(cursoActu.id,nombre,fechaInicio,fechaFin,estudianteActu.id,descripcion)
                        estudianteDAO.updateCurso(curso)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CursoIndividual,
                            "¡Se ha actualizado con exito!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                        val intent = Intent(this@CursoIndividual, CursosAcademicos::class.java)
                        intent.putExtra("ESTUDIANTE",estudianteActu)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        binding.nombreTexto.setOnClickListener {
            Toast.makeText(this@CursoIndividual, "No se puede modificar el nombre", Toast.LENGTH_SHORT).show()
        }

        binding.btEliminar.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                estudianteDAO.deleteCursoById(cursoActu.id)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CursoIndividual,
                        "¡Se ha eliminado con exito!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(this@CursoIndividual, CursosAcademicos::class.java)
                intent.putExtra("ESTUDIANTE", estudianteActu)
                startActivity(intent)
                finish()
            }
        }

        }




    private fun showDatePickerDialog2() {
        val datePicker = DatePickerFragment { day, month, year -> onDaySelected(day,month,year) }
        datePicker.show(supportFragmentManager,"datePicker")    }

    private fun onDaySelected2(dia: Int, mes: Int, anio:Int) {
        binding.textoFechaFin.setText("${dia}/${mes+1}/${anio}")
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDaySelected2(day,month,year) }
        datePicker.show(supportFragmentManager,"datePicker")
    }

    private fun onDaySelected(dia: Int, mes: Int, anio:Int) {
        binding.textoFechaInicio.setText("${dia}/${mes+1}/${anio}")
    }


    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }
}
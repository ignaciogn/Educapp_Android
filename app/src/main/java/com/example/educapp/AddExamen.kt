package com.example.educapp

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityAddExamenBinding
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.Examen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddExamen : AppCompatActivity() {
    lateinit var binding: ActivityAddExamenBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var opciones: List<Asignatura>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExamenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.btGuarda.setOnClickListener {
            val nombre = binding.nombreTexto.text.toString()
            val fecha = binding.textoFecha.text.toString()
            val hora = binding.textoHora.text.toString()
            val aula = binding.textAula.text.toString()
            val descripcion = binding.textoDescripcion.text.toString()
            val asigSelec = binding.spinneAsig.selectedItem as Asignatura
            val asiganaturaID  = asigSelec.id

            if (nombre.isBlank() || fecha.isBlank() || hora.isBlank() || aula.isBlank()) {
                Toast.makeText(
                    this@AddExamen,
                    "Todos los campos son obligatorios(excepto Descripcion)",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                 val examen = Examen(0,nombre,fecha,hora,aula,descripcion,false,-1,asiganaturaID)
                    estudianteDAO.insertarExamen(examen)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AddExamen,
                            "¡Se ha añadido el examen con exito!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val intent = Intent(this@AddExamen, Examenes::class.java)
                    intent.putExtra("ESTUDIANTE",estudianteActu)
                    startActivity(intent)
                    finish()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            opciones = estudianteDAO.obtenerAsignaturasPorEstudiante(estudianteActu.id)
            val adapter = ArrayAdapter(this@AddExamen, R.layout.simple_spinner_item, opciones)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinneAsig.adapter = adapter
        }

        binding.btFechaHora.setOnClickListener {
            showDateTimePickerDialog()
        }
    }

    private fun showDateTimePickerDialog() {
        val dateTimePicker = DateTimePickerFragment { day, month, year, hour, minute ->
            onDateTimeSelected(
                day,
                month,
                year,
                hour,
                minute
            )
        }
        dateTimePicker.show(supportFragmentManager, "dateTimePicker")
    }

    private fun onDateTimeSelected(dia: Int, mes: Int, anio: Int, hora: Int, minuto: Int) {
        binding.textoFecha.setText("${dia}/${mes+1}/${anio}")
        binding.textoHora.setText("${hora}:${minuto}")
    }

    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }


}
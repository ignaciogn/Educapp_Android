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
import com.example.educapp.databinding.ActivityExamenIndividualBinding
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.Examen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExamenIndividual : AppCompatActivity() {
    lateinit var binding: ActivityExamenIndividualBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var examenActu: Examen
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var opciones: List<Asignatura>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamenIndividualBinding.inflate(layoutInflater)
        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!
        examenActu = (intent.getSerializableExtra("EXAMEN") as? Examen)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.nombreTexto.setText(examenActu.nombre)
        binding.textoDescripcion.setText(examenActu.descripcion)
        binding.textoFecha.setText(examenActu.fecha)
        binding.textoHora.setText(examenActu.hora)
        binding.textAula.setText(examenActu.aula)
        if(examenActu.nota!=-1){
            binding.textoNota.setText(examenActu.nota.toString())
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val aux: Asignatura? = estudianteDAO.getAsignaturaPorId(examenActu.asignaturaId)
            opciones = estudianteDAO.getAsignaturasPorEstudiante(estudianteActu.id)
            val adapter = ArrayAdapter(this@ExamenIndividual, R.layout.simple_spinner_item, opciones)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            withContext(Dispatchers.Main) {
                binding.spinneAsig.adapter = adapter
                val posicionCursoDeseado = opciones.indexOf(aux)
                if (posicionCursoDeseado != -1) {
                    binding.spinneAsig.setSelection(posicionCursoDeseado)
                }
            }
        }

        binding.btEditar.setOnClickListener {
            val nombre = binding.nombreTexto.text.toString()
            val fecha = binding.textoFecha.text.toString()
            val hora = binding.textoHora.text.toString()
            val aula = binding.textAula.text.toString()
            val descripcion = binding.textoDescripcion.text.toString()
            val asigSelec = binding.spinneAsig.selectedItem as Asignatura
            val asiganatruaID = asigSelec.id
            var nota = binding.textoNota.text.toString()
            var notaFinal: Int

            if (nota.isNotBlank()) {
                notaFinal = try {
                    nota.toInt()
                } catch (e: NumberFormatException) {
                    0 // Valor predeterminado si no se puede convertir a entero.
                }
                examenActu.realizado = true
            } else {
                notaFinal = -1
                examenActu.realizado = false
            }

            if (nombre.isBlank() || fecha.isBlank() || hora.isBlank() || aula.isBlank() || aula.isBlank()) {
                Toast.makeText(
                    this@ExamenIndividual,
                    "Todos los campos son obligatorios (excepto Descripcion)",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val examen = Examen(examenActu.id, nombre, fecha, hora, aula, descripcion, examenActu.realizado, notaFinal, asiganatruaID)
                    estudianteDAO.updateExamen(examen)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ExamenIndividual,
                            "¡Se ha actualizado el examen con éxito!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val intent = Intent(this@ExamenIndividual, Examenes::class.java)
                    intent.putExtra("ESTUDIANTE", estudianteActu)
                    startActivity(intent)
                    finish()
                }
            }
        }


        binding.btEliminar.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                estudianteDAO.deleteExamenById(examenActu.id)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ExamenIndividual,
                        "¡Se ha eliminado con exito!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(this@ExamenIndividual, Examenes::class.java)
                intent.putExtra("ESTUDIANTE", estudianteActu)
                startActivity(intent)
                finish()
            }
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
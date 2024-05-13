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
import com.example.educapp.databinding.ActivityTareaIndividualBinding
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.Tarea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareaIndividual : AppCompatActivity() {
    lateinit var binding: ActivityTareaIndividualBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var tareaActu: Tarea
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var opciones: List<Asignatura>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTareaIndividualBinding.inflate(layoutInflater)
        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!
        tareaActu = (intent.getSerializableExtra("TAREA") as? Tarea)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.nombreTexto.setText(tareaActu.nombre)
        binding.textoDescripcion.setText(tareaActu.descripcion)
        binding.textoFecha.setText(tareaActu.fechaEntrega)
        if(tareaActu.nota!=-1){
            binding.textoNota.setText(tareaActu.nota.toString())
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val aux: Asignatura? = estudianteDAO.getAsignaturaPorId(tareaActu.asignaturaId)
            opciones = estudianteDAO.getAsignaturasPorEstudiante(estudianteActu.id)
            val adapter = ArrayAdapter(this@TareaIndividual, R.layout.simple_spinner_item, opciones)
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
            val descripcion = binding.textoDescripcion.text.toString()
            val asigSelec = binding.spinneAsig.selectedItem as Asignatura
            val asiganaturaID = asigSelec.id
            var nota = binding.textoNota.text.toString()
            var notaFinal: Int

            if (nota.isNotBlank()) {
                notaFinal = try {
                    nota.toInt()
                } catch (e: NumberFormatException) {
                    0 // Valor predeterminado si no se puede convertir a entero.
                }
                tareaActu.entregado = true
            } else {
                notaFinal = -1
                tareaActu.entregado = false

            }

            if (nombre.isBlank() || fecha.isBlank()) {
                Toast.makeText(
                    this@TareaIndividual,
                    "Todos los campos son obligatorios (excepto Descripcion)",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val tarea = Tarea(tareaActu.id,nombre,fecha,descripcion,tareaActu.entregado,notaFinal,asiganaturaID)
                    estudianteDAO.actualizarTarea(tarea)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@TareaIndividual,
                            "¡Se ha actualizado la tarea con éxito!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val intent = Intent(this@TareaIndividual, Tareas::class.java)
                    intent.putExtra("ESTUDIANTE", estudianteActu)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.btEliminar.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                estudianteDAO.eliminarTareaPorId(tareaActu.id)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@TareaIndividual,
                        "¡Se ha eliminado con exito!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(this@TareaIndividual, Tareas::class.java)
                intent.putExtra("ESTUDIANTE", estudianteActu)
                startActivity(intent)
                finish()
            }
        }

        binding.btFechaHora.setOnClickListener{
            showDatePickerDialog()
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
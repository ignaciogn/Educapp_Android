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
import com.example.educapp.databinding.ActivityAsignaturaIndividualBinding
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsignaturaIndividual : AppCompatActivity() {
    lateinit var binding: ActivityAsignaturaIndividualBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var asignaturaActu: Asignatura
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var opciones: List<CursoAcademico>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsignaturaIndividualBinding.inflate(layoutInflater)
        setContentView(binding.root)
        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!
        asignaturaActu = (intent.getSerializableExtra("ASIGNATURA") as? Asignatura)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()

        binding.nombreTexto.setText(asignaturaActu.nombre)
        binding.textoHorario.setText(asignaturaActu.horario)
        binding.textoAula.setText(asignaturaActu.aula)
        binding.textProfe.setText(asignaturaActu.aula)
        binding.textoDescripcion.setText(asignaturaActu.descripcion)
        binding.textoCreditos.setText(asignaturaActu.creditos.toString())

        binding.nombreTexto.setOnClickListener {
            Toast.makeText(this@AsignaturaIndividual, "No se puede modificar el nombre", Toast.LENGTH_SHORT).show()
        }

        binding.btEditar.setOnClickListener {
            val nombre = binding.nombreTexto.text.toString()
            val creditosAux = binding.textoCreditos.text.toString()
            val creditos = try {
                creditosAux.toInt()
            } catch (e: NumberFormatException) {
                0 // Valor predeterminado si no se puede convertir a entero.
            }
            val aula = binding.textoAula.text.toString()
            val horario = binding.textoHorario.text.toString()
            val profe = binding.textProfe.text.toString()
            val descripcion = binding.textoDescripcion.text.toString()
            // Obtener el CursoAcademico seleccionado del Spinner.
            val cursoSeleccionado = binding.spinneCursos.selectedItem as CursoAcademico
            // Ahora accedemos a las propiedades del objeto 'cursoSeleccionado'.
            val idCurso = cursoSeleccionado.id

            if (nombre.isBlank() || creditosAux.isBlank() || aula.isBlank() || horario.isBlank() || profe.isBlank()){
                Toast.makeText(this@AsignaturaIndividual, "Todos los campos son obligatorios(excepto Descripcion)", Toast.LENGTH_SHORT).show()
            }else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val asignatura =
                        Asignatura(asignaturaActu.id, nombre, creditos, aula, horario, descripcion, profe, idCurso)
                    estudianteDAO.updateAsig(asignatura)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AsignaturaIndividual,
                            "¡Se ha actualizado la asignatura con exito!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val intent = Intent(this@AsignaturaIndividual, Asignturas::class.java)
                    intent.putExtra("ESTUDIANTE", estudianteActu)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.btEliminar.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                estudianteDAO.deleteAsignaturaById(asignaturaActu.id)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AsignaturaIndividual,
                        "¡Se ha eliminado con exito!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(this@AsignaturaIndividual, Asignturas::class.java)
                intent.putExtra("ESTUDIANTE", estudianteActu)
                startActivity(intent)
                finish()
            }
        }


        lifecycleScope.launch(Dispatchers.IO) {
            val aux: CursoAcademico? = estudianteDAO.getCursoPorId(asignaturaActu.cursoAcademicoId)
            opciones = estudianteDAO.obtenerCursosAcademicosPorEstudiante(estudianteActu.id)
            val adapter = ArrayAdapter(this@AsignaturaIndividual, R.layout.simple_spinner_item, opciones)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            withContext(Dispatchers.Main) {
                binding.spinneCursos.adapter = adapter
                val posicionCursoDeseado = opciones.indexOf(aux)
                if (posicionCursoDeseado != -1) {
                    binding.spinneCursos.setSelection(posicionCursoDeseado)
                }
            }
        }
    }
    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }
}
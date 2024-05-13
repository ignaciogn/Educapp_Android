package com.example.educapp

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityAddAsignaturaBinding
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Estudiante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddAsignatura : AppCompatActivity() {
    lateinit var binding: ActivityAddAsignaturaBinding
    lateinit var estudianteActu: Estudiante
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var opciones: List<CursoAcademico>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAsignaturaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        estudianteActu = (intent.getSerializableExtra("ESTUDIANTE") as? Estudiante)!!

        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        estudianteDAO = bd.estudianteDAO()


        lifecycleScope.launch(Dispatchers.IO) {
            opciones = estudianteDAO.obtenerCursosAcademicosPorEstudiante(estudianteActu.id)
            val adapter = ArrayAdapter(this@AddAsignatura, R.layout.simple_spinner_item, opciones)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinneCursos.adapter = adapter
        }

        binding.btGuarda.setOnClickListener {
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
                Toast.makeText(this@AddAsignatura, "Todos los campos son obligatorios(excepto Descripcion)", Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch(Dispatchers.IO) {
                    val existingAsig = estudianteDAO.getAsignaturaPorIdCursoYNombre(idCurso,nombre)

                    if (existingAsig == null) {
                        val asignatura = Asignatura(0,nombre,creditos,aula,horario,descripcion,profe,idCurso)
                        estudianteDAO.insertarAsignatura(asignatura)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddAsignatura,
                                "¡Se ha añadido el curso con exito!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val intent = Intent(this@AddAsignatura, Asignturas::class.java)
                        intent.putExtra("ESTUDIANTE",estudianteActu)
                        startActivity(intent)
                        finish()
                    }else{
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddAsignatura,
                                "Ya existe una asignatura con ese nombre para ese curso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }



                }

            }
        }
    }
    override fun onDestroy() {
        bd.close()
        super.onDestroy()
    }
}
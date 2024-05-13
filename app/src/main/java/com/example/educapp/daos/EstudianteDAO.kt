package com.example.educapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.EstudianteConCurso
import com.example.educapp.entidades.Examen
import com.example.educapp.entidades.Tarea


@Dao
interface EstudianteDAO {

    //------------------------------------ESTUDIANTE----------------------------------
    @Insert
    suspend fun register(estudiante: Estudiante)

    @Query("SELECT * FROM estudiantes")
    suspend fun consultaTodos(): List<Estudiante>

    @Update
    suspend fun actualiza(estudiante: Estudiante)

    @Delete
    suspend fun borra(estudiante: Estudiante)

    @Query("SELECT * FROM estudiantes WHERE correo = :correo LIMIT 1")
    suspend fun getStudentByEmail(correo: String): Estudiante?

    @Query("SELECT * FROM estudiantes WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun authenticate(correo: String, contrasena: String): Estudiante?

    @Query("SELECT * FROM estudiantes WHERE id = :estudianteId LIMIT 1")
    suspend fun getStudentById(estudianteId: Long): Estudiante?


    //------------------------------------CURSOS----------------------------------
    @Transaction
    @Query("SELECT * FROM estudiantes WHERE id = :estudianteId")
    fun getEstudianteConCursos(estudianteId: Long): EstudianteConCurso

    @Insert
    suspend fun insertCurso(curso: CursoAcademico)

    @Query("SELECT * FROM cursos_academicos WHERE estudianteId = :idEstudiante AND nombre = :nombreCurso")
    suspend fun getCursoPorEstudianteYNombre(idEstudiante: Long, nombreCurso: String): CursoAcademico?

    @Update
    suspend fun updateCurso(curso: CursoAcademico)

    @Query("DELETE FROM cursos_academicos WHERE id = :cursoId")
    suspend fun deleteCursoById(cursoId: Long)

    @Query("SELECT * FROM cursos_academicos WHERE id = :cursoId")
    suspend fun getCursoPorId(cursoId: Long): CursoAcademico?

    @Query("SELECT * FROM cursos_academicos WHERE nombre = :nombreCurso LIMIT 1")
    suspend fun getCursoPorNombre(nombreCurso: String): CursoAcademico?

    @Query("SELECT * FROM cursos_academicos WHERE estudianteId = :estudianteId")
    suspend fun obtenerCursosAcademicosPorEstudiante(estudianteId: Long): List<CursoAcademico>

    @Query("SELECT COUNT(*) FROM cursos_academicos WHERE estudianteId = :estudianteId")
    fun getNumeroDeCursosParaEstudiante(estudianteId: Long): Int

    //------------------------------------ASIGNATRUAS----------------------------------
    @Query("SELECT * FROM asignaturas WHERE cursoAcademicoId IN (SELECT id FROM cursos_academicos WHERE estudianteId = :estudianteId)")
    suspend fun getAsignaturasPorEstudiante(estudianteId: Long): List<Asignatura>

    @Insert
    suspend fun insertarAsignatura(asignatura: Asignatura)

    @Query("SELECT * FROM asignaturas WHERE cursoAcademicoId = :idCurso AND nombre = :nombre LIMIT 1")
    fun getAsignaturaPorIdCursoYNombre(idCurso: Long, nombre: String): Asignatura?

    @Update
    suspend fun updateAsig(asig: Asignatura)

    @Query("DELETE FROM asignaturas WHERE id = :asignaturaId")
    suspend fun deleteAsignaturaById(asignaturaId: Long)

    @Query("SELECT asignaturas.* FROM asignaturas INNER JOIN cursos_academicos ON asignaturas.cursoAcademicoId = cursos_academicos.id WHERE cursos_academicos.estudianteId = :estudianteId")
    suspend fun obtenerAsignaturasPorEstudiante(estudianteId: Long): List<Asignatura>

    @Query("SELECT * FROM asignaturas WHERE id = :asignaturaId LIMIT 1")
    suspend fun getAsignaturaPorId(asignaturaId: Long): Asignatura?

    @Query("SELECT COUNT(*) FROM asignaturas WHERE cursoAcademicoId IN (SELECT id FROM cursos_academicos WHERE estudianteId = :estudianteId)")
    fun getNumeroDeAsignaturasParaEstudiante(estudianteId: Long): Int

    //------------------------------------EXAMEN----------------------------------
    @Query("SELECT * FROM examenes WHERE asignaturaId IN (SELECT id FROM asignaturas WHERE cursoAcademicoId IN (SELECT id FROM cursos_academicos WHERE estudianteId = :estudianteId))")
    suspend fun getExamenesPorEstudianteId(estudianteId: Long): List<Examen>

    @Insert
    suspend fun insertarExamen(examen: Examen)

    @Query("DELETE FROM examenes WHERE id = :examenId")
    suspend fun deleteExamenById(examenId: Long)

    @Update
    suspend fun updateExamen(examen: Examen)

    @Query("SELECT * FROM examenes WHERE asignaturaId IN (SELECT id FROM asignaturas WHERE cursoAcademicoId IN (SELECT id FROM cursos_academicos WHERE estudianteId = :estudianteId)) AND fecha = :fechaExamen")
    suspend fun getExamenesPorEstudianteYFecha(estudianteId: Long, fechaExamen: String): List<Examen>


    //------------------------------------TAREA----------------------------------

    @Query("SELECT * FROM tareas WHERE asignaturaId IN (SELECT id FROM asignaturas WHERE cursoAcademicoId IN (SELECT id FROM cursos_academicos WHERE estudianteId = :estudianteId))")
    suspend fun getTareasPorEstudianteId(estudianteId: Long): List<Tarea>

    @Insert
    suspend fun insertarTarea(tarea: Tarea)

    @Query("DELETE FROM tareas WHERE id = :tareaId")
    suspend fun eliminarTareaPorId(tareaId: Long)

    @Update
    suspend fun actualizarTarea(tarea: Tarea)


    
}

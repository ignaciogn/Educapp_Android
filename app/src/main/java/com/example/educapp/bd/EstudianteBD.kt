package com.example.educapp.bd

import com.example.educapp.daos.EstudianteDAO
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.educapp.entidades.Asignatura
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Estudiante
import com.example.educapp.entidades.Examen
import com.example.educapp.entidades.Tarea

@Database(entities = [Estudiante::class, CursoAcademico::class, Asignatura::class, Examen::class, Tarea::class], version = 1, exportSchema = false)
abstract class EstudianteBD : RoomDatabase() {
    abstract fun estudianteDAO(): EstudianteDAO
}

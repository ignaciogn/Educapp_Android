package com.example.educapp.entidades

import androidx.room.Embedded
import androidx.room.Relation

data class EstudianteConCurso(
    @Embedded val estudiante: Estudiante,
    @Relation(
        parentColumn = "id",
        entityColumn = "estudianteId"
    )
    val cursosAcademicos: List<CursoAcademico>
)


package com.example.educapp.entidades

import androidx.room.Embedded
import androidx.room.Relation

data class CursoAcademicoConAsignaturas(
    @Embedded val cursoAcademico: CursoAcademico,
    @Relation(
        parentColumn = "id",
        entityColumn = "cursoAcademicoId"
    )
    val asignaturas: List<Asignatura>
)

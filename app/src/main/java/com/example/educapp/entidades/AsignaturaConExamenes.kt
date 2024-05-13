package com.example.educapp.entidades

import androidx.room.Embedded
import androidx.room.Relation

data class AsignaturaConExamenes(
    @Embedded val asignatura: Asignatura,
    @Relation(
        parentColumn = "id",
        entityColumn = "asignaturaId"
    )
    val examenes: List<Examen>
)

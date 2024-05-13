package com.example.educapp.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "examenes", foreignKeys = [ForeignKey(entity = Asignatura::class, parentColumns = ["id"], childColumns = ["asignaturaId"], onDelete = ForeignKey.CASCADE)])
data class Examen(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val aula: String,
    val descripcion: String,
    var realizado: Boolean,
    val nota: Int,
    val asignaturaId: Long
) : Serializable

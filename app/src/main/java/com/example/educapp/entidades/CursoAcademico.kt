package com.example.educapp.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cursos_academicos",
    foreignKeys = [
        ForeignKey(
            entity = Estudiante::class,
            parentColumns = ["id"],
            childColumns = ["estudianteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("estudianteId")]
)
data class CursoAcademico(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val nombre: String,
    val fechaInicio: String,
    val fechaFin: String,
    val estudianteId: Long,
    val descripcion: String
): Serializable {
    override fun toString(): String {
        return nombre
    }
}

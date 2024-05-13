package com.example.educapp.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "asignaturas",
    foreignKeys = [
        ForeignKey(
            entity = CursoAcademico::class,
            parentColumns = ["id"],
            childColumns = ["cursoAcademicoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cursoAcademicoId")]
)
data class Asignatura(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val nombre: String,
    val creditos: Int,
    val aula: String,
    val horario: String,
    val descripcion: String,
    val profesor: String,
    val cursoAcademicoId: Long
) : Serializable{
    override fun toString(): String {
        return nombre
    }
}

package com.example.educapp.entidades

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = Asignatura::class,
            parentColumns = ["id"],
            childColumns = ["asignaturaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("asignaturaId")]
)
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val nombre: String,
    val fechaEntrega: String,
    val descripcion: String,
    var entregado: Boolean,
    val nota: Int,
    val asignaturaId: Long
) : Serializable {
    override fun toString(): String {
        return nombre
    }
}

package com.example.educapp.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.io.Serializable

@Entity(tableName = "estudiantes")
data class Estudiante(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val nombre: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val contrasena: String,
) : Serializable{
    override fun toString(): String {
        return nombre
    }
}

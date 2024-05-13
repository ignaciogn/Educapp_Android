package com.example.educapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.educapp.databinding.ItemAsignaturaBinding
import com.example.educapp.entidades.Asignatura

class ReciclerViewAdapter2(var asignaturas: List<Asignatura>): RecyclerView.Adapter<ReciclerViewAdapter2.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAsignaturaBinding) : RecyclerView.ViewHolder(binding.root)

    var onItemClick:((Asignatura) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReciclerViewAdapter2.ViewHolder {
        val binding = ItemAsignaturaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return asignaturas.size
    }

    override fun onBindViewHolder(holder: ReciclerViewAdapter2.ViewHolder, position: Int) {
        with(holder) {
            with(asignaturas[position]) {
               binding.nombreCurso.text = this.nombre
                binding.twAula.text = "AULA: ${this.aula}"
                binding.twHorario.text = this.horario

                binding.root.setOnClickListener{
                    onItemClick?.invoke(asignaturas[position])
                }
            }
        }
    }
}
package com.example.educapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.educapp.databinding.ItemCursoBinding
import com.example.educapp.entidades.CursoAcademico



class ReciclerViewAdapter(var cursos: List<CursoAcademico>): RecyclerView.Adapter<ReciclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCursoBinding) : RecyclerView.ViewHolder(binding.root)

    var onItemClick:((CursoAcademico) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cursos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(cursos[position]) {
                binding.nombreCurso.text = this.nombre
                binding.fechasFin.text = this.fechaFin
                binding.fechasInicio.text = this.fechaInicio

                binding.root.setOnClickListener{
                    onItemClick?.invoke(cursos[position])
                }
            }
        }
    }
}

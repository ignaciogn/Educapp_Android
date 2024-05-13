package com.example.educapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.educapp.databinding.ItemTareaBinding
import com.example.educapp.entidades.Tarea


class ReciclerViewAdapter4(var tareas: List<Tarea>): RecyclerView.Adapter<ReciclerViewAdapter4.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTareaBinding) : RecyclerView.ViewHolder(binding.root)

    var onItemClick:((Tarea) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTareaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tareas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(tareas[position]) {
                binding.nombreCurso.text = this.nombre
                binding.twHorario.text = "FECHA: ${this.fechaEntrega}"

                if(this.entregado){
                    binding.twRealizado.text="REALIZADO"
                    binding.twNota.text = "NOTA: ${this.nota}"

                }else{
                    binding.twRealizado.text="NO REALIZADO"
                    binding.twNota.text=" "
                }

                binding.root.setOnClickListener{
                    onItemClick?.invoke(tareas[position])
                }
            }
        }
    }
}

package com.example.educapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.educapp.databinding.ItemCursoBinding
import com.example.educapp.databinding.ItemExamenBinding
import com.example.educapp.entidades.CursoAcademico
import com.example.educapp.entidades.Examen


class ReciclerViewAdapter3(var examenes: List<Examen>): RecyclerView.Adapter<ReciclerViewAdapter3.ViewHolder>() {
    inner class ViewHolder(val binding: ItemExamenBinding) : RecyclerView.ViewHolder(binding.root)

    var onItemClick:((Examen) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExamenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return examenes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(examenes[position]) {
                binding.nombreCurso.text = this.nombre
                binding.twHorario.text = "FECHA: ${this.fecha}"
                binding.twHora.text = "HORA: ${this.hora}"

                if(this.realizado){
                    binding.twRealizado.text="REALIZADO"
                    binding.twNota.text = "NOTA: ${this.nota}"

                }else{
                    binding.twRealizado.text="NO REALIZADO"
                    binding.twNota.text=" "
                }

                binding.root.setOnClickListener{
                    onItemClick?.invoke(examenes[position])
                }
            }
        }
    }
}

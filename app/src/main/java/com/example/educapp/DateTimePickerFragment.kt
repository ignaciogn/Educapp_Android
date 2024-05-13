package com.example.educapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DateTimePickerFragment(val listener: (dia: Int, mes: Int, anio: Int, hora: Int, minuto: Int) -> Unit) : DialogFragment(),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var calendar: Calendar

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Cuando se establece la fecha, guardamos los valores y mostramos el diálogo del selector de tiempo.
        calendar.set(year, month, dayOfMonth)
        TimePickerDialog(requireContext(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            .show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Cuando se establece la hora y el minuto, llamamos a la función de devolución de llamada con ambos conjuntos de valores.
        listener(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR),
            hourOfDay,
            minute
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Obtenemos la instancia de Calendar y configuramos la fecha actual.
        calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month: Int = calendar.get(Calendar.MONTH)
        val year: Int = calendar.get(Calendar.YEAR)

        // Creamos el diálogo de selección de fecha y lo mostramos.
        return DatePickerDialog(requireContext(), this, year, month, day)
    }
}

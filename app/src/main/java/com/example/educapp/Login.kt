package com.example.educapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.educapp.bd.EstudianteBD
import com.example.educapp.daos.EstudianteDAO
import com.example.educapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Login : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var estudianteDAO: EstudianteDAO
    private lateinit var bd: EstudianteBD
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var channelId = "Educaap.examenes"
    private var descripcion = "Notificaciones D15E01"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Inicializa bd antes de estudianteDAO
        bd = Room.databaseBuilder(
            applicationContext,
            EstudianteBD::class.java, "estudiantes_bd"
        ).build()

        binding.Register.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
            finish()
        }

        estudianteDAO = bd.estudianteDAO()

        binding.btLogin.setOnClickListener {
            val correo = binding.textoCorreo.text.toString()
            val contrasena = binding.textoPass.text.toString()

            if (correo.isNotBlank() && contrasena.isNotBlank()) {
                val calendar: Calendar = Calendar.getInstance()
                val date: Date = calendar.getTime()

                // Especifica el formato en el que deseas obtener la fecha

                // Especifica el formato en el que deseas obtener la fecha
                val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
                val fechaActual: String = dateFormat.format(date)
                Log.i("HOY", "Fecha hoy: ${fechaActual}")
                lifecycleScope.launch(Dispatchers.IO) {
                    val estudiante = estudianteDAO.authenticate(correo, contrasena)
                    withContext(Dispatchers.Main) {
                        if (estudiante != null) {
                            // Autenticación exitosa
                            val examenesHoy = estudianteDAO.getExamenesPorEstudianteYFecha(estudiante.id,fechaActual)
                            Log.i("HOY","TAMANIO: ${examenesHoy.size}" )
                            if(examenesHoy.isNotEmpty()){
                                for (examen in examenesHoy) {
                                    val asig = estudianteDAO.getAsignaturaPorId(examen.asignaturaId)
                                    val descripcion2 = "    Tienes un examen hoy: ${examen.nombre}, de la asignatura: ${asig!!.nombre} a la hora: ${examen.hora} en el aula ${examen.aula}"
                                    val contentView = RemoteViews(packageName, R.layout.activity_notificacion)
                                    contentView.setTextViewText(R.id.texto_notificacion, descripcion2)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        notificationChannel = NotificationChannel(
                                            channelId,
                                            descripcion,
                                            NotificationManager.IMPORTANCE_DEFAULT
                                        )
                                        notificationChannel.enableLights(true)
                                        notificationChannel.lightColor = Color.GREEN
                                        notificationChannel.enableVibration(true)
                                        notificationManager.createNotificationChannel(
                                            notificationChannel
                                        )

                                        notificationBuilder =
                                            NotificationCompat.Builder(this@Login, channelId)
                                                .setContent(contentView)
                                                .setSmallIcon(R.drawable.icono)
                                    } else {
                                        notificationBuilder = NotificationCompat.Builder(this@Login)
                                            .setContent(contentView)
                                            .setSmallIcon(R.drawable.icono)
                                    }

                                    // Aquí es donde se muestra la notificación
                                    notificationManager.notify(examen.id.toInt(), notificationBuilder.build())
                                }
                            }
                            Toast.makeText(this@Login, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Login, Principal::class.java)
                            intent.putExtra("ESTUDIANTE", estudiante)
                            startActivity(intent)
                            // Cierra la actividad actual si ya no es necesaria
                            finish()
                        } else {
                            // Credenciales incorrectas
                            Toast.makeText(this@Login, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@Login, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
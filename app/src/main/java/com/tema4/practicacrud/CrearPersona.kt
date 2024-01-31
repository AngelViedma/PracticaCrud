package com.tema4.practicacrud

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.isDigitsOnly
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.tema4.practicacrud.databinding.ActivityCrearPersonaBinding
import com.tema4.practicacrud.databinding.ActivityEditarPersonaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class CrearPersona : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityCrearPersonaBinding
    val ref = FirebaseDatabase.getInstance().reference
    val storageRef = Firebase.storage.reference
    var fotoSubida: Boolean = false
    var baseDatosActualizada = false
    lateinit var ratingbar: RatingBar
    lateinit var fecha: String
    var identificador: String? = null
    var url_persona: Uri?=null
    private lateinit var lista_personas: MutableList<Persona>
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearPersonaBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        ratingbar = binding.ratingBar
        lista_personas=Utilidad.obtenerListaPersonas(ref)
        job = Job()
        val this_activity = this

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    url_persona=uri
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    binding.imgCargarArchivo.setImageBitmap(bitmap)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.imgCargarArchivo.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btEnviarDatos.setOnClickListener {
            val tel = binding.etTelefono.text.toString()
            fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            identificador = ref.child("Usuario").push().key!!

            if (validar()) {
                val persona = Persona(
                    identificador,
                    binding.etNombre.text.toString(),
                    binding.etDescripcion.text.toString(),
                    tel.trim().toInt(),
                    ratingbar.rating,
                    fecha
                )
                subirArchivo(persona)

                launch {
                    val tel = binding.etTelefono.text.toString()
                    val nombre = binding.etNombre.text.toString()
                    val descripcion = binding.etDescripcion.text.toString()
                    val calificacion=binding.ratingBar
                    val url_imagen_firebase =
                        Utilidad.guardarImagen(storageRef, nombre, url_persona!!)
                    Utilidad.escribirPersona(
                        ref, identificador!!,
                        nombre.trim(),
                        descripcion.trim(),
                        tel.trim().toInt(),
                        calificacion.rating,
                        fecha,
                        url_imagen_firebase
                    )
                    Utilidad.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Persona creada con exito"
                    )
                }
            }
        }
    }

    fun validar(): Boolean {
        var validar = true

        val tel = binding.etTelefono.text.toString()
        val nombre = binding.etNombre.text.toString()
        val descripcion = binding.etDescripcion.text.toString()

        if (!tel.isDigitsOnly()) {
            validar = false
            Toast.makeText(this, "El telefono no puede tener letras", Toast.LENGTH_SHORT).show()
        }
        if (tel.isNullOrEmpty() || nombre.isNullOrEmpty() || descripcion.isNullOrEmpty()) {
            validar = false
            Toast.makeText(this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show()
        }
        if (tel.startsWith("0")) {
            validar = false
            Toast.makeText(this, "El telefono no puede empezar por 0", Toast.LENGTH_SHORT).show()
        }
        if(Utilidad.existePersona(lista_personas,nombre)){
            validar=false
            Toast.makeText(applicationContext, "Esa Persona ya existe", Toast.LENGTH_SHORT)
                .show()
        }
        return validar
    }

    fun subirArchivo(persona: Persona) {
        try {
            binding.imgCargarArchivo.isDrawingCacheEnabled = true
            binding.imgCargarArchivo.buildDrawingCache()
            val bitmap = (binding.imgCargarArchivo.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val nom_foto = binding.etNombre.text.toString()

            ref.child("Usuario").child(identificador!!).setValue(persona)
                .addOnSuccessListener {
                    baseDatosActualizada = true
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al subir foto", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}

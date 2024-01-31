package com.tema4.practicacrud

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditarPersona : AppCompatActivity(), CoroutineScope {
    private lateinit var modificar: Button
    private lateinit var volver: Button
    private lateinit var nombre: EditText
    private lateinit var descripcion: EditText
    private lateinit var telefono: EditText
    private lateinit var ratingbar:RatingBar
    private lateinit var fecha:String
    private lateinit var img_contacto: ImageView

    private var url_contacto: Uri? = null
    private lateinit var pojo_persona: Persona
    private lateinit var lista_personas: MutableList<Persona>
    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference
    private lateinit var this_activity: AppCompatActivity

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_persona)

        this_activity = this

        pojo_persona = intent.getParcelableExtra<Persona>("persona")!!

        nombre = findViewById(R.id.et_nombre)
        descripcion = findViewById(R.id.et_descripcion)
        telefono = findViewById(R.id.et_telefono)
        ratingbar=findViewById(R.id.ratingBar)
        fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        img_contacto = findViewById(R.id.img_cargar_archivo)

        nombre.setText(pojo_persona.nombre)
        descripcion.setText(pojo_persona.descripcion)
        telefono.setText(pojo_persona.telefono.toString())

        Glide.with(applicationContext)
            .load(pojo_persona.url_persona)
            .apply(Utilidad.opcionesGlide(applicationContext))
            .transition(Utilidad.transicion)
            .into(img_contacto)

        db_ref = FirebaseDatabase.getInstance().getReference()
        sto_ref = FirebaseStorage.getInstance().getReference()

        volver = findViewById(R.id.bt_volver)
        modificar = findViewById(R.id.bt_modificar_datos)

        //Lista de clubes para buscar en repetidos despues
        lista_personas = Utilidad.obtenerListaPersonas(db_ref)

        modificar.setOnClickListener {

            if (nombre.text.toString().trim() == "" ||
                descripcion.text.toString().trim() == "" ||
                telefono.text.toString().trim() == ""

            ) {

                Toast.makeText(applicationContext, "Falta datos del formulario", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (!nombre.text.toString().trim().equals(pojo_persona.nombre) && Utilidad.existePersona(lista_personas, nombre.text.toString().trim())) {
                    Toast.makeText(applicationContext, "La persona ya existe", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    var url_persona_firebase: String?
                    GlobalScope.launch(Dispatchers.IO) {
                        if (url_contacto == null) {
                            //No ha cambiado el escudo para que volver a subir la imagen
                            url_persona_firebase = pojo_persona.url_persona
                        } else {
                            url_persona_firebase =
                                Utilidad.guardarImagen(sto_ref, pojo_persona.id!!, url_contacto!!)
                        }

                        val androidId =
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                        Utilidad.escribirPersona(
                            db_ref, pojo_persona.id!!,
                            nombre.text.toString().trim(),
                            descripcion.text.toString().trim(),
                            telefono.text.toString().trim().toInt(),
                            ratingbar.rating,
                            fecha,
                            url_persona_firebase!!,
                            Estado.MODIFICADO,
                            androidId
                        )
                        id,
                        nombre,
                        descripcion,
                        telefono,
                        calificacion,
                        fecha,
                        url_firebase

                        Utilidad.tostadaCorrutina(
                            this_activity,
                            applicationContext,
                            "Club modificado con éxito"
                        )
                        val actividad = Intent(applicationContext, VerPersona::class.java)
                        startActivity(actividad)
                    }
                }
            }

        }



        img_contacto.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

        volver.setOnClickListener {
            val actividad = Intent(applicationContext, VerClubs::class.java)
            startActivity(actividad)
        }

    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            url_escudo = uri
            img_contacto.setImageURI(url_escudo)
        }
    }
}
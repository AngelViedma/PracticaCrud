package com.tema4.practicacrud

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.tema4.practicacrud.databinding.ActivityEditarPersonaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class CrearPersona : AppCompatActivity(),CoroutineScope {

    private lateinit var binding: ActivityEditarPersonaBinding
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
        binding = ActivityEditarPersonaBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }
}
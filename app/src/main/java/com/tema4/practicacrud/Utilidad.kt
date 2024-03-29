package com.tema4.practicacrud

import android.app.Person
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Utilidad {
    companion object{

        fun existePersona(persona : List<Persona>, nombre:String):Boolean{
            return persona.any{ it.nombre!!.lowercase()==nombre.lowercase()}
        }


        fun obtenerListaPersonas(db_ref: DatabaseReference):MutableList<Persona>{
            var lista = mutableListOf<Persona>()

            db_ref.child("Usuario")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo : DataSnapshot ->
                            val pojo_persona = hijo.getValue(Persona::class.java)
                            lista.add(pojo_persona!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }

        fun escribirPersona(db_ref: DatabaseReference,id:String,nombre:String, descripcion:String,telefono:Int, calificacion:Float, fecha:String, url_firebase:String,estado:Int,notificador:String)=
            db_ref.child("Usuario").child(id).setValue(Persona(
                id,
                nombre,
                descripcion,
                telefono,
                calificacion,
                fecha,
                url_firebase,
                estado,
                notificador
            ))

        suspend fun guardarImagen(sto_ref: StorageReference, nombre:String, imagen: Uri):String{
            lateinit var url_imagen_persona_firebase: Uri

            url_imagen_persona_firebase=sto_ref.child("contactos").child(nombre)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_imagen_persona_firebase.toString()
        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        fun animacion_carga(contexto: Context): CircularProgressDrawable{
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }


        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context):RequestOptions{
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.imagen_contacto)
                .error(R.drawable.error_404)
            return options

            return options
        }

    }
}
package com.tema4.practicacrud

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide

class PersonaAdaptador(private val lista_persona:MutableList<Persona>):
    RecyclerView.Adapter<PersonaAdaptador.PersonaViewHolder>(), Filterable
    {
        private lateinit var contexto: Context
        private var lista_filtrada = lista_persona


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PersonaAdaptador.PersonaViewHolder {
            val vista_item =
                LayoutInflater.from(parent.context).inflate(R.layout.item_persona, parent, false)
            contexto = parent.context
            return PersonaViewHolder(vista_item)
        }

        override fun onBindViewHolder(holder: PersonaAdaptador.PersonaViewHolder, position: Int) {
            val item_actual = lista_filtrada[position]
            holder.nombre.text = item_actual.nombre
            holder.descripcion.text = item_actual.descripcion
            holder.telefono.text = item_actual.telefono.toString()
            holder.clasificacion.rating=item_actual.calificacion?.toFloat()?:0.0f
            holder.fecha.text=item_actual.fecha

            val URL: String? = when (item_actual.imagen_persona) {
                "" -> null
                else -> item_actual.imagen_persona
            }

            Glide.with(contexto)
                .load(URL)
                .apply(Utilidad.opcionesGlide(contexto))
                .transition(Utilidad.transicion)
                .into(holder.miniatura)

            holder.editar.setOnClickListener {
                val activity = Intent(contexto, EditarPersona::class.java)
                activity.putExtra("persona", item_actual)
                contexto.startActivity(activity)
            }

            holder.eliminar.setOnClickListener {
                val db_ref = FirebaseDatabase.getInstance().getReference()
                val sto_ref = FirebaseStorage.getInstance().getReference()
                lista_filtrada.remove(item_actual)
                sto_ref.child("Usuario").child(item_actual.id!!).delete()
                db_ref.child("Usuario").child(item_actual.id!!).removeValue()

                Toast.makeText(contexto, "Persona borrado con exito", Toast.LENGTH_SHORT).show()
            }


        }

        override fun getItemCount(): Int = lista_filtrada.size

        class PersonaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
            val nombre: TextView = itemView.findViewById(R.id.item_nombre)
            val descripcion: TextView = itemView.findViewById(R.id.item_descripcion)
            val telefono: TextView = itemView.findViewById(R.id.item_telefono)
            val clasificacion:RatingBar=itemView.findViewById(R.id.ratingBar)
            val fecha:TextView=itemView.findViewById(R.id.item_fecha)
            val editar: ImageView = itemView.findViewById(R.id.item_editar)
            val eliminar: ImageView = itemView.findViewById(R.id.item_borrar)
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(p0: CharSequence?): FilterResults {
                    val busqueda = p0.toString().lowercase()
                    if (busqueda.isEmpty()) {
                        lista_filtrada = lista_persona
                    } else {
                        lista_filtrada = (lista_persona.filter {
                            it.nombre.toString().lowercase().contains(busqueda)
                        }) as MutableList<Persona>
                    }

                    val filterResults = FilterResults()
                    filterResults.values = lista_filtrada
                    return filterResults
                }

                override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                    notifyDataSetChanged()
                }

            }
        }
    }
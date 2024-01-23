package com.tema4.practicacrud

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

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
            holder.ciudad.text = item_actual.ciudad
            holder.fundacion.text = item_actual.fundacion.toString()

            val URL: String? = when (item_actual.escudo) {
                "" -> null
                else -> item_actual.escudo
            }

            Glide.with(contexto)
                .load(URL)
                .apply(Utilidad.opcionesGlide(contexto))
                .transition(Utilidad.transicion)
                .into(holder.miniatura)

            holder.editar.setOnClickListener {
                val activity = Intent(contexto, EditarClub::class.java)
                activity.putExtra("club", item_actual)
                contexto.startActivity(activity)
            }

            holder.eliminar.setOnClickListener {
                val db_ref = FirebaseDatabase.getInstance().getReference()
                val sto_ref = FirebaseStorage.getInstance().getReference()
                lista_filtrada.remove(item_actual)
                sto_ref.child("nba").child("clubs")
                    .child("escudos").child(item_actual.id!!).delete()
                db_ref.child("nba").child("club")
                    .child(item_actual.id!!).removeValue()

                Toast.makeText(contexto, "Club borrado con exito", Toast.LENGTH_SHORT).show()
            }


        }

        override fun getItemCount(): Int = lista_filtrada.size

        class PersonaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
            val nombre: TextView = itemView.findViewById(R.id.item_nombre)
            val ciudad: TextView = itemView.findViewById(R.id.item_ciudad)
            val fundacion: TextView = itemView.findViewById(R.id.item_fundacion)
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
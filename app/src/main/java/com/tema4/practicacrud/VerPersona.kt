package com.tema4.practicacrud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerPersona : AppCompatActivity() {


    private lateinit var volver: Button

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Persona>
    private lateinit var adaptador: PersonaAdaptador
    private lateinit var db_ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_persona)


        volver = findViewById(R.id.volver_inicio)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("Usuario")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{hijo: DataSnapshot?
                        ->
                        val pojo_persona = hijo?.getValue(Persona::class.java)
                        lista.add(pojo_persona!!)
                    }
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        adaptador = PersonaAdaptador(lista)
        recycler = findViewById(R.id.rv_persona)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)

        volver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_persona,menu)
        val item = menu?.findItem(R.id.search)
        val searhView = item?.actionView as SearchView


        searhView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter((newText))
                return true
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return  true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                adaptador.filter.filter("")
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }


}
package com.tema4.practicacrud

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Persona(
    var id : String? = null,
    var nombre:String?=null,
    var descripcion:String?=null,
    var telefono:Int?=null,
    var calificacion:Float?=null,
    var fecha:String?=null,
    var imagen_persona:String?=null
):Parcelable
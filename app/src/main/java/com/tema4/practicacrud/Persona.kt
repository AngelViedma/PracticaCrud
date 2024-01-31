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
    var url_persona:String?=null,
    var estado_noti:Int?=0,
    var user_notificador:String?=null,
    var nombreAnterior:String?=null
):Parcelable {
    override fun toString(): String {
        return nombre!!
    }
}
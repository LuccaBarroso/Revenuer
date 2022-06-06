package com.example.revenuer.entity

data class Operation(
    val id:Int? = null,
    val name:String,
    val date:String,
    val value:Double,
    val type:Boolean, // true = receita, false = despesa
    val userId:Int
)

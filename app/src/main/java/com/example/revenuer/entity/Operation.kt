package com.example.revenuer.entity

data class Operation(
    val id:String,
    val name:String,
    val value:String,
    val date:String,
    val type:Boolean, // true = receita, false = despesa
)

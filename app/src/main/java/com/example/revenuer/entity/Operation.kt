package com.example.revenuer.entity

import java.util.*

data class Operation(
    val id:String = "",
    val name:String = "",
    val value:String = "",
    val date:String = "",
    val type:Boolean = false, // true = receita, false = despesa,
)

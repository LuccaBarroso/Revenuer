package com.example.revenuer.entity

import com.google.android.gms.tasks.Task

data class User(
    val id:String = "",
    val name:String = "",
    val phone:String = "",
    val email:String = "",
    val operations: HashMap<String, Operation> = hashMapOf()
)

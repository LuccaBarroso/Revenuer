package com.example.revenuer.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.revenuer.R
import com.example.revenuer.adapter.HistoryAdapter
import com.example.revenuer.entity.User
import com.example.revenuer.listener.OperationListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HistoryActivity : AppCompatActivity(), OperationListener, View.OnClickListener {
    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    // Screen Elements
    private lateinit var mOperationRecyclerView: RecyclerView
    private lateinit var mOperationAdd: Button

    private lateinit var mOperationAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Firebase
        mAuth = Firebase.auth
        mDatabase = Firebase.database

        // Screen Elements
        mOperationAdd = findViewById(R.id.history_button_add)
        mOperationAdd.setOnClickListener(this)

        mOperationRecyclerView = findViewById(R.id.history_recyclerview_operation)
        mOperationRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        val userRef = mDatabase.getReference("/users")
        userRef
            .orderByChild("email")
            .equalTo(mAuth.currentUser?.email)
            .addValueEventListener(object: ValueEventListener, OperationListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(children in snapshot.children){
                        val  user = children.getValue(User::class.java)

                        //passa no adapter a lista de operações
                        val adapter = user?.operations?.values?.toList()?.let { HistoryAdapter(it) }
                        if (adapter != null) {
                            adapter.setOnOperationListener(this@HistoryActivity)
                        }
                        mOperationRecyclerView.adapter = adapter
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
                override fun onListItemClick(View: View, adapterPosition: Int) {
                    //item clicado
                    Log.i("App", "clicado")
                }
            })
    }
    override fun onClick(view: View?) {
        val it = Intent(this, OperationActivity::class.java)
        it.putExtra("isNew", true)
        startActivity(it)
    }

    // TODO: Concertar erro "lateinit property mOperationAdapter has not been initialized"
    override fun onListItemClick(View: View, adapterPosition: Int) {
        val it = Intent(this, OperationActivity::class.java)
        it.putExtra("operationKey", mOperationAdapter.list[adapterPosition].id)
        startActivity(it)
    }
}



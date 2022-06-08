package com.example.revenuer.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.revenuer.R
import com.example.revenuer.adapter.HistoryAdapter
import com.example.revenuer.entity.User
import com.example.revenuer.listener.OperationListener
import com.google.android.gms.common.internal.constants.ListAppsActivityContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.FieldPosition

class HistoryActivity : AppCompatActivity(), OperationListener, View.OnClickListener {
    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private var mUserKey = ""

    // Screen Elements
    private lateinit var mOperationRecyclerView: RecyclerView
    private lateinit var mOperationAdd: Button

    // Adapter
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
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.first().getValue(User::class.java)
                    mUserKey = user?.id ?: ""
                    //passa no adapter a lista de operações
                    mOperationAdapter = user?.operations?.values?.toList()?.let { HistoryAdapter(it) }!!
                    mOperationAdapter.setOnOperationListener(this@HistoryActivity)
                    mOperationRecyclerView.adapter = mOperationAdapter
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onClick(view: View?) {
        val it = Intent(this, OperationActivity::class.java)
        startActivity(it)
    }

    override fun onListItemClick(View: View, position: Int) {
        val it = Intent(this, OperationActivity::class.java)
        it.putExtra("operationKey",  mOperationAdapter.list[position].id)
        it.putExtra("userKey",  mUserKey)
        startActivity(it)
    }

    override fun onListItemLongClick(view: View, adapterPosition: Int) {
        val dialog = AlertDialog.Builder(this)
        .setTitle("Revenuer")
        .setMessage("Você deseja excluir a operação?")
        .setCancelable(false)
        .setPositiveButton("Sim") { dialog, _ ->
            val operation = mOperationAdapter.list[adapterPosition] // recebe a operação na posição que quer excluir
            val operationRef = mDatabase
                .reference
                .child("/users")
                .child(mUserKey)
                .child("/operations")
                .child(operation.id)

            operationRef.removeValue()

            dialog.dismiss()
        }
        .setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }
        .create()

        dialog.show()
    }
}



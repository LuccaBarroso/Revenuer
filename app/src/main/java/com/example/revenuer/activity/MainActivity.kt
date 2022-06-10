package com.example.revenuer.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.revenuer.R
import com.example.revenuer.adapter.HistoryAdapter
import com.example.revenuer.entity.User
import com.example.revenuer.listener.OperationListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, OperationListener {

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private var mUserKey = ""

    // Screen Elements
    private lateinit var mRevenueRecyclerView: RecyclerView
    private lateinit var mExpenseRecyclerView: RecyclerView
    private lateinit var mHistoryButton:Button
    private lateinit var mTextViewAmount: TextView

    // Adapter
    private lateinit var mRevenueAdapter: HistoryAdapter
    private lateinit var mExpenseAdapter: HistoryAdapter

    var  formatoDataHora = SimpleDateFormat("dd/M/yyyy");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase
        mAuth = Firebase.auth
        mDatabase = Firebase.database

        val usersRef = mDatabase.getReference("/users");
        usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email).addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //Ei ZAMUEL, o snapshot tem acesso aos dados do usuario que ta logado, já dxei td certinho pra vc
                snapshot.key?.let { Log.i("App", it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        // Screen Elements:
        mHistoryButton = findViewById(R.id.main_button_history)
        mHistoryButton.setOnClickListener(this)
        mTextViewAmount = findViewById(R.id.main_textview_amount)

        mRevenueRecyclerView = findViewById(R.id.main_recyclerview_revenue)
        mRevenueRecyclerView.layoutManager = LinearLayoutManager(this)
        mExpenseRecyclerView = findViewById(R.id.main_recyclerview_expense)
        mExpenseRecyclerView.layoutManager = LinearLayoutManager(this)
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

                    var somatorio = 0.0

                    for(prop in user?.operations?.values?.toList()!!){
                        if(prop.type) {
                            somatorio += prop.value.toFloat();
                        }else{
                            somatorio -= prop.value.toFloat();
                        }
                    }
                    mTextViewAmount.text = "%.2f".format(somatorio)

                    //Adapter das operações positivas
                    var positivas = user?.operations?.values?.toList()?.filter{
                        it.type == true
                    }?.sortedByDescending{formatoDataHora.parse(it.date.replace(" ", ""))}?.take(5)

                    Log.i("App", "as 5 positivas mais recentes = "+positivas.toString())
                    mRevenueAdapter = positivas?.toList()?.let { HistoryAdapter(it) }!!
                    mRevenueAdapter.setOnOperationListener(this@MainActivity)
                    mRevenueRecyclerView.adapter = mRevenueAdapter

                    //Adapter das operações negativas
                    var negativas = user?.operations?.values?.toList()?.filter{
                        it.type == false
                    }?.sortedByDescending{formatoDataHora.parse(it.date.replace(" ", ""))}?.take(5)

                    Log.i("App", negativas.toString())
                    mExpenseAdapter = negativas?.toList()?.let { HistoryAdapter(it) }!!
                    mExpenseAdapter.setOnOperationListener(this@MainActivity)
                    mExpenseRecyclerView.adapter = mExpenseAdapter
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onClick(view: View?) {
        val it = Intent(this, HistoryActivity::class.java)
        startActivity(it)
    }

    override fun onListItemClick(view: View, position: Int) {
        if( view.parent == mRevenueRecyclerView) {
            val it = Intent(this, OperationActivity::class.java)
            it.putExtra("operationKey",  mRevenueAdapter.list[position].id)
            it.putExtra("userKey",  mUserKey)
            startActivity(it)
        }else{
            val it = Intent(this, OperationActivity::class.java)
            it.putExtra("operationKey",  mExpenseAdapter.list[position].id)
            it.putExtra("userKey",  mUserKey)
            startActivity(it)
        }
    }

    override fun onListItemLongClick(view: View, adapterPosition: Int) {
        if(view.parent == mRevenueRecyclerView) {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Revenuer")
                .setMessage("Você deseja excluir a operação?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, _ ->
                    val operation = mRevenueAdapter.list[adapterPosition] // recebe a operação na posição que quer excluir
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
        }else{
            val dialog = AlertDialog.Builder(this)
                .setTitle("Revenuer")
                .setMessage("Você deseja excluir a operação?")
                .setCancelable(false)
                .setPositiveButton("Sim") { dialog, _ ->
                    val operation = mExpenseAdapter.list[adapterPosition] // recebe a operação na posição que quer excluir
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

}
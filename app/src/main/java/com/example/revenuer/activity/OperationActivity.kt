package com.example.revenuer.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.revenuer.R
import com.example.revenuer.entity.Operation
import com.example.revenuer.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class OperationActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    // Screen Elements
    private lateinit var mRevenueButton: Button
    private lateinit var mExpenseButton: Button
    private lateinit var mTitle: TextView
    private lateinit var mOperationName: EditText
    private lateinit var mOperationValue: EditText
    private lateinit var mOperationDate: TextView
    private lateinit var mDateButton: Button
    private lateinit var mCancelButton: Button
    private lateinit var mOkButton: Button

    // Action Elements
    private var isRevenuePushed: Boolean = false

    // Date Picker
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0

    private var mSetYear: Int = 0
    private var mSetMonth: Int = 0
    private var mSetDay: Int = 0

    private var mUserKey = ""
    private var mOperationKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operation)

        mUserKey = intent.getStringExtra("userKey") ?: ""
        mOperationKey = intent.getStringExtra("operationKey") ?: ""

        // Firebase
        mAuth = Firebase.auth
        mDatabase = Firebase.database

        // Screen Elements
        mRevenueButton = findViewById(R.id.operation_button_revenue)
        mExpenseButton = findViewById(R.id.operation_button_expense)
        mTitle = findViewById(R.id.operation_textview_title)
        mOperationName = findViewById(R.id.operation_edittext_name)
        mOperationValue = findViewById(R.id.operation_edittext_value)
        mOperationDate = findViewById(R.id.operation_textview_date)
        mDateButton = findViewById(R.id.operation_button_date)
        mCancelButton = findViewById(R.id.operation_button_left)
        mOkButton = findViewById(R.id.operation_button_right)

        mRevenueButton.setOnClickListener(this)
        mExpenseButton.setOnClickListener(this)
        mCancelButton.setOnClickListener(this)
        mOkButton.setOnClickListener(this)
        mDateButton.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.operation_button_revenue -> {
                isRevenuePushed = true
                mRevenueButton.backgroundTintList = getColorStateList(R.color.green)
                mExpenseButton.backgroundTintList = getColorStateList(R.color.blue)
            }
            R.id.operation_button_expense -> {
                isRevenuePushed = false
                mRevenueButton.backgroundTintList = getColorStateList(R.color.blue)
                mExpenseButton.backgroundTintList = getColorStateList(R.color.red)
            }
            R.id.operation_button_date -> {
                getDateCalendar()
                DatePickerDialog(this, this, mYear, mMonth, mDay).show()
            }
            R.id.operation_button_left -> {
                finish()
            }
            R.id.operation_button_right -> {
                val name = mOperationName.text.toString().trim()

                // Se o valor escolido pelo usuário não foi digitado com casas decimais, o programa os coloca automaticamente
                val value: String =
                    if (!mOperationValue.text.contains('.')) mOperationValue.text.toString()
                        .trim() + ".00"
                    else mOperationValue.text.toString().trim()

                val date = mOperationDate.text.toString().trim()
                val operationType =
                    isRevenuePushed // true = receita (revenue), false = despesa (expense)

                if (mOperationKey.isBlank()) { // se estiver vazia, irá adicionar uma nova operação
                    var isFormFilled = true;
                    isFormFilled = isFormFilled(name, mOperationName) && isFormFilled;
                    isFormFilled = isFormFilled(value, mOperationValue) && isFormFilled;

                    if (date == "") {
                        Toast.makeText(
                            baseContext, "Uma data válida precisa ser inserida",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!operationType && mExpenseButton.backgroundTintList != getColorStateList(
                            R.color.red
                        )
                    ) {
                        Toast.makeText(
                            baseContext, "Selecione entre receita ou despesa",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (isFormFilled) {
                        val usersRef = mDatabase.getReference("/users");
                        val curUser =
                            usersRef.orderByChild("email").equalTo(mAuth.currentUser?.email)
                        curUser.addChildEventListener(object :
                            ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                val operationRef = usersRef
                                    .child(snapshot.key!!)
                                    .child("/operations")

                                val operationId = operationRef
                                    .push()
                                    .key ?: ""

                                val operation = Operation(
                                    id = operationId,
                                    name = name,
                                    value = value,
                                    date = date,
                                    type = operationType
                                )

                                operationRef
                                    .child(operationId)
                                    .setValue(operation)

                                Toast.makeText(
                                    baseContext, "Operação \"$name\" cadastrada com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {}

                            override fun onChildMoved(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                } else { // se estiver preenchida, irá editar uma operação
                    val operation = Operation(mOperationKey, name, value, date, operationType)

                    val operationRef = mDatabase
                        .reference
                        .child("/users")
                        .child(mUserKey)
                        .child("/operations")
                        .child(mOperationKey)

                    operationRef.setValue(operation)

                    Toast.makeText(
                        baseContext, "Operação \"$name\" atualizada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    // Editando  ou Adicionado operação
    override fun onResume() {
        super.onResume()

        if (mOperationKey.isBlank()) {
            mTitle.text = "Adicionar Operação"
            mOkButton.text = "Criar"
        } else {
            mTitle.text = "Editar Operação"
            mOkButton.text = "Salvar"

            val userRef = mDatabase.getReference("/users")
            userRef
                .orderByChild("email")
                .equalTo(mAuth.currentUser?.email!!)
                .addValueEventListener(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.children.first().getValue(User::class.java)
                        val operation = user?.operations?.values?.find { it.id == mOperationKey }
                        if (operation != null) {
                            mOperationName.text = Editable.Factory.getInstance().newEditable(operation?.name)
                            mOperationValue.text = Editable.Factory.getInstance().newEditable(operation?.value)
                            mOperationDate.text = Editable.Factory.getInstance().newEditable(operation?.date)
                            isRevenuePushed = operation!!.type
                        }

                        // Determina qual o tipo da operação
                        if (isRevenuePushed) {
                            mRevenueButton.backgroundTintList = getColorStateList(R.color.green)
                            mExpenseButton.backgroundTintList = getColorStateList(R.color.blue)
                        } else {
                            mRevenueButton.backgroundTintList = getColorStateList(R.color.blue)
                            mExpenseButton.backgroundTintList = getColorStateList(R.color.red)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun getDateCalendar() {
        val calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        mSetYear = year
        mSetMonth = month
        mSetDay = day

        getDateCalendar()
        mOperationDate.text = "$mSetDay / $mSetMonth / $mSetYear"
    }

    private fun isFormFilled(value: CharSequence, mOfTheValue: EditText): Boolean {
        return if (value.isBlank()) {
            mOfTheValue.error = "Este campo é obrigatório"
            false
        }else {
            true
        }
    }
}
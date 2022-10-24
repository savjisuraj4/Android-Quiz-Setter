package com.example.quizsetter

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import com.google.firebase.firestore.FieldValue.arrayRemove
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.Nonnull
import kotlin.collections.ArrayList

class AfterLogin0 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    var quizlist= ArrayList<quizcodeinfo>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var textView: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var afterloginadpaterActivty: AfterLoginAdpater_Activty
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var swipeRefershlayout:SwipeRefreshLayout
    private lateinit var databaseReference:DatabaseReference
    private lateinit var progressDialog:ProgressDialog
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login0)
        recyclerView=findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        firebaseAuth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://login-9e300-default-rtdb.firebaseio.com/")
        firestore=FirebaseFirestore.getInstance()
        layoutManager = LinearLayoutManager(this)
        textView=findViewById(R.id.textviewnoquizcode)
        textView.visibility=View.INVISIBLE
        recyclerView.layoutManager=layoutManager
        swipeRefershlayout=findViewById(R.id.refreshLayout)
        progressDialog=ProgressDialog(this)
        progressDialog.setCancelable(true)
        progressDialog.setMessage("Loading...")
        progressDialog.onBackPressed()
        progressDialog.create()
        progressDialog.show()
        DisplayQuizcodes()

        swipeRefershlayout.setOnRefreshListener {
            DisplayQuizcodes()
            swipeRefershlayout.isRefreshing=false

        }
    }
    data class quizcodeinfo(
        var sr_no:String,

        var quizcode:String
    )

    private fun DisplayQuizcodes() {


                    quizlist.clear()
        firestore.collection("usersSetters").document(firebaseAuth.currentUser?.email.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result.get("quizcodelist")
                    val ss = document.toString()
                        .replace("quizcode=", "")
                        .replace("{", "")
                        .replace("}", "")
                        .replace("[", "").replace("]", "")
                        .split(",")
                        .toList()

                    if (ss.component1().equals("null")) {
                        recyclerView.visibility=View.INVISIBLE
                        textView.visibility=View.VISIBLE
                        progressDialog.dismiss()

                    } else {
                        textView.visibility = View.INVISIBLE
                        var sr_no = 1

                        ss.forEach {

                            val quizcodeinfo = AfterLogin0.quizcodeinfo(
                                sr_no.toString(),
                                it.toString(),
                            )
                            quizlist.add(quizcodeinfo)
                            sr_no++
                        }
                            afterloginadpaterActivty =
                                AfterLoginAdpater_Activty(this@AfterLogin0, quizlist)

                            recyclerView.adapter = afterloginadpaterActivty
                            progressDialog.dismiss()

                    }
                }
                else{
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_baseline_error_24)
                        .setMessage(task.exception?.message.toString())
                        .setTitle("Error")
                        .setNeutralButton("Ok",DialogInterface.OnClickListener{
                                dialog, which ->
                        })
                        .create().show()
                }

            }
            .addOnFailureListener(OnFailureListener {
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_error_24)
                    .setMessage(it.message.toString())
                    .setTitle("Error")
                    .setNeutralButton("Ok",DialogInterface.OnClickListener{
                        dialog, which ->
                    })
                    .create().show()
            })
    }
    inner class quizcodes1{
        lateinit var quizcode:String
        constructor(quizcode:String){
            this.quizcode=quizcode
        }
    }
    fun deleteQuiz(poistion:Int){
        try {
            AlertDialog.Builder(this)
                .setMessage("Are You Sure Want to delete Quiz")
                .setTitle("Delete Quiz")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setPositiveButton("YES") { dialog, which ->

            val quizcode1 = quizcodes1(quizlist[poistion].quizcode.replace(" ", "").toString())
            firestore.collection("usersSetters").whereArrayContains("quizcodelist", quizcode1)
                .get()
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isComplete) {
                        firestore.collection("usersSetters")
                            .document(firebaseAuth.currentUser?.email.toString())
                            .update("quizcodelist", arrayRemove(quizcode1))
                            .addOnCompleteListener(OnCompleteListener {
                                databaseReference.child(
                                    quizlist[poistion].quizcode.replace(" ", "").toString()
                                )
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(@Nonnull snapshot: DataSnapshot) {
                                            snapshot.ref.removeValue()
                                                .addOnCompleteListener(
                                                    OnCompleteListener {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "Quiz deleted Successfully",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        DisplayQuizcodes()
                                                    }
                                                )
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            AlertDialog.Builder(applicationContext)
                                                .setIcon(R.drawable.ic_baseline_error_24)
                                                .setMessage(error.message.toString())
                                                .setTitle("Error")
                                                .setNeutralButton("OK") { dialog, which ->
                                                }
                                                .create().show()
                                        }

                                    })
                            })
                            .addOnFailureListener(OnFailureListener {
                                AlertDialog.Builder(this)
                                    .setIcon(R.drawable.ic_baseline_error_24)
                                    .setMessage(it.message.toString())
                                    .setTitle("Error")
                                    .setNeutralButton("OK") { dialog, which ->
                                    }
                                    .create().show()
                            })
                    }
                })
        }
                .setNegativeButton("No"){
                    dialog,which->
                }
                .create().show()

        }catch (e:Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }
    fun AfterQuizcodeclicked(quizcode:String) {
        try {

                        val intent = Intent(applicationContext, Questionsetactivty::class.java)
                        intent.putExtra("quizcode", quizcode.toString())
                        startActivity(intent)


        }catch (e:Exception){
            Toast.makeText(applicationContext,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.addquizmenu){
            startActivity(Intent(this,AfterLogin::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
    fun checkvalidquiz(quizcode: String){
        firestore.collection("usersSetters").whereArrayContains("quizcodelist", quizcode)
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isComplete) {
                    firestore.collection("usersSetters")
                        .document(firebaseAuth.currentUser?.email.toString())
                        .update("quizcodelist", arrayRemove(quizcode))
                        .addOnCompleteListener(OnCompleteListener {
                            databaseReference.child(quizcode)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(@Nonnull snapshot: DataSnapshot) {
                                        snapshot.ref.removeValue()
                                            .addOnCompleteListener(
                                                OnCompleteListener {
                                                    DisplayQuizcodes()
                                                }
                                            )
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        AlertDialog.Builder(applicationContext)
                                            .setIcon(R.drawable.ic_baseline_error_24)
                                            .setMessage(error.message.toString())
                                            .setTitle("Error")
                                            .setNeutralButton("OK") { dialog, which ->
                                            }
                                            .create().show()
                                    }

                                })
                        })
                        .addOnFailureListener(OnFailureListener {
                            AlertDialog.Builder(this)
                                .setIcon(R.drawable.ic_baseline_error_24)
                                .setMessage(it.message.toString())
                                .setTitle("Error")
                                .setNeutralButton("OK") { dialog, which ->
                                }
                                .create().show()
                        })
                }
            })
    }
}






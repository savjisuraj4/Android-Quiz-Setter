package com.example.quizsetter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.*

open class Questionsetactivty : AppCompatActivity() {
    private lateinit var recyclerView:RecyclerView
     lateinit var questionslist:ArrayList<questioninfo>
    private lateinit var questioninfo:questioninfo
    lateinit  var databaseReference:DatabaseReference
    lateinit var questionadapterActivty: questionAdapter_activty
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var textView: TextView
     lateinit var quizcode: String
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionsetactivty)
        try {
            recyclerView = findViewById(R.id.recyclerview1)

            quizcode = intent.getStringExtra("quizcode").toString()
            layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager
            questionslist = arrayListOf<questioninfo>()
            refreshLayout = findViewById(R.id.refreshLayout)
            textView = findViewById(R.id.textviewnoquestion)
            databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://login-9e300-default-rtdb.firebaseio.com/")
                .child(quizcode.toString())

            textView.visibility = View.INVISIBLE
            Displayquestions(quizcode.toString())
            refreshLayout.setOnRefreshListener {
            Displayquestions(quizcode.toString())
                refreshLayout.isRefreshing = false
            }
        }catch (E:Exception){
            Toast.makeText(applicationContext,E.message.toString(),Toast.LENGTH_LONG).show()

        }
    }
    private fun Displayquestions(quizcode:String){
        questionslist.clear()
        try {
            var question_no=1;
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun toString(): String {
                    quizcode.toString()
                    return quizcode.toString()
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        Log.i("this", snapshot.value.toString())


                        if (snapshot.exists()) {
                            for (questionsnapshot in snapshot.children) {
                                val question: String =
                                    questionsnapshot.child("question").value.toString()
                                val option1: String =
                                    questionsnapshot.child("option1").value.toString()
                                val option2: String =
                                    questionsnapshot.child("option2").value.toString()
                                val option3: String =
                                    questionsnapshot.child("option3").value.toString()
                                val option4: String =
                                    questionsnapshot.child("option4").value.toString()
                                val answers: String =
                                    questionsnapshot.child("answer").value.toString()

                                questioninfo =
                                    questioninfo(
                                        question_no.toString(),
                                        question,
                                        option1,
                                        option2,
                                        option3,
                                        option4,
                                        answers
                                    )
                                questionslist.add(questioninfo)
                                question_no++
                                Log.i("Tag", "Hello")
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            e.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (questionslist.size.minus(3) <= 0) {
                        textView.visibility = View.VISIBLE
                        recyclerView.visibility = View.INVISIBLE
                    } else {
                        textView.visibility = View.INVISIBLE
                        questionadapterActivty =
                            questionAdapter_activty(this@Questionsetactivty, questionslist)
                        recyclerView.adapter = questionadapterActivty

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(applicationContext, error.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }

            })
        }catch (e:Exception){
            Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show()

        }

    }
    fun questioninfofun(position:Int){
        val intentlist=ArrayList<String>()
        val intent=Intent(this@Questionsetactivty,SetQuestionActivity1::class.java)
        intentlist.add(0,questionslist[position].questiontext.toString())
        intentlist.add(1,questionslist[position].option1.toString())
        intentlist.add(2,questionslist[position].option2.toString())
        intentlist.add(3,questionslist[position].option3.toString())
        intentlist.add(4,questionslist[position].option4.toString())
        intentlist.add(5,questionslist[position].answer.toString())
        intentlist.add(6,questionslist[position].questionno.toString())
        intent.putStringArrayListExtra("questionlist",intentlist)
        startActivity(intent)
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
            val intent=Intent(this@Questionsetactivty,SetQuestionActivity1::class.java)
            intent.putExtra("Quiz-code",quizcode)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun deletequestion(position: Int) {
        databaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(documemtSnapshot in snapshot.children){
                    var question=documemtSnapshot.child("question").value.toString()
                    val option1=documemtSnapshot.child("option1").value.toString()
                    val option2=documemtSnapshot.child("option2").value.toString()
                    val option3=documemtSnapshot.child("option3").value.toString()
                    val option4=documemtSnapshot.child("option4").value.toString()
                    val answers=documemtSnapshot.child("answer").value.toString()
                    if(question==questionslist[position].questiontext &&
                        option1==questionslist[position].option1 &&
                        option2==questionslist[position].option2 &&
                        option3==questionslist[position].option3 &&
                        option4==questionslist[position].option4 &&
                        answers==questionslist[position].answer){
                        var key=documemtSnapshot.key.toString()
                        databaseReference.child(key).removeValue().addOnCompleteListener(
                            OnCompleteListener {task->
                                if(task.isSuccessful){
                                    Toast.makeText(applicationContext,"Question deleted Successfully",Toast.LENGTH_LONG)
                                        .show()
                                    Displayquestions(quizcode.toString())
                                }
                                else{
                                    Toast.makeText(applicationContext,task.exception?.message.toString(),Toast.LENGTH_LONG)
                                        .show()
                                }
                            })
                            .addOnFailureListener(OnFailureListener{
                                Toast.makeText(applicationContext,it.message.toString(),Toast.LENGTH_LONG)
                                    .show()
                            })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@Questionsetactivty,error.message.toString(),Toast.LENGTH_LONG).show()
            }

        })
    }

}
package com.example.quizsetter

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.Nonnull

class AfterLoginAdpater_Activty(afterLogin0: AfterLogin0,quizlist:List<AfterLogin0.quizcodeinfo>):
    RecyclerView.Adapter<ViewHolder>() {
    private var afterLogin0:AfterLogin0
    private lateinit var viewHolder:ViewHolder
    private var databaseReference: DatabaseReference
    private var differenceDates:Long= 0L
    private var quizlist:List<AfterLogin0.quizcodeinfo>
    init {
        databaseReference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://login-9e300-default-rtdb.firebaseio.com/")
        this.afterLogin0=afterLogin0
        this.quizlist=quizlist
    }

    override fun onCreateViewHolder(@Nonnull parent: ViewGroup, viewType: Int): ViewHolder {
        try{
        val itemView:View=
            LayoutInflater.from(parent.context).inflate(R.layout.quizcodrecycleviewlayout,parent,false)
        viewHolder=ViewHolder(itemView)

        viewHolder.setOnClickListener(object : ViewHolder.ClickListener {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onItemClick(view: View, position: Int) {
                try{
                    var progressDialog=ProgressDialog(afterLogin0)
                    progressDialog.setCancelable(true)
                    progressDialog.setMessage("Loading...")
                    progressDialog.onBackPressed()
                    progressDialog.create()
                    progressDialog.show()
                    databaseReference.child(quizlist[position].quizcode.replace(" ","").toString()).addListenerForSingleValueEvent(object :ValueEventListener{
                        @RequiresApi(Build.VERSION_CODES.O)
                        @SuppressLint("SimpleDateFormat")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try{
                            val enddate=snapshot.child("enddate").value.toString()
                            val simpleDateFormat= SimpleDateFormat("dd/MM/yyyy")
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val cuurentdate: Date =simpleDateFormat.parse(LocalDate.now().format(formatter))
                            val enddate1: Date =simpleDateFormat.parse(enddate.toString())
                            val difference :Long= (enddate1.time-cuurentdate.time)

                                differenceDates = difference / (24 * 60 * 60 * 1000)

                            if(differenceDates>0){
                                progressDialog.dismiss()
                                afterLogin0.AfterQuizcodeclicked(quizlist[position].quizcode.replace(" ","").toString())

                            }
                            else{

                                Toast.makeText(afterLogin0,"Quiz is Expired",Toast.LENGTH_LONG).show()
                                progressDialog.dismiss()
                            }

                        }
                            catch (e:Exception){
                                Toast.makeText(afterLogin0,e.message.toString(),Toast.LENGTH_LONG).show()

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(afterLogin0,error.message.toString(),Toast.LENGTH_LONG).show()
                        }

                    })
                }
                catch (e:Exception){
                    Toast.makeText(afterLogin0,e.message.toString(),Toast.LENGTH_LONG).show()

                }

            }
        })

        }catch (e:Exception){
            Toast.makeText(afterLogin0,e.message.toString(),Toast.LENGTH_LONG).show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(@Nonnull holder: ViewHolder, position: Int) {
        try {
            val quizcodeno = quizlist[position].quizcode.toString().replace("QM", "").trim()
            holder.sr_no?.text = quizlist[position].sr_no.toString()

            holder.deletequizbutton?.setOnClickListener { v ->
                if (v != null) {
                    afterLogin0.deleteQuiz(position)
                }
            }
            holder.quizcode?.text =
                Html.fromHtml("<font color=${Color.RED}>QM</font><font color=${Color.BLACK}>${quizcodeno}</font>")
            databaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val startdate=snapshot.child("QM${quizcodeno}").child("startdate").value.toString()
                    val enddate=snapshot.child("QM${quizcodeno}").child("enddate").value.toString()

                    holder.startdate?.text=Html.fromHtml("<font color=${Color.RED}>Start:</font><font color=${Color.BLACK}>${startdate}</font>")
                    holder.enddate?.text=Html.fromHtml("<font color=${Color.RED}>End:</font><font color=${Color.BLACK}>${enddate}</font>")


                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(afterLogin0,error.message.toString(),Toast.LENGTH_LONG).show()
                }

            })

        }catch (e:Exception){
            Toast.makeText(afterLogin0,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return quizlist.size
    }
}
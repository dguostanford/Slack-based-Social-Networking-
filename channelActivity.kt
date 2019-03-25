/*
 * David Guo<dguo874@stanford.edu>, Ashutosh Synghal asynghal@stanford.edu
 * CS 193A, Winter 2019 (instructor: Marty Stepp )
 * Homework Assignment 6
 * The treechat game is a slack-like game that allows users to chat
 * with each other within one single chat
 */
package com.example.ashutosh1299.cs193a_hw7_asynghal_dguo874

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.os.PersistableBundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_channel.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.EditText
import android.R.id.edit
import android.content.SharedPreferences



class channelActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    var messaged = false
    private var user = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        mAuth = FirebaseAuth.getInstance()
        //user_view.setmovementMethod(scrollingMovementMethod())
        user_view.setMovementMethod(ScrollingMovementMethod())
        message_view.setMovementMethod(ScrollingMovementMethod())

        messaged = true


        if (intent.getStringExtra("username") == null) {
            //getUser()
            finish()
            return

        } else {
            user = intent.getStringExtra("username")
        }
        processData()
        displayUsers()
    }


    /* when the function is paused, then we access the service and send notifications */
    override fun onPause() {
        super.onPause()

        //signal the notifications
        if (messaged) {
            val fb = FirebaseDatabase.getInstance().reference
            val messageQuery = fb.child("treechat/channels/general/messages")



            messageQuery.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(err: DatabaseError) {
                    // empty
                }

                override fun onDataChange(data: DataSnapshot) { //if a new message is sent and the fb changes, notify

                    val intent = Intent(this@channelActivity, messageService::class.java)
                    intent.action = "message"
                    intent.putExtra("username", user)

                    startService(intent)
                }
            })
        }
        messaged = false

    }



    /* send a message when the user presses send message. Access the firebase and set all the
     * necessary values  */
    fun sendMessage(view: View) {
        //depending on the message being sent, take the text and add a new message to the database
        //then, add the new message to the text view

        val user_message = message_text.text.toString()
        processData()
        displayUsers()

        val fb = FirebaseDatabase.getInstance().reference

        //adding the data on the database

        val table = fb.child("treechat/channels/general/messages")
        val newMessage = table.push()

        val timeNow = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        val formattedDate = formatter.format(timeNow)

        newMessage.child("from").setValue(user)
        newMessage.child("text").setValue(user_message)
        newMessage.child("timestamp").setValue("$formattedDate")
    }

    /* "Treechat":
                "members": [],
                "messages": [
                    {
                        "from": "stepp",
                        "text": "Here I am! ... Where is everybody?",
                        "timestamp": "2019-03-01T12:02:00.521Z"
                   },*/
    fun processData() {
        /* first, get data from the fb and access the messages within the database*/
        val fb = FirebaseDatabase.getInstance().reference
        val messageQuery = fb.child("treechat/channels/general/messages")



        messageQuery.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
                // empty
            }

            override fun onDataChange(data: DataSnapshot) { //if a new message is sent and the fb changes, notify
                // gets called when data arrives
                displayMessages(messageQuery, data)

                /*
                val intent = Intent(this@channelActivity, messageService::class.java)
                intent.action = "message"
                intent.putExtra("username", user)
                startService(intent)
                */



            }
        })
    }

    /* display all entered users in the beginning */
    private fun displayAllUsers(userQuery : DatabaseReference, arr: DataSnapshot) {
        //val list = ArrayList<String>()
        var list = ""
        for (child in arr.children) {
            val curr_user = child.getValue(User::class.java)!!

            val currUserEmail = curr_user.email

            val userText = "$currUserEmail\n"
            list += userText
            //list.add(userText)
        }
        //user_view.text = list.toString()
        user_view.text = list

    }


    private fun displayUsers() {
        val fb = FirebaseDatabase.getInstance().reference
        val userQuery = fb.child("treechat/users")


        userQuery.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
                // empty
            }

            override fun onDataChange(data: DataSnapshot) { //if a new message is sent and the fb changes, notify
                // gets called when data arrives
                displayAllUsers(userQuery, data)



            }
        })

    }

    /* in the beginning, display all entered messages every submitted */
    private fun displayMessages(messageQuery : DatabaseReference, arr: DataSnapshot) {

        if (!arr.hasChildren()) {
            Toast.makeText(this,
                "${messageQuery} branch has no children", Toast.LENGTH_SHORT).show()
            return
        }

        var list = ""
        for (child in arr.children) {
            val messageKey = child.child("text")
            val messageValue = messageKey.value
            val userNameKey = child.child("from")
            val userNameValue = userNameKey.value

            val completeMessage = "$userNameValue: " + "$messageValue\n"
            //list.add(completeMessage)
            list += completeMessage

        }
        message_view.text = list
    }

    
    private inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // handle the received broadcast message

            user = intent.getStringExtra("username")
            Log.d("DownloadReceiver", "Your message has been successfuly received!!!")
        }
    }


}



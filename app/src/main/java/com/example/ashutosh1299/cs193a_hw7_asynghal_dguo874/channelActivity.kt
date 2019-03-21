package com.example.ashutosh1299.cs193a_hw7_asynghal_dguo874

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_channel.*
import java.util.ArrayList

class channelActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var user = "dguo874@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        mAuth = FirebaseAuth.getInstance()

        user = intent.getStringExtra("username")
    }


    fun sendMessage(view: View) {
        //depending on the message being sent, take the text and add a new message to the database
        //then, add the new message to the text view

        val user_message = message_text.text.toString()
        processData()

        val fb = FirebaseDatabase.getInstance().reference
        val table = fb.child("Treechat/messages")
        val newMessage = table.push()

        newMessage.child("from").setValue(user)
        newMessage.child("text").setValue(user_message)
        newMessage.child("timestamp").setValue("2019-03-01T12:02:00.521Z")

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
        val messageQuery = fb.child("Treechat/messages")

        messageQuery.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
                // empty
            }

            override fun onDataChange(data: DataSnapshot) { //if a new message is sent and the fb changes, notify
                // gets called when data arrives
                Log.d("Marty", "The data is: $data")
                displayMessages(messageQuery, data)

                //signal the notifications
                val intent = Intent(this@channelActivity, messageService::class.java)
                intent.action = "message"
                //intent.putExtra("url", url)
                startService(intent)


            }
        })



    }

    private fun displayMessages(messageQuery : DatabaseReference, arr: DataSnapshot) {
        /*
        if (!arr.hasChildren()) {
            Toast.makeText(this,
                "${messageQuery}", Toast.LENGTH_SHORT).show()
            return
        }
        */

        /* second, parse the messages and display them on the screen */
        val list = ArrayList<String>()
        for (child in arr.children) {
            val message = child.getValue().toString()
            list.add(message)
        }

        //use an ArrayAdapter to update the list depending on a new message being sent
        /*
        list.adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, list
        )
        */
    }

    private inner class DownloadReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // handle the received broadcast message

            //val url = intent.getStringExtra("url")
            //Log.d("DownloadReceiver", "this URL is done: $url")
            Toast.makeText(this@channelActivity, "Your message has been received", Toast.LENGTH_SHORT).show()
        }
    }
}



/*
 * David Guo<dguo874@stanford.edu>, Ashutosh Synghal asynghal@stanford.edu
 * CS 193A, Winter 2019 (instructor: Marty Stepp )
 * Homework Assignment 6
 * The treechat game is a slack-like game that allows users to chat
 * with each other within one single chat
 */
package com.example.ashutosh1299.cs193a_hw7_asynghal_dguo874

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class messageService : Service() {
    //private var username = ""

    companion object {
        /** Default amount of time to pause during downloads to test long delays. */
        private const val NOTIFICATION_CHANNEL_ID = "CS193AMessageService"
        private const val NOTIFICATION_ID = 1234
    }

    /*when the onStart command is first started, if it's the action message (the only
    * action available), make sure to add the username in and create the noficiations. */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if (intent != null) {
            if (intent.action == "message") {
                val username = intent.getStringExtra("username")

                val thread = Thread {
                    doWork(username)
                }
                thread.start()
            }


            stopService(intent)
        }

        return Service.START_STICKY
    }



    private fun doWork(username : String) {
        makeNotification(username)

    }

    /* makeNotifications makes the notifications and notifies the user */
    private fun makeNotification(username: String) {

        val manager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager
        var builder = Notification.Builder(this)



        // new Android versions require us to create a notification "channel"
        // for the notification before we send it


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)

            builder = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)

        }
            builder.setContentTitle("A Message Has Been Sent in the Chat!")
            builder.setAutoCancel(true)
            builder.setSmallIcon(R.drawable.icon_chat_bubble)
            builder.setContentText("$username just sent a message :)")

        val finalIntent = Intent(this, channelActivity::class.java)
        finalIntent.putExtra("username", username)
        val pending = PendingIntent.getActivity(
            this, 0, finalIntent, 0)
        builder.setContentIntent(pending)

            val notification = builder.build()
            manager.notify(NOTIFICATION_ID, notification)

        /* once the user has pressed the notification that's been sent, send him to the activity */


    }

    override fun onBind(intent: Intent?): IBinder? {
        //return null
        return null

    }
}

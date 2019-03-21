package com.example.ashutosh1299.cs193a_hw7_asynghal_dguo874

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log

class messageService : Service() {

    companion object {
        /** Default amount of time to pause during downloads to test long delays. */
        const val DEFAULT_DELAY = 3000
        private const val NOTIFICATION_CHANNEL_ID = "CS193AMessageService"
        private const val NOTIFICATION_ID = 1234
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == "message") {
                //val url = intent.getStringExtra("url")
                Log.d("DownloadService", "Someone just sent a new message")

                // perform the actual work in a thread
                val thread = Thread {
                    doWork()
                }
                thread.start()
            }
        }

        return Service.START_STICKY
    }

    private fun doWork() {

        /* */
        makeNotification()

    }

    private fun makeNotification() {
        val doneIntent = Intent()
        doneIntent.action = "messagecomplete"
        //doneIntent.putExtra("url", url)
        sendBroadcast(doneIntent)

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
        //builder.setContentText(url)
        builder.setAutoCancel(true)
        //builder.setSmallIcon(R.drawable.icon_download)

        /* once the user has pressed the notification that's been sent, send him to the activity */
        val intent = Intent(this, channelActivity::class.java)
        intent.action = "downloadcomplete"
        //intent.putExtra("url", url)
        val pending = PendingIntent.getActivity(
            this, 0, intent, 0)
        builder.setContentIntent(pending)

        // send the notification
        val notification = builder.build()
        manager.notify(NOTIFICATION_ID, notification)



    }

    override fun onBind(intent: Intent): IBinder {
        //return null
        TODO("Return the communication channel to the service.")

    }
}

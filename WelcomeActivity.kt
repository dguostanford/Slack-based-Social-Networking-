package com.example.ashutosh1299.cs193a_hw7_asynghal_dguo874

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    fun signInClick(view : View) {
        val signInIntent = Intent(this, signInActivity :: class.java)
        signInIntent.putExtra("type", "sign")
        startActivity(signInIntent)
    }

    fun createAccountClick(view: View) {
        val createAccountIntent = Intent(this, signInActivity::class.java)
        createAccountIntent.putExtra("type", "create")
        startActivity(createAccountIntent)
    }
}

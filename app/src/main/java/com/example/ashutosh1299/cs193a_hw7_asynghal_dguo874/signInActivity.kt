package com.example.ashutosh1299.cs193a_hw7_asynghal_dguo874

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import com.google.firebase.database.FirebaseDatabase


class signInActivity : AppCompatActivity() {

    private var data = "create"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        FirebaseApp.initializeApp(this) //connect the app to firebase
        data = intent.getStringExtra("type")

    }

    private lateinit var auth: FirebaseAuth

    fun accountClick(view: View) {
        Toast.makeText(this, "$data ", Toast.LENGTH_SHORT).show()

        val nameOfTheUser = userName.text.toString()
        val passwordOfTheUser = userPasswordText.text.toString()
        if(data == "create") {
            createAccount(nameOfTheUser, passwordOfTheUser)
        }
        else if(data == "sign") {
            signInAccount(nameOfTheUser, passwordOfTheUser)
        }
        else {
            Toast.makeText(this, "Username and/or Password invalid. ", Toast.LENGTH_SHORT).show()
            //createAccount(nameOfTheUser, passwordOfTheUser)
            //toast for the error
        }

    }



    fun createAccount(username : String, password : String) { //user chose to create a new account
        // when user clicks to create account

        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication Passed! ", Toast.LENGTH_SHORT).show()
                    val user = auth.getCurrentUser()
                    Log.d("user info", "$user")
                    // account creation was successful

                    addNewUser(username)

                    val channelIntent = Intent(this, channelActivity :: class.java)
                    channelIntent.putExtra("username", "$user")
                    startActivity(channelIntent)

                } else {
                    // account creation failed; print task.exception to learn more
                    Toast.makeText(this, "Authentication Failed. Please Try Again", Toast.LENGTH_SHORT).show()
                    Log.d("exception error:", "${task.exception}")

                }


            }

    }

    fun signInAccount(email : String, password: String) { //user chose to sign into an existing account
        // when user clicks to sign in
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign-in Passed! ", Toast.LENGTH_SHORT).show()
                    val user = auth.getCurrentUser()
                    Log.d("user info", "$user")


                    val channelIntent = Intent(this, channelActivity :: class.java)
                    channelIntent.putExtra("username", "$user")
                    startActivity(channelIntent)

                    // sign-in was successful
                } else {
                    Toast.makeText(this, "Authentication Failed. Please Try Again", Toast.LENGTH_SHORT).show()
                    Log.d("exception error:", "${task.exception}")
                    // sign-in failed; print task.exception to learn more
                }
            }
    }

    /* "users": {
            "stepp": {
                "username": "stepp",
                "name": "Marty Stepp",
                "email": "stepp@stanford.edu",
                "human": true
            },*/

    fun addNewUser(username: String) {

        val fb = FirebaseDatabase.getInstance().reference
        val table = fb.child("Treechat/messages")
        val userTable = table.push()

        //need to add values for input on the create account portion their name and username
        userTable.child("username").setValue(username)
        userTable.child("name").setValue(username)
        userTable.child("email").setValue(username)
        userTable.child("human").setValue("true")

    }

}

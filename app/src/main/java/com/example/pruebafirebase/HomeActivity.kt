package com.example.pruebafirebase

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var bundle:Bundle?=intent.extras
        var email:String?=bundle?.getString("email")
        var provider:String?=bundle?.getString("provider")
        setup(email?:"",provider?:"")

        val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()
    }

    private fun setup(email:String,provider:String) {
        title="Inicio"
        var textView:TextView=findViewById(R.id.textView)
        var textView2:TextView=findViewById(R.id.textView2)
        textView.text=email
        textView2.text=provider

        var logOutButton: Button =findViewById(R.id.LogOutButton)
        logOutButton.setOnClickListener {
            val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}
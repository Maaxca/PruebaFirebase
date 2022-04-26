package com.example.pruebafirebase

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val db = Firebase.firestore
class MainActivity : AppCompatActivity() {

    private  val GOOGLE_SIGN_IN=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val washingtonRef = db.collection("users").document("prueba1@gmail.com")
        //washingtonRef.update("rutas", FieldValue.arrayUnion(mapOf("hola" to "okij")))
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        var authLayout: LinearLayout =findViewById(R.id.authLayout)
        authLayout.visibility=View.VISIBLE
    }

    private fun session(){
        val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email=prefs.getString("email",null)
        val provider=prefs.getString("provider",null)

        if(email!=null && provider!=null){
            var authLayout: LinearLayout =findViewById(R.id.authLayout)
            authLayout.visibility= View.INVISIBLE
            showHome(email,ProviderType.valueOf(provider))
        }
    }

    private fun setup(){
        title="Autenticacion"

        var signUpButton: Button=findViewById(R.id.LogOutButton)
        var loginButton: Button=findViewById(R.id.loginButton)
        var googleButton: Button=findViewById(R.id.GoogleButton)
        var contraseñaButton: Button=findViewById(R.id.ContraseñaButton)
        var emailEditText: EditText=findViewById(R.id.emailEditText)
        var PasswordEditText: EditText=findViewById(R.id.passwordEditText)

        signUpButton.setOnClickListener{
            if (emailEditText.text.isNotEmpty() && PasswordEditText.text.isNotEmpty()){

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailEditText.text.toString(),
                        PasswordEditText.text.toString()).addOnCompleteListener{

                            if(it.isSuccessful){
                                    showHome(it.result?.user?.email ?:"",ProviderType.BASIC)

                            } else{

                                showAlert()
                            }
                    }
            }
        }
        loginButton.setOnClickListener{
            if (emailEditText.text.isNotEmpty() && PasswordEditText.text.isNotEmpty()){

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailEditText.text.toString(),
                        PasswordEditText.text.toString()).addOnCompleteListener{

                        if(it.isSuccessful){
                            showHome(it.result?.user?.email ?:"",ProviderType.BASIC)

                        } else{
                            showAlert()
                        }
                    }
            }
        }
        googleButton.setOnClickListener {
            val googleConf=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1008744362853-mmerf0v426pljufrj29fc76qss5lg8ti.apps.googleusercontent.com")
                .requestEmail()
                .build()
            val googleClient=GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }
        contraseñaButton.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailEditText.text.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GOOGLE_SIGN_IN){
        val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account=task.getResult(ApiException::class.java)

                if(account!=null){
                    val credential=GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                        if(it.isSuccessful){
                            showHome(account.email?:"",ProviderType.GOOGLE)

                        } else{
                            showAlert()
                        }
                    }
                }
            }catch (e:ApiException){
                showAlert()
            }

    }}

    private fun showAlert(){
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog=builder.create()
        dialog.show()
    }

    private fun showHome(email:String, provider:ProviderType){

        val homeIntent=Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }

}
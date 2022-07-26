package kr.co.company.gutest

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    lateinit var email_login_button : Button
    lateinit var google_sign_in_button : Button
    lateinit var email_edittext : EditText
    lateinit var password_edittext : EditText

    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        email_login_button = findViewById(R.id.email_login_button)
        google_sign_in_button = findViewById(R.id.google_sign_in_button)
        email_edittext = findViewById(R.id.email_edittext)
        password_edittext = findViewById(R.id.password_edittext)

        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener{
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }
    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startForResult.launch(signInIntent)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result : ActivityResult ->
            if(result.resultCode == RESULT_OK){
                val intent : Intent = result.data!!
                val task : Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    Log.w(ContentValues.TAG, "Google sign in failed",e)
                }
            }
        }




    fun firebaseAuthWithGoogle(account: GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{
                    task ->
                if(task.isSuccessful) {
                    moveMainPage(task.result?.user)
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener{
                    task -> if(task.isSuccessful) {
                moveMainPage(task.result.user)
            } else if(!task.exception?.message.isNullOrEmpty()) {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }else {
                signinEmail()
            }
            }
    }
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener{
                    task ->
                if(task.isSuccessful) {
                    moveMainPage(task.result?.user)
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage(user: FirebaseUser?){
        if(user != null) {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

}
package kr.co.company.gutest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoooginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var gotoLoginButton: Button
    lateinit var emailEditText: EditText
    lateinit var passWordEditText: EditText

    private val TAG = "SignUpActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_looogin)

        // Initialize Firebase Auth
        auth = Firebase.auth

        gotoLoginButton = findViewById(R.id.gotoLoginButton)
        gotoLoginButton.setOnClickListener{
            signUp()
        }

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

    }

    private fun signUp() {
        emailEditText = findViewById(R.id.emailEditText)
        passWordEditText = findViewById(R.id.passWordEditText)

        val email = emailEditText.text.toString()
        val password = passWordEditText.text.toString()


        // 이메일이나 패스워드나 패스워드 확인 입력 안했을 때
        if(email.length > 0 && password.length > 0 ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        if (task.exception != null) {
                            // 비밀번호 또는 아이디가 잘못 입력됨 (영어로 나오는데 뭘로 투표)
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

        }else {
            Toast.makeText(this, "이메일 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }

    }

}
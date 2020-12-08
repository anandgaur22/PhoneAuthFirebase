package com.art.otpdemo

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.TaskExecutors.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class OtpVerifyActivity : AppCompatActivity() , View.OnClickListener{

    private var verificationId: String? = null
    var phonenumber: String? = null
    private var mAuth: FirebaseAuth? = null
    var phon_txtView: TextView? = null
    var resend_time_txtView:TextView? = null
    var resend_btn:TextView? = null
    var verify_btn: Button? = null
    var et1: EditText? = null
    var et2:EditText? = null
    var et3:EditText? = null
    var et4:EditText? = null
    var et5:EditText? = null
    var et6:EditText? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verify)

        init()

    }

    fun init() {
        mAuth = FirebaseAuth.getInstance()

        phon_txtView = findViewById(R.id.phon_txtView)
        resend_time_txtView = findViewById(R.id.resend_time_txtView)
        resend_btn = findViewById(R.id.resend_btn)
        verify_btn = findViewById(R.id.verify_btn)
        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        et3 = findViewById(R.id.et3)
        et4 = findViewById(R.id.et4)
        et5 = findViewById(R.id.et5)
        et6 = findViewById(R.id.et6)
        verify_btn?.setOnClickListener(this)
        resend_btn?.setOnClickListener(this)
        phonenumber = intent.getStringExtra("phonenumber")
        sendVerificationCode(phonenumber!!)

        // phon_txtView.setText(phonenumber);
        phon_txtView?.setText(Html.fromHtml("<font color=\"#a3a3a3\">Enter OTP send to </font> <font color=\"#000000\">$phonenumber</font>"))
        countdownTime()
        Textwatcher()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.verify_btn -> {
                val a = et1!!.text.toString().trim { it <= ' ' }
                val b = et2!!.text.toString().trim { it <= ' ' }
                val c = et3!!.text.toString().trim { it <= ' ' }
                val d = et4!!.text.toString().trim { it <= ' ' }
                val e = et5!!.text.toString().trim { it <= ' ' }
                val f = et6!!.text.toString().trim { it <= ' ' }
                val otp = a + b + c + d + e + f
                verifyCode(otp)
            }
            R.id.resend_btn -> {
                resend_time_txtView!!.visibility = View.VISIBLE
                sendVerificationCode(phonenumber!!)
                countdownTime()
                resend_btn!!.visibility = View.GONE
            }
            else -> {
            }
        }
    }


    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@OtpVerifyActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                       // Toast.makeText(OtpVerifyActivity.this, task.getException().message(), Toast.LENGTH_LONG).show();
                    }
                }
    }

    private fun sendVerificationCode(number: String) {

        val options = PhoneAuthOptions.newBuilder(mAuth!!)
                .setPhoneNumber(number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            verificationId = s
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null) {
                val a = code.substring(0, 1)
                val b = code.substring(1, 2)
                val c = code.substring(2, 3)
                val d = code.substring(3, 4)
                val e = code.substring(4, 5)
                val f = code.substring(5, 6)
                et1!!.setText(a)
                et2!!.setText(b)
                et3!!.setText(c)
                et4!!.setText(d)
                et5!!.setText(e)
                et6!!.setText(f)

                // verifyCode(code);
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@OtpVerifyActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }


    fun countdownTime() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resend_time_txtView!!.text = "Resend code in " + millisUntilFinished / 1000 + " sec"
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                resend_btn!!.visibility = View.VISIBLE
                resend_time_txtView!!.visibility = View.GONE
            }
        }.start()
    }

    fun Textwatcher() {
        et1!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et2!!.requestFocus()
                } else if (s.length == 0) {
                    et1!!.clearFocus()
                }
            }
        })
        et2!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et3!!.requestFocus()
                } else if (s.length == 0) {
                    et1!!.requestFocus()
                }
            }
        })
        et3!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et4!!.requestFocus()
                } else if (s.length == 0) {
                    et2!!.requestFocus()
                }
            }
        })
        et4!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et5!!.requestFocus()
                } else if (s.length == 0) {
                    et3!!.requestFocus()
                }
            }
        })
        et5!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    et6!!.requestFocus()
                } else if (s.length == 0) {
                    et4!!.requestFocus()
                }
            }
        })
        et6!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    // et6.clearFocus();
                } else if (s.length == 0) {
                    et5!!.requestFocus()
                }
            }
        })
    }
}
package com.art.otpdemo

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hbb20.CountryCodePicker

class MobileNoActivity : AppCompatActivity() {

    var phone_editText: EditText? = null
    var ccp: CountryCodePicker? = null
    var verify_btn: Button? = null
    var otp: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_no)

        init()
    }

    fun init() {
        ccp = findViewById(R.id.ccp)
        phone_editText = findViewById(R.id.phone_editText)
        verify_btn = findViewById(R.id.verify_btn)
        otp = findViewById(R.id.otp)
        ccp?.registerCarrierNumberEditText(phone_editText)
        otp?.setText(Html.fromHtml(resources.getString(R.string.otp)))
        verify_btn?.setOnClickListener(View.OnClickListener {
            val number = ccp?.getFullNumberWithPlus()
            if (number?.isEmpty() == true) {
                phone_editText?.setError("Valid phon number is required")
                phone_editText?.requestFocus()
            } else {
                val intent = Intent(this@MobileNoActivity, OtpVerifyActivity::class.java)
                intent.putExtra("phonenumber", number)
                startActivity(intent)
            }
        })
    }


}
package com.cadabra.sociallogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customListenersButton.setOnClickListener { startActivity(Intent(this, CustomViewListenersActivity::class.java)) }
        customRxButton.setOnClickListener { startActivity(Intent(this, CustomViewRxActivity::class.java)) }
        standardListenersButton.setOnClickListener { startActivity(Intent(this, StandardViewListenersActivity::class.java)) }
        standardRxButton.setOnClickListener { startActivity(Intent(this, StandardViewRxActivity::class.java)) }
    }
}

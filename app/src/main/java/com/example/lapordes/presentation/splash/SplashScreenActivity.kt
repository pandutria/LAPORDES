package com.example.lapordes.presentation.splash

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.lapordes.R
import com.example.lapordes.presentation.auth.AuthActivity
import com.example.lapordes.utils.IntentHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            delay(3000)
//            if (UserIdPref(this@SplashActivity).get() != "")
//                IntentHelper.navigate(this@SplashActivity, MainActivity::class.java)
//            else
//                IntentHelper.navigate(this@SplashActivity, AuthActivity::class.java)
            IntentHelper.navigate(this@SplashScreenActivity, AuthActivity::class.java)
            finish()
        }
    }
}

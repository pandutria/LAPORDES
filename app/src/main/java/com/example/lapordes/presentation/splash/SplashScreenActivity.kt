package com.example.lapordes.presentation.splash

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.lapordes.MainActivity
import com.example.lapordes.R
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.presentation.admin.AdminMainActivity
import com.example.lapordes.presentation.auth.AuthActivity
import com.example.lapordes.utils.IntentHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_splash_screen)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.progressBar)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(0, 0, 0, systemBars.bottom)
//            insets
//        }

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        lifecycleScope.launch {
            delay(3000)
            val user = UserPref(this@SplashScreenActivity).get()
            if (user != null) {
                if (user.isAdmin) {
                    IntentHelper.navigate(this@SplashScreenActivity, AdminMainActivity::class.java)
                } else {
                    IntentHelper.navigate(this@SplashScreenActivity, MainActivity::class.java)
                }
                finish()
                return@launch
            }
            IntentHelper.navigate(this@SplashScreenActivity, AuthActivity::class.java)
            finish()
        }
    }
}

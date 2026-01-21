package com.example.lapordes

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.lapordes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavbar) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)

            (v.layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
                lp.bottomMargin = 0
                v.layoutParams = lp
            }
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavbar.setupWithNavController(navController)

        binding.bottomNavbar.selectedItemId = R.id.homeMenu

        binding.bottomNavbar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeMenu -> {
                    navController.navigate(R.id.homeScreen)
                }
                R.id.notificationMenu -> {
                    navController.navigate(R.id.notificationScreen)
                }
                R.id.profileMenu -> {
                    navController.navigate(R.id.profileScreen)
                }
            }

            moveIndicatorTo(item.itemId)
            true
        }


        binding.bottomNavbar.doOnLayout {
            moveIndicatorTo(binding.bottomNavbar.selectedItemId)
        }
    }

    private fun moveIndicatorTo(itemId: Int) {
        val index = when (itemId) {
            R.id.homeMenu -> 0
            R.id.notificationMenu -> 1
            R.id.profileMenu -> 2
            else -> 0
        }

        val bottomNav = binding.bottomNavbar
        val menuView = bottomNav.getChildAt(0) as? ViewGroup ?: return
        val itemView = menuView.getChildAt(index) ?: return

        binding.frameNavbar.post {
            val itemLoc = IntArray(2)
            val frameLoc = IntArray(2)
            itemView.getLocationOnScreen(itemLoc)
            binding.frameNavbar.getLocationOnScreen(frameLoc)

            val centerX =
                (itemLoc[0] - frameLoc[0]) +
                        itemView.width / 2f -
                        binding.indicator.width / 2f

            binding.indicator.animate()
                .x(centerX)
                .setDuration(200)
                .start()
        }
    }
}
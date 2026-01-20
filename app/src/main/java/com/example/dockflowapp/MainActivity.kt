package com.example.dockflowapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.dockflowapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        changeView(berandaFragment())

        val bottomNav = binding.bottomNavigation

        val warnaAktif = Color.parseColor("#3b79e6")
        val warnaTidakAktif = Color.parseColor("#757575")

        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val colors = intArrayOf(
            warnaAktif,
            warnaTidakAktif
        )

        val myColorStateList = ColorStateList(states, colors)

        bottomNav.itemIconTintList = myColorStateList
        bottomNav.itemTextColor = myColorStateList

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> {
                    changeView(berandaFragment())
                    true
                }
                R.id.nav_inventory -> {
                    changeView(inventoryFragment())
                    true
                }
                R.id.nav_history -> {
                    changeView(riwayatFragment())
                    true
                }
                R.id.nav_profile -> {
                    changeView(profileFragment())
                    true
                }
                else -> false
            }
        }

    }

    private fun changeView(fragment: Fragment){
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fcvMain, fragment)
        transaction.commit()
    }
}
package com.example.bestbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottomNav = findViewById(R.id.bottomNav)
        loadFragment(HomeFragment())
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Home -> {
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.YourBooks -> {
                    loadFragment(YourBooksFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.Favorite -> {
                loadFragment(FaveoretFragment())
                    return@setOnItemSelectedListener true
            }
                R.id.Account -> {
                    loadFragment(AccountFragment())
                    return@setOnItemSelectedListener true
                }

                else -> {
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
            }
        }

    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}

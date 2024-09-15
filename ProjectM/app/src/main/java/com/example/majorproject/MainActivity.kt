package com.example.majorproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.authentication_viewPager)
        fragmentManager = supportFragmentManager

        val fragmentAdapter = SignInSignUpAdapter(fragmentManager, lifecycle)
        viewPager.adapter = fragmentAdapter
    }
}

package com.example.majorproject.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.majorproject.fragments.SignupFragment
import com.example.majorproject.fragments.loginFragment

class SignInSignUpAdapter(
    fragmentManager: FragmentManager,
    lifecycle: androidx.lifecycle.Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 ->loginFragment()
            1 -> SignupFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}

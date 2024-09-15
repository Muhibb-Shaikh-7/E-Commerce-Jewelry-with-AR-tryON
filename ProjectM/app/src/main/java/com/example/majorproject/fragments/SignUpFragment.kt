package com.example.majorproject.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.majorproject.R
import com.example.majorproject.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize View Binding
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        // Set up the sign-up button listener
        binding.signupButton.setOnClickListener {
            signUpUser()
        }

        return binding.root
    }

    private fun signUpUser() {

        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            binding.email.error = "Email is required"
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding.password.error = "Password is required"
            return
        }

        if (password.length < 6) {
            binding.password.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            binding.confirmPassword.error = "Passwords do not match"
            return
        }

        // Create a new user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            MotionToast.createToast(
                                requireActivity(),
                                "Verification email sent",
                                "Please check your inbox.",
                                MotionToastStyle.INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                            )
                        } else {
                            MotionToast.createToast(
                                requireActivity(),
                                "Failed to send verification email",
                                emailTask.exception?.message ?: "Unknown error",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                            )
                        }
                    }
                } else {
                    MotionToast.createToast(
                        requireActivity(),
                        "Sign-up failed",
                        task.exception?.message ?: "Unknown error",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                    )
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

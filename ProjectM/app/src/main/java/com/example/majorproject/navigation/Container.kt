package com.example.majorproject.navigation

import ProfileFragment
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.example.majorproject.R
import com.example.majorproject.databinding.ActivityContainerBinding

class Container : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var binding: ActivityContainerBinding
    private lateinit var bottomBar: LinearLayout
    private lateinit var outerBottomBar: RelativeLayout
    private var fragmentNumber = 0
    private var originalPaddingLeft = 15
    private var originalPaddingRight = 15
    private var originalPaddingTop = 15
    private var originalPaddingBottom = 15
    private var previousFragmentNumber = 0
    private var previousNormalImg: ImageView? = null
    private var previousSelectedImg: ImageView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge layout adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchView =binding.searchView
        // Initialize bottom bar views
        bottomBar = binding.bottomBar
        outerBottomBar = binding.outerBottomBar
        previousNormalImg = binding.home
        previousSelectedImg = binding.home2

        setupSearchView()

        // Set HomeFragment as the default fragment
        if (savedInstanceState == null) {
           selectFragment(1 ,HomeFragment(), binding.home, binding.home2)
        }

        // Set click listeners for navigation
        binding.home.setOnClickListener {
            selectFragment(1, HomeFragment(), binding.home, binding.home2)
        }

        binding.offers.setOnClickListener {
            selectFragment(2, ProductFragment(), binding.offers, binding.offers2)
        }

        binding.you.setOnClickListener {
            selectFragment(3, ProfileFragment(), binding.you, binding.you2)
        }
    }

    private fun selectFragment(
        newFragmentNumber: Int,
        fragment: Fragment,
        normalIcon: ImageView,
        selectedIcon: ImageView
    ) {
        previousFragmentNumber = fragmentNumber
        fragmentNumber = newFragmentNumber
        navigateToFragment(fragment, normalIcon, selectedIcon)
    }

    private fun navigateToFragment(fragment: Fragment, normal: ImageView, selected: ImageView) {
        if (previousFragmentNumber != fragmentNumber) {
            resetAnimation(previousSelectedImg)
            enlargeIconWithBubbleUpAnimation(selected,previousNormalImg,normal)

            previousNormalImg?.visibility = View.VISIBLE
            previousSelectedImg?.visibility = View.INVISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()

            previousNormalImg = normal
            normal.visibility = View.INVISIBLE
            previousSelectedImg = selected
            selected.visibility = View.VISIBLE
        }
    }

    private fun resetAnimation(view: ImageView?) {
        view?.apply {
            scaleX = 1f
            scaleY = 1f
            translationY = 0f // Reset the translation for slide-up effect
            previousSelectedImg?.visibility = View.INVISIBLE
        }
    }
    @SuppressLint("ObjectAnimatorBinding")
    private fun enlargeIconWithBubbleUpAnimation(imageView: ImageView, previousImg: ImageView?, normalImg: ImageView) {

        // Store the initial position and padding of the normal image (for future animations)
        val initialNormalImgTranslationY = normalImg.translationY
        val initialNormalImgPaddingLeft = normalImg.paddingLeft
        val initialNormalImgPaddingTop = normalImg.paddingTop
        val initialNormalImgPaddingRight = normalImg.paddingRight
        val initialNormalImgPaddingBottom = normalImg.paddingBottom

        // Reset padding values to original for imageView (ensure the image starts with the correct padding each time)
        imageView.setPadding(originalPaddingLeft, originalPaddingTop, originalPaddingRight, originalPaddingBottom)
        normalImg.setPadding(10, 10, 10, 10)

        // Animation for the upward translation (without bounce effect) for the selected (current) image
        val moveUp = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, 0f, -20f).apply {
            duration = 300L // Upward move duration for a smoother feel
            interpolator = android.view.animation.OvershootInterpolator(0.5f) // Smooth easing with overshoot for a bouncy effect
        }

        // Animation for the upward translation (without bounce effect) for the normal image (previous one)
        val moveUpNormal = ObjectAnimator.ofFloat(normalImg, View.TRANSLATION_Y, 0f, -20f).apply {
            duration = 300L
            interpolator = android.view.animation.OvershootInterpolator(0.5f) // Same easing for consistency
        }

        // Scaling the icon with a slight "bubble" effect (enlarging and then shrinking) for the current image
        val scaleX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1f, 1.2f, 1.3f).apply {
            duration = 300L // Scaling duration for smooth zoom
            interpolator = android.view.animation.OvershootInterpolator(0.5f) // Overshoot for a smooth scale effect
        }

        val scaleY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1f, 1.2f, 1.3f).apply {
            duration = 300L
            interpolator = android.view.animation.OvershootInterpolator(0.5f)
        }

        // Animate the translation and scaling of the normal image (make it shrink slightly and move up)
        val scaleNormal = ObjectAnimator.ofFloat(normalImg, View.SCALE_X, 1f, 1.2f, 1.3f).apply {
            duration = 300L
            interpolator = android.view.animation.OvershootInterpolator(0.5f)
        }

        val scaleNormalY = ObjectAnimator.ofFloat(normalImg, View.SCALE_Y, 1f, 1.2f, 1.3f).apply {
            duration = 300L
            interpolator = android.view.animation.OvershootInterpolator(0.5f)
        }

        // Scaling the previous image down for smooth transition
        val scalePreviousImgX = ObjectAnimator.ofFloat(previousImg, View.SCALE_X, 1.3f, 1.2f, 1f).apply {
            duration = 300L
            interpolator = android.view.animation.OvershootInterpolator(0.5f)
        }

        val scalePreviousImgY = ObjectAnimator.ofFloat(previousImg, View.SCALE_Y, 1.4f, 2f, 1f).apply {
            duration = 300L
            interpolator = android.view.animation.OvershootInterpolator(0.5f)
        }

        // Animate the padding decrease for the current icon (to make it look like it enlarges)
        val paddingDecrease = 5
        val decreasePaddingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400L // Increased duration for smoother padding animation
            addUpdateListener { animator ->
                val progress = animator.animatedFraction
                val newPaddingLeft = (originalPaddingLeft - (paddingDecrease * progress)).toInt()
                val newPaddingTop = (originalPaddingTop - (paddingDecrease * progress)).toInt()
                val newPaddingRight = (originalPaddingRight - (paddingDecrease * progress)).toInt()
                val newPaddingBottom = (originalPaddingBottom - (paddingDecrease * progress)).toInt()

                // Apply padding dynamically to imageView
                imageView.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
                normalImg.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
            }
        }

        // Restoring padding for the previous image
        val restorePaddingAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 300L
            addUpdateListener { animator ->
                val progress = animator.animatedFraction
                val newPaddingLeft = (10 + (paddingDecrease * progress)).toInt()
                val newPaddingTop = (10 + (paddingDecrease * progress)).toInt()
                val newPaddingRight = (10 + (paddingDecrease * progress)).toInt()
                val newPaddingBottom = (10 + (paddingDecrease * progress)).toInt()

                // Apply restored padding to previous icon
                previousImg?.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
            }
        }

        // Moving the previous image down and restoring its padding to original (for the old icon)
        val moveDown = ObjectAnimator.ofFloat(previousImg, View.TRANSLATION_Y, -20f, 0f).apply {
            duration = 300L
            interpolator = android.view.animation.OvershootInterpolator(0.5f) // Smooth easing
        }

        // Combine all animations (translation, scaling, padding decrease)
        val animatorSet = AnimatorSet().apply {
            playTogether(
                decreasePaddingAnimator,moveUp, moveUpNormal, scaleX, scaleY, scaleNormal, scaleNormalY,  moveDown, restorePaddingAnimator,
                scalePreviousImgX, scalePreviousImgY
            )
            duration = 700 // Total duration for all animations with a slightly longer time for more fluid transitions
        }

        // Start animation for the current image
        animatorSet.start()

        // When the next image is clicked, reset the previous image back to its original position and padding
        previousImg?.apply {
            translationY = initialNormalImgTranslationY
            setPadding(initialNormalImgPaddingLeft, initialNormalImgPaddingTop, initialNormalImgPaddingRight, initialNormalImgPaddingBottom)
        }
    }

    private fun setupSearchView() {
        searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            ?.setHintTextColor(ContextCompat.getColor(this, R.color.dark_gray))

        val searchIconView: ImageView = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon)
        searchIconView.drawable?.let {
            it.setTint(ContextCompat.getColor(this, R.color.cocoa_brown))
            searchIconView.setImageDrawable(it)
        }


    }
}

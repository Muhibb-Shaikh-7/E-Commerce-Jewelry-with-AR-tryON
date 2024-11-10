package com.example.majorproject.navigation

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim) }
    private val rotateCLose: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim) }

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

        if (savedInstanceState == null) {
           selectFragment(1 ,HomeFragment(), binding.home, binding.home2)
        }

        binding.home.setOnClickListener {
            selectFragment(1, HomeFragment(), binding.home, binding.home2)
        }

        binding.offers.setOnClickListener {
            selectFragment(2, ProductFragment(), binding.offers, binding.offers2)
        }

        binding.contactUs.setOnClickListener{
            selectFragment(3, null, binding.contactUs, binding.contactUs2)
        }

        binding.you.setOnClickListener {
            selectFragment(4, ProfileFragment(), binding.you, binding.you2)
        }
    }

    private fun selectFragment(
        newFragmentNumber: Int,
        fragment: Fragment?,
        normalIcon: ImageView,
        selectedIcon: ImageView
    ) {

        previousFragmentNumber = fragmentNumber
        Log.d("Frgament","Previous:$previousFragmentNumber")
        fragmentNumber = newFragmentNumber
        Log.d("Frgament","New:$fragmentNumber")
        if (previousFragmentNumber != newFragmentNumber) {
            navigateToFragment(fragment, normalIcon, selectedIcon)
        }
    }

    private fun navigateToFragment(fragment: Fragment?, normal: ImageView, selected: ImageView) {

            resetAnimation(previousSelectedImg)
            enlargeIconWithBubbleUpAnimation(selected,previousNormalImg,normal)

            previousNormalImg?.visibility = View.VISIBLE
            previousSelectedImg?.visibility = View.INVISIBLE

            previousNormalImg = normal
            normal.visibility = View.INVISIBLE
            previousSelectedImg = selected
            selected.visibility = View.VISIBLE

            if(fragment!=null) {
                replaceFragment(fragment)
            }else{
                openContactUsLayout()
            }

    }

    private fun openContactUsLayout() {

        binding.contactusLayout.visibility = View.VISIBLE

        binding.contactusLayout.setClickable(true)
        binding.contactusLayout.isEnabled = true

        disableFragmentInteraction()

        // Optionally, disable other UI elements (e.g., header, footer) that shouldn't be interactive
        binding.relativeLayout.isClickable = false
        binding.relativeLayout.isFocusable = false
        binding.relativeLayout.isEnabled = false
        binding.headerTitle.isClickable = false
        binding.headerTitle.isFocusable = false
        binding.headerTitle.isEnabled = false

        // Set up the close button to hide the "Contact Us" layout and re-enable interaction
        binding.closeBtn.setOnClickListener {
            // Hide the "Contact Us" layout


            // Re-enable interaction with the fragment
            enableFragmentInteraction()

            // Optionally, re-enable other UI elements
            binding.relativeLayout.isClickable = true
            binding.relativeLayout.isFocusable = true
            binding.relativeLayout.isEnabled = true
            binding.headerTitle.isClickable = true
            binding.headerTitle.isFocusable = true
            binding.headerTitle.isEnabled = true

        }
    }

    // Function to disable touch events for the current fragment
    private fun disableFragmentInteraction() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        // If there's a fragment loaded, disable its root view and all of its children
        currentFragment?.view?.let { rootView ->
            disableViewInteraction(rootView)
        }
    }

    // Recursively disable touch events on a view and all of its child views
    private fun disableViewInteraction(view: View) {
        binding.closeBtn.startAnimation(rotateOpen)
        rotateOpen.setAnimationListener(object : Animation.AnimationListener
        {
            override fun onAnimationStart(animation: Animation?) {
                binding.imgPhoneCall.visibility=View.GONE
                binding.imgWhastapp.visibility=View.GONE
                binding.imgBookAppointment.visibility=View.GONE
                binding.imgChatBot.visibility=View.GONE
                binding.txtCallNow.visibility=View.GONE
                binding.txtWhatsapp.visibility=View.GONE
                binding.txtAppointment.visibility=View.GONE
                binding.txtChatBot.visibility=View.GONE
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imgPhoneCall.startAnimation(fromBottom)
                binding.imgWhastapp.startAnimation(fromBottom)
                binding.imgBookAppointment.startAnimation(fromBottom)
                binding.imgChatBot.startAnimation(fromBottom)
                binding.txtCallNow.startAnimation(fromBottom)
                binding.txtWhatsapp.startAnimation(fromBottom)
                binding.txtAppointment.startAnimation(fromBottom)
                binding.txtChatBot.startAnimation(fromBottom)
                setUpOnCLickListerener()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })

        
        // Disable interaction for this view
        view.isClickable = false
        view.isFocusable = false
        view.isEnabled = false
        binding.home.isClickable=false
        binding.home.isEnabled=false
        binding.offers.isClickable=false
        binding.offers.isEnabled=false
        binding.you.isClickable=false
        binding.you.isEnabled=false

        // If the view is a ViewGroup (e.g., ScrollView, RecyclerView), recursively disable its children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                disableViewInteraction(view.getChildAt(i)) // Recursively disable children
            }
        }

    }

    private fun setUpOnCLickListerener() {

        binding.imgPhoneCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 100)
            }else{

            }
        }

    }

    // Function to enable touch events for the current fragment
    private fun enableFragmentInteraction() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        // If there's a fragment loaded, enable its root view and all of its children
        currentFragment?.view?.let { rootView ->
            enableViewInteraction(rootView)
        }
    }

    // Recursively enable touch events on a view and all of its child views
    private fun enableViewInteraction(view: View) {
        binding.closeBtn.startAnimation(rotateCLose)

        rotateCLose.setAnimationListener(object : Animation.AnimationListener
        {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imgPhoneCall.startAnimation(toBottom)
                binding.imgWhastapp.startAnimation(toBottom)
                binding.imgBookAppointment.startAnimation(toBottom)
                binding.imgChatBot.startAnimation(toBottom)
                binding.txtCallNow.startAnimation(toBottom)
                binding.txtWhatsapp.startAnimation(toBottom)
                binding.txtAppointment.startAnimation(toBottom)
                binding.txtChatBot.startAnimation(toBottom)

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })

        toBottom.setAnimationListener(object : Animation.AnimationListener
        {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                View.GONE.also { binding.contactusLayout.visibility = it }
                    checkFragmentNumber(previousFragmentNumber)

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })

        view.isClickable = true
        view.isFocusable = true
        view.isEnabled = true
        binding.home.isClickable=true
        binding.home.isEnabled=true
        binding.offers.isClickable=true
        binding.offers.isEnabled=true
        binding.you.isClickable=true
        binding.you.isEnabled=true

        // If the view is a ViewGroup, recursively enable its children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                enableViewInteraction(view.getChildAt(i)) // Recursively enable children
            }
        }
    }

    private fun checkFragmentNumber(i: Int) {
        previousFragmentNumber=3
        previousNormalImg=binding.contactUs
        previousSelectedImg=binding.contactUs2
        Log.d("Frgament","Fragment Number:${fragmentNumber}")

        when(i){
            1-> selectFragment(i,HomeFragment(),binding.home, binding.home2)
            2-> selectFragment(i,ProductFragment(),binding.offers, binding.offers2)
            4-> selectFragment(i,ProfileFragment(),binding.you, binding.you2)
        }
    }

    private fun replaceFragment (fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun resetAnimation(view: ImageView?) {
        view?.apply {
            scaleX = 1f
            scaleY = 1f
            translationY = 0f
            previousSelectedImg?.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun enlargeIconWithBubbleUpAnimation(imageView: ImageView, previousImg: ImageView?, normalImg: ImageView) {

        val initialNormalImgTranslationY = normalImg.translationY
        val initialNormalImgPaddingLeft = normalImg.paddingLeft
        val initialNormalImgPaddingTop = normalImg.paddingTop
        val initialNormalImgPaddingRight = normalImg.paddingRight
        val initialNormalImgPaddingBottom = normalImg.paddingBottom

        imageView.setPadding(originalPaddingLeft, originalPaddingTop, originalPaddingRight, originalPaddingBottom)
        normalImg.setPadding(10, 10, 10, 10)

        val moveUp = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, 0f, -20f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val moveUpNormal = ObjectAnimator.ofFloat(normalImg, View.TRANSLATION_Y, 0f, -20f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val scaleX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1f, 1.2f, 1.2f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val scaleY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1f, 1.2f, 1.2f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val scaleNormal = ObjectAnimator.ofFloat(normalImg, View.SCALE_X, 1f, 1.2f, 1.2f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val scaleNormalY = ObjectAnimator.ofFloat(normalImg, View.SCALE_Y, 1f, 1.2f, 1.2f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val scalePreviousImgX = ObjectAnimator.ofFloat(previousImg, View.SCALE_X, 1.2f, 1.2f, 1f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val scalePreviousImgY = ObjectAnimator.ofFloat(previousImg, View.SCALE_Y, 1.4f, 2f, 1f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val paddingDecrease = 3
        val decreasePaddingAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400L
            addUpdateListener { animator ->
                val progress = animator.animatedFraction
                val newPaddingLeft = (originalPaddingLeft - (paddingDecrease * progress)).toInt()
                val newPaddingTop = (originalPaddingTop - (paddingDecrease * progress)).toInt()
                val newPaddingRight = (originalPaddingRight - (paddingDecrease * progress)).toInt()
                val newPaddingBottom = (originalPaddingBottom - (paddingDecrease * progress)).toInt()

                imageView.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
                normalImg.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
            }
        }

        val restorePaddingAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 300L
            addUpdateListener { animator ->
                val progress = animator.animatedFraction
                val newPaddingLeft = (10 + (paddingDecrease * progress)).toInt()
                val newPaddingTop = (10 + (paddingDecrease * progress)).toInt()
                val newPaddingRight = (10 + (paddingDecrease * progress)).toInt()
                val newPaddingBottom = (10 + (paddingDecrease * progress)).toInt()

                previousImg?.setPadding(newPaddingLeft, newPaddingTop, newPaddingRight, newPaddingBottom)
            }
        }

        val moveDown = ObjectAnimator.ofFloat(previousImg, View.TRANSLATION_Y, -20f, 0f).apply {
            duration = 300L
            interpolator = OvershootInterpolator(0.5f)
        }

        val animatorSet = AnimatorSet().apply {
            playTogether(
                decreasePaddingAnimator,moveUp, moveUpNormal, scaleX, scaleY, scaleNormal, scaleNormalY,  moveDown, restorePaddingAnimator,
                scalePreviousImgX, scalePreviousImgY
            )
            duration = 700
        }

        animatorSet.start()

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
            it.setTint(ContextCompat.getColor(this, R.color.black))
            searchIconView.setImageDrawable(it)
        }


    }
}

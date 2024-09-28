package com.example.majorproject

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.majorproject.adapters.ImageSliderAdapter
import com.example.majorproject.adapters.SignInSignUpAdapter
import com.example.majorproject.databinding.ActivityAuthenticationSelectionBinding

import java.util.*

class AuthenticationSelection : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationSelectionBinding
    private lateinit var handler: Handler
    private var imageIndex = 0
    private val autoSlideInterval: Long = 3000 // 3 seconds
    private var autoSlideTask: TimerTask? = null
    private val timer = Timer()
    private lateinit var authViewPager: ViewPager2
    private lateinit var fragmentManager: FragmentManager
    private var autoSlidePaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationSelectionBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        authViewPager = binding.authenticationViewPager
        fragmentManager = supportFragmentManager

        val fragmentAdapter = SignInSignUpAdapter(fragmentManager, lifecycle)
        authViewPager.adapter = fragmentAdapter

        handler = Handler(Looper.getMainLooper())

        val initialImageList = mutableListOf(getNextImage())
        binding.viewPager.adapter = ImageSliderAdapter(initialImageList)

        startAutoSlide()

        binding.viewPager.setOnTouchListener { _, _ ->
            pauseAutoSlide()
            false
        }

        // Resume auto-slide when user stops interacting
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE && autoSlidePaused) {
                    resumeAutoSlide()
                    autoSlidePaused = false
                }
            }
        })

        binding.signInButton.setOnClickListener {
            handleButtonClick()
        }
        binding.signUpButton.setOnClickListener {
            handleButtonClick()
        }
    }

    private fun handleButtonClick() {
        val currentPosition = binding.viewPager.currentItem
        val adapter = binding.viewPager.adapter as? ImageSliderAdapter
        val currentImageResId = adapter?.getImageAtPosition(currentPosition)

        if (currentImageResId != null) {
            binding.backgroundImageView.setImageResource(currentImageResId)
            binding.backgroundImageView.visibility = View.VISIBLE

//            BlurUtil.applyBlur(this, binding.backgroundImageView, 25f)

            Handler(Looper.getMainLooper()).postDelayed({
                binding.authCardView.visibility = View.VISIBLE
                val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
                binding.authCardView.startAnimation(slideUpAnimation)
                binding.viewPager.isUserInputEnabled = false
            }, 100)
        }
    }

    private fun getNextImage(): Int {
        val images = listOf(
            R.drawable.model_img_1,
            R.drawable.model_img_2,
            R.drawable.model_img_3,
            R.drawable.model_img_4
        )
        val image = images[imageIndex]
        imageIndex = (imageIndex + 1) % images.size
        return image
    }

    private fun startAutoSlide() {
        autoSlideTask?.cancel()
        autoSlideTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    val adapter = binding.viewPager.adapter as? ImageSliderAdapter
                    adapter?.let {
                        val nextImage = getNextImage()
                        it.addImage(nextImage)

                        binding.viewPager.currentItem = it.itemCount - 1
                    }
                }
            }
        }
        timer.schedule(autoSlideTask, autoSlideInterval, autoSlideInterval)
    }

    private fun pauseAutoSlide() {
        autoSlideTask?.cancel()
        autoSlidePaused = true
    }

    private fun resumeAutoSlide() {
        startAutoSlide()
    }

    private fun disableUserInteraction() {
        binding.viewPager.isUserInputEnabled = false
    }

    private fun enableUserInteraction() {
        binding.viewPager.isUserInputEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        autoSlideTask?.cancel()
    }
}

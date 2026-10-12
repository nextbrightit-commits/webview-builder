package com.creativedesign.webview

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.creativedesign.webview.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoContainer.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.splash_logo_scale)
        )
        binding.tvAppName.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.splash_text_fade)
        )
        binding.tvTagline.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.splash_text_fade)
        )

        startProgressAnimation()
    }

    private fun startProgressAnimation() {
        ObjectAnimator.ofInt(binding.progressBarLoading, "progress", 0, 100).apply {
            duration = SPLASH_DURATION_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                binding.tvProgressPercent.text =
                    getString(R.string.splash_progress, binding.progressBarLoading.progress)
            }
            doOnEnd { navigateToMain() }
            start()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    companion object {
        private const val SPLASH_DURATION_MS = 2800L
    }
}

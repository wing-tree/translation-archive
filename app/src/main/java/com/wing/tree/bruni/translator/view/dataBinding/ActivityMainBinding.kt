package com.wing.tree.bruni.translator.view.dataBinding

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.graphics.Insets
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.regular.then
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.constant.EXTRA_LOAD_FAVORITES
import com.wing.tree.bruni.translator.databinding.ActivityMainBinding
import com.wing.tree.bruni.translator.extension.resizeText
import com.wing.tree.bruni.translator.view.HistoryActivity
import com.wing.tree.bruni.translator.view.InAppProductsActivity
import com.wing.tree.bruni.translator.view.MainActivity
import com.wing.tree.bruni.windowInsetsAnimation.extension.isTypeMasked
import java.util.concurrent.atomic.AtomicBoolean

internal fun ActivityMainBinding.drawerLayout(mainActivity: MainActivity) = with(mainActivity) {
    val actionBarDrawerToggle = ActionBarDrawerToggle(
        this,
        drawerLayout,
        materialToolbar,
        R.string.open_drawer,
        R.string.close_drawer
    )

    drawerLayout.addDrawerListener(actionBarDrawerToggle)

    actionBarDrawerToggle.syncState()
}

internal fun ActivityMainBinding.editText() = with(sourceText) {
    with(sourceText) {
        addTextChangedListener {
            val textSize = resizeText()

            with(translatedText) {
                translatedText.textSize = textSize
            }
        }
    }
}

internal fun ActivityMainBinding.materialButton(mainActivity: MainActivity) = with(mainActivity) {
    val swapLanguages = sourceText.swapLanguages
    val displaySourceLanguage = sourceText.displaySourceLanguage
    val displayTargetLanguage = translatedText.displayTargetLanguage

    swapLanguages.setOnClickListener {
        swapLanguages.isClickable = false
        swapLanguages.isFocusable = false

        val accelerateQuadInterpolator = accelerateQuadInterpolator
        val decelerateQuadInterpolator = decelerateQuadInterpolator
        val duration = configShortAnimTime.long.half
        val translationY = dimen(R.dimen.text_size_22dp)

        with(displaySourceLanguage) {
            translateDown(duration, translationY, accelerateQuadInterpolator) {
                mainActivity.swapLanguages()
                translateUp(duration, ZERO.float, decelerateQuadInterpolator) {
                    swapLanguages.isClickable = true
                    swapLanguages.isFocusable = true
                }.alpha(ONE.float)
            }.alpha(ZERO.float)
        }

        with(displayTargetLanguage) {
            translateUp(duration, translationY, accelerateQuadInterpolator) {
                translateDown(duration, ZERO.float, decelerateQuadInterpolator)
                    .alpha(ONE.float)
            }.alpha(ZERO.float)
        }
    }
}

internal fun ActivityMainBinding.materialToolbar(mainActivity: MainActivity) = with(mainActivity) {
    setSupportActionBar(
        materialToolbar.apply {
            setNavigationOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
    )

    supportActionBar?.setDisplayShowTitleEnabled(false)
}

internal fun ActivityMainBinding.navigationView(
    activityResultLauncher: ActivityResultLauncher<Intent>,
    mainActivity: MainActivity
) = with(mainActivity) {
    val shouldCloseDrawer = AtomicBoolean(false)

    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    if (shouldCloseDrawer.compareAndSet(true, false)) {
                        drawerLayout.closeDrawer(GravityCompat.START, false)
                    }
                }

                super.onStop(owner)
            }
        }
    )

    navigationView.setNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.history -> {
                activityResultLauncher
                    .launch(Intent(this, HistoryActivity::class.java))
                    .then {
                        overridePendingTransition(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )

                        shouldCloseDrawer.set(true)
                    }

                false
            }
            R.id.clear_all -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.clear_all)
                    .setMessage(R.string.clear_all_histories)
                    .setPositiveButton(R.string.clear_all) { dialog, _ ->
                        viewModel.clearAllHistories()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

                false
            }
            R.id.favorites -> {
                val intent = Intent(this, HistoryActivity::class.java).apply {
                    putExtra(EXTRA_LOAD_FAVORITES, true)
                }

                startActivity(intent)
                overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )

                shouldCloseDrawer.set(true)

                false
            }
            R.id.in_app_products -> {
                startActivity<InAppProductsActivity>()
                overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )

                shouldCloseDrawer.set(true)

                false
            }
            else -> true
        }
    }
}

internal fun ActivityMainBinding.nestedScrollView(mainActivity: MainActivity) {
    val paddingTop = mainActivity.dimen(R.dimen.padding_top_56dp)

    sourceText.nestedScrollView(mainActivity, paddingTop)
    translatedText.nestedScrollView(mainActivity, paddingTop)
}

internal fun ActivityMainBinding.setWindowInsetsAnimationCallback() = post {
    ViewCompat.setWindowInsetsAnimationCallback(
        constraintLayout,
        object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
            private val rootWindowInsets: WindowInsetsCompat?
                get() = ViewCompat.getRootWindowInsets(root)

            private var constraintLayoutHeight = ZERO
            private var imeHeight = ZERO
            private var topCoefficient: Float = ZERO.float
            private var bottomCoefficient: Float = ZERO.float

            override fun onStart(
                animation: WindowInsetsAnimationCompat,
                bounds: WindowInsetsAnimationCompat.BoundsCompat
            ): WindowInsetsAnimationCompat.BoundsCompat {
                constraintLayoutHeight = displayHeight.minus(materialToolbar.height)

                if (animation.isTypeMasked(ime())) {
                    rootWindowInsets?.let { windowInsets ->
                        if (windowInsets.isVisible(ime())) {
                            Insets.subtract(
                                windowInsets.getInsets(ime()),
                                windowInsets.getInsets(systemBars())
                            ).let {
                                Insets.max(it, Insets.NONE).height
                            }.let {
                                imeHeight = it

                                topCoefficient = materialToolbar
                                    .height
                                    .float
                                    .safeDiv(it)

                                bottomCoefficient = linearLayout
                                    .height
                                    .float
                                    .safeDiv(it)
                                    .complement
                            }
                        }
                    }
                }

                return bounds
            }

            override fun onProgress(
                insets: WindowInsetsCompat,
                runningAnimations: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat {
                runningAnimations.notContains {
                    it.isTypeMasked(ime())
                }.let {
                    if (it.or(imeHeight.isZero)) {
                        return insets
                    }
                }

                Insets.subtract(
                    insets.getInsets(ime()),
                    insets.getInsets(systemBars())
                ).let {
                    Insets.max(it, Insets.NONE)
                }.let {
                    it.top.minus(it.bottom).float
                }.let {
                    val topTranslationY = topCoefficient.times(it)
                    val bottomTranslationY = bottomCoefficient.times(it)

                    materialToolbar.translationY = topTranslationY
                    constraintLayout.translationY = topTranslationY

                    with(constraintLayout) {
                        val height = constraintLayoutHeight
                            .minus(topTranslationY)
                            .plus(bottomTranslationY)
                            .int

                        updateHeight(height)
                    }

                    linearLayout.translationY = bottomTranslationY.negative
                }

                return insets
            }

            override fun onEnd(animation: WindowInsetsAnimationCompat) {
                if (animation.isTypeMasked(ime())) {
                    val isVisible = rootWindowInsets?.isVisible(ime()) == true

                    with(sourceText) {
                        isCursorVisible = isVisible

                        post {
                            if (isVisible.and(rootView.findFocus().isNull())) {
                                requestFocus()
                            } else if (isVisible.not().and(isFocused)) {
                                clearFocus()
                            }
                        }
                    }
                }
            }

            private val Insets.height: Int get() = bottom
        }
    )
}

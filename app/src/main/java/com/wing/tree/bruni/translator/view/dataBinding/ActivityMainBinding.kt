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
import com.wing.tree.bruni.translator.extension.setWindowInsetsAnimationCallback
import com.wing.tree.bruni.translator.view.HistoryActivity
import com.wing.tree.bruni.translator.view.InAppProductsActivity
import com.wing.tree.bruni.translator.view.MainActivity
import com.wing.tree.bruni.windowInsetsAnimation.extension.isTypeMasked
import java.util.concurrent.atomic.AtomicBoolean

internal fun ActivityMainBinding.button(mainActivity: MainActivity) {
    with(mainActivity) {
        val swapLanguages = sourceText.swapLanguages
        val displaySourceLanguage = sourceText.displaySourceLanguage
        val displayTargetLanguage = translatedText.displayTargetLanguage

        swapLanguages.setOnClickListener {
            swapLanguages.isClickable = false
            swapLanguages.isFocusable = false

            val accelerateQuadInterpolator = accelerateQuadInterpolator
            val decelerateQuadInterpolator = decelerateQuadInterpolator
            val duration = configShortAnimTime.long.half
            val translationY = dimen(R.dimen.text_size_20dp)

            with(displaySourceLanguage) {
                translateY(translationY, duration, accelerateQuadInterpolator) {
                    swapLanguages()
                    translateY(ZERO.float, duration, decelerateQuadInterpolator) {
                        swapLanguages.isClickable = true
                        swapLanguages.isFocusable = true
                    }.alpha(ONE.float)
                }.alpha(ZERO.float)
            }

            with(displayTargetLanguage) {
                translateY(translationY.negative, duration, accelerateQuadInterpolator) {
                    translateY(ZERO.float, duration, decelerateQuadInterpolator)
                        .alpha(ONE.float)
                }.alpha(ZERO.float)
            }
        }
    }
}

internal fun ActivityMainBinding.drawerLayout(mainActivity: MainActivity) {
    val actionBarDrawerToggle = ActionBarDrawerToggle(
        mainActivity,
        drawerLayout,
        toolbar,
        R.string.open_drawer,
        R.string.close_drawer
    )

    drawerLayout.addDrawerListener(actionBarDrawerToggle)

    actionBarDrawerToggle.syncState()
}

internal fun ActivityMainBinding.navigationView(
    activityResultLauncher: ActivityResultLauncher<Intent>,
    mainActivity: MainActivity
) {
    with(mainActivity) {
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

                    activityResultLauncher
                        .launch(intent)
                        .then {
                            overridePendingTransition(
                                android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right
                            )

                            shouldCloseDrawer.set(true)
                        }

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
}

internal fun ActivityMainBinding.nestedScrollView(mainActivity: MainActivity) {
    sourceText.nestedScrollView(mainActivity)
    translatedText.nestedScrollView(mainActivity)
}

internal fun ActivityMainBinding.setWindowInsetsAnimationCallback() {
    post {
        constraintLayout.setWindowInsetsAnimationCallback(
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                private var materialToolbarRatio = ZERO.float
                private var linearLayoutRatio = ZERO.float
                private var updateHeightRatio = ZERO.float

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    val rootWindowInsets = ViewCompat.getRootWindowInsets(root) ?: return bounds

                    if (rootWindowInsets.isVisible(ime())) {
                        Insets.subtract(
                            rootWindowInsets.getInsets(ime()),
                            rootWindowInsets.getInsets(systemBars())
                        ).let {
                            Insets.max(it, Insets.NONE)
                        }.let {
                            materialToolbarRatio = toolbar.height
                                .float
                                .safeDiv(it.bottom)

                            linearLayoutRatio = linearLayout.height
                                .float
                                .safeDiv(it.bottom)
                                .complement
                                .negative

                            updateHeightRatio = actionBarSize
                                .plus(linearLayout.height)
                                .float
                                .safeDiv(it.bottom)
                                .complement
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
                        if (it.or(insets.getBottom(ime()).isZero)) {
                            return insets
                        }
                    }

                    Insets.subtract(
                        insets.getInsets(ime()),
                        insets.getInsets(systemBars())
                    ).let {
                        Insets.max(it, Insets.NONE)
                    }.let {
                        it.top.minus(it.bottom)
                    }.let {
                        toolbar.translationY = materialToolbarRatio.times(it)
                        linearLayout.translationY = linearLayoutRatio.times(it)
                        constraintLayout.translationY = materialToolbarRatio.times(it)

                        constraintLayout.updateHeight { _ ->
                            screenSize
                                .height
                                .minus(actionBarSize)
                                .plus(updateHeightRatio.times(it))
                                .int
                        }
                    }

                    return insets
                }
            }
        )
    }
}

internal fun ActivityMainBinding.sourceText() {
    with(sourceText) {
        setWindowInsetsAnimationCallback()

        doAfterTextChanged {
            val textSize = resizeText()

            with(translatedText) {
                translatedText.textSize = textSize
            }
        }
    }
}


internal fun ActivityMainBinding.toolbar(mainActivity: MainActivity) {
    with(mainActivity) {
        setSupportActionBar(
            toolbar.apply {
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
}

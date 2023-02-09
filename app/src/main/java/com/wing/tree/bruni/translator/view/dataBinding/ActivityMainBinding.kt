package com.wing.tree.bruni.translator.view.dataBinding

import android.content.Intent
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.graphics.Insets
import androidx.core.view.*
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
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

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
    val ime = WindowInsetsCompat.Type.ime()
    val systemBars = WindowInsetsCompat.Type.systemBars()

    data class ImmutableHeight(
        val constraintLayout: Float,
        val linearLayout: Float,
        val materialToolbar: Float
    )

    data class MutableHeight(
        var ime: Int
    )

    ViewCompat.setWindowInsetsAnimationCallback(
        constraintLayout,
        object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
            private val immutableHeight = ImmutableHeight(
                constraintLayout = constraintLayout.height.float,
                linearLayout = linearLayout.height.float,
                materialToolbar = materialToolbar.height.float
            )

            private val mutableHeight = MutableHeight(
                ime = ZERO
            )

            override fun onStart(
                animation: WindowInsetsAnimationCompat,
                bounds: WindowInsetsAnimationCompat.BoundsCompat
            ): WindowInsetsAnimationCompat.BoundsCompat {
                ViewCompat.getRootWindowInsets(root)?.let { windowInsets ->
                    Insets.subtract(
                        windowInsets.getInsets(ime),
                        windowInsets.getInsets(systemBars)
                    ).let {
                        mutableHeight.ime = max(
                            mutableHeight.ime,
                            Insets.max(it, Insets.NONE).bottom
                        )
                    }
                }

                return bounds
            }

            override fun onProgress(
                insets: WindowInsetsCompat,
                runningAnimations: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat {
                val difference = Insets.subtract(
                    insets.getInsets(ime),
                    insets.getInsets(systemBars)
                ).let {
                    Insets.max(it, Insets.NONE)
                }.let {
                    it.top.minus(it.bottom).float
                }

                val quotient = immutableHeight
                    .linearLayout
                    .div(mutableHeight.ime)

                val translationY = immutableHeight
                    .materialToolbar
                    .times(difference.div(mutableHeight.ime))

                constraintLayout.translationY = translationY
                constraintLayout.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = immutableHeight
                        .constraintLayout
                        .minus(translationY)
                        .plus(ONE.minus(quotient).times(difference))
                        .int
                }

                linearLayout.translationY = quotient.times(difference).negative
                materialToolbar.translationY = translationY

                return insets
            }

            override fun onEnd(animation: WindowInsetsAnimationCompat) {
                if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
                    val isVisible = ViewCompat.getRootWindowInsets(root)?.isVisible(ime) == true

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
        }
    )
}

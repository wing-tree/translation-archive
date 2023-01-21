package com.wing.tree.bruni.translator.view.binding

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.core.regular.then
import com.wing.tree.bruni.translator.R
import com.wing.tree.bruni.translator.databinding.ActivityMainBinding
import com.wing.tree.bruni.translator.view.HistoryActivity
import com.wing.tree.bruni.translator.view.MainActivity
import com.wing.tree.bruni.translator.view.nestedScrollView
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
            else -> true
        }
    }
}

internal fun ActivityMainBinding.nestedScrollView(mainActivity: MainActivity) {
    val paddingTop = mainActivity.dimen(R.dimen.padding_top_56dp)

    sourceText.nestedScrollView(mainActivity, paddingTop)
    translatedText.nestedScrollView(mainActivity, paddingTop)
}

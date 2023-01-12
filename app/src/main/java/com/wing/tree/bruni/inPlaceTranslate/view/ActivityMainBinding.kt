package com.wing.tree.bruni.inPlaceTranslate.view

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.wing.tree.bruni.core.constant.ONE
import com.wing.tree.bruni.core.constant.ZERO
import com.wing.tree.bruni.core.extension.*
import com.wing.tree.bruni.inPlaceTranslate.R
import com.wing.tree.bruni.inPlaceTranslate.databinding.ActivityMainBinding
import com.wing.tree.bruni.inPlaceTranslate.extension.getFloat

internal fun ActivityMainBinding.drawerLayout() = with(requireNotNull(activity)) {
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

internal fun ActivityMainBinding.materialButton() = with(requireNotNull(activity)) {
    swapLanguages.setOnClickListener {
        swapLanguages.isClickable = false
        swapLanguages.isFocusable = false

        val accelerateQuadInterpolator = context.accelerateQuadInterpolator
        val configShortAnimTime = resources.configShortAnimTime.long
        val decelerateQuadInterpolator = context.decelerateQuadInterpolator
        val duration = configShortAnimTime.half
        val translationY = dimen(R.dimen.text_size_24dp)

        with(displaySourceLanguage) {
            translateDown(duration, translationY, accelerateQuadInterpolator) {
                swapLanguages()
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

internal fun ActivityMainBinding.materialToolbar() = with(requireNotNull(activity)) {
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

internal fun ActivityMainBinding.navigationView() = with(requireNotNull(activity)) {
    navigationView.setNavigationItemSelectedListener {
        when(it.itemId) {
            R.id.history -> {
                startActivity<HistoryActivity>()
                false
            }
            else -> true
        }
    }
}

internal fun ActivityMainBinding.nestedScrollView() = with(requireNotNull(activity)) {
    val layoutHeight = dimen(R.dimen.layout_height_60dp)
    val maximumValue = ONE.float
    val minimumValue = getFloat(R.dimen.alpha_0_38)
    val constantOfProportionality = maximumValue
        .minus(minimumValue)
        .div(layoutHeight)

    nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
        val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

        linearLayout.alpha = alpha.coerceAtLeast(minimumValue)
    }

    nestedScrollView2.setOnScrollChangeListener { _, _, scrollY, _, _ ->
        val alpha = maximumValue.minus(scrollY.times(constantOfProportionality))

        linearLayout2.alpha = alpha.coerceAtLeast(minimumValue)
    }
}

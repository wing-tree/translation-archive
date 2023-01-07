package com.wing.tree.bruni.inPlaceTranslate.extension

import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.wing.tree.bruni.core.extension.context
import com.wing.tree.bruni.inPlaceTranslate.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.R

internal fun ViewDataBinding.bannerAd(parent: ViewGroup, adSize: AdSize) {
    val adRequest = AdRequest.Builder().build()

    @StringRes
    val resId = if (BuildConfig.DEBUG) {
        R.string.sample_banner_ad_unit_id
    } else {
        R.string.banner_ad_unit_id
    }

    AdView(context).apply adView@ {
        adUnitId = context.getString(resId)
        adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()

                parent.removeView(this@adView)
                parent.addView(this@adView)
            }
        }

        setAdSize(adSize)
    }.loadAd(adRequest)
}

package com.wing.tree.bruni.inPlaceTranslate.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.wing.tree.bruni.inPlaceTranslate.BuildConfig
import com.wing.tree.bruni.inPlaceTranslate.R

class InterstitialAdLoaderImpl : InterstitialAdLoader {
    private var interstitialAd: InterstitialAd? = null

    override fun clearInterstitialAd() {
        interstitialAd = null
    }

    override fun loadInterstitialAd(
        context: Context,
        onAdFailedToLoad: ((LoadAdError?) -> Unit)?,
        onAdLoaded: ((InterstitialAd) -> Unit)?
    ) {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = context.getString(
            if (BuildConfig.DEBUG) {
                R.string.sample_interstitial_ad_unit_id
            } else {
                R.string.interstitial_ad_unit_id
            }
        )

        fun setInterstitialAd(interstitialAd: InterstitialAd) {
            this.interstitialAd = interstitialAd
        }

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                onAdFailedToLoad?.invoke(adError)
                clearInterstitialAd()
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                setInterstitialAd(interstitialAd)
                onAdLoaded?.invoke(interstitialAd)
            }
        })
    }

    override fun showInterstitialAd(
        activity: Activity,
        onAdClicked: (() -> Unit)?,
        onAdDismissedFullScreenContent: (() -> Unit)?,
        onAdFailedToShowFullScreenContent: ((adError: AdError) -> Unit)?,
        onAdImpression: (() -> Unit)?,
        onAdShowedFullScreenContent: (() -> Unit)?
    ) {
        val fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                onAdClicked?.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                onAdDismissedFullScreenContent?.invoke()
                clearInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                onAdFailedToShowFullScreenContent?.invoke(adError)
                clearInterstitialAd()
            }

            override fun onAdImpression() {
                onAdImpression?.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                onAdShowedFullScreenContent?.invoke()
            }
        }

        interstitialAd?.let {
            it.fullScreenContentCallback = fullScreenContentCallback
            it.show(activity)
        }
    }
}

package com.wing.tree.bruni.translator.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd

interface InterstitialAdLoader {
    fun clearInterstitialAd()

    fun loadInterstitialAd(
        context: Context,
        onAdFailedToLoad: ((LoadAdError?) -> Unit)? = null,
        onAdLoaded: ((InterstitialAd) -> Unit)? = null
    )

    fun showInterstitialAd(
        activity: Activity,
        onAdClicked: (() -> Unit)? = null,
        onAdDismissedFullScreenContent: (() -> Unit)? = null,
        onAdFailedToShowFullScreenContent: ((adError: AdError) -> Unit)? = null,
        onAdImpression: (() -> Unit)? = null,
        onAdShowedFullScreenContent: (() -> Unit)? = null,
    )
}

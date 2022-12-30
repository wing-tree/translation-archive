package com.wing.tree.bruni.inPlaceTranslate.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd

interface InterstitialAdLoader {
    fun clear()

    fun load(
        context: Context,
        onAdFailedToLoad: ((LoadAdError?) -> Unit)? = null,
        onAdLoaded: ((InterstitialAd) -> Unit)? = null
    )

    fun show(
        activity: Activity,
        onAdClicked: (() -> Unit)? = null,
        onAdDismissedFullScreenContent: (() -> Unit)? = null,
        onAdFailedToShowFullScreenContent: ((adError: AdError) -> Unit)? = null,
        onAdImpression: (() -> Unit)? = null,
        onAdShowedFullScreenContent: (() -> Unit)? = null,
    )
}

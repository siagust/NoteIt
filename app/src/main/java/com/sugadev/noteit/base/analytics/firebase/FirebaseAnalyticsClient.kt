package com.sugadev.noteit.base.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.sugadev.noteit.base.analytics.AnalyticsApi
import javax.inject.Inject

class FirebaseAnalyticsClient @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) :
    AnalyticsApi {
    override fun trackEvent(name: String, properties: Map<String, String>?) {
        val bundle = Bundle()
        properties?.forEach {
            bundle.putString(it.key, it.value)
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}
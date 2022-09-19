package com.sugadev.noteit.base.analytics

import com.sugadev.noteit.base.analytics.firebase.FirebaseAnalyticsClient
import javax.inject.Inject

class AnalyticsManager @Inject constructor(private val firebaseAnalyticsClient: FirebaseAnalyticsClient) :
    AnalyticsApi {
    override fun trackEvent(name: String, properties: Map<String, String>?) {
        firebaseAnalyticsClient.trackEvent(name, properties)
    }
}
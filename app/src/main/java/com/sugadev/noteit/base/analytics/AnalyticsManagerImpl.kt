package com.sugadev.noteit.base.analytics

import com.sugadev.noteit.base.analytics.firebase.FirebaseAnalyticsClient
import javax.inject.Inject

class AnalyticsManagerImpl @Inject constructor(private val firebaseAnalyticsClient: FirebaseAnalyticsClient) :
    AnalyticsManager {
    override fun trackEvent(name: String, properties: Map<String, String>?) {
        firebaseAnalyticsClient.trackEvent(name, properties)
    }
}
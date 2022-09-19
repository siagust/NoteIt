package com.sugadev.noteit.base.analytics

interface AnalyticsApi {
    fun trackEvent(name: String, properties: Map<String, String>?)
}

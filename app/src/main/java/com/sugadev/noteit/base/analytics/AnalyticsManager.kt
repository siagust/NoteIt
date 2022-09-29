package com.sugadev.noteit.base.analytics

interface AnalyticsManager {
    fun trackEvent(name: String, properties: Map<String, String>?)
}
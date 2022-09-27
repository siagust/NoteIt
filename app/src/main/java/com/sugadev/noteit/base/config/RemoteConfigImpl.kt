package com.sugadev.noteit.base.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sugadev.noteit.BuildConfig
import com.sugadev.noteit.R
import com.sugadev.noteit.base.config.model.AddNotesPlaceholder
import javax.inject.Inject


class RemoteConfigImpl @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val gson: Gson
) :
    RemoteConfig {
    companion object {
        const val ADD_NOTES_PLACEHOLDER = "FEATURE_ADD_NOTES_PLACEHOLDER"
    }

    init {
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        val configSettings = Builder()
            .setMinimumFetchIntervalInSeconds(
                if (BuildConfig.DEBUG) 10 else 3600
            )
            .build()

        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.fetchAndActivate()
    }

    override fun getAddNotesPlaceholder(): AddNotesPlaceholder {
        return (getJsonType(ADD_NOTES_PLACEHOLDER))
    }

    private fun get(key: String) = firebaseRemoteConfig.getString(key)

    private inline fun <reified T> getJsonType(key: String): T {
        return get(key).let {
            gson.fromJson(it, object : TypeToken<T>() {}.type)
        }
    }

}
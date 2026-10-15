package io.github.zensu357.camswap.utils

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {
    private const val KEY_LANGUAGE = "key_language"

    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String {
        val raw = getPersistedData(context)
        return if (raw == "ar" || raw == "en") raw else ""
    }

    fun setLocale(context: Context, language: String?): Context {
        val cleanLang = if (language == "ar" || language == "en") language else ""
        persist(context, cleanLang)
        if (cleanLang.isEmpty()) {
            val sysLang = Locale.getDefault().language
            val targetLocale = if (sysLang == "ar") Locale("ar") else Locale.ENGLISH
            return applyLocale(context, targetLocale)
        }
        val locale = if (cleanLang == "ar") Locale("ar") else Locale.ENGLISH
        return applyLocale(context, locale)
    }

    private fun applyLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        config.setLocales(localeList)
        return context.createConfigurationContext(config)
    }

    private fun getPersistedData(context: Context): String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(KEY_LANGUAGE, "") ?: ""
    }

    private fun persist(context: Context, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString(KEY_LANGUAGE, language ?: "").apply()
    }
}

package com.micomercio.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("mi_comercio")
class Prefs(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme_name")
    private val themeConfiguredKey = booleanPreferencesKey("theme_configured")
    private val soundsKey = booleanPreferencesKey("interface_sounds")
    private val haloKey = stringPreferencesKey("title_halo")
    private val emailKey = stringPreferencesKey("report_email")
    suspend fun clear() { context.dataStore.edit { it.clear() } }
    suspend fun theme(): String {
        val p=context.dataStore.data.first()
        return if(p[themeConfiguredKey]==true) p[themeKey] ?: "Ocean Glass" else "Ocean Glass"
    }
    suspend fun sounds(): Boolean = context.dataStore.data.first()[soundsKey] ?: true
    suspend fun saveTheme(value:String){context.dataStore.edit{it[themeKey]=value;it[themeConfiguredKey]=true}}
    suspend fun saveSounds(value:Boolean){context.dataStore.edit{it[soundsKey]=value}}
    suspend fun halo():String=context.dataStore.data.first()[haloKey] ?: "Verde Flúor"
    suspend fun saveHalo(value:String){context.dataStore.edit{it[haloKey]=value}}
    suspend fun email():String=context.dataStore.data.first()[emailKey].orEmpty()
    suspend fun saveEmail(value:String){context.dataStore.edit{it[emailKey]=value.trim()}}
}

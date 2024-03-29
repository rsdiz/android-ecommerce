package id.rsdiz.rdshop.base.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceHelper constructor(private val context: Context) {

    fun defaultPrefs(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun customPrefs(name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    object Ext {
        private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
            val editor = this.edit()
            operation(editor)
            editor.apply()
        }

        fun SharedPreferences.flush() = edit { it.clear() }

        /**
         * puts a value for the given [key].
         */
        operator fun SharedPreferences.set(key: String, value: Any?) = when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            is HashSet<*> -> edit {
                it.putStringSet(key, value as MutableSet<String>?)
            }
            else -> throw UnsupportedOperationException("SharedPreference.set() Not yet implemented")
        }

        /**
         * finds a preference based on the given [key].
         * [T] is the type of value
         * @param defaultValue optional defaultValue - will take a default defaultValue if it is not specified
         */
        inline operator fun <reified T : Any> SharedPreferences.get(
            key: String,
            defaultValue: T? = null
        ) = when (T::class) {
            String::class -> getString(key, defaultValue as? String ?: "") as T
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
            Set::class -> getStringSet(
                key,
                defaultValue as? Set<*> as MutableSet<String>?
            ) as T
            else -> throw UnsupportedOperationException("SharedPreference.get() Not yet implemented")
        }
    }
}

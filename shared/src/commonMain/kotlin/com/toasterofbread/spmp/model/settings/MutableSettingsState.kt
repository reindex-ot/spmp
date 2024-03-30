package com.toasterofbread.spmp.model.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toasterofbread.composekit.platform.PlatformPreferences
import com.toasterofbread.composekit.platform.PlatformPreferencesListener
import com.toasterofbread.composekit.utils.composable.OnChangedEffect

@Composable
fun <T> mutableSettingsState(settings_key: SettingsKey, prefs: PlatformPreferences = Settings.prefs): MutableState<T> {
    val state: MutableState<T> = remember { mutableStateOf(settings_key.get(prefs)) }
    var set_to: T by remember { mutableStateOf(state.value) }

    LaunchedEffect(state.value) {
        if (state.value != set_to) {
            set_to = state.value
            settings_key.set(set_to, prefs)
        }
    }

    OnChangedEffect(settings_key) {
        state.value = settings_key.get(prefs)
    }

    DisposableEffect(settings_key) {
        val listener = prefs.addListener(object : PlatformPreferencesListener {
            override fun onChanged(prefs: PlatformPreferences, key: String) {
                if (key == settings_key.getName()) {
                    set_to = settings_key.get(prefs)
                    state.value = set_to
                }
            }
        })

        onDispose {
            prefs.removeListener(listener)
        }
    }

    return state
}

@Composable
inline fun <reified T: Enum<T>> mutableSettingsEnumState(settings_key: SettingsKey, prefs: PlatformPreferences = Settings.prefs): MutableState<T> {
    val state: MutableState<T> = remember { mutableStateOf(
        enumValues<T>()[settings_key.get(prefs)]
    ) }
    var set_to: T by remember { mutableStateOf(state.value) }

    LaunchedEffect(state.value) {
        if (state.value != set_to) {
            set_to = state.value
            settings_key.set(set_to.ordinal, prefs)
        }
    }

    DisposableEffect(settings_key) {
        val listener = prefs.addListener(object : PlatformPreferencesListener {
            override fun onChanged(prefs: PlatformPreferences, key: String) {
                if (key == settings_key.getName()) {
                    set_to = enumValues<T>()[settings_key.get(prefs)]
                    state.value = set_to
                }
            }
        })

        onDispose {
            prefs.removeListener(listener)
        }
    }

    return state
}

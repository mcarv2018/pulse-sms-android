package xyz.klinker.messenger.shared.util

import android.os.Build

object AndroidVersionUtil {
    val isAndroidO: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || Build.VERSION.CODENAME == "O"

    val isAndroidN_MR1: Boolean
        get() =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
}
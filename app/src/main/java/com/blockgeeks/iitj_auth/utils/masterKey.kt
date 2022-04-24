package com.blockgeeks.iitj_auth.utils

import android.content.Context
import androidx.security.crypto.MasterKey

fun getMasterKey(context: Context): MasterKey {
    return MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
}
package com.gitsearch.injection.module

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
class AndroidModule {

    @Provides
    fun resources(context: Context): Resources = context.resources

    @Provides
    fun preferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

}
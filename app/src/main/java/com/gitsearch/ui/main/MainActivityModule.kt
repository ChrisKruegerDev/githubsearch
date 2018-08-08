package com.gitsearch.ui.main

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun activity(activity: MainActivity): Activity

    @ContributesAndroidInjector
    abstract fun searchResultFragment(): SearchResultFragment

}
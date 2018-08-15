package com.gitsearch.injection.module

import android.app.Application
import android.content.Context
import com.gitsearch.GitsearchApplication
import dagger.Binds
import dagger.Module

@Module(includes = [ViewModelModule::class])
abstract class ApplicationModule {

    @Binds
    abstract fun context(application: Application): Context

    @Binds
    abstract fun application(application: GitsearchApplication): Application

}

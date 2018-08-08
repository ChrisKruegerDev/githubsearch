package com.gitsearch.injection.component

import com.gitsearch.GitsearchApplication
import com.gitsearch.injection.module.*
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            ApplicationModule::class,
            AndroidModule::class,
            DataModule::class,
            ActivityBindingModule::class
        ]
)
interface AppComponent : AndroidInjector<GitsearchApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<GitsearchApplication>()

}

package com.gitsearch.injection.component;

import com.gitsearch.GitsearchApplication;
import com.gitsearch.injection.module.ActivityBindingModule;
import com.gitsearch.injection.module.ApplicationModule;
import com.gitsearch.injection.module.DataModule;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                AndroidInjectionModule.class,
                ApplicationModule.class,
                DataModule.class,
                ActivityBindingModule.class
        })
public interface AppComponent extends AndroidInjector<GitsearchApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<GitsearchApplication> {

    }

}

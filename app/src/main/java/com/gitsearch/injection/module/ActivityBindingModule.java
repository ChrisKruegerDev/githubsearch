package com.gitsearch.injection.module;

import com.gitsearch.ui.main.MainActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract MainActivity mainActivity();

}

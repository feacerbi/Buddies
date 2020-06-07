package com.buddies.navigation.di

import com.buddies.common.navigation.Navigator
import com.buddies.navigation.navigator.AppNavigator
import org.koin.dsl.module

val navigationModule = module {
    single<Navigator> { AppNavigator() }
}
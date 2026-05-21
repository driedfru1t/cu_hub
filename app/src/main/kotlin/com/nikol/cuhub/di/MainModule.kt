package com.nikol.cuhub.di

import com.nikol.network.di.modules.NetworkModule
import com.nikol.security.TokenManager
import com.nikol.security.di.TokenModule
import com.nikol.storage.di.module.StoragesModule
import dagger.Module

@Module(includes = [NetworkModule::class, StoragesModule::class, TokenModule::class])
class MainModule
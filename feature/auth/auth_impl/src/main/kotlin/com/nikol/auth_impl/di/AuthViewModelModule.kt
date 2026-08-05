package com.nikol.auth_impl.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nikol.auth_impl.viewModel.AuthViewModel
import com.nikol.viewmodel.DaggerViewModel
import com.nikol.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    fun bindCuAuthViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @AuthScope
    fun bindDaggerViewModel(daggerViewModel: DaggerViewModel): ViewModelProvider.Factory

}
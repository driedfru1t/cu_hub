package com.nikol.auth_impl.di

import androidx.lifecycle.ViewModelProvider
import com.nikol.di.dep.LocalLmsDep
import com.nikol.di.dep.NetworkCuDep
import com.nikol.di.dep.TokenDep
import com.nikol.lms.data.CourseRepositoryImpl
import com.nikol.lms.domain.repo.CourseRepository
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(
    dependencies = [TokenDep::class, NetworkCuDep::class, LocalLmsDep::class],
    modules = [AuthViewModelModule::class, bbb::class]
)
@AuthScope
interface AuthComponent {

    @Component.Factory
    interface Factory {
        fun create(
            tokenDep: TokenDep,
            networkCuDep: NetworkCuDep,
            localLmsDep: LocalLmsDep
        ): AuthComponent
    }

    fun viewModelFactory(): ViewModelProvider.Factory
}

@Module
interface bbb{
    @Binds
    fun provide(courseRepository: CourseRepositoryImpl) : CourseRepository
}
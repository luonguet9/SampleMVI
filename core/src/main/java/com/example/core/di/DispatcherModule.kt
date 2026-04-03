package com.example.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

// ─── Qualifier Annotations ────────────────────────────────────────────────────

/**
 * Qualifier for [Dispatchers.IO] — use for network and disk I/O operations.
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * class MyRepository @Inject constructor(
 *     @IoDispatcher private val ioDispatcher: CoroutineDispatcher
 * )
 * ```
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Qualifier for [Dispatchers.Main] — use for main-thread UI operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

/**
 * Qualifier for [Dispatchers.Default] — use for CPU-intensive work.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

// ─── Hilt Module ─────────────────────────────────────────────────────────────

/**
 * Hilt module that provides [CoroutineDispatcher] instances.
 *
 * Installed in [SingletonComponent] — dispatchers are app-scoped singletons.
 * This makes testing easy: inject a [kotlinx.coroutines.test.TestCoroutineDispatcher]
 * in unit tests via Hilt test bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

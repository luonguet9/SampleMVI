package com.example.core.util

import timber.log.Timber

/**
 * Centralized logging utility for MVI applications.
 *
 * Automatically builds a log prefix in the format: [ClassName::LineNumber]
 * by inspecting the current call stack — callers only need to pass a message.
 *
 * The TAG defaults to "App" but is overridable via [configure] so each app
 * can inject its own name (e.g., from BuildConfig.APPLICATION_ID):
 *
 * HOW TO USE IN NEW PROJECT:
 * 1. In Application.onCreate(), call:
 *    ```kotlin
 *    AppLogger.configure(tag = BuildConfig.APPLICATION_ID)
 *    ```
 * 2. Then use anywhere:
 *    ```kotlin
 *    AppLogger.d("Loaded 10 users")
 *    AppLogger.logIntent(intent)
 *    AppLogger.logError("Something went wrong", throwable)
 *    val result = AppLogger.timedSuspend("fetchUsers") { api.getUsers() }
 *    ```
 */
object AppLogger {

    private var appTag: String = "App"

    /**
     * Configure the log tag. Call this once in Application.onCreate().
     *
     * ```kotlin
     * AppLogger.configure(tag = BuildConfig.APPLICATION_ID)
     * ```
     */
    fun configure(tag: String) {
        appTag = tag
    }

    // ─── Tag builder ──────────────────────────────────────────────────────────

    /**
     * Walks the call stack and returns the first frame that belongs
     * to the caller (i.e. outside AppLogger, Thread, and VM internals).
     * Result: "[UserListViewModel::48]"
     */
    private fun buildPrefix(): String {
        val frame = Thread.currentThread().stackTrace.firstOrNull { element ->
            !element.className.contains("AppLogger") &&
                    !element.className.contains("java.lang.Thread") &&
                    !element.className.contains("dalvik.system.VMStack")
        }
        val className = frame?.className?.substringAfterLast('.') ?: "Unknown"
        val lineNumber = frame?.lineNumber ?: 0
        return "[$className::$lineNumber]"
    }

    // ─── Generic log levels ───────────────────────────────────────────────────

    fun d(message: String) = Timber.tag(appTag).d("${buildPrefix()} $message")
    fun i(message: String) = Timber.tag(appTag).i("${buildPrefix()} $message")
    fun w(message: String) = Timber.tag(appTag).w("${buildPrefix()} $message")
    fun e(message: String, throwable: Throwable? = null) =
        Timber.tag(appTag).e(throwable, "${buildPrefix()} $message")

    // ─── MVI / ViewModel layer ────────────────────────────────────────────────

    /** Log an intent being dispatched to the ViewModel. */
    fun logIntent(intent: Any) =
        Timber.tag(appTag).d("${buildPrefix()} ▶ Intent: ${intent::class.java.simpleName}")

    /** Log a State emission (prints the full state for easy inspection). */
    fun logState(state: Any) =
        Timber.tag(appTag).d("${buildPrefix()} ◉ State: $state")

    /** Log a one-shot Effect being sent from the ViewModel. */
    fun logEffect(effect: Any) =
        Timber.tag(appTag).d("${buildPrefix()} ⚡ Effect: ${effect::class.java.simpleName}")

    // ─── Repository / Data layer ──────────────────────────────────────────────

    /** Log when serving data from local cache. */
    fun logCache(message: String) =
        Timber.tag(appTag).d("${buildPrefix()} 💾 [CACHE] $message")

    /** Log when hitting the network. */
    fun logNetwork(message: String) =
        Timber.tag(appTag).d("${buildPrefix()} 🌐 [NETWORK] $message")

    /** Log a data / network error. */
    fun logError(message: String, throwable: Throwable? = null) =
        Timber.tag(appTag).e(throwable, "${buildPrefix()} ❌ [ERROR] $message")

    // ─── UseCase layer ────────────────────────────────────────────────────────

    /** Log UseCase invocation with optional params. */
    fun logUseCase(params: Any? = null) {
        val msg = if (params != null) "🔧 [UseCase] params=$params" else "🔧 [UseCase] execute"
        Timber.tag(appTag).d("${buildPrefix()} $msg")
    }

    // ─── UI / Fragment lifecycle ──────────────────────────────────────────────

    /** Log a Fragment/ViewModel lifecycle event. */
    fun logLifecycle(event: String) =
        Timber.tag(appTag).d("${buildPrefix()} 🔄 [Lifecycle] $event")

    // ─── Performance / Timing ────────────────────────────────────────────────

    /**
     * Measures the synchronous execution time of [block] and logs it.
     * ```kotlin
     * val users = AppLogger.timed("getUsers") { dao.getUsers() }
     * ```
     */
    fun <T> timed(operationName: String, block: () -> T): T {
        val prefix = buildPrefix()
        val start = System.currentTimeMillis()
        return try {
            block()
        } finally {
            val elapsed = System.currentTimeMillis() - start
            Timber.tag(appTag).d("$prefix ⏱ [$operationName] took ${elapsed}ms")
        }
    }

    /**
     * Measures the suspend execution time of [block] and logs it.
     * ```kotlin
     * val response = AppLogger.timedSuspend("fetchUsersApi") { api.getUsers() }
     * ```
     */
    suspend fun <T> timedSuspend(operationName: String, block: suspend () -> T): T {
        val prefix = buildPrefix()
        val start = System.currentTimeMillis()
        return try {
            block()
        } finally {
            val elapsed = System.currentTimeMillis() - start
            Timber.tag(appTag).d("$prefix ⏱ [$operationName] took ${elapsed}ms")
        }
    }
}

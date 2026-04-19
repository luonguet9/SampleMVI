package com.example.core.base.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.util.AppLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Generic base ViewModel for MVI architecture.
 *
 * Manages the MVI triad: Intent → State → Effect
 * - [state]: StateFlow that the UI observes and re-renders on every emission
 * - [effect]: One-shot Channel-backed Flow (navigation, toasts, dialogs)
 * - [processIntent]: entry point — all user actions go through here
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * @HiltViewModel
 * class LoginViewModel @Inject constructor(
 *     private val loginUseCase: LoginUseCase
 * ) : BaseViewModel<LoginIntent, LoginState, LoginEffect>(LoginState()) {
 *
 *     override fun handleIntent(intent: LoginIntent) {
 *         when (intent) {
 *             is LoginIntent.Submit -> login(intent.email, intent.password)
 *         }
 *     }
 * }
 * ```
 *
 * @param I Intent type — user actions dispatched to the ViewModel
 * @param S State type — immutable snapshot of UI state (must be a data class)
 * @param E Effect type — one-shot side effects (navigation, toast, etc.)
 */
abstract class BaseViewModel<I : MviIntent, S : MviState, E : MviEffect>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)

    /** Observable UI state. Collect in Fragment/Activity inside repeatOnLifecycle. */
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>(Channel.BUFFERED)

    /** One-shot side effects. Collect in Fragment/Activity inside repeatOnLifecycle. */
    val effect: Flow<E> = _effect.receiveAsFlow()

    /** Read-only snapshot of current state — use inside handleIntent() reducers. */
    protected val currentState: S get() = _state.value

    init {
        AppLogger.logLifecycle("init — ${this::class.java.simpleName} created")
        AppLogger.logState(initialState)
    }

    /**
     * Implement this to handle all intents for this screen.
     * Called by [processIntent] after logging.
     */
    abstract fun handleIntent(intent: I)

    /**
     * Public entry point for dispatching user intents.
     * Logs the intent and delegates to [handleIntent].
     */
    fun processIntent(intent: I) {
        AppLogger.logIntent(intent)
        handleIntent(intent)
    }

    /**
     * Applies a reducer function to the current state and emits the new state.
     *
     * Usage:
     * ```kotlin
     * updateState { copy(isLoading = true, error = null) }
     * ```
     */
    protected fun updateState(reducer: S.() -> S) {
        _state.update(reducer)
        AppLogger.logState(_state.value)
    }

    /**
     * Sends a one-shot effect to the UI layer.
     * The effect is buffered and delivered exactly once.
     */
    protected fun sendEffect(effect: E) {
        AppLogger.logEffect(effect)
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * Convenience extension: launches a Flow collection tied to viewModelScope.
     * Used to kick off FlowUseCase results inside [handleIntent].
     *
     * The flow is typically transformed with [kotlinx.coroutines.flow.onEach]
     * before calling launchIn(), so all side effects run there.
     *
     * Usage:
     * ```kotlin
     * getUsersUseCase(params)
     *     .onEach { result -> handleResult(result) }
     *     .launchIn()
     * ```
     */
    protected fun <T> Flow<T>.launchIn() = launchIn(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        AppLogger.logLifecycle("onCleared — ${this::class.java.simpleName} destroyed")
    }
}

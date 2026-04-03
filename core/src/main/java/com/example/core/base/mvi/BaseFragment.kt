package com.example.core.base.mvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.core.util.AppLogger
import kotlinx.coroutines.launch

/**
 * Generic base Fragment for MVI architecture with ViewBinding support.
 *
 * Automatically:
 * - Inflates ViewBinding and cleans up in onDestroyView (no leaks)
 * - Collects [BaseViewModel.state] and calls [renderState] on each emission
 * - Collects [BaseViewModel.effect] and calls [handleEffect] once per effect
 * - Logs all lifecycle events via [AppLogger]
 *
 * HOW TO USE IN NEW PROJECT:
 * ```kotlin
 * @AndroidEntryPoint
 * class LoginFragment : BaseFragment<
 *     FragmentLoginBinding,
 *     LoginIntent,
 *     LoginState,
 *     LoginEffect,
 *     LoginViewModel
 * >() {
 *     override val viewModel: LoginViewModel by viewModels()
 *
 *     override fun createBinding(inflater: LayoutInflater, container: ViewGroup?) =
 *         FragmentLoginBinding.inflate(inflater, container, false)
 *
 *     override fun setupViews(savedInstanceState: Bundle?) {
 *         binding.btnLogin.setOnClickListener {
 *             viewModel.processIntent(LoginIntent.Submit(email, password))
 *         }
 *     }
 *
 *     override fun renderState(state: LoginState) {
 *         binding.progressBar.isVisible = state.isLoading
 *     }
 *
 *     override fun handleEffect(effect: LoginEffect) {
 *         when (effect) {
 *             is LoginEffect.NavigateToHome -> findNavController().navigate(R.id.action_to_home)
 *             is LoginEffect.ShowError -> Toast.makeText(requireContext(), effect.message, LENGTH_SHORT).show()
 *         }
 *     }
 * }
 * ```
 *
 * @param VB ViewBinding type
 * @param I Intent type
 * @param S State type
 * @param E Effect type
 * @param VM ViewModel type
 */
abstract class BaseFragment<
        VB : ViewBinding,
        I : MviIntent,
        S : MviState,
        E : MviEffect,
        VM : BaseViewModel<I, S, E>
        > : Fragment() {

    private var _binding: VB? = null

    /** Safe binding accessor — only valid between [onCreateView] and [onDestroyView]. */
    protected val binding get() = _binding!!

    /** Inject via `by viewModels()` or `by activityViewModels()`. */
    protected abstract val viewModel: VM

    /** Inflate and return your ViewBinding here. */
    protected abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppLogger.logLifecycle("onCreateView — ${this.javaClass.simpleName}")
        _binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppLogger.logLifecycle("onViewCreated — ${this.javaClass.simpleName}")
        setupViews(savedInstanceState)
        observeState()
        observeEffects()
    }

    /** Set up click listeners, adapters, and other view configuration here. */
    protected abstract fun setupViews(savedInstanceState: Bundle?)

    /** Called on every state emission — update UI to reflect the new state. */
    protected abstract fun renderState(state: S)

    /** Called once per effect emission — handle navigation, dialogs, snackbars etc. */
    protected abstract fun handleEffect(effect: E)

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    AppLogger.logState(state)
                    renderState(state)
                }
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    AppLogger.logEffect(effect)
                    handleEffect(effect)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        AppLogger.logLifecycle("onDestroyView — ${this.javaClass.simpleName}")
        _binding = null
    }
}

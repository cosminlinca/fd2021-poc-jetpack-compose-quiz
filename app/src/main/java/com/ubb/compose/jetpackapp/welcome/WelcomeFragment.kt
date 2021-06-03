package com.ubb.compose.jetpackapp.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ubb.compose.jetpackapp.Screen
import com.ubb.compose.jetpackapp.navigate
import com.ubb.compose.jetpackapp.theme.JetpackCustomTheme

/**
 * Fragment containing the welcome UI.
 */
class WelcomeFragment : Fragment() {

    private val viewModel: WelcomeViewModel by viewModels { WelcomeViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.Welcome)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                JetpackCustomTheme {
                    WelcomeScreen(
                        onEvent = { event ->
                            when (event) {
                                WelcomeEvent.SignInAsGuest -> viewModel.signInAsGuest()
                                is WelcomeEvent.SignInSignUp -> TODO()
                            }
                        }
                    )
                }
            }
        }
    }
}

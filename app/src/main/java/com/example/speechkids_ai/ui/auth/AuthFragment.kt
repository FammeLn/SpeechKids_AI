package com.example.speechkids_ai.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.speechkids_ai.R
import com.example.speechkids_ai.model.AuthState
import com.example.speechkids_ai.model.UserRole
import com.example.speechkids_ai.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputLayout

class AuthFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private var isLoginMode = true

    private lateinit var titleText: TextView
    private lateinit var emailInput: EditText
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var toggleModeButton: Button
    private lateinit var errorText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupUI()
        setupObservers()
    }

    private fun initializeViews(view: View) {
        titleText = view.findViewById(R.id.titleText)
        emailInput = view.findViewById(R.id.emailInput)
        nameInputLayout = view.findViewById(R.id.nameInputLayout)
        nameInput = view.findViewById(R.id.nameInput)
        passwordInput = view.findViewById(R.id.passwordInput)
        confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout)
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput)
        loginButton = view.findViewById(R.id.loginButton)
        toggleModeButton = view.findViewById(R.id.toggleModeButton)
        errorText = view.findViewById(R.id.errorText)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupUI() {
        val themeToggleButton = requireView().findViewById<Button>(R.id.themeToggleButton)
        val isNightMode = androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode() == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        themeToggleButton.text = if (isNightMode) "☀️" else "🌙"

        themeToggleButton.setOnClickListener {
            val nextMode = if (androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode() == androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES) {
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            } else {
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            }
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(nextMode)
        }

        toggleModeButton.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        loginButton.setOnClickListener {
            if (isLoginMode) {
                performLogin()
            } else {
                performRegister()
            }
        }

        updateUI()
    }

    private fun updateUI() {
        if (isLoginMode) {
            titleText.text = "Вход"
            nameInputLayout.visibility = View.GONE
            confirmPasswordInputLayout.visibility = View.GONE
            loginButton.text = "Войти"
            toggleModeButton.text = "Нет аккаунта? Зарегистрироваться"
        } else {
            titleText.text = "Регистрация"
            nameInputLayout.visibility = View.VISIBLE
            confirmPasswordInputLayout.visibility = View.VISIBLE
            loginButton.text = "Зарегистрироваться"
            toggleModeButton.text = "Уже есть аккаунт? Войти"
        }
    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        viewModel.login(email, password)
    }

    private fun performRegister() {
        val email = emailInput.text.toString().trim()
        val name = nameInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()
        viewModel.register(email, name, password, confirmPassword, UserRole.PARENT)
    }

    private fun setupObservers() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AuthState.LOADING -> {
                    loginButton.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }
                AuthState.AUTHENTICATED -> {
                    loginButton.isEnabled = true
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Успешно!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_auth_to_role_selection)
                }
                AuthState.ERROR -> {
                    loginButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }
                else -> {
                    loginButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            }
        }

        viewModel.loginError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                errorText.visibility = View.VISIBLE
                errorText.text = error
            } else {
                errorText.visibility = View.GONE
            }
        }
    }
}





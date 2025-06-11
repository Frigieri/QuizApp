package com.example.quizapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Use activityViewModels() se o ViewModel for compartilhado
import androidx.navigation.fragment.findNavController // Import para navegação
import com.example.quizapp.R // Importe seu R
import com.example.quizapp.data.model.AuthResult
import com.example.quizapp.databinding.FragmentLoginBinding // Importe seu ViewBinding gerado
import dagger.hilt.android.AndroidEntryPoint // Se usar Hilt

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewGoToRegister.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    binding.progressBarLogin.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }
                is AuthResult.Success -> {
                    binding.progressBarLogin.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Login bem-sucedido!", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_loginFragment_to_themeListFragment)
                }
                is AuthResult.Error -> {
                    binding.progressBarLogin.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Erro: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


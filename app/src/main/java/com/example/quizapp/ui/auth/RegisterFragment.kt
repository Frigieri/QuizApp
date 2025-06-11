package com.example.quizapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R // Importe seu R
import com.example.quizapp.data.model.AuthResult
import com.example.quizapp.databinding.FragmentRegisterBinding // Importe seu ViewBinding gerado
import dagger.hilt.android.AndroidEntryPoint // Se usar Hilt

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Use o mesmo AuthViewModel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()


            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password.length >= 6) {
                    viewModel.register(email, password)

                } else {
                    Toast.makeText(requireContext(), "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Preencha email e senha", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewGoToLogin.setOnClickListener {

            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.authResult.observe(viewLifecycleOwner) { result ->

            when (result) {
                is AuthResult.Loading -> {
                    binding.progressBarRegister.isVisible = true
                    binding.buttonRegister.isEnabled = false
                }
                is AuthResult.Success -> {
                    binding.progressBarRegister.isVisible = false
                    binding.buttonRegister.isEnabled = true
                    Toast.makeText(requireContext(), "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_registerFragment_to_themeListFragment)
                }
                is AuthResult.Error -> {
                    binding.progressBarRegister.isVisible = false
                    binding.buttonRegister.isEnabled = true
                    Toast.makeText(requireContext(), "Erro no registro: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


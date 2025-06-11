package com.example.quizapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.databinding.FragmentProfileBinding
import com.example.quizapp.util.DataResult
import com.example.quizapp.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth // Importar FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser


        binding.textViewUserId.text = "ID do Usuário: ${currentUser?.uid ?: "Não disponível"}"


        binding.textViewUserEmail.text = "Email: ${currentUser?.email ?: "Não disponível"}"

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {


        viewModel.logoutResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataResult.Success -> {
                    Toast.makeText(context, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                }
                is DataResult.Error -> {
                    Toast.makeText(context, "Erro ao deslogar: ${it.exception.message}", Toast.LENGTH_LONG).show()
                }
                is DataResult.Loading -> {

                }
            }
        })
    }

    private fun setupListeners() {
        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
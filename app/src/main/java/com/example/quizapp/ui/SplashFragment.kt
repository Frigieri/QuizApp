package com.example.quizapp.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
// import com.example.quizapp.databinding.FragmentSplashBinding // REMOVA ESTA LINHA
import com.google.firebase.auth.FirebaseAuth


class SplashFragment : Fragment() {


    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 1500)
    }

    private fun checkUserStatus() {
        if (!isAdded) return

        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {

                findNavController().navigate(R.id.action_splashFragment_to_themeListFragment)
            } else {

                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        } catch (e: Exception) {

            try {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            } catch (e2: Exception) {

                e2.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}

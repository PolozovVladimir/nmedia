package ru.netology.nmedia.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.viewmodel.RegistrationViewModel

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerUserBtn.setOnClickListener {
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val name = binding.nameEditText.text.toString()

            if (login.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                viewModel.registerUser(login, password, name)
            } else {
                Snackbar.make(binding.root, "Заполните все поля", Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.tokenReceived.observe(viewLifecycleOwner) {
            if (it == 0) {
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, "Ошибка регистрации", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
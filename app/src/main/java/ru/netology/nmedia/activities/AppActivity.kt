package ru.netology.nmedia.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activities.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    @Inject
    lateinit var appAuth: AppAuth

    val viewModel: AuthViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    private var wasAuthorized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        var currentMenuProvider: MenuProvider? = null
        viewModel.data.observe(this) {
            val isAuthorized = viewModel.authorized

            currentMenuProvider?.also { removeMenuProvider(it) }
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                    menu.setGroupVisible(R.id.authorized, isAuthorized)
                    menu.setGroupVisible(R.id.unAuthorized, !isAuthorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean  =
                    when (menuItem.itemId) {
                        R.id.signIn -> {
                            appAuth.setAuth(1, "local_token")
                            true
                        }
                        R.id.signUp -> {
                            appAuth.setAuth(1, "local_token")
                            true
                        }
                        R.id.logout -> {
                            showSignOutDialog()
                            true
                        }
                        else -> false
                    }
            }.apply {
                currentMenuProvider = this
            })
        }
    }

    private fun showSignOutDialog(){
        val listener = DialogInterface.OnClickListener{ _, which->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    appAuth.removeAuth()
                }
                DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
        val dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Confirmation")
            .setMessage("Log out?")
            .setPositiveButton("Yes", listener)
            .setNegativeButton("No", listener)
            .create()

        dialog.show()
    }
}
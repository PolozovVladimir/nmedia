package ru.netology.nmedia.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
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

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    val viewModel: AuthViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    private var wasAuthorized: Boolean = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(
                this,
                "Уведомления будут недоступны",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        handleIntent(intent)

        checkGoogleApiAvailability()

        var currentMenuProvider: MenuProvider? = null
        viewModel.data.observe(this) {
            val isAuthorized = viewModel.authorized

            if (wasAuthorized != isAuthorized) {
                wasAuthorized = isAuthorized
            }

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
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_signInFragment)
                            true
                        }
                        R.id.signUp -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_registrationFragment)
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

        wasAuthorized = viewModel.authorized
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
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
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showSignOutDialog() {
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    appAuth.removeAuth()
                }
                DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(
                    this,
                    "ну и ладненько...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Внимание!")
            .setMessage("Вы точно хотите выйти?")
            .setPositiveButton("Уверен!", listener)
            .setNegativeButton("Нет", listener)
            .create()

        dialog.show()
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(
                this@AppActivity,
                R.string.google_play_unavailable,
                Toast.LENGTH_LONG
            ).show()
        }

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }
}
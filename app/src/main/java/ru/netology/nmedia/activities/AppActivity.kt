package ru.netology.nmedia.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import ru.netology.nmedia.R

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let { originalIntent ->
            if (originalIntent.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = originalIntent.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            intent.action = ""

            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    putString(NewPostFragment.TEXT_ARG, text)
                }
            )
        }
    }
}
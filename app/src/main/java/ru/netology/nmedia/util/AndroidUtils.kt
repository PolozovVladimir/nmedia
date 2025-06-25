package ru.netology.nmedia.util

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

object AndroidUtils {
    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService<InputMethodManager>()
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
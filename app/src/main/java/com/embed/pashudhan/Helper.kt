package com.embed.pashudhan

import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.util.*

class Helper : AppCompatActivity() {

    val SUCCESS_STATE = 1
    val ERROR_STATE = 2
    val WARNING_STATE = 3
    val INFO_STATE = 4
    val DEFAULT_STATE = 5

    fun showSnackbar(
        ctx: Context,
        view: View,
        message: String,
        state: Int? = null,
        actionText: String? = null,
        action: (() -> Unit)? = null,
        actionColor: Int? = null
    ) {
        var mSnackbar =
            Snackbar.make(ctx, view, message, Snackbar.LENGTH_SHORT)
        mSnackbar.setAction(actionText, View.OnClickListener {
            action?.invoke()
        })

        when (state) {
            SUCCESS_STATE -> {
                mSnackbar.setBackgroundTint(ContextCompat.getColor(ctx, R.color.success))
            }
            ERROR_STATE -> {
                mSnackbar.setBackgroundTint(ContextCompat.getColor(ctx, R.color.error))
            }
            WARNING_STATE -> {
                mSnackbar.setBackgroundTint(ContextCompat.getColor(ctx, R.color.warning))
            }
            INFO_STATE -> {
                mSnackbar.setBackgroundTint(ContextCompat.getColor(ctx, R.color.info))
            }
            DEFAULT_STATE -> {
            }
        }

        if (actionColor != null) {
            mSnackbar.setActionTextColor(ContextCompat.getColor(ctx, actionColor))
        }
        mSnackbar.show()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun changeAppLanguage(ctx: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = ctx.resources.configuration
        config.setLocale(locale)
        ctx.createConfigurationContext(config)
        ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
    }

}
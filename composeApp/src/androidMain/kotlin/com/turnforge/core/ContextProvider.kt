package com.turnforge.core

import android.content.Context
import java.lang.ref.WeakReference

object ContextProvider {
    private var contextRef: WeakReference<Context>? = null

    fun initialize(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }

    fun getContext(): Context {
        return contextRef?.get() ?: throw IllegalStateException("ContextProvider not initialized")
    }
}

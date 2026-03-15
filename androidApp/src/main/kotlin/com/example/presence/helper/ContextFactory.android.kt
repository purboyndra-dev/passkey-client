package com.example.presence.helper

import android.content.Context

class AndroidContextFactory(private val ctx: Context) : ContextFactory {
    override val context: Any
        get() = ctx
}
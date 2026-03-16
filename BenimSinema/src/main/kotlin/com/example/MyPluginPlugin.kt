package com.example

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class MyPluginPlugin: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(MyPlugin()) // MyPlugin.kt'deki sınıf isminle aynı olmalı
    }
}

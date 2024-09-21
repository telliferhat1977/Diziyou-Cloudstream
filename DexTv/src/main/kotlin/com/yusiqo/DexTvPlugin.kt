package com.yusiqo

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class DexTvPlugin: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(DexTv())
    }
}

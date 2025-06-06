package com.crossBoard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.ktor.client.engine.okhttp.*
import com.crossBoard.utils.createHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidAppContext.initialize(this)
        val host = getHost()
        val settings = getSettings()
        setContent {
            App(
                client = ApiClient(createHttpClient(OkHttp.create()), host),
                settings
            )
        }
    }
}
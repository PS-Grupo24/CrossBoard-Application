package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import io.ktor.client.engine.okhttp.*
import org.example.project.utils.createHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val host = getHost()
        setContent {
            App(
                client = remember {
                    ApiClient(createHttpClient(OkHttp.create()), host)
                }
            )
        }
    }
}

/**
@Preview
@Composable
fun AppAndroidPreview() {
    App()
}*/
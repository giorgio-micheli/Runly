package io.github.giorgiomicheli.runly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import io.github.giorgiomicheli.auth.presentation.register.RegisterScreen
import io.github.giorgiomicheli.auth.presentation.register.RegisterScreenRoot
import io.github.giorgiomicheli.auth.presentation.register.RegisterState
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunlyTheme {
                RegisterScreen(
                    state = RegisterState(),
                    onAction = {}
                )
            }
        }
    }
}
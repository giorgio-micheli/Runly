package io.github.giorgiomicheli.auth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.giorgiomicheli.core.presentation.designsystem.HeroTitleStyle
import io.github.giorgiomicheli.core.presentation.designsystem.Spacing
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyLanesBackground
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthLayout(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    RunlyLanesBackground(modifier = modifier) {
        RunlyToolbar(
            showBackButton = true,
            title = "",
            onBackClick = onBackClick,
            windowInsets = WindowInsets(0),
            modifier = Modifier.padding(top = Spacing.space3)
        )

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = Spacing.space4)
                    .padding(top = Spacing.space4, bottom = Spacing.space8),
                verticalArrangement = Arrangement.spacedBy(Spacing.space4)
            ) {
                AuthHeader(
                    title = title,
                    subtitle = subtitle,
                    modifier = Modifier.padding(bottom = Spacing.space4)
                )

                content()

                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    footer()
                }
            }
        }
    }
}

@Composable
fun AuthHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.space2)
    ) {
        Text(
            text = title,
            style = HeroTitleStyle,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
package io.github.giorgiomicheli.auth.presentation.intro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.giorgiomicheli.core.presentation.designsystem.Black
import io.github.giorgiomicheli.core.presentation.designsystem.Clay
import io.github.giorgiomicheli.core.presentation.designsystem.HeroTitleStyle
import io.github.giorgiomicheli.core.presentation.designsystem.LogoMonoIcon
import io.github.giorgiomicheli.core.presentation.designsystem.Radius
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme
import io.github.giorgiomicheli.core.presentation.designsystem.Spacing
import io.github.giorgiomicheli.core.presentation.designsystem.WordmarkStyle
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyBackground
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyActionButton
import io.github.giorgiomicheli.core.presentation.designsystem.components.RunlyOutlinedActionButton
import io.github.giorgiomicheli.core.presentation.ui.R

@Composable
fun IntroScreenRoot(
    onCreateAccount: () -> Unit,
    onSignInClick: () -> Unit
) {
    IntroScreen(
        onAction = { action ->
            when(action) {
                IntroAction.OnSignInClick -> onSignInClick()
                IntroAction.OnSignUpClick -> onCreateAccount()
            }
        }
    )
}

@Composable
fun IntroScreen(
    onAction: (IntroAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        IntroHeroCard(
            modifier = Modifier
                .padding(start = Spacing.space4, end = Spacing.space4, top = Spacing.space4)
                .fillMaxWidth()
                .height(430.dp)
        )

        // Testi e pulsanti, spinti verso il basso
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = Spacing.space4)
                .padding(top = Spacing.space6, bottom = Spacing.space8),
            verticalArrangement = Arrangement.spacedBy(Spacing.space3, Alignment.Bottom)
        ) {
            Text(
                text = stringResource(id = R.string.welcome_to_runly),
                style = HeroTitleStyle,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.runly_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // 12dp in più prima dei pulsanti, oltre ai 12dp di spacedBy
                modifier = Modifier.padding(bottom = Spacing.space3)
            )
            RunlyActionButton(
                text = stringResource(id = R.string.create_account),
                isLoading = false,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAction(IntroAction.OnSignUpClick) }
            )
            RunlyOutlinedActionButton(
                text = stringResource(id = R.string.sign_in),
                isLoading = false,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAction(IntroAction.OnSignInClick) }
            )
        }
    }
}

@Composable
private fun RunlyLogoHorizontal(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.space2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = LogoMonoIcon,
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(id = R.string.runly).uppercase(),
            style = WordmarkStyle,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun IntroHeroCard(
    modifier: Modifier = Modifier,
    accent: Color = Clay,
    onAccent: Color = Black
) {
    val ringColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            // clip PRIMA di background e Canvas: così i cerchi non escono dagli angoli
            .clip(RoundedCornerShape(Radius.xl))
            .background(accent)
    ) {
        // Cerchi: centro sull'angolo in alto a destra, ne resta visibile un quarto
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(x = size.width, y = 0f)
            val stroke = Stroke(width = 4.dp.toPx())
            listOf(64.dp, 96.dp, 128.dp, 160.dp, 192.dp).forEach { radius ->
                drawCircle(
                    color = ringColor,
                    radius = radius.toPx(),
                    center = center,
                    style = stroke
                )
            }
        }

        // Logo nella tessera + nome
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(Spacing.space5),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(onAccent, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = LogoMonoIcon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = stringResource(id = R.string.runly).uppercase(),
                style = WordmarkStyle,
                color = onAccent
            )
        }

        // Metrica in basso
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Spacing.space5),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(id = R.string.intro_avg_pace).uppercase(),
                style = MaterialTheme.typography.labelSmall, // overline
                color = onAccent
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "5:08",
                    style = MaterialTheme.typography.displayLarge, // metric-xl
                    color = onAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/km",
                    style = MaterialTheme.typography.labelLarge,
                    color = onAccent,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = Spacing.space1, bottom = 10.dp)
                )
            }
        }
    }
}




@Preview
@Composable
private fun IntroScreenPreview() {
    RunlyTheme {
        IntroScreen(
            onAction = {}
        )
    }
}
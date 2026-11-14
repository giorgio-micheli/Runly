package io.github.giorgiomicheli.core.presentation.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import io.github.giorgiomicheli.core.presentation.designsystem.RunlyTheme

@Composable
fun RunlyTextWithLink(
    text: String,
    linkText: String,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val linkColor = MaterialTheme.colorScheme.onPrimaryContainer // brand-text

    val annotatedString = buildAnnotatedString {
        append("$text ")
        withLink(
            LinkAnnotation.Clickable(
                tag = "link",
                styles = TextLinkStyles(
                    style = SpanStyle(fontWeight = FontWeight.SemiBold, color = linkColor)
                ),
                linkInteractionListener = { onLinkClick() }
            )
        ) {
            append(linkText)
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
private fun RunlyTextWithLinkPreview() {
    RunlyTheme {
        RunlyTextWithLink(text = "New to Runly?", linkText = "Create account", onLinkClick = {})
    }
}
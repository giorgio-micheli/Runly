package io.github.giorgiomicheli.core.presentation.designsystem

import androidx.compose.ui.unit.dp

/*
    Runly spacing and size tokens. Use these instead of literal dp values:
    Modifier.padding(horizontal = Spacing.space4)
 */
object Spacing {
    /** 4dp: between a metric's label and its value. */
    val space1 = 4.dp

    /** 8dp: between an icon and its text; rows of a compact list. */
    val space2 = 8.dp

    /** 12dp: inner padding of a RunCard; between two side-by-side buttons. */
    val space3 = 12.dp

    /** 16dp: side margin of every screen; card padding; between form fields. */
    val space4 = 16.dp

    /** 20dp: horizontal padding of buttons and sheets. */
    val space5 = 20.dp

    /** 24dp: dialog padding; between sections of a screen. */
    val space6 = 24.dp

    /** 32dp: between run controls; before a form's main button. */
    val space8 = 32.dp

    /** 48dp: breathing room above the headline of the auth screens. */
    val space12 = 48.dp
}

object Radius {
    /** Small buttons, inner tiles. */
    val sm = 8.dp

    /** Buttons, text fields, map thumbnails. */
    val md = 14.dp

    /** Cards. */
    val lg = 20.dp

    /** Dialogs and the top corners of sheets. */
    val xl = 28.dp
}

object Sizes {
    /** Standard icon size. */
    val iconMd = 24.dp

    /** Icons inside badges and labels. */
    val iconSm = 20.dp

    /** Minimum touch target of every control. */
    val touchMin = 48.dp

    /** Button height. */
    val controlHeight = 52.dp

    /** Text field height. */
    val fieldHeight = 56.dp

    /** RecordButton diameter on the Active run screen. */
    val record = 80.dp

    /** Run disc beside the floating NavBar. */
    val navRecord = 64.dp

    /** Height of the floating NavBar capsule. */
    val navBar = 64.dp
}

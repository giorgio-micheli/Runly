package io.github.giorgiomicheli.core.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

/*
    Utility global properties used to represent an icon in the res/drawable folder
 */
/*
    Runly icon set: one property per vector drawable in res/drawable (ic_*.xml).

    The drawables are black strokes on a 24dp grid (Tabler Icons, MIT): the colour
    always comes from the tint, e.g. Icon(imageVector = RunIcon, tint = ...).
    PlayIcon, PauseIcon and StopIcon are the only filled ones.
 */

// region Logo
val LogoIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.runly_logo)

val LogoMonoIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.runly_logo_mono)
// endregion

// region Icons
val AlertIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_alert)

val ArrowLeftIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_arrow_left)

val ArrowRightIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_arrow_right)

val BoltIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_bolt)

val CalendarIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_calendar)

val ChartIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_chart)

val CheckIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_check)

val ChevronDownIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_chevron_down)

val ChevronRightIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_chevron_right)

val ChevronUpIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_chevron_up)

val ClockIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_clock)

val CloseIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_close)

val CloudCheckIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_cloud_check)

val CloudOffIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_cloud_off)

val ExpandIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_expand)

val EyeIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_eye)

val EyeOffIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_eye_off)

val FlagIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_flag)

val FlameIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_flame)

val GaugeIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_gauge)

val HomeIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_home)

val LayersIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_layers)

val ListIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_list)

val LocateIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_locate)

val LocationOffIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_location_off)

val LockIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_lock)

val LogoutIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_logout)

val MailIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_mail)

val MapIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_map)

val MoreIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_more)

val MountainIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_mountain)

val PauseIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_pause)

val PinIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_pin)

val PlayIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_play)

val RouteIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_route)

val RulerIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_ruler)

val RunIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_run)

val ShareIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_share)

val ShoeIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_shoe)

val StopIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_stop)

val StopwatchIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_stopwatch)

val TrashIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_trash)

val TrendIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_trend)

val TrophyIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_trophy)

val UserIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_user)
// endregion

// region Names used by the screens before the redesign.
// They point at the new icons so existing code keeps compiling; rename the call sites when convenient.
val AnalyticsIcon: ImageVector
    @Composable
    get() = ChartIcon

val CrossIcon: ImageVector
    @Composable
    get() = CloseIcon

val EmailIcon: ImageVector
    @Composable
    get() = MailIcon

val ExclamationMarkIcon: ImageVector
    @Composable
    get() = AlertIcon

val EyeClosedIcon: ImageVector
    @Composable
    get() = EyeOffIcon

val EyeOpenedIcon: ImageVector
    @Composable
    get() = EyeIcon

val FinishIcon: ImageVector
    @Composable
    get() = FlagIcon

val KeyboardArrowDownIcon: ImageVector
    @Composable
    get() = ChevronDownIcon

val KeyboardArrowUpIcon: ImageVector
    @Composable
    get() = ChevronUpIcon

val LocationIcon: ImageVector
    @Composable
    get() = PinIcon

val PersonIcon: ImageVector
    @Composable
    get() = UserIcon

val RunOutlinedIcon: ImageVector
    @Composable
    get() = RunIcon

val StartIcon: ImageVector
    @Composable
    get() = PlayIcon
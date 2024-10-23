package sk.skillmea.kandrac.compose.coolswitch

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class CoolSwitchType(
    @DrawableRes val handleOn: Int,
    @DrawableRes val decorationOn: Int,
    val backgroundOnTop: Color,
    val backgroundOnBottom: Color,
    @DrawableRes val handleOff: Int,
    @DrawableRes val decorationOff: Int,
    val backgroundOffTop: Color,
    val backgroundOffBottom: Color,
    @FloatRange(0.0, 1.0) val decorationScale: Float,
    @FloatRange(0.0, 1.0) val decorationOffsetHorizontalPercent: Float,
    @FloatRange(0.0, 1.0) val decorationOffsetVerticalPercent: Float
) {
    DAY_NIGHT(
        handleOn = R.drawable.day_night_on,
        decorationOn = R.drawable.clouds,
        backgroundOnTop = Color(0xFF66FFED),
        backgroundOnBottom = Color(0xFFFFEEB2),
        handleOff = R.drawable.day_night_off,
        decorationOff = R.drawable.stars,
        backgroundOffTop = Color(0xFF2B4485),
        backgroundOffBottom = Color(0xFFAFCAFF),
        decorationScale = 0.6f,
        decorationOffsetHorizontalPercent = 0.5f,
        decorationOffsetVerticalPercent = 0.3f
    ),
    ORANGE(
        handleOn = R.drawable.orange_on,
        decorationOn = R.drawable.orange_decoration_on,
        backgroundOnTop = Color(0xFFFFD954),
        backgroundOnBottom = Color(0xFFFF9736),
        handleOff = R.drawable.orange_off,
        decorationOff = R.drawable.orange_decoration_off,
        backgroundOffTop = Color(0xFFFFA336),
        backgroundOffBottom = Color(0xFFFFE24B),
        decorationScale = 0.3f,
        decorationOffsetHorizontalPercent = 0.1f,
        decorationOffsetVerticalPercent = 0.3f
    )
}

@Composable
fun CoolSwitch(
    modifier: Modifier = Modifier,
    handlePadding: PaddingValues = PaddingValues(8.dp),
    type: CoolSwitchType = CoolSwitchType.DAY_NIGHT,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val paddingStart: Float
    val paddingEnd: Float
    val paddingTop: Float
    val paddingBottom: Float

    with(density) {
        paddingStart = handlePadding.calculateStartPadding(layoutDirection).toPx()
        paddingEnd = handlePadding.calculateEndPadding(layoutDirection).toPx()
        paddingTop = handlePadding.calculateTopPadding().toPx()
        paddingBottom = handlePadding.calculateBottomPadding().toPx()
    }

    val onHandle = ImageBitmap.imageResource(type.handleOn)
    val offHandle = ImageBitmap.imageResource(type.handleOff)
    val onBgImage = ImageBitmap.imageResource(type.decorationOn)
    val offBgImage = ImageBitmap.imageResource(type.decorationOff)

    val topBgColor = animateColorAsState(if (checked) type.backgroundOnTop else type.backgroundOffTop, label = "top color")
    val bottomBgColor = animateColorAsState(if (checked) type.backgroundOnBottom else type.backgroundOffBottom, label = "bottom color")
    val animated = animateFloatAsState(if (checked) 1f else 0f, label = "progress")

    Canvas(
        modifier = modifier
            .innerShadow(
                shape = CircleShape, color = Color(0xAA000000),
                offsetY = 4.dp, offsetX = 2.dp
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {

        // handle positioning and sizing
        val handleSize = size.height - paddingTop - paddingBottom
        val startPosition = paddingStart
        val endPosition = size.width - paddingEnd - handleSize
        val actualPosition = animated.value * (endPosition - startPosition) + startPosition

        // background
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(topBgColor.value, bottomBgColor.value)),
            cornerRadius = CornerRadius(size.height / 2)
        )

        // on/off handles
        drawImage(
            onHandle,
            dstSize = IntSize(handleSize.toInt(), handleSize.toInt()),
            dstOffset = IntOffset(actualPosition.toInt(), paddingTop.toInt()),
            alpha = animated.value
        )

        drawImage(
            offHandle,
            dstSize = IntSize(handleSize.toInt(), handleSize.toInt()),
            dstOffset = IntOffset(actualPosition.toInt(), paddingTop.toInt()),
            alpha = 1f - animated.value
        )

        // on/off decoration
        run {
            val downscaled = size.height * type.decorationScale / offBgImage.width
            val remainingWidth = size.width - handleSize - offBgImage.width * downscaled - paddingStart - paddingEnd
            val remainingHeight = size.height - offBgImage.height * downscaled

            drawImage(
                offBgImage,
                dstSize = IntSize(
                    (onBgImage.width * downscaled).toInt(),
                    (onBgImage.height * downscaled).toInt()
                ),
                dstOffset = IntOffset(
                    (startPosition + handleSize + remainingWidth * type.decorationOffsetHorizontalPercent).roundToInt(),
                    (remainingHeight * type.decorationOffsetVerticalPercent).roundToInt()
                ),
                alpha = 1f - animated.value
            )
        }

        run {
            val downscaled = size.height * type.decorationScale / onBgImage.width
            val remainingWidth = size.width - handleSize - onBgImage.width * downscaled - paddingStart - paddingEnd
            val remainingHeight = size.height - onBgImage.height * downscaled

            drawImage(
                onBgImage,
                dstSize = IntSize(
                    (onBgImage.width * downscaled).toInt(),
                    (onBgImage.height * downscaled).toInt()
                ),
                dstOffset = IntOffset(
                    (startPosition + remainingWidth * (1f-type.decorationOffsetHorizontalPercent)).roundToInt(),
                    (remainingHeight * type.decorationOffsetVerticalPercent).roundToInt()
                ),
                alpha = animated.value
            )
        }
    }
}

@Composable
@Preview
fun CoolSwitchPreview() {
    val checked = remember { mutableStateOf(true) }
    CoolSwitch(
        Modifier.size(400.dp, 200.dp),
        checked = checked.value,
        onCheckedChange = { checked.value = it }
    )
}

@Composable
@Preview
fun CoolSwitchPreviewOrange() {
    val checked = remember { mutableStateOf(true) }
    CoolSwitch(
        Modifier.size(400.dp, 200.dp),
        type = CoolSwitchType.ORANGE,
        checked = checked.value,
        onCheckedChange = { checked.value = it }
    )
}

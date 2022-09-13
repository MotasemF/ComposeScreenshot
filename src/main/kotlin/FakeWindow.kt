import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import java.awt.Cursor
import java.awt.Rectangle

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FakeWindow(image: ImageBitmap?, windowClosed: () -> Unit) {
    var isWindowVisible by remember { mutableStateOf(true) }
    val windowState = WindowState(
        isMinimized = false,
    )
    Window(
        onCloseRequest = {},
        visible = isWindowVisible,
        undecorated = true,
        transparent = true,
        state = windowState,
        alwaysOnTop = true,
        focusable = false,
        resizable = false,
        onKeyEvent = {
            windowClosed()
            false
        }
    ) {


        var rectWidth by remember { mutableStateOf(0f) }
        var rectHeight by remember { mutableStateOf(0f) }

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        val size = WindowUtils.getWidthOfScreens()
        LaunchedEffect(Unit) {
            window.isTransparent = true
            window.bounds = Rectangle(0, 0, size.first, size.second)
            window.placement = WindowPlacement.Floating
            window.isTransparent = true

        }
        Surface(color = Color.Transparent, modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        rectWidth = change.position.x - offsetX
                        rectHeight = change.position.y - offsetY
                    },
                    onDragStart = {
                        offsetX = it.x
                        offsetY = it.y
                    },
                    onDragEnd = { }
                )
            }
        ) {
            Box(
                Modifier.fillMaxWidth()
                    .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)))
            ) {
                Canvas(modifier = Modifier, onDraw = {
                    val circlePath = Path().apply {
                        if (offsetX < rectWidth + offsetX && offsetY < rectHeight + offsetY) {
//                            addRect(Rect(Offset(offsetX, offsetY), Size(rectWidth, rectHeight)))
                            drawRect(Color.Green, Offset(offsetX, offsetY), Size(rectWidth, rectHeight), 0.2f)
                        }

                    }

                })

                if (offsetX != 0f) {
                    Box(
                        modifier = Modifier
                            .offset(offsetX.dp, offsetY.dp)
                            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)))
                            .size(rectWidth.dp, rectHeight.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consumeAllChanges()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                    ) {
                        BorderCenteredCircle(
                            alignment = Alignment.CenterEnd,
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onMove = { rectWidth += it })
                        BorderCenteredCircle(
                            alignment = Alignment.BottomCenter,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            onMove = { rectHeight += it })

                    }
                }
                var rowWidth by remember { mutableStateOf(0) }

                Row(modifier = Modifier.offset((offsetX + rectWidth - rowWidth).dp, (offsetY + rectHeight).dp)
                    .onGloballyPositioned { rowWidth = it.size.width }) {
                    Button(onClick = {
                        val croppedImage = ImageUtils.cropImage(image!!, offsetX, offsetY, rectWidth, rectHeight)
                        FileUtils.saveImage(croppedImage!!)
                        windowClosed()
//                        onImageCropped(ImageUtils.cropImage(image, offsetX, offsetY, buttonWidth, buttonHeight))
//                        closeWindows()
                    }) {
                        Text("Save")
                    }
                    Layout({}, Modifier.size(8.dp)) { _, constraints ->
                        with(constraints) {
                            val width = if (hasFixedWidth) maxWidth else 0
                            val height = if (hasFixedHeight) maxHeight else 0
                            layout(width, height) {}
                        }
                    }
                    Button(onClick = {
//                        onImageCropped(ImageUtils.cropImage(image, offsetX, offsetY, buttonWidth, buttonHeight))
//                        closeWindows()
                    }) {
                        Text("Copy")
                    }
                }
            }

//            Box(
//                modifier = Modifier
//                    .graphicsLayer {
//                        translationX += offsetX
//                        translationY += offsetY
//                    }
//                    .pointerInput(Unit) {
//                        detectDragGestures { change, dragAmount ->
//                            change.consumeAllChanges()
//                            offsetX += dragAmount.x
//                            offsetY += dragAmount.y
//                        }
//                    }
//                    .background(Color.Blue.copy(0.5f))
//                    .size(0.dp, 0.dp)
//
//
//            ) {
////                    BorderCenteredCircle(Alignment.CenterEnd, modifier = Modifier.align(Alignment.CenterEnd), onMove = { rectWidth += it })
////                    BorderCenteredCircle(Alignment.BottomCenter, modifier = Modifier.align(Alignment.BottomCenter), onMove = { rectWidth += it })
//
//            }

            if (offsetX < rectWidth && offsetY < rectHeight) {
//                Box(
//                    modifier = Modifier
//                        .graphicsLayer {
//                            translationX += offsetX
//                            translationY += offsetY
//                        }
//                        .size(rectWidth.dp, rectHeight.dp)
//                        .pointerInput(Unit) {
//                            detectDragGestures { change, dragAmount ->
//                                change.consumeAllChanges()
//                                offsetX += dragAmount.x
//                                offsetY += dragAmount.y
//                            }
//                        }
//                        .background(Color.Blue.copy(0.5f))
//
//                ) {
////                    BorderCenteredCircle(Alignment.CenterEnd, modifier = Modifier.align(Alignment.CenterEnd), onMove = { rectWidth += it })
////                    BorderCenteredCircle(Alignment.BottomCenter, modifier = Modifier.align(Alignment.BottomCenter), onMove = { rectWidth += it })
//
//                }
            }
        }
    }
}


@Composable
fun BorderCenteredCircle(alignment: Alignment, modifier: Modifier, onMove: (Float) -> Unit) {
    val shapeColor = Color(0xFF42AAFF)

    var x = 0.dp
    var y = 0.dp
    var pointer = PointerIcon(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))

    if (alignment == Alignment.CenterEnd) {
        x = 7.dp
    } else {
        y = 7.dp
        pointer = PointerIcon(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))
    }
    Surface(
        color = shapeColor,
        shape = CircleShape,
        border = BorderStroke(2.dp, Color.White),
        modifier = modifier
            .size(15.dp)
            .pointerHoverIcon(pointer)
            .offset(x = x, y = y)
            .draggable(
                orientation = if (alignment == Alignment.CenterEnd) Orientation.Horizontal else Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    onMove(delta)
                }
            )
    ) {}
}
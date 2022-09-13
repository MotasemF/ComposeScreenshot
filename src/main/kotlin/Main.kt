import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import helper.FolderWatchService
import kotlinx.coroutines.*
import java.awt.Desktop
import java.awt.Frame
import java.nio.file.Path
import javax.imageio.ImageIO

@OptIn(
    DelicateCoroutinesApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
fun main() = application {
    val windowState = rememberWindowState(placement = WindowPlacement.Floating)
    var windowVisible by remember { mutableStateOf(true) }
    var isFakeWindowOpen by remember { mutableStateOf(false) }

    var image by remember { mutableStateOf<ImageBitmap?>(null) }

    var imagePaths by remember { mutableStateOf(FileUtils.listImages()) }

    GlobalScope.launch(Dispatchers.IO) {
        //Create App Folder
        FileUtils.createAppFolder()
        //Listen to changes in folder
        FolderWatchService.watchDirectoryPath(Path.of(FileUtils.appFolder)) {
            GlobalScope.launch(Dispatchers.IO) {
                imagePaths = FileUtils.listImages()
            }
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        visible = windowVisible,
        title = "Compose Shortcut",
        icon = painterResource("logo.png")
    ) {
        if (isFakeWindowOpen) {
            FakeWindow(image) {
                isFakeWindowOpen = false
                window.state = Frame.NORMAL
            }
        }


        MaterialTheme {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.size(10.dp))
                Button(
                    onClick = {
                        GlobalScope.launch {
                            window.state = Frame.ICONIFIED
                            delay(400)
                            image = ScreenshotUtils.allMonitors()
                            isFakeWindowOpen = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4F40FF)),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Take Screenshot",
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(0.9f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(Modifier.size(20.dp))

                Text(
                    text = "Recent",
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                )
                Divider(Modifier.padding(horizontal = 32.dp).width(100.dp), color = Color.Black)

                Spacer(Modifier.size(20.dp))
                LazyVerticalGrid(
                    cells = GridCells.Adaptive(180.dp),
                    contentPadding = PaddingValues(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                        bottom = 10.dp
                    ),
                ) {

                    items(items = imagePaths!!) { path ->
                        AnimatedVisibility(
                            visible = imagePaths.any { it == path },
                            enter = scaleIn(
                                animationSpec = TweenSpec(2000, 200, FastOutLinearInEasing)
                            ),
                            exit = scaleOut(
                                animationSpec = TweenSpec(2000, 200, FastOutLinearInEasing)
                            )
                        ) {
                            Card(path)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Card(path: Path) {
    var itemVisable by remember { mutableStateOf(false) }
    Column(
        Modifier.padding(horizontal = 8.dp).padding(bottom = 16.dp)
            .clickable { Desktop.getDesktop().open(path.toFile()) }
            .clip(RoundedCornerShape(10.dp)).onHover { itemVisable = it }) {
        FileImage(path = path, Modifier.fillMaxSize().height(250.dp))
        AnimatedVisibility(visible = itemVisable) {
            Row(
                Modifier.fillMaxWidth().height(50.dp).background(Color(0XFFF5F5F5)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var elevation by remember { mutableStateOf(1f) }
                Spacer(Modifier.size(8.dp))
                ImageButton(painterResource("open_file_location.png")) { FileUtils.openFileLocation() }
                ImageButton(painterResource("copy.png")) { ClipboardImage(ImageIO.read(path.toFile())); Unit }
                Spacer(Modifier.weight(1f))
                ImageButton(painterResource("delete.png")) { FileUtils.deleteFile(path) }
                Spacer(Modifier.size(8.dp))
            }
        }
    }
}


@Composable
fun ImageButton(imagePainter: Painter, onClick: (() -> Unit?)? = null) {
    var hovered by remember { mutableStateOf(false) }
    var alpha = animateFloatAsState(if (hovered) 0.5f else 1f)
    Image(
        imagePainter,
        "",
        Modifier.size(35.dp).alpha(alpha.value).onHover { hovered = it }.clickable { onClick?.invoke() })

}

@Composable
fun FileImage(path: Path, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop) {
    Image(
        bitmap = ImageIO.read(path.toFile()).toComposeImageBitmap(),
        contentDescription = "",
        modifier = modifier,
        contentScale = contentScale
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onHover(hover: (Boolean) -> Unit) = this
    .onPointerEvent(PointerEventType.Enter) {
        hover(true)
    }
    .onPointerEvent(PointerEventType.Exit) {
        hover(false)
    }

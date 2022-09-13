import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.font.GraphicAttribute
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


data class CapturedImage(
    var startPosition: Int,
    var image: ImageBitmap
)

object ScreenshotUtils {

    fun capture(): ArrayList<CapturedImage> {
        val images = ArrayList<CapturedImage>()
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gs = ge.screenDevices
        gs.forEach {
            val startPosition = it.defaultConfiguration.bounds.x
            val screenShot = Robot().createScreenCapture(it.defaultConfiguration.bounds)
            val stream = ByteArrayOutputStream()
            ImageIO.write(screenShot, "PNG", stream)
            val byteArray = stream.toByteArray()
            images.add(CapturedImage(startPosition, Image.makeFromEncoded(byteArray).toComposeImageBitmap()))

        }
        return images
    }

    fun allMonitors(): ImageBitmap{
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val screens = ge.screenDevices
        var allScreensBounds = Rectangle()
        screens.forEach{ screen ->
            val scresnBoungs = screen.defaultConfiguration.bounds
            allScreensBounds = allScreensBounds.union(scresnBoungs)
        }
        val robot = Robot()
        return robot.createScreenCapture(allScreensBounds).toComposeImageBitmap()
    }
}
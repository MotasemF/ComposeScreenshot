import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

object ImageUtils {
    fun cropImage(image: ImageBitmap, x:Float, y:Float, w:Float, h:Float): BufferedImage? {
        val bufferedImage = image.toAwtImage()
        val bufferedCroppedImage = bufferedImage.getSubimage(x.roundToInt(), y.roundToInt(), w.roundToInt(), h.roundToInt())
        return bufferedCroppedImage
    }

    fun setTrayLogo(){
        val trayIcon = TrayIcon(Toolkit.getDefaultToolkit().createImage("./././resources/logo.png"))
        val systemTray = SystemTray.getSystemTray()
        systemTray.add(trayIcon)


    }
}

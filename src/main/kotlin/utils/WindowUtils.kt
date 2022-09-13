import java.awt.GraphicsEnvironment
import javax.swing.JFrame


object WindowUtils {

    fun getWidthOfScreens(): Pair<Int, Int> {
        var width = 0
        var height = 0
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gs = ge.screenDevices
        for (curGs in gs) {
            val mode = curGs.displayMode
            width += mode.width
            height = mode.height
        }
        return Pair(width, height)
    }

    fun showOnScreen(screen: Int, frame: JFrame?) {
        val ge = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
        val gs = ge.screenDevices
        if (screen > -1 && screen < gs.size) {
            gs[screen].fullScreenWindow = frame
        } else if (gs.size > 0) {
            gs[0].fullScreenWindow = frame
        } else {
            throw RuntimeException("No Screens Found")
        }
    }
}
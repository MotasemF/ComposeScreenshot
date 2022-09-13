import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.*
import java.awt.image.BufferedImage
import java.io.IOException


class ClipboardImage(val image: BufferedImage): ClipboardOwner {

    init {
        val trans = TransferableImage(image)
        val c = Toolkit.getDefaultToolkit().systemClipboard
        c.setContents(trans, this)
    }

    override fun lostOwnership(p0: Clipboard?, p1: Transferable?) {
    }
}


private class TransferableImage(var i: Image?) : Transferable {
    override fun getTransferData(flavor: DataFlavor?): Any {
        return if (flavor!!.equals(DataFlavor.imageFlavor) && i != null) {
            i as Any
        } else {
            throw UnsupportedFlavorException(flavor)
        }
    }

    override fun getTransferDataFlavors(): Array<DataFlavor?> {
        val flavors = arrayOfNulls<DataFlavor>(1)
        flavors[0] = DataFlavor.imageFlavor
        return flavors
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        val flavors = transferDataFlavors
        for (i in flavors.indices) {
            if (flavor.equals(flavors[i])) {
                return true
            }
        }
        return false
    }
}
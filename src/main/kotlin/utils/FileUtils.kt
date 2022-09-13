import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOError
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import javax.imageio.ImageIO

object FileUtils {
    @JvmStatic
    val FOLDER_APP_NAME = "Compose Screenshot"

    @JvmStatic
    private val homeFolder = System.getProperty("user.home")

    @JvmStatic
    val appFolder = (homeFolder + File.separator + "Pictures" + File.separator + FOLDER_APP_NAME)

    fun createAppFolder() {
        val file = File(appFolder)
        if (!file.exists()){
            if(file.mkdirs()){
                println("Folders is created")
            } else {
                println("Failed to create folders")
            }
        }
    }

    fun deleteFile(filePath: Path) {
        val file = filePath.toFile()
        if(file.exists()){
            file.delete()
        }
    }

    fun openFileLocation(){
        Desktop.getDesktop().open(File(appFolder))
    }

    fun listImages(): List<Path> {
        val imagePaths = ArrayList<Path>()
        val files = File(appFolder).listFiles()

        files.forEach { file ->
            if(file.extension == "png"){
                imagePaths.add(file.toPath())
            }
        }
        imagePaths.sortBy {
            Files.readAttributes(it, BasicFileAttributes::class.java).creationTime().toMillis()
        }

        return imagePaths.reversed()
    }

    fun saveImage(image: BufferedImage){
        try {
            val filePath = appFolder + File.separator + "composeScreenshot-" + Date().time + ".png"
            val outputFile = File(filePath)
            ImageIO.write(image, "PNG", outputFile)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

}
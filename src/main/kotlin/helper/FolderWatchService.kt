package helper

import kotlinx.coroutines.delay
import java.nio.file.*


object FolderWatchService {

    suspend fun watchDirectoryPath(path: Path, onUpdated: () -> Unit) {
        try {
            val watchService: WatchService = FileSystems.getDefault().newWatchService()
            val watchKey: WatchKey = path.register(
                watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE
            )
            while (true) {
                delay(50)
                val pathEvent = watchKey.pollEvents()
                pathEvent.forEach {
                    val kind = it.kind()
                    val fileName = it.context()
                    when (kind) {
                        StandardWatchEventKinds.ENTRY_CREATE -> onUpdated()
                        StandardWatchEventKinds.ENTRY_DELETE -> onUpdated()
                        StandardWatchEventKinds.ENTRY_MODIFY -> {}
                    }
                }
                val valid = watchKey.reset()
                if (!valid) {
                    break
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


}
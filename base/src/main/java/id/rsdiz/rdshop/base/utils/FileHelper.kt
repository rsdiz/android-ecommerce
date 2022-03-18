package id.rsdiz.rdshop.base.utils

import android.webkit.MimeTypeMap
import java.io.File

object FileHelper {
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}

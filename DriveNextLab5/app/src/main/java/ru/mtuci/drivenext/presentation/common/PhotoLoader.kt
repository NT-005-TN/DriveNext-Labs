package ru.mtuci.drivenext.presentation.common
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import ru.mtuci.drivenext.DriveNextApplication
fun WorkspaceActivity.showPhoto(view:ImageView,bucket:String,path:String) {
    if(path.isBlank() || path=="null") return
    view.tag=path
    lifecycleScope.launch {
        try {
            val image=(application as DriveNextApplication).backend.image(bucket,path)
            image?.let { if(view.tag==path) view.setImageBitmap(it) }
        } catch(e:CancellationException) { throw e }
        catch(_:Exception) { view.contentDescription="Фотография временно недоступна" }
    }
}

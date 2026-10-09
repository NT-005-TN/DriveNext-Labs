package ru.mtuci.drivenext.presentation.common
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.mtuci.drivenext.presentation.auth.AuthActivity
import ru.mtuci.drivenext.presentation.connection.NoConnectionActivity

abstract class WorkspaceActivity : AuthActivity() {
    protected val work: WorkspaceViewModel by viewModels()
    private var retryAfterNetwork = false
    override fun onResume() {
        super.onResume()
        if (retryAfterNetwork && (application as ru.mtuci.drivenext.DriveNextApplication).auth.hasNetwork()) {
            retryAfterNetwork = false
            work.retry()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.STARTED) {
            work.state.collect { state ->
                loading(state.busy)
                if (state.value != null) { work.consume(); render(state.operation,state.value) }
                if (state.offline) { retryAfterNetwork = true; work.consume(); startActivity(Intent(this@WorkspaceActivity,NoConnectionActivity::class.java).putExtra("return_to_caller",true)) }
                state.error?.let { error ->
                    work.consume()
                    MaterialAlertDialogBuilder(this@WorkspaceActivity).setTitle("Ошибка").setMessage(error)
                        .setPositiveButton("Повторить") { _,_ -> work.retry() }.setNegativeButton("Закрыть",null).show()
                }
            }
        } }
    }
    protected abstract fun render(operation: String, value: JSONObject)
}

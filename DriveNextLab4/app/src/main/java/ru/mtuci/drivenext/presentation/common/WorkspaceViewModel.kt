package ru.mtuci.drivenext.presentation.common
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import ru.mtuci.drivenext.DriveNextApplication
import ru.mtuci.drivenext.data.cars.CarRepository
import ru.mtuci.drivenext.domain.NoInternetException

data class WorkState(val busy: Boolean = false, val operation: String = "", val value: JSONObject? = null, val error: String? = null, val offline: Boolean = false)
class WorkspaceViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = CarRepository(app, (app as DriveNextApplication).backend)
    private val mutable = MutableStateFlow(WorkState())
    val state = mutable.asStateFlow()
    private var job: Job? = null
    private var last: Pair<String,JSONObject>? = null
    fun run(operation: String, parameters: JSONObject = JSONObject()) {
        if (mutable.value.busy) { if (operation == "cars") job?.cancel() else return }
        last = operation to JSONObject(parameters.toString())
        mutable.value = WorkState(busy = true)
        job = viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { repository.execute(operation, parameters) }
                mutable.value = WorkState(operation = operation, value = result)
            } catch (e: CancellationException) { throw e }
            catch (_: NoInternetException) { mutable.value = WorkState(offline = true) }
            catch (e: Exception) { mutable.value = WorkState(error = e.message ?: "Не удалось загрузить данные. Попробуйте снова.") }
        }
    }
    fun retry() { last?.let { run(it.first,it.second) } }
    fun consume() { mutable.value = WorkState() }
}

package ru.mtuci.drivenext.presentation.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.presentation.connection.NoConnectionActivity
import ru.mtuci.drivenext.presentation.main.MainActivity
import ru.mtuci.drivenext.presentation.registration.RegisterStep1Activity
import ru.mtuci.drivenext.presentation.registration.SuccessActivity
import ru.mtuci.drivenext.presentation.registration.RegisterStep3Activity

// Общая обработка состояния запроса; бизнес-логика находится во ViewModel.
abstract class AuthActivity : AppCompatActivity() {
    protected val authModel: AuthViewModel by viewModels()
    private var overlay: FrameLayout? = null
    private var passwordDialog: androidx.appcompat.app.AlertDialog? = null
    override fun onStart() { super.onStart(); authModel.checkNetwork() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this) {
            if (!authModel.state.value.busy) { isEnabled = false; onBackPressedDispatcher.onBackPressed() }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authModel.state.collect { state ->
                    loading(state.busy)
                    val event = state.event ?: return@collect
                    authModel.consume()
                    when (event) {
                        "main" -> { passwordDialog?.dismiss(); navigate(MainActivity::class.java, true) }
                        "choice" -> navigate(AuthChoiceActivity::class.java, true)
                        "authenticated" -> onAuthenticated()
                        "success" -> navigate(SuccessActivity::class.java)
                        "continue_registration" -> navigate(RegisterStep3Activity::class.java)
                        "pending" -> { message("Регистрация не завершена. Продолжите заполнение формы."); }
                        "offline" -> startActivity(Intent(this@AuthActivity, NoConnectionActivity::class.java).putExtra("return_to_caller", true))
                        "browser" -> try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(state.message))) } catch (_: Exception) { message("Установите браузер для входа через Google.") }
                        "password" -> passwordDialog()
                        "error", "info" -> message(state.message)
                    }
                }
            }
        }
    }
    protected open fun onAuthenticated() = Unit
    protected fun message(text: String) { MaterialAlertDialogBuilder(this).setTitle("DriveNext").setMessage(text).setPositiveButton("OK", null).show() }
    protected fun navigate(target: Class<*>, clear: Boolean = false) {
        startActivity(Intent(this, target).apply { if (clear) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) })
    }
    private fun passwordDialog() {
        if (passwordDialog?.isShowing == true) return
        val input = EditText(this).apply { hint = "Новый пароль (от 6 символов)"; inputType = 129 }
        passwordDialog = MaterialAlertDialogBuilder(this).setTitle("Новый пароль").setView(input)
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена", null).create().apply {
                setOnShowListener {
                    getButton(android.content.DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        authModel.newPassword(input.text.toString())
                    }
                }
                show()
            }
    }
    protected fun loading(show: Boolean) {
        if (overlay == null || overlay?.parent == null) {
            overlay = FrameLayout(this).apply {
                setBackgroundColor(0xAAFFFFFF.toInt()); isClickable = true; isFocusable = true
                contentDescription = "Выполняется запрос к серверу"
                addView(ProgressBar(this@AuthActivity), FrameLayout.LayoutParams(120, 120, Gravity.CENTER))
            }
            addContentView(overlay, ViewGroup.LayoutParams(-1, -1))
        }
        overlay?.visibility = if (show) View.VISIBLE else View.GONE
    }
}

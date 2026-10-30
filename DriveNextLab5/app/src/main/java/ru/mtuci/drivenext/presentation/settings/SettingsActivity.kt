package ru.mtuci.drivenext.presentation.settings
import android.content.Intent
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import ru.mtuci.drivenext.BuildConfig
import ru.mtuci.drivenext.presentation.common.*
import ru.mtuci.drivenext.presentation.booking.BookingsActivity
import ru.mtuci.drivenext.presentation.landlord.BecomeLandlordActivity
class SettingsActivity:WorkspaceActivity() {
    private val labNumber by lazy { BuildConfig.APPLICATION_ID.substringAfterLast("lab").toIntOrNull() ?: 5 }
    // При каждом открытии запрашиваем актуальные имя, email и аватар.
    override fun onStart() { super.onStart();screen("Настройки");work.run("profile") }
    override fun render(operation:String,value:JSONObject) {
        // Строим пункты настроек после получения пользовательского профиля.
        val body=screen("Настройки")
        val avatar=ImageView(this).apply { setImageResource(ru.mtuci.drivenext.R.drawable.ic_app_logo);contentDescription="Профиль";setOnClickListener { startActivity(Intent(this@SettingsActivity,ProfileActivity::class.java)) } }
        body.addView(avatar,LinearLayout.LayoutParams(180,180));showPhoto(avatar,"registration-documents",value.optString("profile_photo"))
        fun button(label:String,action:()->Unit) {
            val margin=(8*resources.displayMetrics.density).toInt()
            body.addView(Button(this).apply {
                text=label;isAllCaps=false;textSize=16f;gravity=android.view.Gravity.CENTER_VERTICAL
                setPadding(margin*2,0,margin*2,0);setTextColor(getColor(ru.mtuci.drivenext.R.color.text_primary))
                background=getDrawable(ru.mtuci.drivenext.R.drawable.bg_secondary_button);setOnClickListener { action() }
            },LinearLayout.LayoutParams(-1,(58*resources.displayMetrics.density).toInt()).apply { topMargin=margin })
        }
        button(value.optString("name")+"\n"+value.optString("email")) { startActivity(Intent(this,ProfileActivity::class.java)) }
        button("Мои бронирования") {
            if (labNumber < 4) startActivity(FeatureStubActivity.intent(this,"Мои бронирования",4))
            else startActivity(Intent(this,BookingsActivity::class.java))
        }
        button("Подключить свой автомобиль") {
            if (labNumber < 5) startActivity(FeatureStubActivity.intent(this,"Добавление автомобиля",5))
            else startActivity(Intent(this,BecomeLandlordActivity::class.java))
        }
        val prefs=getSharedPreferences("settings",MODE_PRIVATE)
        button("Тема") {
            // Тема относится к установке приложения и хранится локально.
            val names=arrayOf("Системная","Светлая","Тёмная")
            MaterialAlertDialogBuilder(this).setTitle("Тема").setItems(names) { _,index->
                val modes=intArrayOf(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,AppCompatDelegate.MODE_NIGHT_NO,AppCompatDelegate.MODE_NIGHT_YES)
                prefs.edit().putInt("theme",modes[index]).apply();AppCompatDelegate.setDefaultNightMode(modes[index])
            }.show()
        }
        // Переключатель уведомлений сохраняет простое логическое значение.
        body.addView(Switch(this).apply { text="Уведомления";isChecked=prefs.getBoolean("notifications",true);setOnCheckedChangeListener { _,enabled->prefs.edit().putBoolean("notifications",enabled).apply() } })
        button("Помощь") { message("Выберите автомобиль, укажите даты и подтвердите аренду. Для бронирования заполните профиль и удостоверение. Используйте только учебные данные.") }
        button("Пригласи друга") { startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"DriveNext — учебное приложение аренды автомобилей: https://github.com/NT-005-TN/DriveNext-Labs"),"Поделиться")) }
    }
}

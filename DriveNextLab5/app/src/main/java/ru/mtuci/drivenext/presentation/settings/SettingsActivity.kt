package ru.mtuci.drivenext.presentation.settings
import android.content.Intent
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import ru.mtuci.drivenext.presentation.common.*
import ru.mtuci.drivenext.presentation.booking.BookingsActivity
import ru.mtuci.drivenext.presentation.landlord.BecomeLandlordActivity
class SettingsActivity:WorkspaceActivity() {
    override fun onStart() { super.onStart();screen("Настройки");work.run("profile") }
    override fun render(operation:String,value:JSONObject) {
        val body=screen("Настройки")
        val avatar=ImageView(this).apply { setImageResource(ru.mtuci.drivenext.R.drawable.ic_app_logo);contentDescription="Профиль";setOnClickListener { startActivity(Intent(this@SettingsActivity,ProfileActivity::class.java)) } }
        body.addView(avatar,LinearLayout.LayoutParams(180,180));showPhoto(avatar,"registration-documents",value.optString("profile_photo"))
        fun button(label:String,action:()->Unit) { body.addView(Button(this).apply { text=label;setOnClickListener { action() } }) }
        button(value.optString("name")+"\n"+value.optString("email")) { startActivity(Intent(this,ProfileActivity::class.java)) }
        button("Мои бронирования") { startActivity(Intent(this,BookingsActivity::class.java)) }
        button("Подключить свой автомобиль") { startActivity(Intent(this,BecomeLandlordActivity::class.java)) }
        val prefs=getSharedPreferences("settings",MODE_PRIVATE)
        button("Тема") {
            val names=arrayOf("Системная","Светлая","Тёмная")
            MaterialAlertDialogBuilder(this).setTitle("Тема").setItems(names) { _,index->
                val modes=intArrayOf(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,AppCompatDelegate.MODE_NIGHT_NO,AppCompatDelegate.MODE_NIGHT_YES)
                prefs.edit().putInt("theme",modes[index]).apply();AppCompatDelegate.setDefaultNightMode(modes[index])
            }.show()
        }
        body.addView(Switch(this).apply { text="Уведомления";isChecked=prefs.getBoolean("notifications",true);setOnCheckedChangeListener { _,enabled->prefs.edit().putBoolean("notifications",enabled).apply() } })
        button("Помощь") { message("Выберите автомобиль, укажите даты и подтвердите аренду. Для бронирования заполните профиль и удостоверение. Используйте только учебные данные.") }
        button("Пригласи друга") { startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"DriveNext — учебное приложение аренды автомобилей: https://github.com/NT-005-TN/DriveNext-Labs"),"Поделиться")) }
    }
}

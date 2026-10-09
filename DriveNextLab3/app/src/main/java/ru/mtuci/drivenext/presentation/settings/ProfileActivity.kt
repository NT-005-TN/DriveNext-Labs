package ru.mtuci.drivenext.presentation.settings
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONObject
import ru.mtuci.drivenext.presentation.common.*
class ProfileActivity:WorkspaceActivity() {
    private lateinit var name:EditText
    private lateinit var surname:EditText
    private lateinit var license:EditText
    private lateinit var avatar:ImageView
    private var chosen:String=""
    private var restoredFields:Bundle?=null
    private val picker=registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri->
        if(uri!=null) {
            runCatching { contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            chosen=uri.toString();avatar.tag=chosen;avatar.setImageURI(uri)
        }
    }
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState);screen("Профиль");chosen=savedInstanceState?.getString("avatar").orEmpty()
        restoredFields=savedInstanceState
        work.run("profile")
    }
    override fun render(operation:String,value:JSONObject) {
        val body=screen("Профиль")
        avatar=ImageView(this).apply { contentDescription="Нажмите, чтобы выбрать аватар";setImageResource(ru.mtuci.drivenext.R.drawable.ic_app_logo);setOnClickListener { picker.launch(arrayOf("image/jpeg","image/png","image/webp")) } }
        body.addView(avatar,LinearLayout.LayoutParams(250,250))
        if(operation=="saveProfile") { chosen="";restoredFields=null }
        if(chosen.isNotBlank()) avatar.setImageURI(android.net.Uri.parse(chosen))
        else showPhoto(avatar,"registration-documents",value.optString("profile_photo"))
        body.label(value.optString("email"))
        fun field(hint:String,text:String)=EditText(this).apply { this.hint=hint;setText(text);body.addView(this) }
        surname=field("Фамилия",value.optString("surname"));name=field("Имя",value.optString("name"));license=field("Удостоверение: 10 символов",value.optString("license_number").let { if(it=="null") "" else it })
        // После поворота сохраняем ещё не отправленные изменения.
        restoredFields?.let { saved ->
            if(saved.containsKey("name")) { name.setText(saved.getString("name"));surname.setText(saved.getString("surname"));license.setText(saved.getString("license")) }
        }
        restoredFields=null
        body.addView(Button(this).apply { text="Сохранить профиль";setOnClickListener {
            if(name.text.isBlank()||surname.text.isBlank()||(license.text.isNotBlank()&&license.text.length!=10)) message("Заполните имя и фамилию; номер удостоверения — 10 символов.")
            else work.run("saveProfile",JSONObject().put("name",name.text.toString()).put("surname",surname.text.toString()).put("license",license.text.toString()).put("avatar",chosen))
        } })
        body.addView(Button(this).apply { text="Выйти из профиля";setOnClickListener { authModel.logout() } })
        if(operation=="saveProfile") { chosen="";message("Профиль сохранён на сервере.") }
    }
    override fun onSaveInstanceState(out:Bundle) {
        out.putString("avatar",chosen)
        if(::name.isInitialized) { out.putString("name",name.text.toString());out.putString("surname",surname.text.toString());out.putString("license",license.text.toString()) }
        super.onSaveInstanceState(out)
    }
}

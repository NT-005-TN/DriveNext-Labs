package ru.mtuci.drivenext.presentation.common
import android.widget.*
fun WorkspaceActivity.screen(title:String):LinearLayout {
    val body=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL;setPadding(28,28,28,28) }
    val scroll=ScrollView(this).apply { addView(body) }; setContentView(scroll)
    body.addView(Button(this).apply { text="Назад";setOnClickListener { finish() } })
    body.addView(TextView(this).apply { text=title;textSize=28f;setPadding(0,20,0,24) })
    return body
}
fun LinearLayout.label(value:String) { addView(TextView(context).apply { text=value;textSize=18f;setPadding(0,14,0,14) }) }

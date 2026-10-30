package ru.mtuci.drivenext.presentation.common
import android.widget.*
import ru.mtuci.drivenext.R
// Создаём общий прокручиваемый каркас простых служебных экранов.
fun WorkspaceActivity.screen(title:String):LinearLayout {
    // Android-размеры программных View переводим из dp в пиксели.
    fun dp(value:Int)=(value*resources.displayMetrics.density).toInt()
    val body=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL;setPadding(dp(24),dp(20),dp(24),dp(32)) }
    val scroll=ScrollView(this).apply { setBackgroundColor(getColor(R.color.background));addView(body) }; setContentView(scroll)
    body.addView(Button(this).apply {
        text="Назад";isAllCaps=false;setTextColor(getColor(R.color.purple_primary))
        background=getDrawable(R.drawable.bg_secondary_button);setOnClickListener { finish() }
    },LinearLayout.LayoutParams(-1,dp(52)))
    body.addView(TextView(this).apply {
        text=title;textSize=28f;setTextColor(getColor(R.color.text_primary));setPadding(0,dp(24),0,dp(20))
        typeface=android.graphics.Typeface.DEFAULT_BOLD
    })
    return body
}
// Добавляем единообразный текстовый блок в вертикальный контейнер.
fun LinearLayout.label(value:String) {
    val space=(14*resources.displayMetrics.density).toInt()
    addView(TextView(context).apply { text=value;textSize=18f;setTextColor(context.getColor(R.color.text_primary));setPadding(0,space,0,space) })
}

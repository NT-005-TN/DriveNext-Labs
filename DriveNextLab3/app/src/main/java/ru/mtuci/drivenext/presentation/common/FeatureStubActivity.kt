package ru.mtuci.drivenext.presentation.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FeatureStubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Читаем название функции и номер лабораторной из Intent.
        val feature = intent.getStringExtra(EXTRA_FEATURE).orEmpty()
        val lab = intent.getIntExtra(EXTRA_LAB, 0)
        val padding = (24 * resources.displayMetrics.density).toInt()
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(getColor(ru.mtuci.drivenext.R.color.background))
            setPadding(padding,padding,padding,padding)
            addView(Button(context).apply {
                text = "Назад";isAllCaps=false;setTextColor(getColor(ru.mtuci.drivenext.R.color.purple_primary))
                background=getDrawable(ru.mtuci.drivenext.R.drawable.bg_secondary_button);setOnClickListener { finish() }
            },LinearLayout.LayoutParams(-1,(52*resources.displayMetrics.density).toInt()))
            addView(TextView(context).apply {
                text = feature
                textSize = 28f
                setTextColor(getColor(ru.mtuci.drivenext.R.color.text_primary))
                typeface=android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0,padding * 2,0,padding)
            })
            addView(TextView(context).apply {
                text = "Эта функция реализована в лабораторной работе №$lab."
                textSize = 18f
                setTextColor(getColor(ru.mtuci.drivenext.R.color.text_secondary))
            })
        })
    }

    companion object {
        private const val EXTRA_FEATURE = "feature"
        private const val EXTRA_LAB = "lab"
        // Фабрика Intent не даёт вызывающим экранам перепутать ключи extras.
        fun intent(context: Context, feature: String, lab: Int) =
            Intent(context,FeatureStubActivity::class.java).putExtra(EXTRA_FEATURE,feature).putExtra(EXTRA_LAB,lab)
    }
}

package com.example.alarmapplication.ui.component

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.marginLeft
import com.example.alarmapplication.R
import com.example.alarmapplication.util.shotToast
import com.example.alarmapplication.util.sp2px
import kotlin.math.log

class LineRadioGroup :
    RadioGroup {
    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        buttons: List<Pair<String, Any>>
    ) : super(context, attributeSet) {
        init(buttons)
    }

    constructor(context: Context, buttons: List<Pair<String, Any>>) : this(context, null, buttons)
    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        listOf()
    )

    fun init(buttons: List<Pair<String, Any>>) {
        val transparentColorDrawable = ColorDrawable(Color.TRANSPARENT)
        val textSize = 12f.sp2px(context)
        for (button in buttons) {
            val radioButton = RadioButton(context).apply {
                setLines(1)
                setText(button.first)
                setButtonDrawable(transparentColorDrawable)
                background = transparentColorDrawable
                setTextSize(textSize)
            }
            addView(radioButton)
            radioButton.apply {
                layoutParams = layoutParams.apply {
                    width = LayoutParams.MATCH_PARENT
                }
            }
//            radioButton.width = LayoutParams.MATCH_PARENT
//            radioButton.background = ColorDrawable(context.resources.getColor(R.color.black))
        }
    }
}
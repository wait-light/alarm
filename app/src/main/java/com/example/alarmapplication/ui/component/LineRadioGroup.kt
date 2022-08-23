package com.example.alarmapplication.ui.component

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.example.alarmapplication.R
import com.example.alarmapplication.util.dp2px
import com.example.alarmapplication.util.shotToast
import com.example.alarmapplication.util.sp2px
import kotlin.math.log

class LineRadioGroup<T> :
    RadioGroup {
    constructor(
        context: Context,
        attributeSet: AttributeSet?,
        buttons: List<Pair<String, T>>
    ) : super(context, attributeSet) {
        init(buttons)
    }

    constructor(context: Context, buttons: List<Pair<String, T>>) : this(context, null, buttons)
    constructor(context: Context, attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        listOf()
    )

    fun init(buttons: List<Pair<String, T>>) {
        val transparentColorDrawable = ColorDrawable(Color.TRANSPARENT)
        val textSize = 12f.sp2px(context)
        val buttonTopMargin = 5.dp2px(context).toInt()
        val buttonBottomMargin = 5.dp2px(context).toInt()
        val buttonLeftMargin = 15.dp2px(context).toInt()
        for (button in buttons) {
            val radioButton = RadioButton(context).apply {
                setLines(1)
                setText(button.first)
                setButtonDrawable(transparentColorDrawable)
                background = transparentColorDrawable
                setTextSize(textSize)
                if (button.second is Int) {
                    id = button.second as Int
                }
            }
            addView(radioButton)
            radioButton.apply {
                layoutParams = (layoutParams as RadioGroup.LayoutParams).apply {
                    width = LayoutParams.MATCH_PARENT
                    topMargin = buttonTopMargin
                    leftMargin = buttonLeftMargin
                    bottomMargin = buttonBottomMargin
                }
            }
//            radioButton.width = LayoutParams.MATCH_PARENT
//            radioButton.background = ColorDrawable(context.resources.getColor(R.color.black))
        }
    }
}
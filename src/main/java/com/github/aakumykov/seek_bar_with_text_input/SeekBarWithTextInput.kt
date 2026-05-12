package com.github.aakumykov.seek_bar_with_text_input

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class SeekBarWithTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val initialAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SeekBarWithTextInput, 0, 0)
    private val attribMaxValue = initialAttributes.getInt(R.styleable.SeekBarWithTextInput_max, DEFAULT_MAX)
    private val attribProgress = initialAttributes.getInt(R.styleable.SeekBarWithTextInput_progress, DEFAULT_PROGRESS)

    private val seekBar: SeekBar
    private val progressTextInput: EditText
    private val label: TextView

    private var changeListener: ChangeListener? = null
    private var labelProvider: ((progress: Int) -> String)? = null

    init {
        inflate(context, R.layout.seek_bar_with_text_input, this)

        seekBar = findViewById(R.id.seekBar)
        progressTextInput = findViewById(R.id.valueInput)
        label = findViewById(R.id.label)

        try {
            seekBar.max = attribMaxValue
            seekBar.progress = attribProgress
            updateProgressTextInput(attribProgress)

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser)
                        updateProgressTextInput(progress)
                    changeListener?.onSeekBarWithTextInputProgressChanged(progress, fromUser)
                    updateProgressLabel(progress)
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            progressTextInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    try {
                        seekBar.progress = s.toString().toInt()
                    } catch (e: NumberFormatException) {
                        Log.e(TAG, e.message, e)
                    }
                }
            })

        } catch (t: Throwable) {
            Log.e(TAG, t.message, t)
        }
        finally {
            initialAttributes.recycle()
        }
    }

    private fun updateProgressTextInput(progress: Int) {
        progressTextInput.setText(progress.toString())
    }

    private fun updateProgressLabel(progress: Int) {
        labelProvider?.let { label.text = it.invoke(progress) }
    }

    var progress: Int
        get() = seekBar.progress
        set(value) {
            seekBar.progress = value
            progressTextInput.setText(value.toString())
        }

    var max: Int
        get() = seekBar.max
        set(value) { seekBar.max = value }

    /**
     * Для удаления слушателя передайте в качестве аргумента null.
     */
    fun setChangeListener(listener: ChangeListener?) {
        changeListener = listener
        updateProgressLabel(attribProgress)
    }

    /**
     * Для удаления провайдера передайте в качестве аргумента null.
     */
    fun setProgressLabelProvider(provider: ((progress: Int) -> String)?) {
        labelProvider = provider
        updateProgressLabel(attribProgress)
    }


    interface ChangeListener {
        fun onSeekBarWithTextInputProgressChanged(progress: Int, fromUser: Boolean)
    }

    companion object {
        val TAG: String = SeekBarWithTextInput::class.java.simpleName
        const val DEFAULT_MAX = 100
        const val DEFAULT_PROGRESS = 0
    }
}
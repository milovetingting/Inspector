package com.wangyz.lib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.wangyz.lib.R


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/26 8:39 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/26 8:39 上午
 * 修改备注：
 * @version
 */
class EventDialog(
    private val targetActivity: Activity,
    private val submitCallback: (String, String) -> Unit,
    private val closeCallback: () -> Unit = {}
) {

    private lateinit var eventId: EditText

    private lateinit var eventName: EditText

    private lateinit var submit: Button

    private lateinit var close: Button

    private val rootView by lazy {
        val view = targetActivity.layoutInflater.inflate(R.layout.layout_event_add, null)
        eventId = view.findViewById(R.id.etEventId)
        eventName = view.findViewById(R.id.etEventName)
        submit = view.findViewById(R.id.btnSubmit)
        close = view.findViewById(R.id.btnClose)
        submit.setOnClickListener {
            submitCallback.invoke(eventId.text.trim().toString(), eventName.text.trim().toString())
            reset()
            closeCallback.invoke()
            dismiss()
        }
        close.setOnClickListener {
            reset()
            closeCallback.invoke()
            dismiss()
        }
        view
    }

    private val dialog by lazy {
        val dialog = Dialog(targetActivity, R.style.style_dialog)
        dialog.setContentView(rootView)
        dialog.setCancelable(false)
        dialog.show()

        val window = dialog.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.dialog_animation)
        window?.decorView?.setPadding(0, 0, 0, 0)

        val param = window?.attributes
        param?.width = WindowManager.LayoutParams.MATCH_PARENT
        param?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = param

        dialog
    }

    fun show() {
        dialog.show()
    }

    private fun dismiss() {
        dialog.dismiss()
    }

    private fun reset() {
        eventId.setText("")
        eventName.setText("")
    }

}
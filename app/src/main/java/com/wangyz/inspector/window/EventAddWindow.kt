package com.wangyz.inspector.window

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.wangyz.inspector.R


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/25 7:13 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/25 7:13 下午
 * 修改备注：
 * @version
 */
class EventAddWindow(
    activity: FragmentActivity,
    val submitCallback: (String, String) -> Unit,
    val closeCallback: () -> Unit = {}
) : BaseFloatWindow(activity) {

    private lateinit var view: View

    private lateinit var eventId: EditText

    private lateinit var eventName: EditText

    private lateinit var submit: Button

    private lateinit var close: Button

    override fun initView(inflater: LayoutInflater): View {
        view = inflater.inflate(R.layout.layout_event_add, null)
        eventId = view.findViewById(R.id.etEventId)
        eventName = view.findViewById(R.id.etEventName)
        submit = view.findViewById(R.id.btnSubmit)
        close = view.findViewById(R.id.btnClose)
        submit.setOnClickListener {
            submitCallback.invoke(eventId.text.trim().toString(), eventName.text.trim().toString())
            reset()
            closeCallback.invoke()
            hide()
        }
        close.setOnClickListener {
            reset()
            closeCallback.invoke()
            hide()
        }
        return view
    }

    private fun reset() {
        eventId.setText("")
        eventName.setText("")
    }

}
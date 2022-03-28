package com.wangyz.lib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import com.wangyz.lib.R


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 3:53 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 3:53 下午
 * 修改备注：
 * @version
 */
class CommitDialog(
    private val targetActivity: Activity,
    private val commitCallback: () -> Unit,
    private val closeCallback: () -> Unit = {}
) {
    private lateinit var commit: Button

    private lateinit var close: Button

    private val rootView by lazy {
        val view = targetActivity.layoutInflater.inflate(R.layout.layout_commit, null)
        commit = view.findViewById(R.id.btnCommit)
        close = view.findViewById(R.id.btnClose)
        commit.setOnClickListener {
            commitCallback.invoke()
            hide()
            closeCallback.invoke()
        }
        close.setOnClickListener {
            hide()
            closeCallback.invoke()
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

    fun hide() {
        dialog.dismiss()
    }

}
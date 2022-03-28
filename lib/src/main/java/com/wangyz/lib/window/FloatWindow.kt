package com.wangyz.lib.window

import android.app.Activity
import android.view.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import com.wangyz.lib.R


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 3:35 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 3:35 下午
 * 修改备注：
 * @version
 */
class FloatWindow() {

    fun setupWindow(activity: Activity, callback: () -> Unit) {
        var layoutParam = WindowManager.LayoutParams().apply {
            //设置大小 自适应
            width = WRAP_CONTENT
            height = WRAP_CONTENT
            gravity = Gravity.LEFT or Gravity.TOP
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        // 新建悬浮窗控件
        val floatRootView = LayoutInflater.from(activity).inflate(R.layout.layout_float_item, null)
        floatRootView.setOnClickListener {
            callback.invoke()
        }
        //设置拖动事件
        floatRootView?.setOnTouchListener(
            ItemViewTouchListener(
                layoutParam,
                activity.window.windowManager
            )
        )
        // 将悬浮窗控件添加到WindowManager
        activity.window.windowManager.addView(floatRootView, layoutParam)
    }

    class ItemViewTouchListener(
        val wl: WindowManager.LayoutParams,
        val windowManager: WindowManager
    ) :
        View.OnTouchListener {
        private var x = 0
        private var y = 0
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = motionEvent.rawX.toInt()
                    y = motionEvent.rawY.toInt()

                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = motionEvent.rawX.toInt()
                    val nowY = motionEvent.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    wl.apply {
                        x += movedX
                        y += movedY
                    }
                    //更新悬浮控件位置
                    windowManager?.updateViewLayout(view, wl)
                }
                else -> {

                }
            }
            return false
        }
    }

}
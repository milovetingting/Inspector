package com.wangyz.lib.state

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.wangyz.lib.R
import com.wangyz.lib.delegate.AccessibilityDelegate
import com.wangyz.lib.dialog.EventDialog
import com.wangyz.lib.ext.simpleId
import com.wangyz.lib.hierarchy.ViewHierarchy
import com.wangyz.lib.util.LogUtils


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/26 9:27 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/26 9:27 上午
 * 修改备注：
 * @version
 */
class InspectorLifecycleState(private val activity: Activity) {

    private val rootView by lazy {
        activity.window.decorView.findViewById<View>(android.R.id.content) as ViewGroup
    }

    private val views by lazy {
        ViewHierarchy.getAllChildViews(rootView)
    }

    private val scrollView = views.filterIsInstance<NestedScrollView>().firstOrNull()

    private lateinit var anchorView: View

    private val dialog by lazy {
        EventDialog(activity, submitCallback = { id, name ->
            LogUtils.i("id:$id,name:$name")
            anchorView.setTag(anchorView.simpleId, "")
            addFlag(anchorView)
        }, closeCallback = {
            LogUtils.i("close")
        })
    }

    private fun setAccessibilityDelegate(view: View?) {
        view?.apply {
            accessibilityDelegate = AccessibilityDelegate(accessibilityDelegate) {
                LogUtils.i("${this.simpleId} accessibilityDelegate-->click")
                if (!hasEvent(it)) {
                    LogUtils.i("准备注册事件")
                    anchorView = it!!
                    showDialog()
                } else {
                    LogUtils.i("已经注册事件")
                    Toast.makeText(activity, "已经注册事件", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resetAccessibilityDelegate(view: View?) {
        view?.apply {
            if (accessibilityDelegate is AccessibilityDelegate) {
                accessibilityDelegate =
                    (accessibilityDelegate as AccessibilityDelegate).originDelegate
            }
        }
    }

    private fun hasEvent(view: View?): Boolean = view?.getTag(view.simpleId) != null

    private fun addFlag(anchorView: View) {
        val anchorRect = Rect()
        val rootViewRect = Rect()
        anchorView.getGlobalVisibleRect(anchorRect)
        rootView.getGlobalVisibleRect(rootViewRect)

        // 创建imageView
        val imageView = ImageView(activity)
        imageView.tag = Pair("Flag", anchorView)
        imageView.setImageResource(R.drawable.flag)
        rootView.addView(imageView)
        views.add(imageView)

        // 调整显示区域大小
        val params = imageView.layoutParams as FrameLayout.LayoutParams
        params.width = 50
        params.height = 50
        imageView.layoutParams = params

        // 设置左上角显示
        imageView.x = (anchorRect.left).toFloat()
        imageView.y = (anchorRect.top - rootViewRect.top).toFloat()
    }

    private fun removeFlags() {
        val flags = views.filter { view -> view.tag is Pair<*, *> }
        flags.forEach { view ->
            rootView.removeView(view)
            views.remove(view)
        }
    }

    private fun showDialog() {
        dialog.show()
    }

    private val scrollChangeListener by lazy {
        (View.OnScrollChangeListener { p0, p1, p2, p3, p4 ->
            val flags = views.filter { view -> view.tag is Pair<*, *> }
            flags.forEach { view ->
                rootView.removeView(view)
                views.remove(view)
                val tag = view.tag as? Pair<String, View>
                tag?.apply {
                    addFlag(second)
                }
            }
        })
    }

    fun onResume() {

        views.forEach { view ->
            setAccessibilityDelegate(view)
        }

        scrollView?.setOnScrollChangeListener(scrollChangeListener)
    }

    fun onPause() {

        views.forEach { view ->
            resetAccessibilityDelegate(view)
        }

    }
}
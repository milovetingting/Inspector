package com.wangyz.inspector.ui

import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.wangyz.inspector.R
import com.wangyz.lib.Constants
import com.wangyz.lib.delegate.AccessibilityDelegate
import com.wangyz.lib.ext.simpleId
import com.wangyz.lib.hierarchy.ViewHierarchy
import com.wangyz.lib.util.LogUtils
import com.wangyz.lib.window.EventAddWindow


class MainActivity : AppCompatActivity() {

    private val rootView by lazy {
        window.decorView.findViewById<View>(android.R.id.content) as ViewGroup
    }

    private val views by lazy {
        ViewHierarchy.getAllChildViews(rootView)
    }

    private val floatWindow by lazy {
        EventAddWindow(this, submitCallback = { id, name ->
            LogUtils.i("id:$id,name:$name")
            anchorView.setTag(anchorView.simpleId, "")
            addFlag(anchorView)
        }, closeCallback = {
            LogUtils.i("close")
        })
    }

    private lateinit var anchorView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        views.forEach { view ->
            setAccessibilityDelegate(view)
        }
        val scrollView = views.filterIsInstance<NestedScrollView>().firstOrNull()
        scrollView?.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            val flags = views.filter { view -> view.tag is Pair<*, *> }
            flags.forEach { view ->
                rootView.removeView(view)
                views.remove(view)
                val tag = view.tag as? Pair<String, View>
                tag?.apply {
                    addFlag(second)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.WINDOW_PERMISSION) {
            val granted = grantResults.toList()
                .count { result -> result == PackageManager.PERMISSION_GRANTED } == grantResults.size
            if (granted) {
                floatWindow.setPermission(true)
                floatWindow.show()
            } else {
                floatWindow.setPermission(false)
            }
        }
    }

    private fun addFlag(anchorView: View) {
        val anchorRect = Rect()
        val rootViewRect = Rect()
        anchorView.getGlobalVisibleRect(anchorRect)
        rootView.getGlobalVisibleRect(rootViewRect)

        // 创建imageView
        val imageView = ImageView(this)
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

    private fun hasEvent(view: View?): Boolean = view?.getTag(view.simpleId) != null

    private fun setAccessibilityDelegate(view: View?) {
        view?.apply {
            accessibilityDelegate = AccessibilityDelegate(accessibilityDelegate) {
                LogUtils.i("${this.simpleId} accessibilityDelegate-->click")
                if (!hasEvent(it)) {
                    LogUtils.i("准备注册事件")
                    anchorView = it!!
                    floatWindow.show()
                } else {
                    LogUtils.i("已经注册事件")
                    Toast.makeText(this@MainActivity, "已经注册事件", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
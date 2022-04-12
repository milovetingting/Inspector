package com.wangyz.lib.state

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.wangyz.lib.R
import com.wangyz.lib.config.Config
import com.wangyz.lib.delegate.AccessibilityDelegate
import com.wangyz.lib.dialog.CommitDialog
import com.wangyz.lib.dialog.EventDialog
import com.wangyz.lib.ext.simpleId
import com.wangyz.lib.hierarchy.ViewHierarchy
import com.wangyz.lib.inspector.Inspector
import com.wangyz.lib.proxy.ProxyHandler
import com.wangyz.lib.util.HookHelper
import com.wangyz.lib.util.LogUtils
import com.wangyz.lib.window.FloatWindow


/**
 * 类描述：页面状态管理
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/26 9:27 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/26 9:27 上午
 * 修改备注：
 * @version
 */
class InspectorLifecycleState(private val activity: Activity) {

    private val proxyHandlerMap = mutableMapOf<View, ProxyHandler?>()

    private val rootView by lazy {
        activity.window.decorView.findViewById<View>(android.R.id.content) as ViewGroup
    }

    private val views by lazy {
        ViewHierarchy.getAllChildViews(rootView)
    }

    private val scrollView by lazy {
        views.filterIsInstance<NestedScrollView>().firstOrNull()
    }

    private var anchorView: View? = null

    private val events by lazy {
        mutableListOf<Config.TrackConfig>()
    }

    private val dialog by lazy {
        EventDialog(activity, submitCallback = { id, name ->
            anchorView?.apply {
                LogUtils.i("id:$id,name:$name")
                val config = Config.TrackConfig(
                    id,
                    name,
                    this.simpleId.toString(),
                    activity.javaClass.name
                )
                events.add(config)
                Inspector.getInstance().getConfig()?.apply {
                    (this as Config).configs.add(config)
                }
                this.setTag(this.simpleId, "")
                addFlag(this)
            }
        }, closeCallback = {
            LogUtils.i("close")
            anchorView?.apply {
                proxyHandlerMap[anchorView]?.onClickListener?.onClick(anchorView)
            }
        })

    }

    private var inited = false

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

    private fun setProxyOnclickListener(view: View?) {
        view?.apply {
            val proxyHandler = ProxyHandler() {
                LogUtils.i("click")
                if (!hasEvent(view)) {
                    LogUtils.i("准备注册事件")
                    anchorView = view
                    showDialog()
                } else {
                    LogUtils.i("已经注册事件")
                    Toast.makeText(activity, "已经注册事件", Toast.LENGTH_SHORT).show()
                    anchorView = view
                    proxyHandlerMap[anchorView]?.onClickListener?.onClick(anchorView)
                }
            }
            HookHelper.hook(view, proxyHandler)
            proxyHandlerMap[view] = proxyHandler
        }
    }

    private fun resetOnclickListener(view: View?) {
        view?.apply {

        }
    }

    private fun hasEvent(view: View?): Boolean = view?.getTag(view.simpleId) != null

    private fun initFlag() {
        Inspector.getInstance().getConfig()?.apply {
            (this as Config).configs.filter { it.page == activity.javaClass.name }.forEach { it ->
                views.firstOrNull { view -> view.simpleId.toString() == it.anchor }?.apply {
                    addFlag(this)
                }
            }
        }
    }

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

    private fun showDialog() {
        dialog.show()
    }

    fun onCreate() {
        FloatWindow().setupWindow(activity) {
            CommitDialog(activity, commitCallback = {
                Toast.makeText(activity, "提交", Toast.LENGTH_SHORT).show()
            }, closeCallback = {

            }).show()
        }
    }

    fun onResume() {
        if (!inited) {
            initFlag()
            inited = true
        }

        views.forEach { view ->
            setProxyOnclickListener(view)
        }
        scrollView?.setOnScrollChangeListener(scrollChangeListener)
    }

    fun onPause() {

        views.forEach { view ->
            resetOnclickListener(view)
        }

    }
}
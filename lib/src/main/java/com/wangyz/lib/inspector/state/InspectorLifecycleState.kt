package com.wangyz.lib.inspector.state

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import com.wangyz.lib.R
import com.wangyz.lib.config.Config
import com.wangyz.lib.config.ConfigManager
import com.wangyz.lib.inspector.dialog.CommitDialog
import com.wangyz.lib.inspector.dialog.EventDialog
import com.wangyz.lib.ext.simpleId
import com.wangyz.lib.ext.viewHierarchy
import com.wangyz.lib.util.ViewHierarchyUtil
import com.wangyz.lib.inspector.proxy.ProxyHandler
import com.wangyz.lib.util.HookHelper
import com.wangyz.lib.util.LogUtils
import com.wangyz.lib.inspector.window.FloatWindow


/**
 * 类描述：页面状态管理
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/26 9:27 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/26 9:27 上午
 * 修改备注：
 * @version
 */
class InspectorLifecycleState(private val activity: FragmentActivity) {

    private var config: Config? = null

    private var tempConfig: Config? = null

    private val proxyHandlerMap = mutableMapOf<View, ProxyHandler?>()

    private val rootView by lazy {
        activity.window.decorView.findViewById<View>(android.R.id.content) as ViewGroup
    }

    private val views by lazy {
        ViewHierarchyUtil.getAllChildViews(rootView)
    }

    private val scrollView by lazy {
        views.filterIsInstance<NestedScrollView>().firstOrNull()
    }

    private var anchorView: View? = null

    private var floatWindow: View? = null

    private val dialog by lazy {
        EventDialog(activity, submitCallback = { id, name ->
            anchorView?.apply {
                LogUtils.i("id:$id,name:$name")
                val bean = Config.TrackConfig(
                    id,
                    name,
                    this.simpleId.toString(),
                    this.viewHierarchy,
                    activity.javaClass.name
                )
                config?.configs?.add(bean)
                tempConfig?.configs?.add(bean)
                saveConfig()
                saveTempConfig()
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

    private fun setProxyOnclickListener() {
        views.forEach { view ->
            val proxyHandler = ProxyHandler() {
                LogUtils.i("click")
                if (!hasEvent(view)) {
                    LogUtils.i("准备注册事件")
                    anchorView = view
                    showDialog()
                } else {
                    LogUtils.i("已经注册事件")
                    anchorView = view
                    proxyHandlerMap[anchorView]?.onClickListener?.onClick(anchorView)
                }
            }
            HookHelper.hook(view, proxyHandler)
            proxyHandlerMap[view] = proxyHandler
        }
    }

    private fun resetOnclickListener() {
        views.forEach { view ->
            proxyHandlerMap[view]?.onClickListener?.apply {
                HookHelper.resetOnclickListener(view, this)
            }
        }
    }

    private fun hasEvent(view: View?): Boolean {
        var result = false
        config?.apply {
            result = configs.count { it.anchor == view?.simpleId.toString() } > 0
        }
        return result
    }

    private fun initFlag() {
        config?.configs?.filter { it.page == activity.javaClass.name }?.forEach { it ->
            views.firstOrNull { view -> view.simpleId.toString() == it.anchor && view.isVisible }
                ?.apply {
                    addFlag(this)
                }
        }
        tempConfig?.configs?.forEach {
            LogUtils.i(it.toString())
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

    }

    fun onResume() {
        floatWindow = FloatWindow().setupWindow(activity) {
            CommitDialog(activity, commitCallback = {
                commitConfig()
            }, closeCallback = {

            }).show()
        }
        loadConfig {
            if (!inited) {
                initFlag()
                inited = true
            }
            setProxyOnclickListener()
            scrollView?.setOnScrollChangeListener(scrollChangeListener)
        }
    }

    fun onPause() {
        floatWindow?.apply {
            activity.window.windowManager.removeView(this)
        }
        resetOnclickListener()
    }

    fun onDestroy() {

    }

    private fun loadConfig(callback: () -> Unit) {
        ConfigManager.getInstance()
            .loadAllConfig(activity, activity, true) { localConfig, tempConfig ->
                this.config = localConfig
                this.tempConfig = tempConfig
                callback.invoke()
            }
    }


    private fun saveConfig() {
        config?.apply {
            ConfigManager.getInstance().saveToLocal(activity, activity, this)
        }
    }

    private fun saveTempConfig() {
        tempConfig?.apply {
            ConfigManager.getInstance().saveTempConfig(activity, activity, this)
        }
    }

    private fun commitConfig() {
        config?.apply {
            ConfigManager.getInstance().commitConfig(activity, activity, this)
        }
    }
}
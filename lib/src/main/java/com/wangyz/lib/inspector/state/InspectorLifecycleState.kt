package com.wangyz.lib.inspector.state

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import com.wangyz.lib.R
import com.wangyz.lib.config.Config
import com.wangyz.lib.config.ConfigManager
import com.wangyz.lib.ext.lifeRecycle
import com.wangyz.lib.ext.simpleId
import com.wangyz.lib.ext.viewHierarchy
import com.wangyz.lib.inspector.dialog.CommitDialog
import com.wangyz.lib.inspector.dialog.EventDialog
import com.wangyz.lib.inspector.proxy.ProxyOnClickListener
import com.wangyz.lib.inspector.window.FloatWindow
import com.wangyz.lib.util.HookHelper
import com.wangyz.lib.util.LogUtils
import com.wangyz.lib.util.ViewHierarchyUtil
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean


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

    private val originListenerMap = mutableMapOf<View, View.OnClickListener?>()

    private val proxyListenerMap = mutableMapOf<View, ProxyOnClickListener?>()

    private val rootView by lazy {
        activity.window.decorView.findViewById<View>(android.R.id.content) as ViewGroup
    }

    private val views = mutableListOf<View>()

    private val flags = CopyOnWriteArrayList<View>()

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
                originListenerMap[anchorView]?.onClick(anchorView)
            }
        })
    }

    private val scrollChangeListener by lazy {
        (View.OnScrollChangeListener { p0, p1, p2, p3, p4 ->
            flags.forEach { view ->
                removeFlag(view)
                val tag = view.tag as? Pair<String, View>
                tag?.apply {
                    addFlag(second)
                }
            }
        })
    }

    private fun setProxyOnclickListener() {
        views.forEach { view ->
            if (view is EditText) {
                return@forEach
            }
            val proxyOnClickListener = ProxyOnClickListener {
                LogUtils.i("click")
                if (!hasEvent(view)) {
                    LogUtils.i("准备注册事件")
                    anchorView = view
                    showDialog()
                } else {
                    LogUtils.i("已经注册事件")
                    anchorView = view
                    originListenerMap[view]?.onClick(view)
                }
            }
            val listener = HookHelper.getOnclickListener(view)
            listener?.apply {
                if (this is ProxyOnClickListener) {
                    //已经设置过代理
                } else {
                    originListenerMap[view] = this
                }
            }
            proxyListenerMap[view] = proxyOnClickListener
            HookHelper.hook(view, proxyOnClickListener)
        }
    }

    private fun resetOnclickListener() {
        views.forEach { view ->
            proxyListenerMap[view]?.apply {
                HookHelper.resetOnclickListener(view, originListenerMap[view])
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
        clearFlag()
        config?.configs?.filter { it.page == activity.javaClass.name }?.forEach { it ->
            views.firstOrNull { view -> view.simpleId.toString() == it.anchor && view.isVisible }
                ?.apply {
                    addFlag(this)
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
        flags.add(imageView)

        // 调整显示区域大小
        val params = imageView.layoutParams as FrameLayout.LayoutParams
        params.width = 50
        params.height = 50
        imageView.layoutParams = params

        // 设置左上角显示
        imageView.x = (anchorRect.left).toFloat()
        imageView.y = (anchorRect.top - rootViewRect.top).toFloat()
    }

    private fun removeFlag(view: View) {
        rootView.removeView(view)
        flags.remove(view)
    }

    private fun clearFlag() {
        flags.forEach { view ->
            removeFlag(view)
        }
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
            initView()
            scrollView?.setOnScrollChangeListener(scrollChangeListener)

            MainScope().launch {
                repeat(3) {
                    withContext(Dispatchers.IO) {
                        delay(1000)
                        withContext(Dispatchers.Main) {
                            LogUtils.i("重新获取布局")
                            initView()
                        }
                    }
                }
            }.lifeRecycle(activity.lifecycle)
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

    private fun refreshViews() {
        views.clear()
        views.addAll(ViewHierarchyUtil.getAllChildViews(rootView))
    }

    private fun initView() {
        refreshViews()
        initFlag()
        setProxyOnclickListener()
    }
}
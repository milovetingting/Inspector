package com.wangyz.lib.inspector.state

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.wangyz.lib.config.Config
import com.wangyz.lib.config.ConfigManager
import com.wangyz.lib.constant.Constants
import com.wangyz.lib.ext.simpleId
import com.wangyz.lib.ext.viewHierarchy
import com.wangyz.lib.inspector.dialog.CommitDialog
import com.wangyz.lib.inspector.dialog.EventDialog
import com.wangyz.lib.inspector.proxy.ProxyOnClickListener
import com.wangyz.lib.inspector.window.FloatWindow
import com.wangyz.lib.util.DrawableUtil.addBorder
import com.wangyz.lib.util.HookHelper
import com.wangyz.lib.util.LogUtils
import com.wangyz.lib.util.TimeUtil
import com.wangyz.lib.util.ViewHierarchyUtil
import kotlinx.coroutines.*


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

    private var anchorView: View? = null

    private var floatWindow: View? = null

    private var repeatJob: Job? = null

    private var times = 0

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

    private fun addFlag(view: View?) {
        view?.apply {
            addBorder(this)
        }
    }

    private fun initFlag() {
        config?.configs?.filter { it.page == activity.javaClass.name }?.forEach { it ->
            views.firstOrNull { view -> view.simpleId.toString() == it.anchor && view.isVisible }
                ?.apply {
                    addBorder(this)
                }
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
            repeatJob = MainScope().launch {
                times = 0
                repeat(Constants.REPEAT_TIMES) {
                    withContext(Dispatchers.IO) {
                        times++
                        delay(TimeUtil.fibonacci(times.toLong()) * 1000)
                        withContext(Dispatchers.Main) {
                            LogUtils.i("重新获取布局")
                            initView()
                        }
                    }
                }
            }
        }
    }

    fun onPause() {
        floatWindow?.apply {
            activity.window.windowManager.removeView(this)
        }
        resetOnclickListener()
        repeatJob?.cancel()
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
package com.wangyz.lib.tracker.state

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.wangyz.lib.config.Config
import com.wangyz.lib.config.ConfigManager
import com.wangyz.lib.ext.simpleId
import com.wangyz.lib.ext.viewHierarchy
import com.wangyz.lib.tracker.Tracker
import com.wangyz.lib.tracker.delegate.AccessibilityDelegate
import com.wangyz.lib.util.LogUtils
import com.wangyz.lib.util.ViewHierarchyUtil


/**
 * 类描述：TrackerLifecycleState
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/14 7:44 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/14 7:44 下午
 * 修改备注：
 * @version
 */
class TrackerLifecycleState(private val activity: FragmentActivity) {

    private var config: Config? = null

    private val rootView by lazy {
        activity.window.decorView.findViewById<View>(android.R.id.content) as ViewGroup
    }

    private val views by lazy {
        ViewHierarchyUtil.getAllChildViews(rootView)
    }

    fun onCreate() {

    }

    fun onResume() {
        loadConfig {
            setAccessibilityDelegate()
        }
    }

    fun onPause() {
        resetAccessibilityDelegate()
    }

    fun onDestroy() {

    }

    private fun loadConfig(callback: () -> Unit) {
        ConfigManager.getInstance()
            .loadLocalConfig(activity, activity) { localConfig ->
                this.config = localConfig
                callback.invoke()
            }
    }

    private fun setAccessibilityDelegate() {
        views.map { view ->
            view.apply {
                accessibilityDelegate = AccessibilityDelegate(accessibilityDelegate) {
                    LogUtils.i("${this.viewHierarchy} accessibilityDelegate-->click")
                    val event = hasEvent(it)
                    if (event == null) {
                        LogUtils.i("未注册事件")
                    } else {
                        LogUtils.i("上报事件:$event")
                        Tracker.getInstance().report(event)
                    }
                }
            }
        }
    }

    private fun resetAccessibilityDelegate() {
        views.map { view ->
            view.apply {
                if (accessibilityDelegate is AccessibilityDelegate) {
                    accessibilityDelegate =
                        (accessibilityDelegate as AccessibilityDelegate).originDelegate
                }
            }
        }
    }

    private fun hasEvent(view: View?): Config.TrackConfig? {
        return config?.configs?.firstOrNull { it.anchor == view?.simpleId.toString() }
    }
}
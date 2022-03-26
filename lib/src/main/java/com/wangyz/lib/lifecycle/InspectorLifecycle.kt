package com.wangyz.lib.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.MainThread
import com.wangyz.lib.state.InspectorLifecycleState


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/26 9:01 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/26 9:01 上午
 * 修改备注：
 * @version
 */
internal class InspectorLifecycle {

    companion object {
        const val STATE_CREATE = 1
        const val STATE_RESUME = 2
        const val STATE_DESTROY = 3
    }

    var currentActivity: Activity? = null
        private set

    fun register(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycle)
    }

    fun unRegister(application: Application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycle)
    }

    private val activityLifecycle = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            onActivityChanged(activity, STATE_CREATE)
        }

        override fun onActivityResumed(activity: Activity) {
            onActivityChanged(activity, STATE_RESUME)
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity === activity) {
                onActivityChanged(null, STATE_DESTROY)
            }
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }

    @MainThread
    private fun onActivityChanged(newActivity: Activity?, state: Int) {
        currentActivity = newActivity
        currentActivity?.apply {
            val inspectorLifecycleState = InspectorLifecycleState(this)
            when (state) {
                STATE_RESUME -> {
                    inspectorLifecycleState.onResume()
                }
            }
        }
    }

}
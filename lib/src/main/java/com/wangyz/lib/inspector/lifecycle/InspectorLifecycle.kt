package com.wangyz.lib.inspector.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.fragment.app.FragmentActivity
import com.wangyz.lib.inspector.state.InspectorLifecycleState


/**
 * 类描述：生命周期
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
        const val STATE_PAUSE = 3
        const val STATE_DESTROY = 4
    }

    private var currentActivity: Activity? = null

    private val stateMap = mutableMapOf<Activity, InspectorLifecycleState?>()

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

        override fun onActivityPaused(activity: Activity) {
            onActivityChanged(activity, STATE_PAUSE)
        }

        override fun onActivityDestroyed(activity: Activity) {
            onActivityChanged(activity, STATE_DESTROY)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }

    @MainThread
    private fun onActivityChanged(newActivity: Activity, state: Int) {

        if (newActivity is FragmentActivity) {
            if (currentActivity != newActivity) {
                currentActivity = newActivity
                if (stateMap[currentActivity!!] == null) {
                    stateMap[currentActivity!!] =
                        InspectorLifecycleState(currentActivity!! as FragmentActivity)
                }
            }

            when (state) {
                STATE_CREATE -> {
                    stateMap[currentActivity]?.onCreate()
                }
                STATE_RESUME -> {
                    stateMap[currentActivity]?.onResume()
                }
                STATE_PAUSE -> {
                    stateMap[currentActivity]?.onPause()
                }
                STATE_DESTROY -> {
                    stateMap[currentActivity]?.onDestroy()
                    stateMap[currentActivity!!] = null
                    currentActivity = null
                }
            }
        }

    }

}
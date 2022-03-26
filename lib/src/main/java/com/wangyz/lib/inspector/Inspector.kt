package com.wangyz.lib.inspector

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import com.wangyz.lib.lifecycle.InspectorLifecycle
import com.wangyz.lib.util.LogUtils


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/26 8:53 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/26 8:53 上午
 * 修改备注：
 * @version
 */
class Inspector {

    companion object {

        private var INSTANCE: Inspector? = null

        fun getInstance(): Inspector {
            if (INSTANCE == null) {
                synchronized(Inspector::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Inspector()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private lateinit var application: Application

    private val lifecycle = InspectorLifecycle()

    @MainThread
    fun create(context: Context) {
        application = context.applicationContext as Application
        lifecycle.register(application)
        LogUtils.i("Inspector create")
    }

    @MainThread
    fun destroy() {
        lifecycle.unRegister(application)
    }
}
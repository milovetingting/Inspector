package com.wangyz.lib.inspector

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import com.wangyz.lib.config.ConfigLoader
import com.wangyz.lib.config.IConfig
import com.wangyz.lib.lifecycle.InspectorLifecycle
import com.wangyz.lib.util.LogUtils


/**
 * 类描述：Inspector
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

    private val configLoader by lazy {
        ConfigLoader()
    }

    private var config: IConfig? = null

    @MainThread
    fun create(context: Context) {
        loadConfig()

        application = context.applicationContext as Application
        lifecycle.register(application)

        LogUtils.i("Inspector create")
    }

    @MainThread
    fun destroy() {
        lifecycle.unRegister(application)
    }

    fun getConfig() = config

    private fun loadConfig() {
        config = configLoader.loadConfig()
    }
}
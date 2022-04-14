package com.wangyz.lib.tracker

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import com.wangyz.lib.config.Config
import com.wangyz.lib.config.ConfigManager
import com.wangyz.lib.tracker.lifecycle.TrackerLifecycle
import com.wangyz.lib.tracker.report.IReportHandler
import com.wangyz.lib.util.LogUtils


/**
 * 类描述：Tracker
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/14 7:27 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/14 7:27 下午
 * 修改备注：
 * @version
 */
class Tracker {

    companion object {

        private var INSTANCE: Tracker? = null

        fun getInstance(): Tracker {
            if (INSTANCE == null) {
                synchronized(Tracker::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Tracker()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private lateinit var application: Application

    private val lifecycle = TrackerLifecycle()

    private var reportHandler: IReportHandler? = null

    @MainThread
    fun create(context: Context, reportHandler: IReportHandler?) {
        LogUtils.i("Tracker create")

        this.reportHandler = reportHandler

        application = context.applicationContext as Application
        lifecycle.register(application)

        ConfigManager.getInstance().loadRemoteConfig(application, null)
    }

    @MainThread
    fun destroy() {
        lifecycle.unRegister(application)
    }

    fun report(event: Config.TrackConfig) {
        reportHandler?.report(event)
    }
}
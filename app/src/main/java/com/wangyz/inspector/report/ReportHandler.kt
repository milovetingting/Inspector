package com.wangyz.inspector.report

import com.wangyz.lib.config.Config
import com.wangyz.lib.tracker.report.IReportHandler
import com.wangyz.lib.util.LogUtils


/**
 * 类描述：上报事件
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/14 8:36 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/14 8:36 下午
 * 修改备注：
 * @version
 */
class ReportHandler : IReportHandler {
    override fun report(event: Config.TrackConfig) {
        LogUtils.i("正在上报事件:$event")
        LogUtils.i("上报事件成功")
    }
}
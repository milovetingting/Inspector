package com.wangyz.lib.tracker.report

import com.wangyz.lib.config.Config


/**
 * 类描述：事件上报
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/14 8:31 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/14 8:31 下午
 * 修改备注：
 * @version
 */
interface IReportHandler {
    /**
     * 上报事件
     */
    fun report(event: Config.TrackConfig)
}
package com.wangyz.lib.config


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 11:52 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 11:52 上午
 * 修改备注：
 * @version
 */
data class Config(
    var configs: MutableList<TrackConfig>
) : IConfig {


    data class TrackConfig(
        var eventId: String,
        var eventName: String,
        var anchor: String,
        var page: String
    )
}
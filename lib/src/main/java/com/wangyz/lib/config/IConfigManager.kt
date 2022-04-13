package com.wangyz.lib.config


/**
 * 类描述：配置管理类接口
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 11:47 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 11:47 上午
 * 修改备注：
 * @version
 */
interface IConfigManager<T : IConfig> {
    /**
     * 加载配置
     */
    fun loadConfig(): T

    /**
     * 加载临时配置
     */
    fun loadTempConfig(): T

    /**
     * 保存到本地
     */
    fun saveToLocal(config: T)

    /**
     * 保存临时配置
     */
    fun saveTempConfig(config: T)

    /**
     * 提交配置
     */
    fun commitConfig(config: T)
}
package com.wangyz.lib.config


/**
 * 类描述：配置加载器接口
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 11:47 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 11:47 上午
 * 修改备注：
 * @version
 */
interface IConfigLoader<T : IConfig> {
    fun loadConfig(): T
}
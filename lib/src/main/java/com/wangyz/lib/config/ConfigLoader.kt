package com.wangyz.lib.config


/**
 * 类描述：配置加载器
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 11:50 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 11:50 上午
 * 修改备注：
 * @version
 */
class ConfigLoader : IConfigLoader<Config> {
    override fun loadConfig(): Config {
        return Config(mutableListOf())
    }
}
package com.wangyz.lib.config

import android.content.Context
import com.google.gson.Gson
import com.wangyz.lib.util.LogUtils
import java.io.File


/**
 * 类描述：配置管理类
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 11:50 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 11:50 上午
 * 修改备注：
 * @version
 */
class ConfigManager(private val context: Context) : IConfigManager<Config> {

    private val gson by lazy {
        Gson()
    }

    override fun loadConfig(): Config {
        LogUtils.i("获取配置")
        val file = File(context.filesDir, "configs.json")
        if (file.exists()) {
            val configs = file.readText()
            val list = gson.fromJson(configs, Array<Config.TrackConfig>::class.java).toMutableList()
            return Config(list)
        }
        return Config(mutableListOf())
    }

    override fun loadTempConfig(): Config {
        LogUtils.i("获取临时配置")
        val file = File(context.getExternalFilesDir(null), "temp_configs.json")
        if (file.exists()) {
            val configs = file.readText()
            val list = gson.fromJson(configs, Array<Config.TrackConfig>::class.java).toMutableList()
            return Config(list)
        }
        return Config(mutableListOf())
    }

    override fun saveToLocal(config: Config) {
        LogUtils.i("保存至本地:$config")
        val configs = gson.toJson(config.configs)
        val file = File(context.filesDir, "configs.json")
        file.writeText(configs)
    }

    override fun saveTempConfig(config: Config) {
        LogUtils.i("保存临时配置:$config")
        val configs = gson.toJson(config.configs)
        val file = File(context.getExternalFilesDir(null), "temp_configs.json")
        file.writeText(configs)
    }

    override fun commitConfig(config: Config) {
        LogUtils.i("提交并删除临时配置:$config")
        val file = File(context.getExternalFilesDir(null), "temp_configs.json")
        file.delete()
    }

}
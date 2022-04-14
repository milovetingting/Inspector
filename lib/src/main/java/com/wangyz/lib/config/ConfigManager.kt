package com.wangyz.lib.config

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.wangyz.lib.ext.lifeRecycle
import com.wangyz.lib.util.LogUtils
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.*


/**
 * 类描述：配置管理类
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/28 11:50 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/28 11:50 上午
 * 修改备注：
 * @version
 */
class ConfigManager : IConfigManager<Config> {

    companion object {

        private var INSTANCE: ConfigManager? = null

        fun getInstance(): ConfigManager {
            if (INSTANCE == null) {
                synchronized(ConfigManager::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = ConfigManager()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private val gson by lazy {
        Gson()
    }

    override fun loadRemoteConfig(
        context: Context,
        owner: LifecycleOwner?,
        callback: (Config) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val config = doLoadRemoteConfig(context)
            withContext(Dispatchers.Main) {
                callback.invoke(config)
            }
        }.lifeRecycle(owner?.lifecycle)
    }

    override fun loadLocalConfig(
        context: Context,
        owner: LifecycleOwner?,
        callback: (Config) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val config = doLoadLocalConfig(context)
            withContext(Dispatchers.Main) {
                callback.invoke(config)
            }
        }.lifeRecycle(owner?.lifecycle)
    }

    override fun loadTempConfig(
        context: Context,
        owner: LifecycleOwner?,
        callback: (Config) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val config = doLoadTempConfig(context)
            withContext(Dispatchers.Main) {
                callback.invoke(config)
            }
        }.lifeRecycle(owner?.lifecycle)
    }

    override fun loadAllConfig(
        context: Context,
        owner: LifecycleOwner?,
        delay: Boolean?,
        callback: (Config?, Config?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            if (delay == true) {
                delay(1000)
            }
            val configs = doLoadAllConfig(context)
            withContext(Dispatchers.Main) {
                callback.invoke(configs.first, configs.second)
            }
        }.lifeRecycle(owner?.lifecycle)
    }

    override fun saveToLocal(
        context: Context,
        owner: LifecycleOwner?,
        config: Config,
        callback: (Boolean) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = doSaveToLocal(context, config)
            withContext(Dispatchers.Main) {
                callback.invoke(result)
            }
        }.lifeRecycle(owner?.lifecycle)
    }

    override fun saveTempConfig(
        context: Context,
        owner: LifecycleOwner?,
        config: Config,
        callback: (Boolean) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = doSaveTempConfig(context, config)
            withContext(Dispatchers.Main) {
                callback.invoke(result)
            }
        }.lifeRecycle(owner?.lifecycle)
    }

    override fun commitConfig(
        context: Context,
        owner: LifecycleOwner?,
        config: Config,
        callback: (Boolean) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = doCommitConfig(context, config)
            withContext(Dispatchers.Main) {
                callback.invoke(result)
            }
        }.lifeRecycle(owner?.lifecycle)
    }

    private fun doLoadRemoteConfig(context: Context): Config {
        LogUtils.i("获取远程配置")
        val config = Config(mutableListOf())
        //todo 从远程下载最新配置,此处先模拟
        val file = File(context.filesDir, "configs.json")
        if (file.exists()) {
            val configs = file.readText()
            val list =
                gson.fromJson(configs, Array<Config.TrackConfig>::class.java)
                    .toMutableList()
            config.configs = list
            //先备份原来的文件
            val localFile = File(context.filesDir, "local_configs.json")
            if (localFile.exists()) {
                localFile.copyTo(File(context.filesDir, "local_configs_bak.json"), true)
            }
            //复制一份到本地
            file.copyTo(File(context.filesDir, "local_configs.json"), true)
        } else {
            file.createNewFile()
        }
        return config
    }

    private fun doLoadLocalConfig(context: Context): Config {
        LogUtils.i("获取本地配置")
        val remoteConfig = File(context.filesDir, "configs.json")
        var count = 0
        while (!remoteConfig.exists() && count < 5) {
            Thread.sleep(1000)
            count++
        }
        return if (!remoteConfig.exists()) {
            doLoadRemoteConfig(context)
        } else {
            val config = Config(mutableListOf())
            val file = File(context.filesDir, "local_configs.json")
            if (file.exists()) {
                val configs = file.readText()
                val list =
                    gson.fromJson(configs, Array<Config.TrackConfig>::class.java).toMutableList()
                config.configs = list
            }
            config
        }
    }

    private fun doLoadTempConfig(context: Context): Config {
        LogUtils.i("获取临时配置")
        val config = Config(mutableListOf())
        val file = File(context.getExternalFilesDir(null), "temp_configs.json")
        if (file.exists()) {
            val configs = file.readText()
            val list =
                gson.fromJson(configs, Array<Config.TrackConfig>::class.java).toMutableList()
            config.configs = list
        }
        return config
    }

    private fun doLoadAllConfig(context: Context): Pair<Config, Config> {
        LogUtils.i("获取所有配置")
        val localConfig = doLoadLocalConfig(context)
        val tempConfig = doLoadTempConfig(context)
        return Pair(localConfig, tempConfig)
    }

    private fun doSaveToLocal(
        context: Context,
        config: Config
    ): Boolean {
        LogUtils.i("保存至本地:$config")
        val configs = gson.toJson(config.configs)
        val file = File(context.filesDir, "local_configs.json")
        file.writeText(configs)
        val remoteConfig = File(context.filesDir, "configs.json")
        if (!remoteConfig.exists()) {
            remoteConfig.createNewFile()
        }
        file.copyTo(remoteConfig, true)
        return true
    }

    private fun doSaveTempConfig(
        context: Context,
        config: Config
    ): Boolean {
        LogUtils.i("保存临时配置:$config")
        val configs = gson.toJson(config.configs)
        val file = File(context.getExternalFilesDir(null), "temp_configs.json")
        file.writeText(configs)
        return true
    }

    private fun doCommitConfig(
        context: Context,
        config: Config
    ): Boolean {
        LogUtils.i("提交并删除临时配置:$config")
        val file = File(context.getExternalFilesDir(null), "temp_configs.json")
        file.delete()
        return true
    }
}
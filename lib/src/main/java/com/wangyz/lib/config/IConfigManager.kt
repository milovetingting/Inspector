package com.wangyz.lib.config

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner


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
     * 加载远程配置
     */
    fun loadRemoteConfig(context: Context, owner: LifecycleOwner?, callback: (T) -> Unit = {})

    /**
     * 加载本地配置
     */
    fun loadLocalConfig(context: Context, owner: LifecycleOwner?, callback: (T) -> Unit = {})

    /**
     * 加载临时配置
     */
    fun loadTempConfig(context: Context, owner: LifecycleOwner?, callback: (T) -> Unit = {})

    /**
     * 加载所有配置信息
     */
    fun loadAllConfig(
        context: Context,
        owner: LifecycleOwner?,
        delay: Boolean?,
        callback: (T?, T?) -> Unit = { localConfig, tempConfig -> }
    )

    /**
     * 保存到本地
     */
    fun saveToLocal(
        context: Context,
        owner: LifecycleOwner?,
        config: T,
        callback: (Boolean) -> Unit = {}
    )

    /**
     * 保存临时配置
     */
    fun saveTempConfig(
        context: Context,
        owner: LifecycleOwner?,
        config: T,
        callback: (Boolean) -> Unit = {}
    )

    /**
     * 提交配置
     */
    fun commitConfig(
        context: Context,
        owner: LifecycleOwner?,
        config: T,
        callback: (Boolean) -> Unit = {}
    )
}
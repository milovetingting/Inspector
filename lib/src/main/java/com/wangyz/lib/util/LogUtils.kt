package com.wangyz.lib.util

import android.util.Log
import com.wangyz.lib.constant.Constants
import java.lang.reflect.Method


/**
 * 类描述：日志类
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/25 5:11 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/25 5:11 下午
 * 修改备注：
 * @version
 */
object LogUtils {
    /**
     * 打印info信息
     */
    fun i(msg: String) {
        if (showLog()) {
            Log.v(Constants.TAG, msg)
        }
    }

    private fun showLog(): Boolean {
        return getProperties(Constants.PROPERTIES_SHOW_LOG) == "1"
    }

    private fun getProperties(key: String?): String? {
        var value: String? = null
        try {
            val clazz = Class.forName("android.os.SystemProperties")
            val get: Method = clazz.getMethod("get", String::class.java)
            value = get.invoke(clazz, key) as String?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }
}
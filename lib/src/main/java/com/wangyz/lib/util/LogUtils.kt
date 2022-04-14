package com.wangyz.lib.util

import android.util.Log
import com.wangyz.lib.constant.Constants


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
        if (Constants.SHOW_LOG) {
            Log.v(Constants.TAG, msg)
        }
    }
}
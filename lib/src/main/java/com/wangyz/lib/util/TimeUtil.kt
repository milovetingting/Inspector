package com.wangyz.lib.util


/**
 * 类描述：TimeUtil
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/15 4:35 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/15 4:35 下午
 * 修改备注：
 * @version
 */
object TimeUtil {
    fun fibonacci(number: Long): Long {
        return if (number == 0L || number == 1L) number else fibonacci(number - 1) + fibonacci(
            number - 2
        )
    }
}
package com.wangyz.lib.inspector.proxy

import android.view.View

/**
 * 类描述：Handler
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/12 4:44 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/12 4:44 下午
 * 修改备注：
 * @version
 */
class ProxyHandler(
    var onClickListener: View.OnClickListener? = null,
    private val perform: () -> Unit
) {
    fun perform() {
        perform.invoke()
    }
}
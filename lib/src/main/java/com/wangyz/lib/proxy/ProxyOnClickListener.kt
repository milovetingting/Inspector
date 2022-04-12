package com.wangyz.lib.proxy

import android.view.View
import com.wangyz.lib.util.LogUtils


/**
 * 类描述：点击事件代理类
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/12 4:22 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/12 4:22 下午
 * 修改备注：
 * @version
 */
class ProxyOnClickListener(
    private val proxyHandler: ProxyHandler
) :
    View.OnClickListener {

    override fun onClick(view: View?) {
        LogUtils.i("hook click")
        proxyHandler.perform()
    }
}
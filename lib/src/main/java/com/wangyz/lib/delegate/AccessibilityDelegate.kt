package com.wangyz.lib.delegate

import android.view.View
import android.view.accessibility.AccessibilityEvent


/**
 * 类描述：自定义delegate
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/25 6:47 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/25 6:47 下午
 * 修改备注：
 * @version
 */
class AccessibilityDelegate(
    val originDelegate: View.AccessibilityDelegate?,
    private val perform: (view: View?) -> Unit
) :
    View.AccessibilityDelegate() {
    override fun sendAccessibilityEvent(host: View?, eventType: Int) {
        host?.apply {
            originDelegate?.apply {
                this.sendAccessibilityEvent(host, eventType)
            }
            when (eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    perform.invoke(host)
                }
            }
        }
    }
}
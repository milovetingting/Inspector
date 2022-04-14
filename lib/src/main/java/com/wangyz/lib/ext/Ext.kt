package com.wangyz.lib.ext

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.wangyz.lib.hierarchy.ViewHierarchy
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren


/**
 * 类描述：扩展类
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/25 5:08 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/25 5:08 下午
 * 修改备注：
 * @version
 */

/**
 * 获取简短名称
 */
val Any?.simpleName: String
    get() =
        if (this == null) "null" else {
            val cn = this::class.java.simpleName
            if (cn.isNullOrBlank()) {
                this::class.java.name
            } else {
                cn
            }
        }

/**
 * 获取简短id
 */
val View.simpleId: Int
    get() = "${ViewHierarchy.getHierarchy(this).map { it.simpleName }.joinToString("/")}@${
        ViewHierarchy.getIndexAtParent(this)
    }".hashCode()


fun Job.lifeRecycle(lifecycle: Lifecycle?): Job {
    lifecycle?.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                cancelChildren()
                cancel()
            }
        }
    })
    return this
}
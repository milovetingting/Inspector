package com.wangyz.inspector.ext

import android.view.View


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
    get() = "${this.simpleName}_${this.left}_${this.top}_${this.right}_${this.bottom}".hashCode()
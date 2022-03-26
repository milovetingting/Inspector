package com.wangyz.inspector

import android.app.Application
import com.wangyz.lib.inspector.Inspector


/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/26 10:02 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/26 10:02 上午
 * 修改备注：
 * @version
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Inspector.getInstance().create(this)
    }
}
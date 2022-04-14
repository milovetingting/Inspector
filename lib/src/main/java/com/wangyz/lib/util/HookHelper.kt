package com.wangyz.lib.util

import android.view.View
import com.wangyz.lib.inspector.proxy.ProxyHandler
import com.wangyz.lib.inspector.proxy.ProxyOnClickListener
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * 类描述：HookHelper
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/12 4:24 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/12 4:24 下午
 * 修改备注：
 * @version
 */
object HookHelper {
    fun hook(view: View, proxyHandler: ProxyHandler) {
        try {
            //首先进行反射执行View类的getListenerInfo()方法，拿到v的mListenerInfo对象，这个对象就是点击事件的持有者
            val method: Method = View::class.java.getDeclaredMethod("getListenerInfo")
            //由于getListenerInfo()方法并不是public的，所以要加这个代码来保证访问权限
            method.isAccessible = true
            //这里拿到的就是mListenerInfo对象，也就是点击事件的持有者
            val mListenerInfo: Any = method.invoke(view)
            //获取到当前的点击事件
            val clz = Class.forName("android.view.View\$ListenerInfo")
            //获取内部类的表示方法
            val field: Field = clz.getDeclaredField("mOnClickListener")
            //保证访问权限
            field.isAccessible = true
            //获取真实的mOnClickListener对象
            val onClickListenerInstance = field.get(mListenerInfo) as View.OnClickListener
            proxyHandler.onClickListener = onClickListenerInstance
            // 用自定义的 OnClickListener 替换原始的 OnClickListener
            val hookedOnClickListener: View.OnClickListener = ProxyOnClickListener(proxyHandler)
            //设置到"持有者"中
            field.set(mListenerInfo, hookedOnClickListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetOnclickListener(view: View, listener: View.OnClickListener) {
        try {
            //首先进行反射执行View类的getListenerInfo()方法，拿到v的mListenerInfo对象，这个对象就是点击事件的持有者
            val method: Method = View::class.java.getDeclaredMethod("getListenerInfo")
            //由于getListenerInfo()方法并不是public的，所以要加这个代码来保证访问权限
            method.isAccessible = true
            //这里拿到的就是mListenerInfo对象，也就是点击事件的持有者
            val mListenerInfo: Any = method.invoke(view)
            //获取到当前的点击事件
            val clz = Class.forName("android.view.View\$ListenerInfo")
            //获取内部类的表示方法
            val field: Field = clz.getDeclaredField("mOnClickListener")
            //保证访问权限
            field.isAccessible = true
            //设置到"持有者"中
            field.set(mListenerInfo, listener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
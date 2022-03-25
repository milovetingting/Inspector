package com.wangyz.inspector.hierarchy

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent


object ViewHierarchy {

    /**
     * 获取层级关系列表
     */
    fun getHierarchy(target: View): List<View> {
        val result = mutableListOf(target)
        var parent: ViewParent? = target.parent
        while (parent is View) {
            result.add(parent)
            parent = (parent as View).parent
        }
        result.reverse()
        return result
    }

    /**
     * 获取所有子View
     */
    fun getAllChildViews(view: View): MutableList<View> {
        val children: MutableList<View> = ArrayList()
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                children.add(child)
                children.addAll(getAllChildViews(child))
            }
        }
        return children
    }


}
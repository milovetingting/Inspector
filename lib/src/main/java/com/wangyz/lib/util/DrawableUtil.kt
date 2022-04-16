package com.wangyz.lib.util

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View


/**
 * 类描述：DrawableUtil
 * 创建人：wangyuanzhi
 * 创建时间：2022/4/16 10:29 上午
 * 修改人：wangyuanzhi
 * 修改时间：2022/4/16 10:29 上午
 * 修改备注：
 * @version
 */
object DrawableUtil {

    fun addBorder(view: View) {
        view.postDelayed({
            val background = view.background
            background?.apply {
                val bitmap = drawableToBitmap(this, view.width, view.height)
                bitmap?.apply {
                    val canvas = Canvas(bitmap)
                    val paint = Paint()
                    paint.color = Color.parseColor("#F44336")
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 10f
                    canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
                    view.background = BitmapDrawable(bitmap)
                }
            } ?: run {
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val paint = Paint()
                paint.color = Color.parseColor("#F44336")
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 10f
                canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
                view.background = BitmapDrawable(bitmap)
            }
        }, 100)
    }

    private fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap? {
        // 取 drawable 的长宽
        val w = width
        val h = height

        // 取 drawable 的颜色格式
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        // 建立对应 bitmap
        val bitmap = Bitmap.createBitmap(w, h, config)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }
}
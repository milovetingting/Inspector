package com.wangyz.lib.window;

import static com.wangyz.lib.Constants.WINDOW_PERMISSION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * 类描述：
 * 创建人：wangyuanzhi
 * 创建时间：2022/3/25 7:03 下午
 * 修改人：wangyuanzhi
 * 修改时间：2022/3/25 7:03 下午
 * 修改备注：
 */
public abstract class BaseFloatWindow {

    private WindowManager.LayoutParams wmParams = null;

    private WindowManager mWindowManager = null;

    private View mRootView = null;

    private boolean isShown = false;

    private Activity activity;

    private boolean hasPermission = false;

    public BaseFloatWindow(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        initWindow(activity);
        initPermission(activity);
    }

    private void initWindow(Context context) {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //设置window type ，根据android系统版本选择不同的type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作） FLAG_ALT_FOCUSABLE_IM
        // wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //弹出的View收不到Back键的事件
        // wmParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        // 设置一下flag可以让window内部与外部事件互不干扰，不设置，可能会出现window内部接收不到点击事件或者外部接收不到事件
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity偏移
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        LayoutInflater inflater = LayoutInflater.from(context);
        mRootView = initView(inflater);
    }

    private void initPermission(Activity activity) {
        //申请悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + mRootView.getContext().getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, WINDOW_PERMISSION);
        } else {
            hasPermission = true;
            show();
        }
    }

    public void setPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public void show() {
        if (hasPermission) {
            if (!isShown && null != mRootView) {
                //添加mFloatLayout
                mWindowManager.addView(mRootView, wmParams);
                isShown = true;
            }
        } else {
            initPermission(activity);
        }

    }

    public void hide() {
        if (isShown && null != mRootView) {
            mWindowManager.removeView(mRootView);
            isShown = false;
        }
    }

    public abstract View initView(LayoutInflater inflater);
}

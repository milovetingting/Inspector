## 前言

Kotlin写的一个埋点demo,适用于androidx。

> 分为`配置端`和`用户端`。
>
> `配置端`提供界面，供配置人员埋点，埋点完成后，可导出配置文件,配置文件生成在/data/data/包名/files/configs.json,可自行修改源码。
>
> `用户端`在应用启动后，拉取配置文件，根据配置文件，动态埋点，无需重新发版。

## 使用

### 配置端

> 下载源码，在自己的工程中直接依赖源码里的lib模块。在Application的onCreate方法中初始化

```kotlin
Inspector.getInstance().create(this)
```

### 用户端

> 下载源码，在自己的工程中直接依赖源码里的lib模块。实现`IReportHandler`接口

```kotlin
class ReportHandler : IReportHandler {
    override fun report(event: Config.TrackConfig) {
        LogUtils.i("正在上报事件:$event")
        LogUtils.i("上报事件成功")
    }
}
```

> 在Application的onCreate方法中初始化

```kotlin
Tracker.getInstance().create(this, ReportHandler())
```


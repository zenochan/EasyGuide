# EasyGuide
> 分析别人家的源码作为学习，方便以后满足需求的修改[smuyyh/EasyGuideView](https://github.com/smuyyh/EasyGuideView)

## 实现思路

#### 自定义透明高亮遮罩 View

1. `EasyGuideView : RelativeLayout`
1. 计算高亮视图在窗口的 `RectF`
1. 绘制透明区域 bitmap
    > 1. 根据 RectF 创建 Bitmap `Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)`
    > 1. 创建画布，将内容绘制到 Bitmap `new Canvas(bitmap)`
    > 1. 绘制背景色 `canvas.drawColor(colorInt)`
    > 1. 初始化画笔 (抗锯齿，清楚模式，模糊边缘)
    >     ```kotlin
    >     paint.isAntiAlias = true
    >     paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.INNER)
    >     paint.mode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    >     ```
    > 1. 根据形状绘制到 `bitmap` 上
1. 重写 onDraw ，绘制 bitmap, 和一个正好不覆盖高亮区域而且覆盖完窗口的矩形边框

> **PS**  
> 看着坐着的代码，本想改写直接绘制背景色，然后在通过 clear 扣掉高亮的部分，[然而扣掉后就变黑色了](http://www.it1352.com/135923.html),扑街

#### 添加视图
1. 获取跟视图 `val root = Activity.window.decorView as FrameView`
2. 将自定义的视图添加到根视图
3. 在自定义视图里为所欲为吧！

## 总结
- 卧槽，这个方法可以用来优化我的购物车动画项目拉！腻害了,不用添加一个容器视图了


```groovy
maven {url "http://maven.mjtown.cn/"}
compile "name.zeno:EasyGuide:0.0.1"
```

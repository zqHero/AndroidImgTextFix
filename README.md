
//
##一，需求：Android 使用TextView实现  图文混排
	android项目中很多图文混排问题，如果使用简单的TextView和ImageView很难实现我们想要的效果。但Android TextView其实很强大，如果简单只用于显示我们需要的文本信息，就太浪费资源了。最近也因为项目中遇到了混排问题，才开始探索这些东西。    有意学习者 可以看看。   会的可以忽略。
	谢谢。
	
```
//普通的  图文显示简单略过，我们应该知道TextView 的android:drawableLeft以及android:drawableRight android:drawableBottom 以及 android:drawableTop   可以很简单的实现  图文混排
<TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:layout_gravity="center"
        android:drawableBottom="@mipmap/ic_launcher"
        android:drawableLeft="@mipmap/ic_launcher"
        android:drawableTop="@mipmap/ic_launcher"
        android:drawableRight="@mipmap/ic_launcher"
        android:text="文本信息"
        />

```

##二，首先 我们要有一段富文本：无论是从网上下载过来的，还是我们本地生成的。

```
  private static String netStr = " <div id=\"body\">\n" +
            "            <div id=\"main\">\n" +
            "                <div class=\"main\">\n" +
            "                        <div class=\"ad_class\" pngo4j1=\"\" hidden=\"\">\n" +
            "<div class=\"notice tracking-ad\" data-mod=\"popu_3\">\n" +
            "\n" +
            "\n" +
            "<a href=\"http://blog.csdn.net/blogdevteam/article/details/72917467\" target=\"_blank\">\n" +
            "<font color=\"red\"><strong>征文 | 从高考，到程序员</strong></font></a>\n" +
            "\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
            "\n" +
            "<a href=\"http://edu.csdn.net/huiyiCourse/detail/422?ref=text\" target=\"_blank\">\n" +
            "<font color=\"blue\"><strong>深度学习与TensorFlow入门一课搞定！</strong></font></a>\n" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\n" +
            "\n" +
            "\n" ";
            
```

##三，下面主要尝试  富文本的操作：含有网络图片  以及img url  的富文本操作：

###1，第一次尝试：

```
    @Override
    public void updateView() {
        //刷新数据
        String str = presenter.getBean().getCharSque();
        netData.setText(Html.fromHtml(str));
    }
```
//结果  显示：

![这里写图片描述](http://img.blog.csdn.net/20170614210509608?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzIzMzA5Nw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


显然结果差强人意。

###2,第二次尝试： 添加IamgeGet 类：

	使用Html自带的  方法，使用  三个参数的  方法：
```
@Override
    public void updateView() {
        //刷新数据
        String str = presenter.getBean().getCharSque().trim();
        Spanned spanned = Html.fromHtml(str, new MImageGetter(), null);
        netData.setText(spanned);
    }
```

	重写ImageGet类  获取图片：
```
   //1，自定义  IamgGetter  获取 网络图片数据：
    private class MImageGetter implements Html.ImageGetter {
        @Override
        public Drawable getDrawable(String source) {
            //异步 获取 网络图片并返回：  这里使用  Glide 获取 图片  当然你也可以使用   其他：
            //1,设置 图片占位符：
            final LevelListDrawable draw = new LevelListDrawable();
            Drawable empty = getResources().getDrawable(R.mipmap.ic_launcher);
            draw.addLevel(0, 0, empty);
            draw.setBounds(0, 0, 100, 100);

            final String url = source;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    //请求  图片
                    try {
                        //2,获取 网络图片   该方法  为同步方法   需在子线程中执行
                        bitmap =
                                Glide.with(MainActivity.this)
                                        .load(url)
                                        .asBitmap()
                                        .into(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                                        .get();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("info", "异常");
                    }
                    //3,设置  图片
                    runUiThr(bitmap, draw);
                }
            }).start();
            return draw;
        }
    }
```

// 2，设置图片

```
private void runUiThr(final Bitmap bitmap, final LevelListDrawable draw) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    BitmapDrawable drawable = new BitmapDrawable(bitmap);
                    draw.addLevel(1, 1, drawable);
                    draw.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    draw.setLevel(1);

                    CharSequence charSequence = netData.getText();
                    netData.setText(charSequence);
                    netData.invalidate();
                }
            }
        });
    }
```

//3，TODO 别忘了添加权限：

```

    <uses-permission android:name="android.permission.INTERNET"/>
```

最后效果：

![这里写图片描述](http://img.blog.csdn.net/20170614214811395?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzIzMzA5Nw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

网路图片  已经完全展示出来。当然如果大家对  展示效果不满意你还可以重写ImageSpan。完美展示。


###3,加载本地文本数据  根据正则匹配资源图片

效果：

![这里写图片描述](http://img.blog.csdn.net/20170614220805143?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMzIzMzA5Nw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


直接上代码：

```
 //初始化 本地数据  并显示：   本地数据  并没什么
        final SpannableStringBuilder builder =
                getSpanStrBuilder("YW  百年学典-同步-英语六年级下册（4年级）P1-1 \n" +
                        "YW  百年学典-同步-英语六年级下册（4年级）P1-1 \n" +
                        "YW  百年学典-同步-英语六年级下册（4年级）P1-1 \n" +
                        "YW  百年学典-同步-英语六年级下册（4年级）P1-1", "YW");
        locaData.setText(builder);
```

```
   //-----------------------------------------------------加载 本地图片------------------------------------------------------------------------
    @NonNull
    private SpannableStringBuilder getSpanStrBuilder(CharSequence text, String rex) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = Pattern.compile(rex).matcher(text);
        Drawable draw = getResources().getDrawable(R.mipmap.ic_yuwen);
        draw.setBounds(0, 0, draw.getIntrinsicWidth(), draw.getIntrinsicWidth());
        while (matcher.find()) {
            builder.setSpan(
                    new MyImageSpan(draw), matcher.start(), matcher
                            .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    //显示 位置
    class MyImageSpan extends ImageSpan {
        public MyImageSpan(Drawable drawable) {
            super(drawable);
        }
        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            Drawable drawable = getDrawable();
            int transY = (y + fm.descent + y + fm.ascent) / 2
                    - drawable.getBounds().bottom / 2;
            canvas.save();
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }
```


完毕。







package com.hero.zhaoq.androidimgtextfix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hero.zhaoq.androidimgtextfix.presenter.MainPresenter;
import com.hero.zhaoq.androidimgtextfix.view.MainView;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Android  图文混排  实例：
 */
public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter presenter;
    private Unbinder unbinder;

    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.local_data_tv)
    TextView locaData;
    @BindView(R.id.net_data_tv)
    TextView netData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        presenter = new MainPresenter(this);
        presenter.init();


        //初始化 本地数据  并显示：   本地数据  并没什么
        final SpannableStringBuilder builder =
                getSpanStrBuilder("YW  百年学典-同步-英语六年级下册（4年级）P1-1 \n" +
                        "YW  百年学典-同步-英语六年级下册（4年级）P1-1 \n" +
                        "YW  百年学典-同步-英语六年级下册（4年级）P1-1 \n" +
                        "YW  百年学典-同步-英语六年级下册（4年级）P1-1", "YW");
        locaData.setText(builder);
    }

    @Override
    public void showLoading() {
        progress_bar.setVisibility(View.VISIBLE);
        netData.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progress_bar.setVisibility(View.GONE);
        netData.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateView() {
        //刷新数据
        String str = presenter.getBean().getCharSque().trim();
        Spanned spanned = Html.fromHtml(str, new MImageGetter(), null);
        netData.setText(spanned);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        unbinder = null;
        presenter = null;
    }
    //--------------------------------------------------加载  网络图片------------------------------------------------------------
    //自定义  IamgGetter  获取 网络图片数据：
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
                    updateNetTxtView(bitmap,draw);
                }
            }).start();
            return draw;
        }
    }
    @Override
    public void updateNetTxtView(final Bitmap bitmap, final LevelListDrawable draw) {
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

}

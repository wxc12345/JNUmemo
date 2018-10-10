package com.example.wxc.memo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;
import org.litepal.tablemanager.Connector;

import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    DisplayMetrics dm = new DisplayMetrics();
    public int NowWeek = 0;
    private Button button_setting;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button_setting = (Button) findViewById(R.id.setting);
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        getTime();
        Connector.getDatabase();//创建数据库

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        showData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int count = LitePal.count(Note.class);
        if (count >= 1) {
            setNotes();
        }
    }

    void getTime(){//获取当前周
        DateFormat df = new SimpleDateFormat("yyyyMMdd");	//设置日期格式
        try {
            if (NowWeek == 0) {
                Date d1 = df.parse("20180903");//开学日期
                Date d2 = new Date(System.currentTimeMillis());//当前日期
                long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别
                long days;
                days = diff / (1000 * 60 * 60 * 24);
                if (days >= 0) {
                    NowWeek = (int) days / 7 + 1;
                } else {
                    NowWeek = 1;
                }
            }
        } catch (Exception e) {
        }
        TextView textView = (TextView) findViewById(R.id.now_week);
        String weekChinese = "第" + NowWeek + "周";
        textView.setText(weekChinese);
    }

    //隐藏标题栏上的应用名
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
        }
    }

    //将日程显示在界面上
    public void setNotes() {
        Log.d(TAG, "进入setNotes");
        List<Note> notes = LitePal.findAll(Note.class);
        for (Note note : notes) {
            Log.d(TAG, note.getTheme() + note.getNote());
            if (note.getWeek() == NowWeek) {
                createCardView(note.getId(),note.getStart_hour(), note.getEnd_hour(), note.getDay(), note.getTheme(), note.getNote());
            }
        }
    }

    //接受数据，动态创建cardview并显示在正确位置上
    public void createCardView(final int id, int start_hour, int end_hour, int day, String theme, String note) {
        Log.d(TAG, "进入createCardView ");
        Log.d(TAG, "周几" + day);
        if (day == 0) {
            day = 7;
        }
        CardView cardView = new CardView(this);

        //cardview的相关属性
        cardView.setCardElevation(5);//阴影
        cardView.setRadius(20);//圆角
        int[] backGroundColor = {R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6};
        Random rand = new Random();
        int i = rand.nextInt(6);
        cardView.setCardBackgroundColor(getResources().getColor(backGroundColor[i]));//随机颜色

        //暂定为只能添加6-23点的数据
        if (start_hour < 6) {
            start_hour = 6;
        } else if (start_hour >= 24) {
            start_hour = 23;
        }
        if (end_hour <= 6) {
            end_hour = 7;
        } else if (end_hour > 24) {
            end_hour = 24;
        }

        //设置文本的相关属性
        TextView textView = new TextView(this);
        if ((end_hour - start_hour) >= 3) {
            textView.setText(theme + "\n" + note + "\n" + start_hour + "--" + end_hour);
        } else if ((end_hour - start_hour) >= 2) {
            textView.setText(theme + "\n" + note);
        } else {
            textView.setText(theme);
        }
        textView.setTextColor(Color.argb(255, 255, 255, 255));//颜色
        textView.setGravity(Gravity.CENTER);//居中
        textView.setTypeface(Typeface.SANS_SERIF);//字体
        textView.setTextSize(12);//字号

        cardView.addView(textView);//将文本添加到cardview中
        getWindowManager().getDefaultDisplay().getMetrics(dm);//获取屏幕的相关数据
        //设置长，宽
        RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams((dm.widthPixels / 7 - 10), ((dm.heightPixels - Dp2Px(this, 86)) * (end_hour - start_hour) / 18 - 10));
        //设置离顶部的距离，其中toolbar加上周几一共86dp高
        clp.topMargin = Dp2Px(this, 86) + (dm.heightPixels - Dp2Px(this, 86)) * (start_hour - 6) / 18 + 5;
        //设置离左边的距离
        clp.leftMargin = dm.widthPixels / 7 * (day - 1) + 5;
        cardView.setLayoutParams(clp);

        //将CardView添加到布局中
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
        relativeLayout.addView(cardView);

       cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    //dp转像素
    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    void showData() {
        List<Note> notes = LitePal.findAll(Note.class);
        for (Note note : notes) {
            Log.d(TAG, "showData:" + note.getTheme());
        }
    }
}

package com.example.wxc.memo;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditorActivity extends AppCompatActivity {

    private static final String TAG = "EditorActivity";
    private TimePickerView timePickerView;
    private String theme;
    private String notes;
    private Date start_date;
    private Date end_date;
    private EditText editText_theme;
    private EditText editText_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //初始化时间，结束比开始晚1小时
        start_date = new Date(System.currentTimeMillis());
        end_date = new Date(System.currentTimeMillis() + (long) 1 * 60 * 60 * 1000);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Button button_return = (Button) findViewById(R.id.editor_return);
        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button button_delete = (Button) findViewById(R.id.editor_delete);
        button_delete.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View view) {

            }
        });

        editText_theme = (EditText) findViewById(R.id.editor_theme);
        editText_note = (EditText) findViewById(R.id.editor_note);

        final DateFormat dateFormat1 = new SimpleDateFormat("yyyy年MM月dd日");
        final DateFormat dateFormat2 = new SimpleDateFormat("HH：mm");

        final TextView start_time = (TextView) findViewById(R.id.editor_start_time);
        start_time.setText(dateFormat1.format(start_date) + "\n" + dateFormat2.format(start_date));
        start_time.setOnClickListener(new View.OnClickListener() {
            boolean timeType[] = {false, false, true, true, true, false};
            @Override
            public void onClick(View view) {
                timePickerView = new TimePickerBuilder(EditorActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        start_date = date;
                        start_time.setText(dateFormat1.format(date) + "\n" + dateFormat2.format(date));
                    }
                })
                        .setTitleText("起始时间")
                        .setType(timeType)
                        .build();
                timePickerView.show();
            }
        });


        final TextView end_time = (TextView) findViewById(R.id.editor_end_time);
        end_time.setText(dateFormat1.format(end_date) + "\n" + dateFormat2.format(end_date));
        end_time.setOnClickListener(new View.OnClickListener() {
            boolean timeType[] = {false, false, true, true,true, false};
            @Override
            public void onClick(View view) {
                timePickerView = new TimePickerBuilder(EditorActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        end_date = date;
                        end_time.setText(dateFormat1.format(date) + "\n" + dateFormat2.format(date));
                    }
                })
                        .setTitleText("结束时间")
                        .setType(timeType)
                        .build();
                timePickerView.show();
            }
        });

        Button button_save_editor = (Button) findViewById(R.id.editor_save);
        button_save_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveData() {
        int week = 0;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");	//设置日期格式
        try {
            Date d = df.parse("20180903");
            long diff = start_date.getTime() - d.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            week = (int)days / 7 + 1;
        } catch (Exception e) {
        }

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(start_date);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(end_date);

        theme = editText_theme.getText().toString().trim();
        notes = editText_note.getText().toString().trim();

        Note note = new Note();
        note.setWeek(week);
        note.setDay(calendar1.get(Calendar.DAY_OF_WEEK) - 1);
        note.setTheme(theme);
        note.setNote(notes);
        note.setStart_hour(calendar1.get(Calendar.HOUR_OF_DAY));
        note.setStart_minute(calendar1.get(Calendar.MINUTE));
        note.setEnd_hour(calendar2.get(Calendar.HOUR_OF_DAY));
        note.setEnd_minute(calendar2.get(Calendar.MINUTE));
        note.save();
    }
}

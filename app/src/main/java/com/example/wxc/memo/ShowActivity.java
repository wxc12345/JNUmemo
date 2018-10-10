package com.example.wxc.memo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import org.litepal.LitePal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowActivity extends AppCompatActivity {

    private static final String TAG = "ShowActivity";
    private EditText editText_theme;
    private EditText editText_note;
    private TextView textView_start;
    private TextView textView_end;
    private Button button_delete;
    private Button button_save;
    private Date start_date;
    private Date end_date;
    private TimePickerView timePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        getViews();

        final Intent intent = getIntent();
        final int id = intent.getIntExtra("id", 0);

        findData(id);

        //初始化时间，结束比开始晚1小时
        start_date = new Date(System.currentTimeMillis());
        end_date = new Date(System.currentTimeMillis() + (long) 1 * 60 * 60 * 1000);
        final DateFormat dateFormat1 = new SimpleDateFormat("yyyy年MM月dd日");
        final DateFormat dateFormat2 = new SimpleDateFormat("HH：mm");
        textView_start.setOnClickListener(new View.OnClickListener() {
            boolean timeType[] = {false, false, true, true, true, false};
            @Override
            public void onClick(View view) {
                timePickerView = new TimePickerBuilder(ShowActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        start_date = date;
                        textView_start.setText(dateFormat1.format(date) + "\n" + dateFormat2.format(date));
                    }
                })
                        .setTitleText("起始时间")
                        .setType(timeType)
                        .build();
                timePickerView.show();
            }
        });
        textView_end.setOnClickListener(new View.OnClickListener() {
            boolean timeType[] = {false, false, true, true,true, false};
            @Override
            public void onClick(View view) {
                timePickerView = new TimePickerBuilder(ShowActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        end_date = date;
                        textView_end.setText(dateFormat1.format(date) + "\n" + dateFormat2.format(date));
                    }
                })
                        .setTitleText("结束时间")
                        .setType(timeType)
                        .build();
                timePickerView.show();
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LitePal.delete(Note.class, id);
                Intent intent1 = new Intent(ShowActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(id, editText_theme.getText().toString().trim(), editText_note.getText().toString().trim(), start_date, end_date);
                Intent intent1 = new Intent(ShowActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }

    void getViews() {
        editText_theme = (EditText) findViewById(R.id.editor_theme);
        editText_note = (EditText) findViewById(R.id.editor_note);
        textView_start = (TextView) findViewById(R.id.editor_start_time);
        textView_end = (TextView) findViewById(R.id.editor_end_time);
        button_delete = (Button) findViewById(R.id.editor_delete);
        button_save = (Button) findViewById(R.id.editor_save);
    }

    void findData(int id) {
        Log.d(TAG, "进入findData");
        List<Note> notes = LitePal.findAll(Note.class);
        for (Note note : notes) {
            if (note.getId() == id) {
                editText_theme.setText(note.getTheme());
                editText_note.setText(note.getNote());

                Date date1 = new Date(System.currentTimeMillis());
                Date date2 = new Date(System.currentTimeMillis() + (long) 1 * 60 * 60 * 1000);
                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

                String start_time = dateFormat.format(date1)+"\n" + note.getStart_hour() + ":" + note.getStart_minute();
                String end_time = dateFormat.format(date2) + "\n" + note.getEnd_hour() + ":" + note.getEnd_minute();
                textView_start.setText(start_time);
                textView_end.setText(end_time);
            } else continue;
        }


    }

    void saveData(int id, String theme, String notes, Date start_date, Date end_date) {

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


        Note note = new Note();
        note.setWeek(week);
        note.setDay(calendar1.get(Calendar.DAY_OF_WEEK) - 1);
        note.setTheme(theme);
        note.setNote(notes);
        note.setStart_hour(calendar1.get(Calendar.HOUR_OF_DAY));
        note.setStart_minute(calendar1.get(Calendar.MINUTE));
        note.setEnd_hour(calendar2.get(Calendar.HOUR_OF_DAY));
        note.setEnd_minute(calendar2.get(Calendar.MINUTE));
        note.update(id);
    }
}

package com.example.wzes.protectflower;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Record;

public class RecordActivity extends AppCompatActivity {
    private List<Record> records;
    private List<Map<String, String>> data = new ArrayList<>();
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        linearLayout = (LinearLayout) findViewById(R.id.listLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.RecordToolBar);
        Button back = (Button)findViewById(R.id.RecordBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        records = DataSupport.findAll(Record.class);

        if (records.size()==0){
            linearLayout.setBackground(getResources().getDrawable(R.drawable.back));
        }else {
            linearLayout.setBackground(getResources().getDrawable(R.drawable.back_has));
        }
        for(Record record : records){
            if(!record.equals("")){
                String date = record.getDate();
                String tem = record.getTemperature();
                String hum = record.getHumidity();
                Map<String, String> map = new HashMap<>();
                map.put("时间", date);
                map.put("内容", "温度："+ tem + "    湿度：" + hum);
                data.add(map);
            }

        }
        ListView listView = (ListView) findViewById(R.id.recordList);
        Collections.reverse(data);
        listView.setAdapter(new SimpleAdapter(this,data,android.R.layout.simple_list_item_2,
                new String[]{"时间","内容"},
                new int[]{android.R.id.text1,android.R.id.text2}
        ));
    }
}

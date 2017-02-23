package com.example.wzes.protectflower;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.Record;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    public static String username, email;
    private LinearLayout linearLayout , linearLayoutInfo;
    private static boolean get = true;
    private ImageView imageView;
    private TextView temperatureTxt, humidityTxt;
    private String IP = "192.168.1.11";
    private BufferedReader bufferedReader;
    private Bitmap bitmap;
    //private byte[] data;
    private String LEN;
    private boolean send = false;
    private int T, H;
    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences(
                "user", MODE_PRIVATE);
        username = sharedPreferences.getString("Username", "XXXXX");
        email = sharedPreferences.getString("Email", "000000");
        imageView = (ImageView) findViewById(R.id.image);
        temperatureTxt = (TextView) findViewById(R.id.temperature);
        humidityTxt = (TextView) findViewById(R.id.humidity);


        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        linearLayout = (LinearLayout) findViewById(R.id.showData);
        linearLayoutInfo = (LinearLayout) findViewById(R.id.showDataInfo);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutInfo.setVisibility(View.GONE);
                    }
                });
                getServer();
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "是否浇水", Snackbar.LENGTH_SHORT)
                        .setAction("是", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Send();
                                if(send){
                                    Toast.makeText(MainActivity.this, "已发送浇水命令", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this, "发送命令失败", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).show();
                //
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.opendrawer);
        }
        navView.setCheckedItem(R.id.nav_record);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_record) {
                    startActivity(new Intent(MainActivity.this, RecordActivity.class));
                }else{
                    mDrawerLayout.closeDrawers();
                }
                //mDrawerLayout.closeDrawers();
                return true;
            }
        });
        View headerView = navView.getHeaderView(0);
        TextView usernameTxt = (TextView) headerView.findViewById(R.id.username);
        TextView emailTxt = (TextView) headerView.findViewById(R.id.email);

        usernameTxt.setText(username);
        emailTxt.setText(email);
        swipeRefreshLayout.setRefreshing(true);
        getServer();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.setting:
                Toast.makeText(this, "You clicked Setting", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

        }
        return true;
    }
    public void Send(){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DataOutputStream dataOutputStream = new DataOutputStream(
                                socket.getOutputStream());
                        dataOutputStream.writeBoolean(true);
                        send = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        send = false;
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getServer(){
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(IP, 5209), 5000);
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        T = dataInputStream.readInt();
                        H = dataInputStream.readInt();
                        int size = dataInputStream.readInt();
                        LEN = String.valueOf(size);
                        byte[] data = new byte[size];
                        int len = 0;
                        while (len < size) {
                            len += dataInputStream.read(data, len, size - len);
                        }
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                linearLayout.setBackground(getResources().getDrawable(R.drawable.failed));
                                linearLayoutInfo.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    linearLayoutInfo.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                    temperatureTxt.setText("温度：" + T + "  °C");
                    humidityTxt.setText("湿度：" + H + "  %RH");
                    Record record = new Record(String.valueOf(T), String.valueOf(H));
                    Date date = new Date();
                    long times = date.getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = formatter.format(date);
                    record.setDate(dateString);
                    record.save();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
            super.handleMessage(msg);
        }
    };

}

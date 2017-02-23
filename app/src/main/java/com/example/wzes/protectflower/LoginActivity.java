package com.example.wzes.protectflower;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import util.User;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn = null;
    private TextView registerBtn = null;
    private EditText userNameTxt, passwordTxt;
    public static String userName, password;
    private SharedPreferences sharedPreferences;
    private boolean find = false;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bmob.initialize(this, "5cc42640cf48034151b72da4f1054243");
        loginBtn = (Button) findViewById(R.id.login);
        init();
    }
    private void init() {
        loginBtn = (Button) findViewById(R.id.login);
        registerBtn = (TextView) findViewById(R.id.register);
        userNameTxt = (EditText) findViewById(R.id.userName);
        passwordTxt = (EditText) findViewById(R.id.passWord);
        userNameTxt.setText("wzes");
        passwordTxt.setText("123456");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = userNameTxt.getText().toString();
                password = passwordTxt.getText().toString();

                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("Logging in...");
                dialog.setMessage("Please wait...");
                dialog.show();
                //progressDialog = ProgressDialog.show(LoginActivity.this, , "Please wait...", false, false);
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //显示ProgressDialog
                            findNet(userName);
                        }
                    }).start();
                }

            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    public void findNet(final String Name){
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.setLimit(1000);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    //创建数据库
                    for(User user : list){
                        if(user.getUserName().equals(Name)){
                            if(password.equals(user.getPassword())) {
                                //进入main
                                find = true;
                                sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Username", userName);
                                editor.putString("Email",user.getEmail());
                                editor.apply();
                                Message msg = hand.obtainMessage();
                                hand.sendMessage(msg);
                                break;
                            }
                        }
                    }
                    if(!find){
                        Toast.makeText(LoginActivity.this, "用户名或密码输入错误", Toast.LENGTH_SHORT).show();
                        userNameTxt.setFocusable(true);
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "不存在该用户名", Toast.LENGTH_SHORT).show();
                    userNameTxt.setFocusable(true);
                }
            }
        });
    }
    Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dialog.setMessage("Success......");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        dialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
}

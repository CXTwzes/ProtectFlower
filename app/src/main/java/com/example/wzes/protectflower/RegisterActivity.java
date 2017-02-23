package com.example.wzes.protectflower;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import util.User;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    private Button backBtn, registerBtn;
    private EditText userNameTxt, passwordTxt, passwordAgainTxt, emailTxt, telTxt;
    private String userName, password, passwordAgain;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.RegisterToolBar);
        Button back = (Button)findViewById(R.id.RegisterBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        init();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = userNameTxt.getText().toString();
                password = passwordTxt.getText().toString();
                passwordAgain = passwordAgainTxt.getText().toString();
                dialog = new ProgressDialog(RegisterActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("Register in...");
                dialog.setMessage("Please wait...");
                dialog.show();
                //检查输入是否正确
                if(isInputTrue()){

                    User user = new User(userName, password);
                    user.setEmail(emailTxt.getText().toString());
                    user.setTel(telTxt.getText().toString());
                    user.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId,BmobException e) {
                            if(e==null){

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.setMessage("Success......");
                                        dialog.dismiss();
                                    }
                                });
                                finish();
                            }else{
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private boolean isInputTrue() {
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            userNameTxt.setFocusable(true);
            return false;
        }
        if(TextUtils.isEmpty(password)){
            passwordTxt.setFocusable(true);
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(passwordAgain)){
            passwordAgainTxt.setFocusable(true);
            Toast.makeText(this, "两次输入密码不一样，请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(telTxt.getText().toString())){
            Toast.makeText(this, "请输入电话", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(emailTxt.getText().toString())){
            Toast.makeText(this, "请输入Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6){
            myAlertDialog("密码位数不能低于6位");
            passwordTxt.setFocusable(true);
            return false;
        }
        return true;
    }

    private void myAlertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确认", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void init() {
        userNameTxt = (EditText) findViewById(R.id.RegisterUsername);
        passwordTxt = (EditText) findViewById(R.id.RegisterPassword);
        passwordAgainTxt = (EditText) findViewById(R.id.RegisterPasswordSure);
        backBtn = (Button) findViewById(R.id.RegisterBack);
        registerBtn = (Button) findViewById(R.id.RegisterRegister);
        emailTxt = (EditText) findViewById(R.id.RegisterEmail);
        telTxt = (EditText) findViewById(R.id.RegisterTel);
    }
}

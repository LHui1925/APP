package com.example.smartlock;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.JPushMessageReceiver;

public class MainActivity extends AppCompatActivity {
    NavController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JPushInterface.setDebugMode(true); //允许被debug，正式版本的时候注掉
        JPushInterface.init(this);  //初始化

        controller= Navigation.findNavController(this,R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this,controller);//出现返回导航键
    }




   @Override
    public boolean onSupportNavigateUp() {
        if(controller.getCurrentDestination().getId()==R.id.menuFragment){
            onBackPressed();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);//创建对话框
            builder.setTitle("你确定离开吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    controller.navigateUp();
                    finish();

                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        } else if(controller.getCurrentDestination().getId()==R.id.loginFragment){
            finish();
        }
       //NavController controller=Navigation.findNavController(this,R.id.fragment);
       return controller.navigateUp();
       //return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }
}

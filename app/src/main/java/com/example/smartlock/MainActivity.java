package com.example.smartlock;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.JGpush.TagAliasOperatorHelper;
import com.example.smartlock.util.HttpUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity implements Observer {
    NavController controller;
    SharedPreferences sp;
    MyViewModel myViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("login", Context.MODE_PRIVATE);

        JPushInterface.setDebugMode(true); //允许被debug，正式版本的时候注掉
        JPushInterface.init(this);  //初始化

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        myViewModel.getLiveData().observe(this, this);

        //注册别名
        TagAliasOperatorHelper.TagAliasBean aliasBean = new TagAliasOperatorHelper.TagAliasBean();
        aliasBean.action = TagAliasOperatorHelper.ACTION_SET;
        aliasBean.isAliasAction = true;
        //aliasBean.alias =sp.getString("login_phone","");
        aliasBean.alias = "15084728436";
        TagAliasOperatorHelper.sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(),TagAliasOperatorHelper.sequence,aliasBean);


        //注册标签
        TagAliasOperatorHelper.TagAliasBean tagBean = new TagAliasOperatorHelper.TagAliasBean();
        tagBean.action = TagAliasOperatorHelper.ACTION_ADD;
        tagBean.isAliasAction = false;
        Set<String> tagSet = new HashSet<>();
        tagSet.add("dev00001");
        tagSet.add("dev00002");
        tagBean.tags = tagSet;
        TagAliasOperatorHelper.sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(),TagAliasOperatorHelper.sequence,tagBean);

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

    @Override
    public void onChanged(Object o) {

        Map<String, String> map = (Map<String, String>) o;
        JSONObject object;
         if (map.get("type").equals("QueryUserInfo1")) {
            object = JSON.parseObject(map.get("content"));
            //Toast.makeText(getContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
            HttpUtil.user_info_extra=object.getJSONArray("extra");
            System.out.println(HttpUtil.user_info_extra+"**************+++++++++++++++");

        }


    }
}

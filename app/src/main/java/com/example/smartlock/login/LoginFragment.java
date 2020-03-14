package com.example.smartlock.login;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.MyViewModel;
import com.example.smartlock.R;
import com.example.smartlock.RSATest.Base64Utils;
import com.example.smartlock.RSATest.MD5;
import com.example.smartlock.RSATest.RSAUtils;
import com.example.smartlock.util.HttpUtil;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements Observer {
    private MyViewModel myViewModel;

    private EditText edt_callNumber;
    private EditText edt_password;
    private String phone_number;
    private String password;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myViewModel= ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        myViewModel.getLiveData().observe(this, this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login,container,false);


        edt_callNumber=view.findViewById(R.id.edt_callNumber);
        edt_password=view.findViewById(R.id.edt_password);

        HttpUtil.shp=getContext().getSharedPreferences("login", Context.MODE_PRIVATE);


        if(HttpUtil.shp.getString("login","")!=null){
            phone_number=HttpUtil.shp.getString("login_phone","");
            password=HttpUtil.shp.getString("login_password","");
            edt_callNumber.setText(phone_number);
            edt_password.setText(password);
        }
        HttpUtil.id=phone_number;


        Button btn_register=view.findViewById(R.id.btn_register);
        Button forget_password=view.findViewById(R.id.forget_password);

        //切换到密码验证登录界面
        view.findViewById(R.id.LoginByVerificationCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_loginFragment_to_loginByVerificationCodeFragment);
            }
        });
        //切换到注册界面
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
        //切换到修改密码界面
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_loginFragment_to_changingCodeFragment);
            }
        });
        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_loginFragment_to_menuFragment);
            }
        });

        //通过密码登录
        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_callNumber.getText().toString().equals("")||edt_callNumber.getText().toString()==null){
                    Toast.makeText(getContext(),"请输入手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edt_password.getText().toString().equals("")||edt_password.getText().toString()==null){
                    Toast.makeText(getContext(),"请输入登录密码",Toast.LENGTH_SHORT).show();
                    return;
                }

                phone_number=edt_callNumber.getText().toString().trim();
                password=edt_password.getText().toString().trim();
                HttpUtil.editor=HttpUtil.shp.edit();
                HttpUtil.editor.putString("login_phone",phone_number);
                HttpUtil.editor.putString("login_password",password);
                HttpUtil.editor.apply();

                String MD5_password= MD5.getMD5(edt_password.getText().toString());
                Map<String,String> map=new LinkedHashMap<>();
                map.put("phone",edt_callNumber.getText().toString());
                map.put("password", Base64Utils.encodeURL(MD5_password));
                Map key= RSAUtils.generateKeyPair();
                HttpUtil.phonePrivateKey=key.get("privateKey").toString();
                HttpUtil.id=edt_callNumber.getText().toString();
                map.put("publicKey",key.get("publicKey").toString());
                /*for(String key1:map.keySet()){
                System.out.println(key1+"++++++++++"+map.get(key1));}*/
                HttpUtil.webRequest(HttpUtil.byPassword_url,map,myViewModel,"byPassword");

            }
        });


        return  view;
               // Inflate the layout for this fragment
               //return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    //处理订阅信息
    public void onChanged(Object o) {
        Map<String,String> map=(Map<String, String>)(o);
        JSONObject object;
        if(map.get("type").equals("byPassword")){
            object= JSON.parseObject(map.get("content"));
            Toast.makeText(getContext(),object.getString("msg"),Toast.LENGTH_SHORT).show();
            if(object.getString("res").equals("0")){
                HttpUtil.serverPublicKey=object.getString("extra");
                Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_menuFragment);
            }
        }
        //再这里，服务端返回的信息里含有serverpublkkey,再这里保存

    }
}

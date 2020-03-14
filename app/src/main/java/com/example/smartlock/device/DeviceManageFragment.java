package com.example.smartlock.device;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.MyViewModel;
import com.example.smartlock.R;
import com.example.smartlock.util.HttpUtil;

import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceManageFragment extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SharedPreferences sp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyViewModel myViewModel;
    private String dialog_phone;
    private String query_phone;
    private String device_number;

    public DeviceManageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceManageFragment newInstance(String param1, String param2) {
        DeviceManageFragment fragment = new DeviceManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        myViewModel= ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        myViewModel.getLiveData().observe(this, this);
        sp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //final Context context;
        View view=inflater.inflate(R.layout.fragment_device_manage,container,false);
        final View edit_view=inflater.inflate(R.layout.edit,container,false);
        final View dialog_password=inflater.inflate(R.layout.password,container,false);

        dialog_phone=sp.getString("login_phone","");
        query_phone=sp.getString("login_phone","");
        device_number=HttpUtil.shp.getString("current_number", "");
        System.out.println(query_phone+",,,,,,,,,,,,"+device_number);

        final EditText dialog_device_num=edit_view.findViewById(R.id.edt_dialog_number);
        final EditText edt_dialog_password=dialog_password.findViewById(R.id.edt_dialog_password);

        Button cancle_bind=view.findViewById(R.id.cancle_bind);
        //切换到绑定设备界面
        view.findViewById(R.id.bind_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_deviceManageFragment_to_bindDeviceFragment);
            }
        });
        //切换到修改信息界面
        view.findViewById(R.id.modify_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_deviceManageFragment_to_modifyInfoFragment);
            }
        });
        //取消绑定设备
        cancle_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_deviceManageFragment_to_unbindDeviceFragment);*/
               AlertDialog.Builder builder=new AlertDialog.Builder(getView().getContext());//创建对话框
                builder.setTitle("你确定取消设备吗？").setView(edit_view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ViewGroup) edit_view.getParent()).removeView(edit_view);

                        if(dialog_device_num.getText().toString().equals("")||dialog_device_num.getText().toString()==null){
                            Toast.makeText(getContext(),"请输入设备号",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject object=new JSONObject();
                        object.put("phone",dialog_phone);
                        object.put("device", dialog_device_num.getText().toString());

                        Map<String,String> map=new LinkedHashMap<>();
                        map.put("content", object.toJSONString());

                        try {
                            HttpUtil.webRequestWithToken(true,HttpUtil.UnbindDevice_url,map,myViewModel,"UnbindDevice");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();//创建对话框
                dialog.show();

            }
        });

        //查询手机绑定了多少台设备
        view.findViewById(R.id.query_user_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog.Builder builder=new AlertDialog.Builder(getContext());//创建对话框
                builder.setTitle("你确定查询手机绑定了多少台设备吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {*/

                        JSONObject object=new JSONObject();
                        object.put("phone",query_phone);

                        Map<String,String> map=new HashMap<>();
                        map.put("content", object.toJSONString());

                        try {
                            HttpUtil.webRequestWithToken(true,HttpUtil.QueryBindInfo_url,map,myViewModel,"QueryBindInfo");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
               // });
               /* builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();//创建对话框
                dialog.show();

            }*/
        });
        //修改密码
        view.findViewById(R.id.modifyPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getView().getContext());//创建对话框
                builder.setTitle("你确定修改密码吗？").setView(dialog_password);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ViewGroup) dialog_password.getParent()).removeView(dialog_password);

                        if(edt_dialog_password.getText().toString().equals("")||edt_dialog_password.getText().toString()==null){
                            Toast.makeText(getContext(),"请输入新密码",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject object=new JSONObject();
                        object.put("phone",dialog_phone);
                        object.put("device",device_number);
                        object.put("control","modifyPassword");
                        object.put("password", edt_dialog_password.getText().toString());

                        /*Map<String,String> map=new LinkedHashMap<>();
                        map.put("content", object.toJSONString());*/

                      HttpUtil.wrPOST_text(true,HttpUtil.operation,null,object.toJSONString(),myViewModel,"modifyPassword");
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();//创建对话框
                dialog.show();

            }
        });

        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_device_manage, container, false);
    }

    @Override
    public void onChanged(Object o) {
        Map<String,String> map =(Map<String, String>) o;
        JSONObject object;
        if(map.get("type").equals("UnbindDevice")){
            Toast.makeText(getContext(),map.get("content"),Toast.LENGTH_SHORT).show();
           /* object= JSON.parseObject(map.get("content"));
            Toast.makeText(getContext(),object.getString("msg"),Toast.LENGTH_SHORT).show();*/
        }
        if(map.get("type").equals("QueryBindInfo")) {
            object= JSON.parseObject(map.get("content"));
            Toast.makeText(getContext(),object.getString("msg"),Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), map.get("content"), Toast.LENGTH_SHORT).show();
            HttpUtil.device_info_extra=object.getJSONArray("extra");
            System.out.println(HttpUtil.device_info_extra+"+++++++++++++++");
            if(object.getString("res").equals("0")){
                Navigation.findNavController(getView()).navigate(R.id.action_deviceManageFragment_to_deviceInfoFragment);
            }
        }
        if(map.get("type").equals("modifyPassword")){
            //object=JSON.parseObject(map.get("content"));
            Toast.makeText(getContext(),map.get("content"),Toast.LENGTH_SHORT).show();
        }

    }
}

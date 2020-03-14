package com.example.smartlock.query;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.MyViewModel;
import com.example.smartlock.R;
import com.example.smartlock.adapter.UserAdapter;
import com.example.smartlock.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MyViewModel myViewModel;
    SharedPreferences sp;
    String phone;
    String device_number;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
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
        myViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        myViewModel.getLiveData().observe(this, this);
        //sp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        final View user_name_view=inflater.inflate(R.layout.user_name,container,false);

        phone=HttpUtil.id;
        device_number=HttpUtil.shp.getString("current_number", "");
        System.out.println(phone+",,,,,,,,,,,,"+device_number);

        RecyclerView recyclerView = view.findViewById(R.id.user_recyclerview);

        final EditText edt_user_name=user_name_view.findViewById(R.id.edt_user_name);

        List<Map<String, String>> user_info_list=new ArrayList<>();
        //指定RecyclerView的布局为线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //创建UserAdapter的实例，将数据传入构造函数
        UserAdapter userAdapter = new UserAdapter(user_info_list);
        recyclerView.setAdapter(userAdapter);

        JSONObject jsonObject;

        JSONArray user_info_Array=HttpUtil.user_info_extra;

        for(int i=0;i<user_info_Array.size();i++){
            jsonObject=user_info_Array.getJSONObject(i);
            Map<String, String> map=new HashMap<>();

            String name=jsonObject.getString("user_name");
            String number=jsonObject.getString("user_number");
            map.put("name",name);
            map.put("number",number);
            System.out.println(map+"*****************");
            user_info_list.add(map);
        }
        System.out.println("user_info_list:"+user_info_list+"*****************");

        //添加用户
        view.findViewById(R.id.addUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getView().getContext());//创建对话框
                builder.setTitle("请输入用户名").setView(user_name_view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ViewGroup) user_name_view.getParent()).removeView(user_name_view);

                        if(edt_user_name.getText().toString().equals("")||edt_user_name.getText().toString()==null){
                            Toast.makeText(getContext(),"请输入用户名",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject object=new JSONObject();
                        object.put("phone",phone);
                        object.put("device",device_number);
                        object.put("control","addUser");
                        object.put("name",edt_user_name.getText().toString());
                        System.out.println(object+"*****************");
                        /*Map<String,String> map=new LinkedHashMap<>();
                        map.put("content", object.toJSONString());*/
                        HttpUtil.wrPOST_text(true,HttpUtil.operation,null,object.toJSONString(),myViewModel,"addUser");


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
        //删除用户

        //修改用户名
        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onChanged(Object o) {
        Map<String,String> map =(Map<String, String>) o;
        if(map.get("type").equals("addUser")){
            Toast.makeText(getContext(),map.get("content"),Toast.LENGTH_SHORT).show();
        }

    }
}

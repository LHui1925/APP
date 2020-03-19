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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.MyViewModel;
import com.example.smartlock.R;
import com.example.smartlock.adapter.UserAdapter;
import com.example.smartlock.util.HttpUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private RefreshLayout refreshLayout;
    private UserAdapter userAdapter;
    private JSONArray new_info_extra;
    private List<Map<String, String>> new_list=new ArrayList<>();
    private  RecyclerView recyclerView;


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
        final View user_number_view=inflater.inflate(R.layout.number,container,false);
        final View modify_name_view=inflater.inflate(R.layout.modify_user_name,container,false);

        phone=HttpUtil.id;
        device_number=HttpUtil.shp.getString("current_number", "");
        System.out.println(phone+",,,,,,,,,,,,"+device_number);

        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.user_recyclerview);


        final EditText edt_user_name=user_name_view.findViewById(R.id.edt_user_name);
        final EditText edt_user_number=user_number_view.findViewById(R.id.edt_dialog_UserNumber);
        final EditText modify_number=modify_name_view.findViewById(R.id.edit_user_number);
        final EditText modify_name=modify_name_view.findViewById(R.id.edt_new_name);

        List<Map<String, String>> user_info_list=new ArrayList<>();
        //指定RecyclerView的布局为线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //创建UserAdapter的实例，将数据传入构造函数
        userAdapter = new UserAdapter(user_info_list);
        recyclerView.setAdapter(userAdapter);
        initView();

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
        view.findViewById(R.id.deleteUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getView().getContext());//创建对话框
                builder.setTitle("请输入用户编号").setView(user_number_view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ((ViewGroup) user_number_view.getParent()).removeView(user_number_view);

                        if(edt_user_number.getText().toString().equals("")||edt_user_number.getText().toString()==null){
                            Toast.makeText(getContext(),"请输入用户编号",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject object=new JSONObject();
                        object.put("phone",phone);
                        object.put("device",device_number);
                        object.put("control","deleteUser");
                        object.put("number",edt_user_number.getText().toString());
                        System.out.println(object+"*****************");
                        /*Map<String,String> map=new LinkedHashMap<>();
                        map.put("content", object.toJSONString());*/
                        HttpUtil.wrPOST_text(true,HttpUtil.operation,null,object.toJSONString(),myViewModel,"deleteUser");


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

        //修改用户名
        view.findViewById(R.id.modifyUserName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getView().getContext());//创建对话框
                builder.setTitle("你确定修改用户名吗？").setView(modify_name_view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ((ViewGroup) modify_name_view.getParent()).removeView(modify_name_view);

                        if(modify_number.getText().toString().equals("")||modify_number.getText().toString()==null){
                            Toast.makeText(getContext(),"请输入用户编号",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(modify_name.getText().toString().equals("")||modify_name.getText().toString()==null){
                            Toast.makeText(getContext(),"请输入新的用户名",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject object=new JSONObject();
                        object.put("phone",phone);
                        object.put("device",device_number);
                        object.put("control","modifyUserName");
                        object.put("number",modify_number.getText().toString());
                        object.put("name",modify_name.getText().toString());
                        System.out.println(object+"*****************");
                        /*Map<String,String> map=new LinkedHashMap<>();
                        map.put("content", object.toJSONString());*/
                        HttpUtil.wrPOST_text(true,HttpUtil.operation,null,object.toJSONString(),myViewModel,"modifyUserName");


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
        //return inflater.inflate(R.layout.fragment_user_info, container, false);
    }
    private void initView() {

        /*设置不同的头部、底部样式*/
//        refreshLayout.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
//        refreshLayout.setRefreshHeader(new BezierRadarHeader(this));
//        refreshLayout.setRefreshHeader(new TwoLevelHeader(this));

        refreshLayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()).setShowBezierWave(true));

        //设置样式后面的背景颜色
        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);

        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                JSONObject object = new JSONObject();
                object.put("phone", phone);
                object.put("device", device_number);
                Map<String, String> map = new LinkedHashMap<>();
                map.put("content", object.toJSONString());
                try {
                    HttpUtil.webRequestWithToken(true, HttpUtil.QueryUserInfo_url, map, myViewModel, "new_QueryUserInfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                userAdapter.refreshData(new_list); //下拉刷新，数据从上往下添加到界面上
                refreshLayout.finishRefresh(1000); //这个记得设置，否则一直转圈
                new_list.clear();



            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

                userAdapter.loadMore(new_list);  //上滑刷新，数据从下往上添加到界面上
                refreshLayout.finishLoadMore(1000); //这个记得设置，否则一直转圈
            }
        });

    }

    @Override
    public void onChanged(Object o) {
        Map<String, String> map = (Map<String, String>) o;
        JSONObject object;
        if (map.get("type").equals("addUser")) {
            Toast.makeText(getContext(), map.get("content"), Toast.LENGTH_SHORT).show();

        }
        if (map.get("type").equals("deleteUser")) {
            Toast.makeText(getContext(), map.get("content"), Toast.LENGTH_SHORT).show();
        }
        if (map.get("type").equals("modifyUserName")) {
            Toast.makeText(getContext(), map.get("content"), Toast.LENGTH_SHORT).show();
        }

        if (map.get("type").equals("new_QueryUserInfo")) {
            object = JSON.parseObject(map.get("content"));
            //Toast.makeText(getContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
            new_info_extra=object.getJSONArray("extra");
            System.out.println(new_info_extra+"+++++++++++++++");
            for(int i=0;i<new_info_extra.size();i++){
                JSONObject jsonObject;
                jsonObject=new_info_extra.getJSONObject(i);
                Map<String, String> new_map=new HashMap<>();

                String name=jsonObject.getString("user_name");
                String number=jsonObject.getString("user_number");
                new_map.put("name",name);
                new_map.put("number",number);
                new_list.add(new_map);
            }
            System.out.println("new_list:"+new_list+"*****************");
        }
    }
}

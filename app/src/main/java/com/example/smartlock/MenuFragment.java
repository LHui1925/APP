package com.example.smartlock;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.util.HttpUtil;

import org.json.JSONException;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MyViewModel myViewModel;
    private String current_number;
    private String query_user_phone;
    private String query_device_number;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu, container, false);
        final TextView current_device = view.findViewById(R.id.current_device);
        HttpUtil.shp = getContext().getSharedPreferences("current_device_number", Context.MODE_PRIVATE);
       //HttpUtil.shp=getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        //query_user_phone = HttpUtil.shp.getString("login_phone", "");
        query_user_phone=HttpUtil.id;
        query_device_number = HttpUtil.shp.getString("current_number", "");
        System.out.println(query_user_phone+",,,,,,,,,,,,"+query_device_number);

        if (HttpUtil.shp.getString("current_device_number", "") != null) {
            current_number = HttpUtil.shp.getString("current_number", "");
            current_device.setText(current_number);
        }


        //切换到设备管理界面
        view.findViewById(R.id.device_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_menuFragment_to_deviceManageFragment);
            }
        });
        //切换到选择设备界面
        view.findViewById(R.id.select_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_menuFragment_to_selectDeviceFragment);
            }
        });

        //用户查询
        /*view.findViewById(R.id.query_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_menuFragment_to_userQueryFragment);
            }
        });*/

        //用户查询
        view.findViewById(R.id.query_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                object.put("phone", query_user_phone);
                object.put("device", query_device_number);

                Map<String, String> map = new LinkedHashMap<>();
                map.put("content", object.toJSONString());

                try {
                    HttpUtil.webRequestWithToken(true, HttpUtil.QueryUserInfo_url, map, myViewModel, "QueryUserInfo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //切换到记录查询界面
       /* view.findViewById(R.id.opening_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller = Navigation.findNavController(v);
                controller.navigate(R.id.action_menuFragment_to_recordQueryFragment);
            }
        });*/

        //记录查询
       view.findViewById(R.id.opening_record).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               JSONObject object=new JSONObject();
               object.put("phone",query_user_phone);
               object.put("device", query_device_number);

               Map<String,String> map=new LinkedHashMap<>();
               map.put("content", object.toJSONString());
               try {
                   HttpUtil.webRequestWithToken(true,HttpUtil.QueryRecord_url,map,myViewModel,"QueryRecord");
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });

        //解锁
        view.findViewById(R.id.unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object=new JSONObject();
                object.put("phone",query_user_phone);
                object.put("device", query_device_number);
                object.put("control","unlock");
                /*Map<String,String> map=new LinkedHashMap<>();
                map.put("content", object.toJSONString());*/
                HttpUtil.wrPOST_text(true,HttpUtil.operation,null,object.toJSONString(),myViewModel,"unlock");
            }
        });

        //设置临时密码
        view.findViewById(R.id.TemporaryCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object=new JSONObject();
                object.put("phone",query_user_phone);
                object.put("device", query_device_number);
                object.put("control","setTemporaryCode");

                /*Map<String,String> map=new LinkedHashMap<>();
                map.put("content", object.toJSONString());*/
                HttpUtil.wrPOST_text(true,HttpUtil.operation,null,object.toJSONString(),myViewModel,"setTemporaryCode");
            }
        });


        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onChanged(Object o) {
        Map<String, String> map = (Map<String, String>) o;
        System.out.println(map + "----------------------");
        JSONObject object;
        if (map.get("type").equals("QueryUserInfo")) {
            object = JSON.parseObject(map.get("content"));
            Toast.makeText(getContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();

            HttpUtil.user_info_extra=object.getJSONArray("extra");
            System.out.println(HttpUtil.user_info_extra+"+++++++++++++++");
            if(object.getString("res").equals("0")){
                Navigation.findNavController(getView()).navigate(R.id.action_menuFragment_to_userInfoFragment);
            }
        }

        if(map.get("type").equals("QueryRecord")){
            object= JSON.parseObject(map.get("content"));
            Toast.makeText(getContext(), object.getString("msg"),Toast.LENGTH_SHORT).show();
            HttpUtil.record_info_extra=object.getJSONArray("extra");
            System.out.println(HttpUtil.record_info_extra+"+++++++++++++++");
            if(object.getString("res").equals("0")){
                Navigation.findNavController(getView()).navigate(R.id.action_menuFragment_to_recordInfoFragment);
            }

        }

        if(map.get("type").equals("unlock")){
        Toast.makeText(getContext(),map.get("content"),Toast.LENGTH_SHORT).show();
    }

        if(map.get("type").equals("setTemporaryCode")){
        Toast.makeText(getContext(),map.get("content"),Toast.LENGTH_SHORT).show();
    }

    }

}

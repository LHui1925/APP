package com.example.smartlock.query;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.MyViewModel;
import com.example.smartlock.R;
import com.example.smartlock.adapter.RecordAdapter;
import com.example.smartlock.util.HttpUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordInfoFragment extends Fragment implements Observer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyViewModel myViewModel;


    public RecordInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordInfoFragment newInstance(String param1, String param2) {
        RecordInfoFragment fragment = new RecordInfoFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_info, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.record_recyclerview);

        List<Map<String, String>> record_info_list = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray record_info_Array = HttpUtil.record_info_extra;

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RecordAdapter recordAdapter = new RecordAdapter(record_info_list);
        recyclerView.setAdapter(recordAdapter);

        for (int i = 0; i <record_info_Array.size(); i++) {
            jsonObject = record_info_Array.getJSONObject(i);
            Map<String, String> map = new HashMap<>();

            String name = jsonObject.getString("name");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            //Date date = new Date(lt * 1000);
            Date date = jsonObject.getDate("unlock_time");
            String open_time = sdf.format(date);

            map.put("user_name", name);
            map.put("open_time",open_time);
            record_info_list.add(map);
        }
        System.out.println(record_info_list+"*****************");

        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_record_info, container, false);
    }

    @Override
    public void onChanged(Object o) {

    }
}
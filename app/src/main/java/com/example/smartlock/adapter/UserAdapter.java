package com.example.smartlock.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlock.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//适配器   管理recyclerview的内容
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    List<Map<String, String>> user_info_list=new ArrayList<>();
    //generate-setter

public UserAdapter(List<Map<String, String>> user_info_list){
    this.user_info_list = user_info_list;
}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载view
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View itemView=layoutInflater.inflate(R.layout.user_info_cell,parent,false);
        return new MyViewHolder(itemView);//将加载的itemView传递进来，创建ViewHolder
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //绑定数据

        holder.user_number.setText(user_info_list.get(position).get("number").toString());
        holder.user_name.setText(user_info_list.get(position).get("name").toString());
    }

    @Override
    public int getItemCount() {
        return user_info_list.size();
    }
    //底部上拉刷新，数据直接在底部显示
    public void loadMore(List<Map<String, String>> new_list) {

        user_info_list.clear();
        user_info_list.addAll(new_list);
        notifyDataSetChanged();
    }

    //顶部下拉刷新，数据直接从上往下添加数据，显示在顶部
    public void refreshData(List<Map<String, String>> new_list) {

        user_info_list.clear();
        user_info_list.addAll(0,new_list);
        notifyDataSetChanged();
//            notifyItemInserted(0); 一次只能加一项数据
    }

    //管理界面,static防止内存泄漏
    static class MyViewHolder extends RecyclerView.ViewHolder{//creat constructor
        TextView user_number,user_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_number=itemView.findViewById(R.id.user_number);
            user_name=itemView.findViewById(R.id.user_name);
        }
    }
}

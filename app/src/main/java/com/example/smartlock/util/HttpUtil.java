package com.example.smartlock.util;


import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.smartlock.MyViewModel;
import com.example.smartlock.RSATest.AESUtils;
import com.example.smartlock.RSATest.Base64Utils;
import com.example.smartlock.RSATest.RSAUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    //创建OkHttpClient实例，静态对象，不用每次请求都重新创建一个对象
    private static OkHttpClient client = new OkHttpClient();

    public static String register_url="http://47.97.165.179:9910/p/connect/register";
    public static String byVerificationCode_url="http://47.97.165.179:9910/p/connect/byVerificationCode";
    public static String getVerificationCode_url="http://47.97.165.179:9910/p/connect/getVerificationCode";
    public static String byPassword_url="http://47.97.165.179:9910/p/connect/byPassword";
    public static String modifyPassword_url="http://47.97.165.179:9910/p/connect/modifyPassword";
    public static String exchangeKey_url="http://47.97.165.179:9910/p/manage/exchangeKey";
    public static String BindDevice_url="http://47.97.165.179:9910/p/query/phoneUserBindDevice";
    public static String UnbindDevice_url="http://47.97.165.179:9910/p/query/phoneUserUnbindDevice";
    //修改绑定设备的备注信息
    public static String modifyInfo_url="http://47.97.165.179:9910/p/query/modifyPhoneUserAndDeviceNote";
    //查询手机绑定了多少台设备
    public static String QueryBindInfo_url="http://47.97.165.179:9910/p/query/selectPhoneUserAndDeviceInfoByPhone";
    //查询使用该设备的用户
    public static String QueryUserInfo_url="http://47.97.165.179:9910/p/query/selectDeviceUserInfo";
    //查询记录
    public static String QueryRecord_url="http://47.97.165.179:9910/p/query/selectUnlockRecordByDevice";
    public static String operation="http://47.97.165.179:9910/p/operation/phoneOperation";

    public static String phonePrivateKey;
    public static String serverPublicKey;
    public static String id;

    public static SharedPreferences shp;
    public static SharedPreferences.Editor editor;

    public static JSONArray user_info_extra;
    public static JSONArray record_info_extra;
    public static JSONArray device_info_extra;

    //生成请求头
    public static String genToken(String id,String commKey,String phonePriKey,String servePubKey){
        JSONObject res=new JSONObject();
        res.put("id",id);//id为用户的手机号码

        //signature(id和time)用于用户验证
        // 将JSON字符串用用户的私钥加密，再用URL安全的base64算法加密后得到signature参数
       JSONObject signature=new JSONObject();
        signature.put("id",id);
        signature.put("time",System.currentTimeMillis());
        byte[] sigtem= RSAUtils.encodeByPrivateKey(phonePriKey,signature.toJSONString().getBytes());
        String sig=Base64Utils.encodeURL(sigtem);
        res.put("signature",sig);

        //key是加密后的通信密钥，原为URL安全的base64算法加密后的通信密钥
        // 将此字符串用服务端的公钥加密后，再使用URL安全的base64算法加密后得到key参数。
        byte[] keytem=RSAUtils.encodeByPublicKey(servePubKey,commKey.getBytes());
        String key=Base64Utils.encodeURL(keytem);
        res.put("key",key);

        return Base64Utils.encodeURL(res.toJSONString().getBytes());

    }


    //不带请求头，网络请求
    public static void webRequest(String url, Map<String,String> content, final MyViewModel myViewModel, final String tip){
        //添加请求的参数
        FormBody.Builder fromBodyBuilder=new FormBody.Builder();
        for (String key:content.keySet()){//遍历
            fromBodyBuilder.add(key,content.get(key));//添加参数,多次调用add函数，添加多个参数
        }
        //生成请求的对象
        //先创建builder对象，通过builder对象创建请求对象
        Request.Builder builder=new Request.Builder();
        builder.url(url);//添加请求路径
        builder.post(fromBodyBuilder.build());//将参数装进请求对象，请求方式为post
        Request request=builder.build();

        //发起一次网络请求
        Call call=client.newCall(request);//client为请求的客户端
        call.enqueue(new Callback() {
            @Override
            //enqueue函数为异步请求，异步请求，请求会重新开一个线程，不会一直等结果
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            //服务端返回的结果，结果在response的body方法中
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res=response.body().string();
                Map<String,String> map=new HashMap<>();
                map.put("type",tip);
                map.put("content",res);
                myViewModel.liveData.postValue(map);
        }
        });
    }

    //带有Token的请求
    public static void webRequestWithToken(final boolean isEncode, String url, Map<String,String> content, final MyViewModel myViewModel, final String tip) throws JSONException {
                    //添加请求的参数
                    FormBody.Builder fromBodyBuilder=new FormBody.Builder();
                    final String commk= AESUtils.generateKey().get("key");
                    String token=genToken(id,commk,phonePrivateKey,serverPublicKey);

                    //判断是否需要加密数据
                    if(isEncode){
                        for (String key:content.keySet()){
                            fromBodyBuilder.add(key,Base64Utils.encodeURL(AESUtils.encode(commk,content.get(key).getBytes())));
                        }
                    }else{
                        for (String key:content.keySet()){
                            fromBodyBuilder.add(key,content.get(key));
                        }
                    }

                    //生成请求的对象
                    //先创建builder对象，通过builder对象创建请求对象
                    Request.Builder builder=new Request.Builder();
                    builder.url(url);//请求的路径
                    builder.addHeader("token",token);
                    builder.post(fromBodyBuilder.build());//将参数装进请求对象，请求方式为post
                    final Request request=builder.build();//生成请求对象

                    //发起一次网络请求
                    Call call=client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String res=response.body().string();
                            //判断是否是JSON格式，如果请求被拦截，返回的JSON格式数据，
                            //没有被拦截返回的数据是被加密的，不是JSON字符串，解密之后才是
                            JSONObject jsonObject=isJSON(res);
                            //被拦截的返回
                            //LIVEDATA中的存储的数据是map类型的，订阅者根据map中的type的值来判断是否是自己需要的信息
                            if(jsonObject!=null){
                                Map<String,String> map=new HashMap<>();
                                map.put("type",tip);
                                map.put("content",jsonObject.toJSONString());
                                myViewModel.liveData.postValue(map);
                            }
                            //判断是否是加密通信
                            if(isEncode){
                                //加密通信，先解密
                                byte[] t=AESUtils.decode(commk,Base64Utils.decodeURL(res));
                                Map<String,String> map=new HashMap<>();
                                map.put("type",tip);
                                map.put("content",new String(t));
                                myViewModel.liveData.postValue(map);
                            }else {
                                //非加密通信直接返回JSON格式数据
                                //LIVEDATA中的存储的数据是map类型的，订阅者根据map中的type的值来判断是否是自己需要的信息
                                Map<String,String> map=new HashMap<>();
                                map.put("type",tip);
                    map.put("content",res);
                    myViewModel.liveData.postValue(map);

                }

            }
        });

    }

    public static JSONObject isJSON(String src){
        try{
            //try catch不一定都只有检测异常一种功能，还有其他用途
            JSONObject object = JSONObject.parseObject(src);//将src转换为JSONObject对象
            return object;
        }catch (RuntimeException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void wrPOST_text(final boolean isEncode,String url, Map<String, String> parameter, String content,final MyViewModel myViewModel, final String tip) {
        final String commk= AESUtils.generateKey().get("key");
        String token=genToken(id,commk,phonePrivateKey,serverPublicKey);
        //判断是否需要加密数据
        if(isEncode){
                content =Base64Utils.encodeURL(AESUtils.encode(commk,content.getBytes()));
        }

        StringBuffer sb = new StringBuffer();
        sb.append(url);
        if (parameter!=null&&!parameter.isEmpty()) {
            sb.append("?");
            for (String key : parameter.keySet()) {
                sb.append(key + "=" + parameter.get(key) + "&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        url = sb.toString();

        RequestBody requestBody = RequestBody.create(content,MediaType.parse("text/plain;charset=utf-8"));
        //生成请求的对象
        //先创建builder对象，通过builder对象创建请求对象
        Request.Builder builder = new Request.Builder();
        builder.url(url);//请求的路径
        builder.addHeader("token",token);
        builder.post(requestBody);
        Request request = builder.build();//生成请求对象

        //发起一次网络请求
        Call call = client.newCall(request);//client为请求的客户端
        call.enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res=response.body().string();
                //判断是否是JSON格式，如果请求被拦截，返回的JSON格式数据，
                //没有被拦截返回的数据是被加密的，不是JSON字符串，解密之后才是
                JSONObject jsonObject=isJSON(res);
                //被拦截的返回
                //LIVEDATA中的存储的数据是map类型的，订阅者根据map中的type的值来判断是否是自己需要的信息
                if(jsonObject!=null){
                    Map<String,String> map=new HashMap<>();
                    map.put("type",tip);
                    map.put("content",jsonObject.toJSONString());
                    myViewModel.liveData.postValue(map);
                }
                //判断是否是加密通信
                if(isEncode){
                    //加密通信，先解密
                    byte[] t=AESUtils.decode(commk,Base64Utils.decodeURL(res));
                    Map<String,String> map=new HashMap<>();
                    map.put("type",tip);
                    map.put("content",new String(t));
                    myViewModel.liveData.postValue(map);
                }else {
                    //非加密通信直接返回JSON格式数据
                    //LIVEDATA中的存储的数据是map类型的，订阅者根据map中的type的值来判断是否是自己需要的信息
                    Map<String,String> map=new HashMap<>();
                    map.put("type",tip);
                    map.put("content",res);
                    myViewModel.liveData.postValue(map);

                }

            }
        });
    }

}

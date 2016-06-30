package uk.ac.openlab.radio.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.zeromq.ZMQ;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.datatypes.JoinResult;
import uk.ac.openlab.radio.datatypes.Studio;

/**
 * Created by kylemontague on 28/04/16.
 */
public class CloudStudioApi {


    final OkHttpClient  client = new OkHttpClient();
    String host = null;
    int port;


    private String cookie ="";
    private String session = null;


    private static CloudStudioApi sharedInstance;
    public static CloudStudioApi shared(){
        if(sharedInstance == null)
            sharedInstance = new CloudStudioApi();
        return sharedInstance;
    }


    public void init(Context context){
        host = context.getString(R.string.pref_studio_http_host);
        port = context.getResources().getInteger(R.integer.pref_studio_http_port);

        GlobalUtils.shared().init(context);
        if(GlobalUtils.shared().sessionID()!= null) {
            this.session = GlobalUtils.shared().sessionID();
            this.cookie = GlobalUtils.shared().getCookie();
        }
    }

    private void setCookie(String cookie){
        if(cookie != null && cookie.length()>1) {
            this.cookie = cookie;
            this.session = URLDecoder.decode(((this.cookie.split("=")[1]).split(";")[0]));
            GlobalUtils.shared().setCookie(this.cookie);
            GlobalUtils.shared().setSession(this.session);
        }
    }

    public String getCookie(){
        return (cookie==null)?"":cookie;
    }



    public void listStudios(Callback callback){
        String path = "/studios";

        try {
            get(path,callback);
        } catch (Exception e) {
            Log.d("HTTP3", e.toString());
        }
    }

    public void join(final String joincode, String areacode, String identifier, String lang, final IMessageListener isValid){
        String path = "/studio/join";
        Hashtable<String, String> map = new Hashtable<>();
        map.put("lang",lang);
        map.put("areacode",areacode);
        map.put("joincode",joincode);
        map.put("phonenumber",identifier);
        map.put("name","host");

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                isValid.fail();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
                setCookie(responseHeaders.get("set-cookie"));
                String data = response.body().string();
                Gson gson = new GsonBuilder().create();
                JoinResult joinResult = gson.fromJson(data,JoinResult.class);

                GlobalUtils.shared().setCitizenRadioNumber(joinResult.studio.getFreeswitch_phonenumber(), joinResult.studio.getName());
                GlobalUtils.shared().setStudioID(String.format("%d",joinResult.studio.getId()));

                isValid.success();

            }
        };


        try {
            post(path,map, callback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("HTTP3",e.toString());
        }
    }


    private void get(String path, Callback callback) throws Exception{

        if(host == null)
            throw new Exception("Host is null, ensure you have called init(Context context)");


        Request request = new Request.Builder()
                .url(String.format("%s:%d%s",host,port,path))
                .addHeader("Cookie",getCookie())
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    private void post(String path, Hashtable<String, String> params, Callback callback) throws Exception {

        if(host == null)
            throw new Exception("Host is null, ensure you have called init(Context context)");

        Gson gson = new Gson();
        String body = gson.toJson(params);

        Log.d("GSON",body);

        Request request = new Request.Builder()
                .url(String.format("%s:%d%s",host,port,path))
                .addHeader("Cookie",getCookie())
                .post(RequestBody.create(MediaType.parse("application/json"),body))
                .build();

        client.newCall(request).enqueue(callback);
    }
}

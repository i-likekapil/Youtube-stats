package com.example.kapil.youtubestats;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapil.youtubestats.adapter.ChannelNameSearchAdapter;
import com.example.kapil.youtubestats.model.ChannelStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    //https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UCQHLxxBFrbfdrk1jF0moTpw&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw
    //https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=love%20babber&type=channel&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw

    ArrayList<ChannelStats> channelName;
    String Query_url1 = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=";
    String Query_url2 = "&type=channel&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw";

    public static String stats_url1 = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails%2Cstatistics&id=";
    public static String stats_url2="&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw";

    EditText et_search;
    Button btn_submit;
    String searchText = "";
    ChannelNameSearchAdapter adapter;
    RecyclerView recyclerView;
    private ArrayList<ChannelStats> dup_channel = new ArrayList<>();
    private String Query_url;
    public static RequestQueue requestQueue;
    ArrayList<ChannelStats> arrayList /*= new ArrayList<>()*/;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        channelName = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        //check network connectivity
        ConnectivityManager cManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            Toast.makeText(getApplicationContext(), "Network Available", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Network Not Available", Toast.LENGTH_SHORT).show();

        et_search = findViewById(R.id.et_channel_name);
        btn_submit = findViewById(R.id.btn_search);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        btn_submit.setOnClickListener(l -> {
            searchText = et_search.getText().toString().toLowerCase().trim();
            Query_url = Query_url1 + searchText + Query_url2;
            channelName = doNetworkCall();
        });
        adapter = new ChannelNameSearchAdapter(getApplicationContext()/*, dup_channel*/);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ChannelNameSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                System.out.println(arrayList.size()+"  hhhhhhhh ");
                System.out.println(arrayList.get(position).getChannel_name());
               String url = stats_url1 + arrayList.get(position).getChannel_id() + stats_url2;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //JSONObject baseJsonResponse  = new JSONObject(response);
                            JSONArray itemsArray = response.getJSONArray("items");
                            JSONObject statistics = itemsArray.getJSONObject(0).getJSONObject("statistics");
                            System.out.println(statistics);
                            String result = "viewCount : "+statistics.getString("viewCount")+"\nsubscriberCount : "+statistics.getString("subscriberCount")+"\nvideoCount : "+statistics.getString("videoCount");
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(jsonObjectRequest);


            }


        });
    }

    private ArrayList<ChannelStats> doNetworkCall() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ArrayList<ChannelStats> dup_channel = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Query_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //JSONObject baseJsonResponse  = new JSONObject(response);
                    JSONArray itemsArray = response.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject currentChannel = itemsArray.getJSONObject(i);
                        JSONObject snippet = currentChannel.getJSONObject("snippet");
                        String channelId = snippet.getString("channelId");
                        String channelName = snippet.getString("title");
                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        JSONObject MediumUrl = thumbnails.getJSONObject("medium");
                        String thumbnail = MediumUrl.getString("url");
                        //System.out.println(thumbnail);
                        ChannelStats stats = new ChannelStats(channelId, channelName,thumbnail);
                        dup_channel.add(stats);

                    }
                    adapter.setData(dup_channel);
                    arrayList=dup_channel;
                    System.out.println(dup_channel.size());
                    adapter.notifyDataSetChanged();


                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
        return dup_channel;
    }


}

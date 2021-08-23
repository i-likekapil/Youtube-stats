package com.example.kapil.youtubestats;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static String stats_url1 = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails%2Cstatistics&id=";
    public static String stats_url2 = "&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw";
    public static RequestQueue requestQueue;
    private ArrayList<ChannelStats> channelName;
    private final String Query_url1 = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=";
    private final String Query_url2 = "&type=channel&key=AIzaSyDM9pabe09smtH5laOLBXx4iVL_B-XnrKw";
    private String searchText = "";
    private ChannelNameSearchAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<ChannelStats> arrayList;
    private String Query_url;
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());

        channelName = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        if (checkNetworkAccess()) {
            ConstraintLayout layout_available = findViewById(R.id.main_layout);
            layout_available.setVisibility(View.VISIBLE);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            adapter = new ChannelNameSearchAdapter(getApplicationContext()/*, dup_channel*/);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new ChannelNameSearchAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    System.out.println(arrayList.size() + "  hhhhhhhh ");
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
                                String result = "viewCount : " + statistics.getString("viewCount") + "\nsubscriberCount : " + statistics.getString("subscriberCount") + "\nvideoCount : " + statistics.getString("videoCount");
                                //Result=getResult(result);
                                //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                                builder.setTitle("Your Stats");
                                builder.setMessage(result);
                                builder.setCancelable(true);
                                builder.show();
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

        } else {
            ConstraintLayout layout_unavailable = findViewById(R.id.layout_unavailable);
            layout_unavailable.setVisibility(View.VISIBLE);
        }

    }

    private boolean checkNetworkAccess() {
        ConnectivityManager cManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        //Toast.makeText(getApplicationContext(), "Network Not Available", Toast.LENGTH_SHORT).show();
        return networkInfo != null && networkInfo.isConnected(); //Toast.makeText(getApplicationContext(), "Network Available", Toast.LENGTH_SHORT).show();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            System.out.println(query);
            searchText = query.trim();
            searchText.replace(" ", "%20");
            Query_url = Query_url1 + searchText + Query_url2;
            channelName = doNetworkCall();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    private ArrayList<ChannelStats> doNetworkCall() {
        ProgressDialog progressDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
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
                        ChannelStats stats = new ChannelStats(channelId, channelName, thumbnail);
                        dup_channel.add(stats);

                    }
                    adapter.setData(dup_channel);
                    arrayList = dup_channel;
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
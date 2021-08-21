package com.example.kapil.youtubestats.adapter;

import static com.example.kapil.youtubestats.MainActivity.requestQueue;
import static com.example.kapil.youtubestats.MainActivity.stats_url1;
import static com.example.kapil.youtubestats.MainActivity.stats_url2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.kapil.youtubestats.R;
import com.example.kapil.youtubestats.model.ChannelStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelNameSearchAdapter extends RecyclerView.Adapter<ChannelNameSearchAdapter.ChannelNameAdapterViewHolder> {

    Context context;
    ArrayList<ChannelStats> channelMap = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setData(ArrayList<ChannelStats> arrayList){
        channelMap.clear();
        channelMap=arrayList;
    }

    public ChannelNameSearchAdapter(Context context) {
        this.context = context;
        /*this.channelMap = channelMap;*/
    }
    ViewGroup parent;

    @NonNull
    @Override
    public ChannelNameAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent= parent;
        return new ChannelNameAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.channel_name_item_list, parent, false),listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelNameAdapterViewHolder holder, int position) {
        String channelNameId = channelMap.get(position).getChannel_name();
        holder.tv_channel_name.setText(channelNameId);
        Glide.with(context).load(channelMap.get(position).getThumbnail()).into(holder.iv_thumbnail);


    }


    public  void setOnItemClickListener (OnItemClickListener mlistener){
        listener = mlistener;
    }

    @Override
    public int getItemCount() {
        return channelMap.size();
    }

    class ChannelNameAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_channel_name;
        CircleImageView iv_thumbnail;
        public ChannelNameAdapterViewHolder(@NonNull View itemView,OnItemClickListener mlistener) {
            super(itemView);
            tv_channel_name = itemView.findViewById(R.id.tv_channel_name_rv);
            iv_thumbnail =itemView.findViewById(R.id.iv_thumbnail);
        itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mlistener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mlistener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

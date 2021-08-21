package com.example.kapil.youtubestats.model;

public class ChannelStats {
    private String channel_name;
    private String channel_id;
    private String thumbnail;

    public ChannelStats(String channel_id, String channel_name, String thumbnail) {
        this.channel_id = channel_id;
        this.channel_name = channel_name;
        this.thumbnail = thumbnail;

    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public String getChannel_id() {
        return channel_id;
    }
}

package com.kangtech.tauonstream.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class musicModel {

    @SerializedName("index")
    @Expose
    public Integer index;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("artist")
    @Expose
    public String artist;
    @SerializedName("lyrics")
    @Expose
    public String lyrics;

}

package com.kangtech.tauonstream.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class updateModel {

    @SerializedName("position")
    @Expose
    public Float position;
    @SerializedName("index")
    @Expose
    public Integer index;
    @SerializedName("port")
    @Expose
    public String port;

}

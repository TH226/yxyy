package com.example.asus.yxyyeapp.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 威威 on 2016/8/24.
 */
public class Recruit_information extends BmobObject {
    private String my_phone;
    private String address;
    private String apply_time;
    private String gather_time;
    private String gather_address;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    private BmobFile activity_image;

    public String getMy_phone() {
        return my_phone;
    }

    public void setMy_phone(String my_phone) {
        this.my_phone = my_phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApply_time() {
        return apply_time;
    }

    public void setApply_time(String apply_time) {
        this.apply_time = apply_time;
    }

    public String getGather_time() {
        return gather_time;
    }

    public void setGather_time(String gather_time) {
        this.gather_time = gather_time;
    }

    public String getGather_address() {
        return gather_address;
    }

    public void setGather_address(String gather_address) {
        this.gather_address = gather_address;
    }

    public BmobFile getActivity_image() {
        return activity_image;
    }

    public void setActivity_image(BmobFile activity_image) {
        this.activity_image = activity_image;
    }
}

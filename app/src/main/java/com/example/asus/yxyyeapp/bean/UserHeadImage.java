package com.example.asus.yxyyeapp.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 威威 on 2016/8/18.
 */
public class UserHeadImage extends BmobObject {

    private BmobFile imageFile;

    public BmobFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(BmobFile imageFile) {
        this.imageFile = imageFile;
    }
}

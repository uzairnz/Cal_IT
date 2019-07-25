package com.zubairy.cal_it;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ravi Tamada on 07/10/16.
 * www.androidhive.info
 */

@IgnoreExtraProperties
public class Users {

    public String name;
    public String email , phone , age , height , weight;



    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Users() {
    }

    public Users(String name, String email , String phone , String age , String height , String weight) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.phone = phone;
        this.height = height;
        this.weight = weight;
    }
}

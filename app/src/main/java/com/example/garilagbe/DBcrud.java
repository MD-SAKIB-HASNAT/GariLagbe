package com.example.garilagbe;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class DBcrud extends DBhelper{
    public DBcrud(@Nullable Context context) {
        super(context);
    }


    public long userInsert(String email, String pass){
        SQLiteDatabase wv = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", pass);
        long result = wv.insert(TB_Name,null,values);
        wv.close();
        return result;
    }
}

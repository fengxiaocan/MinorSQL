package com.app.msql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.app.data.ObjectReflect;
import com.app.db.SQLiteRecord;
import com.app.db.SQLiteRelevance;

import java.util.Map;

class MSQLiteDatabase extends SQLiteOpenHelper {

    public MSQLiteDatabase(Context context,String name,int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLiteRecord.CREATE_SQL);
        sqLiteDatabase.execSQL(SQLiteRelevance.CREATE_SQL);
        Map<String, ObjectReflect> reflects = MSQLHelper.getReflect();
        for (String keySet : reflects.keySet()) {
            ObjectReflect reflect = reflects.get(keySet);
            reflect.invokeCreateSQL(sqLiteDatabase);
            SQLiteRecord.saveSQL(sqLiteDatabase,keySet,reflect.getCreateTable());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,int newVersion) {
        Map<String, ObjectReflect> reflects = MSQLHelper.getReflect();
        for (String keySet : reflects.keySet()) {
            ObjectReflect reflect = reflects.get(keySet);
            reflect.invokeUpdateSQL(sqLiteDatabase);
        }
    }
}

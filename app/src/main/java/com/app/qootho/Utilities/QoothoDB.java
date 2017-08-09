package com.app.qootho.Utilities;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.app.qootho.Model.PassengerAccount;
import com.app.qootho.Model.RiderType;
import com.app.qootho.Model.TokenObject;
import com.app.qootho.Model.UserAccount;

import java.io.File;
import java.util.ArrayList;

public class QoothoDB extends SQLiteOpenHelper {

    // Logcat tag
    public static final String LOG = "DatabaseHelper";

    // Database Version
    public static final int DATABASE_VERSION = 5;


    // Table Names
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_UTILITY = "Utility";
    public static final String TABLE_LOGIN_DETAIL = "TokenDetail";
    public static final String TABLE_PASSENGER_ACCOUNT = "PassengerAccount";
    public static final String TABLE_RIDER_TYPE = "RiderType";
    //BankName
    public static final String KEY_ID = "Id";
    public static final String KEY_USER_TOKEN = "token";
    public static final String KEY_TIME_EXPIRES = "timeExpires";
    public static final String KEY_EXPIRE_DATE = "expireDate";
    public static final String KEY_EXPIRE_IN = "expireIn";
    public static final String KEY_TOKEN_TYPE = "tokenType";
    public static final String KEY_INSTITUTION_ID = "institutionID";
    public static final String KEY_INSTITUTION_NAME = "institutionName";
    public static final String KEY_RIDER_ID = "riderId";
    public static final String KEY_RIDER_NAME = "rideTypeName";
    private static final String DATABASE_NAME = "QoothoDB";
    private static final File FILE_DIR = null;
    private static final String KEY_COUNTRY_ID = "countryID";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DOB = "dob";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_HOME_ADDRESS = "homeAddress";
    private static final String KEY_HOME_LATITUDE = "homeLat";
    private static final String KEY_HOME_LONGITUDE = "homeLong";
    private static final String KEY_PERSONAL_REFERAL_CODE = "personalReferralCode";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_REGISTER_DATE = "registeredDate";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_WORK_ADDRESS = "workAddress";
    private static final String KEY_WORK_LATITUDE = "workLat";
    private static final String KEY_WORK_LONGITUDE = "workLong";
    private static final String KEY_ISSUE_DATE = "issueDate";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DATE_CREATED = "DateCreated";
    // create registration
    public static final String CREATE_PASSENGER_ACCOUNT = "CREATE TABLE "
            + TABLE_PASSENGER_ACCOUNT + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_INSTITUTION_ID + " TEXT,"
            + KEY_INSTITUTION_NAME + " TEXT, "
            + KEY_DATE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";

    // create registration
    public static final String CREATE_RIDER_TYPE = "CREATE TABLE "
            + TABLE_RIDER_TYPE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_RIDER_ID + " TEXT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_RIDER_NAME + " TEXT, "
            + KEY_DATE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";

    // create registration
    public static final String CREATE_TABLE_TOKEN = "CREATE TABLE "
            + TABLE_LOGIN_DETAIL + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_TOKEN + " TEXT,"
            + KEY_TIME_EXPIRES + " TEXT,"
            + KEY_ISSUE_DATE + " DATETIME,"
            + KEY_EXPIRE_DATE + " DATETIME,"
            + KEY_USERNAME + " TEXT,"
            + KEY_EXPIRE_IN + " TEXT, "
            + KEY_TOKEN_TYPE + " TEXT,"
            + KEY_DATE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";
    // create registration
    public static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_COUNTRY_ID + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_DOB + " TEXT,"
            + KEY_FIRST_NAME + " TEXT,"
            + KEY_HOME_ADDRESS + " TEXT,"
            + KEY_HOME_LATITUDE + " TEXT, "
            + KEY_HOME_LONGITUDE + " TEXT,"
            + KEY_PERSONAL_REFERAL_CODE + " TEXT,"
            + KEY_PHONE_NUMBER + " TEXT,"
            + KEY_PHOTO + " TEXT,"
            + KEY_REGISTER_DATE + " TEXT,"
            + KEY_SURNAME + " TEXT,"
            + KEY_WORK_ADDRESS + " TEXT,"
            + KEY_WORK_LATITUDE + " TEXT,"
            + KEY_WORK_LONGITUDE + " TEXT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_DATE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";
    private static final String KEY_COUNTRY_CODE = "CountryCode";
    // create registration
    public static final String CREATE_UTILITY = "CREATE TABLE "
            + TABLE_UTILITY + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_COUNTRY_CODE + " TEXT, "
            + KEY_DATE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";
    private Context ctx;


    public QoothoDB(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME,null);
        /*super(context, Environment.getExternalStorageDirectory()
                + File.separator + FILE_DIR
	            + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
		  SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()
		            + File.separator + FILE_DIR
		            + File.separator + DATABASE_NAME,null);
		*/
        //	insertRole();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_TOKEN);
        db.execSQL(CREATE_UTILITY);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_PASSENGER_ACCOUNT);
        db.execSQL(CREATE_RIDER_TYPE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i("DATABASE_UPGRADE", "oldVersion=" + oldVersion + " newVersion=" + newVersion);
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSENGER_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDER_TYPE);

        onCreate(db);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////CRUD OPERATION //////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////

    private SQLiteDatabase getDatabaseContext() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db;
    }


    public boolean saveUserProfile(Context ctx, UserAccount user, String username) {
        boolean returnValue = false;
        SQLiteDatabase db = getDatabaseContext();
        SessionManager session = new SessionManager(ctx);
        delete(ctx, TABLE_USERS, "");
        try {
            //db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
            System.out.println("token.getExpires_in():: " + user.getFirstName() + " =token.username" + username);

            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, username);
            values.put(KEY_COUNTRY_ID, user.getCountryID());
            values.put(KEY_FIRST_NAME, user.getFirstName());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_DOB, user.getDob());
            values.put(KEY_HOME_ADDRESS, user.getHomeAddress());
            values.put(KEY_HOME_LATITUDE, user.getHomeLat());
            values.put(KEY_HOME_LONGITUDE, user.getHomeLong());
            values.put(KEY_PERSONAL_REFERAL_CODE, user.getPersonalReferralCode());
            values.put(KEY_PHONE_NUMBER, user.getPhoneNumber());
            values.put(KEY_PHOTO, user.getPhoto());
            values.put(KEY_REGISTER_DATE, user.getRegisteredDate());
            values.put(KEY_SURNAME, user.getSurname());
            values.put(KEY_WORK_ADDRESS, user.getWorkAddress());
            values.put(KEY_WORK_LATITUDE, user.getWorkLat());
            values.put(KEY_WORK_LONGITUDE, user.getWorkLong());
            Log.i("values:: ", values.toString());

            long id = db.insert(TABLE_USERS, null, values);
            returnValue = id > 0;
            Log.i("returnValue::id ", id + "");
            session.createProfileSession(user, username);
            db.close();

            //returnValue ;
        } catch (Exception ex) {
            ex.printStackTrace();
            db.close();
            returnValue = false;
        }
        Log.i("returnValue:: ", returnValue + "");
        return returnValue;
    }

    public boolean registerToken(Context ctx, TokenObject token) {
        //String token,String username,String timeExpire,String dateIssued,String dateExpire
        SQLiteDatabase db = getDatabaseContext();
        SessionManager session = new SessionManager(ctx);
        delete(ctx, TABLE_LOGIN_DETAIL, "");
        try {
            //db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
            System.out.println("token.getExpires_in():: " + token.getExpires_in() + " =token.getExpires()" + token.getExpires());
            ContentValues values = new ContentValues();
            values.put(KEY_USER_TOKEN, token.getToken());
            values.put(KEY_USERNAME, token.getUserName());
            values.put(KEY_TIME_EXPIRES, token.getExpires_in());
            values.put(KEY_EXPIRE_DATE, token.getExpires());
            values.put(KEY_ISSUE_DATE, token.getIssued());
            values.put(KEY_TOKEN_TYPE, token.getToken_type());

            Log.i("values:: ", values.toString());

            boolean returnValue = db.insert(TABLE_LOGIN_DETAIL, null, values) > 0;
            Log.i("returnValue Token :: ", returnValue + "");
            db.close();
            session.createLoginSession(token);
            return (returnValue);
        } catch (Exception ex) {
            db.close();
            return false;
        }
    }

    public boolean saveCountryCode(Context ctx, String token, String username) {
        SQLiteDatabase db = getDatabaseContext();
        try {
            //db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
            ContentValues values = new ContentValues();
            values.put(KEY_COUNTRY_CODE, token);
            values.put(KEY_USERNAME, username);
            boolean returnValue = db.insert(TABLE_USERS, null, values) > 0;
            db.close();
            return (returnValue);
        } catch (Exception ex) {
            db.close();
            return false;
        }
    }

    public boolean SetUpPassengerAccount(Context ctx, PassengerAccount account, String username) {
        SQLiteDatabase db = getDatabaseContext();
        try {
            //db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
            ContentValues values = new ContentValues();
            values.put(KEY_INSTITUTION_ID, account.getInstitutionID());
            values.put(KEY_INSTITUTION_NAME, account.getInstitutionName());
            values.put(KEY_USERNAME, username);
            boolean returnValue = db.insert(TABLE_PASSENGER_ACCOUNT, null, values) > 0;
            db.close();
            System.out.println("returnValue:: " + returnValue);

            return (returnValue);
        } catch (Exception ex) {
            db.close();
            return false;
        }
    }

    public boolean SetUpRiderType(Context ctx, RiderType rider, String username) {
        SQLiteDatabase db = getDatabaseContext();
        delete(ctx, TABLE_RIDER_TYPE, "");
        try {
            //db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
            ContentValues values = new ContentValues();
            values.put(KEY_RIDER_ID, rider.getId());
            values.put(KEY_USERNAME, username);
            values.put(KEY_RIDER_NAME, rider.getRideTypeName());
            boolean returnValue = db.insert(TABLE_RIDER_TYPE, null, values) > 0;
            db.close();
            return (returnValue);
        } catch (Exception ex) {
            db.close();
            return false;
        }
    }

    /////////////GET QUERY /////////////


    public Cursor getUSerByUsername(String profileId) {
        Cursor cursor = null;
        //String selectQuery = "SELECT  * FROM " + TABLE_CARDS +" WHERE ProfileId ="+ profileId ;
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE username ='" + profileId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e(LOG, selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public UserAccount getUserProfile(String UserName) {
        UserAccount user = null;
        try {
            Cursor cursor = null;
            String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE username ='" + UserName + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            Log.e(LOG, selectQuery);
            cursor = db.rawQuery(selectQuery, null);
            System.out.println("cursor:::  " + cursor.getCount());
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        user = new UserAccount();
                        Log.i("KEY_COUNTRY_ID ", cursor.getInt(cursor.getColumnIndex(KEY_COUNTRY_ID)) + "");

                        user.setCountryID(cursor.getInt(cursor.getColumnIndex(KEY_COUNTRY_ID)));
                        user.setDob(cursor.getString(cursor.getColumnIndex(KEY_DOB)));
                        user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));

                        user.setFirstName(cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)));
                        user.setHomeAddress(cursor.getString(cursor.getColumnIndex(KEY_HOME_ADDRESS)));


                        user.setPersonalReferralCode(cursor.getString(cursor.getColumnIndex(KEY_PERSONAL_REFERAL_CODE)));

                        user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)));
                        user.setRegisteredDate(cursor.getString(cursor.getColumnIndex(KEY_REGISTER_DATE)));
                        user.setPhoto(cursor.getString(cursor.getColumnIndex(KEY_PHOTO)));

                        user.setSurname(cursor.getString(cursor.getColumnIndex(KEY_SURNAME)));
                        user.setWorkAddress(cursor.getString(cursor.getColumnIndex(KEY_WORK_ADDRESS)));

                        if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(KEY_WORK_LATITUDE)))) {
                            user.setWorkLat(0.0);
                        } else {
                            user.setWorkLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_WORK_LATITUDE))));
                        }

                        if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(KEY_WORK_LONGITUDE)))) {
                            user.setWorkLong(0.0);
                        } else {
                            user.setWorkLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_WORK_LONGITUDE))));
                        }

                        if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(KEY_HOME_LATITUDE)))) {
                            user.setHomeLat(0.0);
                        } else {
                            user.setHomeLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_HOME_LATITUDE))));
                        }

                        if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(KEY_HOME_LONGITUDE)))) {
                            user.setHomeLong(0.0);
                        } else {
                            user.setHomeLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_HOME_LONGITUDE))));
                        }

                    } while (cursor.moveToNext());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            user = null;
        }
        return user;
    }

    public Cursor getCountryCodeByUSername(String profileId) {
        Cursor cursor = null;
        //String selectQuery = "SELECT  * FROM " + TABLE_CARDS +" WHERE ProfileId ="+ profileId ;
        String selectQuery = "SELECT  * FROM " + TABLE_UTILITY + " WHERE username ='" + profileId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e(LOG, selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public TokenObject getTokenByUsername(String profileId) {
        TokenObject object = new TokenObject();
        Cursor cursor = null;
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN_DETAIL + " WHERE username ='" + profileId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e(LOG, selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        System.out.println("cursor:::  " + cursor.getCount());
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    object.setToken(cursor.getString(cursor.getColumnIndex(KEY_USER_TOKEN)));
                    object.setExpiresIn(cursor.getString(cursor.getColumnIndex(KEY_EXPIRE_DATE)));

                } while (cursor.moveToNext());
            }
        } else {
            object = null;
        }
        return object;
    }


    public ArrayList<PassengerAccount> GetPassengerAccount(String profileId) {
        PassengerAccount object = new PassengerAccount();
        ArrayList<PassengerAccount> _account = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery = "SELECT  * FROM " + TABLE_PASSENGER_ACCOUNT + " WHERE username ='" + profileId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e(LOG, selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        System.out.println("TABLE_PASSENGER_ACCOUNT :::  " + cursor.getCount());
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    object.setInstitutionID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_INSTITUTION_ID))));
                    object.setInstitutionName(cursor.getString(cursor.getColumnIndex(KEY_INSTITUTION_NAME)));
                    _account.add(object);
                } while (cursor.moveToNext());
            }
        } else {
            _account = null;
        }
        return _account;
    }

    public ArrayList<RiderType> GetRiderType(String profileId) {
        RiderType object = new RiderType();
        ArrayList<RiderType> riderType = new ArrayList<>();
        Cursor cursor = null;
        String selectQuery = "SELECT  * FROM " + TABLE_RIDER_TYPE + " WHERE username ='" + profileId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e(LOG, selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        System.out.println("Rider Type cursor:::  " + cursor.getCount());
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    object.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_RIDER_ID))));
                    object.setRideTypeName(cursor.getString(cursor.getColumnIndex(KEY_RIDER_NAME)));
                    riderType.add(object);
                } while (cursor.moveToNext());
            }
        } else {
            riderType = null;
        }
        return riderType;
    }


    public Cursor getToken() {
        Cursor cursor;
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN_DETAIL;
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e(LOG, selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        Log.i("getCount- Token ", cursor.getCount() + "");
        return cursor;
    }

    public void delete(Context ctx, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DELETE", "<< DELETE IN PROGRESSSSSSSSSSSSS>>> " + tableName);
        try {
            //db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
            int rowsAffected = db.delete(tableName, "", null);
            boolean returnValue = false;
            if (rowsAffected > 0) {
                returnValue = true;
            }

            db.close();
        } catch (Exception ex) {
            db.close();
            ex.printStackTrace();
        }
    }

    public void delete(Context ctx, String tableName, String empty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DELETE", "<< DELETE IN PROGRESSSSSSSSSSSSS>>> " + tableName);
        try {
            int rowsAffected = db.delete(tableName, "", null);
            boolean returnValue = false;
            if (rowsAffected > 0) {
                returnValue = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

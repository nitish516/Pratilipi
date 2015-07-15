package com.pratilipi.pratilipi.helper;

/**
 * Created by Nitish on 01-04-2015.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

public class PratilipiProvider extends ContentProvider {
    // fields for pratilipi content provider
    public static final String PROVIDER_NAME = "com.pratilipi.pratilipi.helper.PratilipiData";
    public static final String CONTENT_URL = "content://" + PROVIDER_NAME + "/content";
    public static final String METADATA_URL = "content://" + PROVIDER_NAME + "/metadata";
    public static final String CATEGORIES_URL = "content://" + PROVIDER_NAME + "/categories";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URL);
    public static final Uri METADATA_URI = Uri.parse(METADATA_URL);
    public static final Uri CATEGORIES_URI = Uri.parse(CATEGORIES_URL);

    // fields for the database
    public static final String ID = "id";
    public static final String PID = "_pid";
    public static final String CH_NO = "_ch_no";
    public static final String CONTENT = "_content";
    public static final String TITLE = "_title";
    public static final String CONTENT_TYPE = "_content_type";
    public static final String AUTHOR_ID = "_author_id";
    public static final String AUTHOR_NAME = "_author_name";
    public static final String CH_COUNT = "_ch_count";
    public static final String INDEX = "_index";
    public static final String IMG_URL = "_img_url";
    public static final String PG_URL = "_pg_url";
    public static final String IMAGE = "_img";
    public static final String LIST_TYPE = "_list_type";
    public static final String RATING_COUNT = "_rating_count";
    public static final String STAR_COUNT = "_star_count";
    public static final String SUMMARY = "_summary";
    public static final String IS_DOWNLOADED = "_is_downloaded";
    public static final String CURRENT_CHAPTER = "_current_chapter";
    public static final String CURRENT_PAGE = "_current_page";
    public static final String TIME_STAMP = "_time_stamp";
    public static final String FONT_SIZE = "_font_size";

    // integer values used in content sURI
    static final int uriContent = 1;
    static final int uriMetadata = 2;
    static final int uriCategories = 3;
    static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // projection map for a query
    private static HashMap<String, String> values;

    // maps content URI "patterns" to the integer values that were set above
    static {
        uriMatcher.addURI(PROVIDER_NAME, "content", uriContent);
        uriMatcher.addURI(PROVIDER_NAME, "metadata", uriMetadata);
        uriMatcher.addURI(PROVIDER_NAME, "categories", uriCategories);
    }

    // database declarations
    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "PratilipiDb";
    public static final String TABLE_CONTENT = "contentTable";
    public static final String TABLE_METADATA = "metadaTable";
    public static final String TABLE_CATEGORIES = "categoriesTable";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_CONTENT_TABLE =
            " CREATE TABLE IF NOT EXISTS " + TABLE_CONTENT +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " _pid TEXT NOT NULL, " +
                    " _content TEXT , " +
                    " _img BLOB , " +
                    " _ch_no TEXT );";

    static final String CREATE_METADATA_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_METADATA
            +" (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "_pid TEXT , "
            + " _title TEXT , "
            + " _content_type TEXT , "
            + " _author_id TEXT , "
            + " _author_name TEXT , "
            + " _ch_count INTEGER , "
            + " _index TEXT , "
            + " _img_url TEXT , "
            + " _list_type TEXT , "
            + " _rating_count INTEGER , "
            + " _star_count INTEGER , "
            + " _summary TEXT , "
            + " _is_downloaded TEXT , "
            + " _current_chapter INTEGER , "
            + " _current_page INTEGER , "
            + " _time_stamp INTEGER, "
            + " _font_size INTEGER, "
            + " _pg_url TEXT );";

    static final String CREATE_CATEGORIES_TABLE =
            " CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " _pid TEXT NOT NULL, " +
                    " _title TEXT );";

    // class that creates and manages the provider's database
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_CONTENT_TABLE);
            db.execSQL(CREATE_METADATA_TABLE);
            db.execSQL(CREATE_CATEGORIES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_METADATA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            onCreate(db);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case uriContent:
                count = db.delete(TABLE_CONTENT, selection, selectionArgs);
                break;
            case uriMetadata:
                count = db.delete(TABLE_METADATA, selection, selectionArgs);
                break;
            case uriCategories:
                count = db.delete(TABLE_CATEGORIES, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID;
        switch (uriMatcher.match(uri)) {
            case uriContent:
                 rowID = db.insert(TABLE_CONTENT, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case uriMetadata:
                rowID = db.insert(TABLE_METADATA, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(METADATA_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case uriCategories:
                rowID = db.insert(TABLE_CATEGORIES, "", values);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(CATEGORIES_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
           case uriContent:
                qb.setTables(TABLE_CONTENT);
                qb.setProjectionMap(values);
                break;
           case uriMetadata:
                qb.setTables(TABLE_METADATA);
                qb.setProjectionMap(values);
                break;
            case uriCategories:
                qb.setTables(TABLE_CATEGORIES);
                qb.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder.equals("")) sortOrder = PID;
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case uriContent:
                count = db.update(TABLE_CONTENT, values, selection, selectionArgs);
                break;
            case uriMetadata:
                count = db.update(TABLE_METADATA, values, selection, selectionArgs);
                break;
            case uriCategories:
                count = db.update(TABLE_CATEGORIES, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
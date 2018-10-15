package com.example.a11059.mlearning.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.example.a11059.mlearning.application.MyApplication;

/**
 * Created by 11059 on 2018/7/31.
 */

public class UtilFile {
    private static Context mContext = MyApplication.getContext();

    public static String getRealFilePath(Uri uri ) {
        String path = "";
        if(mContext == null || uri == null){
            return path;
        }
        if(DocumentsContract.isDocumentUri(mContext, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if(isExternalStorageDocument(uri)) {
                String [] split = docId.split(":");
                if(split.length >= 2) {
                    path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    String type = split[0];
                    if("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
            else if(isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getDataColumn(mContext, contentUri, null, null);
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        else {
            path = getDataColumn(mContext, uri, null, null);
        }
        return path;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return "";
    }

    public static String getFileName(String fullPath){
        if(fullPath.length() > 0){
            String[] split = fullPath.split("/");
            if(split.length > 0){
                return split[split.length - 1];
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getFileFormat(String fileName){
        if(fileName.length() > 0){
            String[] split = fileName.split("\\.");
            if(split.length > 0){
                return split[split.length - 1];
            } else {
                return "no format";
            }
        } else {
            return "no format";
        }
    }
}

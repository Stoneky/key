package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String privatePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri uri = appLinkIntent.getData();
        String dataString = appLinkIntent.getDataString();
        findViewById(R.id.test).setOnClickListener(this);
        File[] externalFilesDirs = getExternalFilesDirs(null);
        privatePath = getFilesDir().getAbsolutePath();
        assetFile2Str(this,"patch.patch",new File(privatePath,"patch.patch"));
        assetFile2Str(this,"app-release.apk",new File(privatePath,"app-release.apk"));
    }

    public  void  assetFile2Str(Context c, String assetName,File targetFile){
        InputStream in = null;
        FileOutputStream fileOutputStream = null ;
        byte[] buf = new byte[2048];
        try{
            in = c.getAssets().open(assetName);
            fileOutputStream = new FileOutputStream(targetFile, false /* append */);
            while (true){
                int r = in.read(buf);
                if (r == -1) {
                    break;
                }
                fileOutputStream.write(buf, 0, r);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (getPermission(MainActivity.this,100, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    BsPatchUtil.patch(privatePath+File.separator+"app-release.apk",privatePath+File.separator+"patch.patch",privatePath+File.separator+"new.apk");
                    copyFile(privatePath+File.separator+"new.apk",new File(getFilesDir()+File.separator+"icity","new.apk"));
                    install(getFilesDir()+File.separator+"icity"+File.separator+"new.apk");

                }
            }.start();
        }

    }
    public  void  copyFile(String filePath,File targetFile){
        InputStream in = null;
        FileOutputStream fileOutputStream = null ;
        byte[] buf = new byte[2048];
        try{
            in = new FileInputStream(filePath);
            File file = new File(getFilesDir(),"icity");
            if (!file.exists()){
                file.mkdir();
            }
            fileOutputStream = new FileOutputStream(targetFile, false /* append */);
            while (true){
                int r = in.read(buf);
                if (r == -1) {
                    break;
                }
                fileOutputStream.write(buf, 0, r);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public  void install(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        //安装
        Intent install = new Intent(Intent.ACTION_VIEW);
        //判断是否是android 7.0及以上
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N) {
            //7.0获取存储文件的uri
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //赋予临时权限
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //设置dataAndType
            install.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
         startActivity(install);
    }

    public  boolean getPermission(Activity context, int requestCode, String... permissions) {
        ArrayList<String> neededPermissions = new ArrayList<>();
        for (String permission : permissions) {
            int hasPermission = ContextCompat.checkSelfPermission(context, permission);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }

        if (neededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(context, neededPermissions.toArray(new String[neededPermissions.size()]), requestCode);
            return false;
        }
        return true;
    }
}

package com.example.myapplication;

public class BsPatchUtil {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * @param oldApkPath 旧apk文件路径
     * @param newApkPath 新apk文件路径
     * @param patchPath  生成的差分包的存储路径
     */
    public static  native void patch(String oldApkPath, String patchPath, String newApkPath);
}

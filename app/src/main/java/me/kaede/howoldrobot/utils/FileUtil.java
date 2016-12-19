/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.io.FileUtils.deleteQuietly;

public class FileUtil {

    private static final String TAG = "plugin.files";

    public static void closeQuietly(Closeable closeable) {
        IOUtils.closeQuietly(closeable);
    }

    public static boolean delete(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return delete(new File(path));
    }

    public static boolean delete(File file) {
        return deleteQuietly(file);
    }

    public static boolean exist(String path) {
        return !TextUtils.isEmpty(path) && (new File(path).exists());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkCreateFile(File file) throws IOException {
        if (file == null) {
            throw new IOException("File is null.");
        }
        if (file.exists()) {
            delete(file);
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.createNewFile()) {
            throw new IOException("Create file fail, file already exists.");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkCreateDir(File file) throws IOException {
        if (file == null) {
            throw new IOException("Dir is null.");
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                return;
            }
            if (!delete(file)) {
                throw new IOException("Fail to delete existing file, file = "
                        + file.getAbsolutePath());
            }
            file.mkdir();
        } else {
            file.mkdirs();
        }
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException("Fail to create dir, dir = " + file.getAbsolutePath());
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile == null) {
            throw new IOException("Source file is null.");
        }
        if (destFile == null) {
            throw new IOException("Dest file is null.");
        }
        if (!sourceFile.exists()) {
            throw new IOException("Source file not found.");
        }

        checkCreateFile(destFile);
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(destFile);
            FileDescriptor fd = ((FileOutputStream) out).getFD();
            out = new BufferedOutputStream(out);
            IOUtils.copy(in, out);
            out.flush();
            fd.sync();
        } catch (IOException e) {
            Log.w(TAG, e);
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }

    public static void copyFileFromAsset(Context context, String pathAssets, File destFile)
            throws IOException {
        if (TextUtils.isEmpty(pathAssets)) {
            throw new IOException("Asset path is empty.");
        }

        checkCreateFile(destFile);
        InputStream in = null;
        OutputStream out = null;

        try {
            in = context.getAssets().open(pathAssets);
            out = new FileOutputStream(destFile);
            FileDescriptor fd = ((FileOutputStream) out).getFD();
            out = new BufferedOutputStream(out);
            IOUtils.copy(in, out);
            out.flush();
            fd.sync();
        } catch (IOException e) {
            Log.w(TAG, e);
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }
}

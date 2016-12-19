/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.kaede.howoldrobot.utils.FileUtil;

/**
 * @author Kaede
 * @version 16/12/19
 */
@RunWith(AndroidJUnit4.class)
public class AnalyseApiTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void shutDown() {
    }

    @Test
    public void testHttpUrlConnection() throws IOException {
        String tmpDirPath = System.getProperty("java.io.tmpdir", ".");
        File tmpDir = new File(tmpDirPath);
        Log.d("age.test", "Get system temp dir, path = " + tmpDirPath);

        File testImage = File.createTempFile("age_test_", ".jpg", tmpDir);
        FileUtil.copyFileFromAsset(mContext, "test.jpg", testImage);
        Assert.assertTrue(testImage.exists());

        String requestUrl = "http://cn.how-old.net/Home/Analyze?isTest=False&source=&version=cn.how-old.net";
        Map<String, String> data = new HashMap<>();
        data.put("file", testImage.getAbsolutePath());
        String post = PostTask.post(requestUrl, data);
        Assert.assertNotNull(post);
    }

    @Test
    public void testHttpClient() throws IOException, InterruptedException {
        String tmpDirPath = System.getProperty("java.io.tmpdir", ".");
        File tmpDir = new File(tmpDirPath);
        Log.d("age.test", "Get system temp dir, path = " + tmpDirPath);

        File testImage = File.createTempFile("age_test_", ".jpg", tmpDir);
        FileUtil.copyFileFromAsset(mContext, "test.jpg", testImage);
        Assert.assertTrue(testImage.exists());

        uploadImage(testImage);
    }

    private void uploadImage(File image) {
        String uri = "http://cn.how-old.net/Home/Analyze?isTest=False&source=&version=cn.how-old.net";
        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(uri);
            post.setHeader("Content-Type", "application/octet-stream");
            post.setEntity(new FileEntity(image, "/"));
            response = client.execute(post);
        } catch (IOException e) {
            Logger.w(e);
        }

        Assert.assertNotNull(response);
        HttpEntity responseEntity = response.getEntity();
        Assert.assertNotNull(responseEntity);
    }
}

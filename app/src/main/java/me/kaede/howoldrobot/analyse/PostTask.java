/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import me.kaede.howoldrobot.utils.FileUtil;

/**
 * Copycat form "http://msdev.sakura.ne.jp/know-how/?p=32".
 */
public class PostTask {

    private final static String TWO_HYPHEN = "--";
    private final static String EOL = "\r\n";
    private final static String BOURDARY = String.format("%x", new Random().hashCode());
    private final static String CHARSET = "UTF-8";

    public static String post(String requestUrl, Map<String, String> postData) {
        return new PostTask().postRequest(requestUrl, postData);
    }

    @SuppressLint("DefaultLocale")
    public String postRequest(String requestUrl, Map<String, String> postData) {
        String result = "";

        // 送信するコンテンツを成形する
        StringBuilder contentsBuilder = new StringBuilder();
        String closingContents;
        int contentsLength = 0;
        String fileTag = "";
        String filePath = "";
        File file = null;

        for (Map.Entry<String, String> data : postData.entrySet()) {
            String key = data.getKey();
            String value = data.getValue();

            // ファイル以外
            if (!new File(value).isFile()) {
                contentsBuilder.append(String.format("%s%s%s", TWO_HYPHEN, BOURDARY, EOL));
                contentsBuilder.append(String.format("Content-Disposition: form-data; name=\"%s\"%s", key, EOL));
                contentsBuilder.append(EOL);
                contentsBuilder.append(value);
                contentsBuilder.append(EOL);
            }
            // ファイル
            else {
                // ファイル情報を保持しておく
                fileTag = key;
                filePath = value;
                file = new File(filePath);
            }
        }

        // ファイル情報のセット
        contentsBuilder.append(String.format("%s%s%s", TWO_HYPHEN, BOURDARY, EOL));
        contentsBuilder.append(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"%s", fileTag, filePath, EOL));

        // ファイルがあるとき
        if (file != null) {
            // ファイルサイズの取得
            contentsLength += file.length();

            // MIME取得
            int extPos = filePath.lastIndexOf(".");
            String ext = (extPos > 0) ? filePath.substring(extPos + 1) : "";
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
            contentsBuilder.append(String.format("Content-Type: %s%s", mime, EOL));
        }
        // ファイルがないとき
        else {
            contentsBuilder.append(String.format("Content-Type: application/octet-stream%s", EOL));
        }

        contentsBuilder.append(EOL);
        closingContents = String.format("%s%s%s%s%s", EOL, TWO_HYPHEN, BOURDARY, TWO_HYPHEN, EOL);

        // コンテンツの長さを取得
        try {
            // StringBuilderを文字列に変化してからバイト長を取得しないと
            // 実際送ったサイズと異なる場合があり、コンテンツを正しく送信できなくなる
            contentsLength += contentsBuilder.toString().getBytes(CHARSET).length;
            contentsLength += closingContents.getBytes(CHARSET).length;
        } catch (UnsupportedEncodingException e) {
            Logger.w(e);
        }

        // サーバへ接続する
        HttpURLConnection connection = null;
        DataOutputStream os = null;
        BufferedReader br = null;
        FileInputStream fis = null;

        try {
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // キャッシュを使用しない
            connection.setUseCaches(false);

            // HTTPストリーミングを有効にする
            connection.setChunkedStreamingMode(0);

            // リクエストヘッダを設定する
            // リクエストメソッドの設定
            connection.setRequestMethod("POST");

            // 持続接続を設定
            connection.setRequestProperty("Connection", "Keep-Alive");

            // ユーザエージェントの設定（必須ではない）
            connection.setRequestProperty("User-Agent", String.format("Mozilla/5.0 (Linux; U; Android %s;)", Build.VERSION.RELEASE));

            // POSTデータの形式を設定
            connection.setRequestProperty("Content-Type", String.format("multipart/form-data; boundary=%s", BOURDARY));
            // POSTデータの長さを設定
            connection.setRequestProperty("Content-Length", String.valueOf(contentsLength));

            // データを送信する
            os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(contentsBuilder.toString());

            // ファイルの送信
            if (file != null) {
                byte buffer[] = new byte[1024 * 4];
                fis = new FileInputStream(file);
                while (fis.read(buffer, 0, buffer.length) > -1) {
                    os.write(buffer, 0, buffer.length);
                }
            }

            os.writeBytes(closingContents);

            // レスポンスを受信する
            int code = connection.getResponseCode();

            // 接続が確立したとき
            if (code == HttpURLConnection.HTTP_OK) {
                StringBuilder resultBuilder = new StringBuilder();
                String line;
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // レスポンスの読み込み
                while ((line = br.readLine()) != null) {
                    resultBuilder.append(String.format("%s%s", line, EOL));
                }

                result = resultBuilder.toString();
            }
            // 接続が確立できなかったとき
            else {
                result = String.valueOf(code);
            }
            return result;

        } catch (IOException e) {
            Logger.w(e);
            return null;

        } finally {
            // 開いたら閉じる
            FileUtil.closeQuietly(br);
            FileUtil.closeQuietly(os);
            FileUtil.closeQuietly(fis);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
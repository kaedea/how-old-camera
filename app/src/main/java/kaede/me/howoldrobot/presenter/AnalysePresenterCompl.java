package kaede.me.howoldrobot.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import kaede.me.howoldrobot.Model.Face;
import kaede.me.howoldrobot.R;
import kaede.me.howoldrobot.util.AppUtil;
import kaede.me.howoldrobot.util.BitmapUtil;
import kaede.me.howoldrobot.view.IPhotoView;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kaede on 2015/5/23.
 */
public class AnalysePresenterCompl implements IAnalysePresenter {
    private static final String TAG = "AnalysePresenterCompl";
    public static final int TYPE_PICK_CAMERA = 0;
    public static final int TYPE_PICK_GALLERY = 1;
    IPhotoView iPhotoView;
    File outputImage;
    private Uri imageUri;

    public AnalysePresenterCompl(IPhotoView iPhotoView) {
        this.iPhotoView = iPhotoView;
    }

    @Override
    public void doAnalyse(String imgPath) {
        File file = new File(imgPath);
        if (!file.exists()) {
            Log.w(TAG, "Image Not Exists!");
            return;
        }

        postRequest(imgPath);

    }

    @Override
    public void pickPhoto(Activity activity, int type) {
        switch (type) {
            case TYPE_PICK_CAMERA:
                Intent takePicture = new Intent("android.media.action.IMAGE_CAPTURE");
                outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                outputImage.delete();
                imageUri = Uri.fromFile(outputImage);
                //takePicture.putExtra("return-data", false);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(takePicture, 0);
                break;
            case TYPE_PICK_GALLERY:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.putExtra("crop", "true");//允许裁剪
                outputImage = new File(Environment.getExternalStorageDirectory(),
                        "output_image.jpg");

                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imageUri = Uri.fromFile(outputImage);
                //takePicture.putExtra("return-data", false);
                pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                activity.startActivityForResult(pickPhoto, 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void getImage(Context context, Intent intent) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));
            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            int widthBitmap = bitmap.getWidth();
            int heightBitmap = bitmap.getHeight();
            int widthMax = AppUtil.getScreenWitdh(context) - (context.getResources().getDimensionPixelSize(R.dimen.margin_main_left) + context.getResources().getDimensionPixelSize(R.dimen.border_main_photo)) * 2 - context.getResources().getDimensionPixelSize(R.dimen.offset_main_photo);
            int heightMax = AppUtil.getScreenHeight(context) - (context.getResources().getDimensionPixelSize(R.dimen.margin_main_top) + context.getResources().getDimensionPixelSize(R.dimen.border_main_photo)) * 2 - context.getResources().getDimensionPixelSize(R.dimen.offset_main_photo);
            if (widthBitmap > widthMax && heightBitmap > heightMax) {
                float rateWidth = (float)widthBitmap/(float)widthMax;
	            float rateHeight = (float)heightBitmap/(float)heightMax;
	            if (rateWidth>=rateHeight)
		            bitmap = BitmapUtil.zoomBitmapToWidth(bitmap, widthMax);
	            else
		            bitmap = BitmapUtil.zoomBitmapToHeight(bitmap, heightMax);
            }else if (widthBitmap > widthMax){
	            bitmap = BitmapUtil.zoomBitmapToWidth(bitmap, widthMax);
            }else if(heightBitmap>heightMax){
	            bitmap = BitmapUtil.zoomBitmapToHeight(bitmap, heightMax);
            }
            saveImage(bitmap);
            iPhotoView.onGetImage(bitmap, Environment.getExternalStorageDirectory() + File.separator + "output_image_small.jpg");
            //analysePresenter.doAnalyse(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "output_image_small.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postRequest(String imagePath) {
        new PostHandler().execute(imagePath);
    }

    private void parserJson(String string) {
        List<Face> faceList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(string);
            if (jsonArray.length() <= 0) {
                Log.w(TAG, "No Face");
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Face face = new Face();
                face.faceId = item.optInt("faceId", 0);
                JSONObject faceRectangle = item.optJSONObject("faceRectangle");
                face.faceRectangle.left = faceRectangle.optInt("left", 0);
                face.faceRectangle.top = faceRectangle.optInt("top", 0);
                face.faceRectangle.width = faceRectangle.optInt("width", 65);
                face.faceRectangle.height = faceRectangle.optInt("height", 65);
                JSONObject attributes = item.optJSONObject("attributes");
                face.attributes.gender = attributes.optString("gender", "Female");
                face.attributes.age = attributes.optInt("age", 17);
                faceList.add(face);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Iterator<Face> iterator = faceList.iterator();
        while (iterator.hasNext()) {
            Face item = iterator.next();
            Log.d(TAG, "Face : " + item.toString());
        }

        iPhotoView.onGetFaces(faceList);
    }


    public void saveImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "output_image_small.jpg");
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class PostHandler extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://how-old.net/Home/Analyze?isTest=False");
                // 一个本地的文件
                FileBody file = new FileBody(new File(params[0]));
                // 一个字符串
                StringBody comment = new StringBody("False");
                // 多部分的实体
                MultipartEntity reqEntity = new MultipartEntity();
                // 增加
                reqEntity.addPart("data", file);
                reqEntity.addPart("isTest", comment);
                // 设置
                httppost.setEntity(reqEntity);

                Log.d(TAG, "执行: " + httppost.getRequestLine());

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                Header[] headers = response.getAllHeaders();
                for (Header header : headers) {
                    System.out.println(header.getName() + " " + header.getValue());
                }
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    Log.d(TAG, "返回长度: " + resEntity.getContentLength());
                    String result = EntityUtils.toString(resEntity, "utf-8");
                    String str1 = result.replaceAll("\\\\", "");
                    String str2 = str1.replaceAll("rn", "");
                    String json = str2.substring(str2.indexOf("\"Faces\":[") + 8, str2.lastIndexOf("]") + 1);
                    Log.d(TAG, json);
                    return json;
                }
                if (resEntity != null) {
                    resEntity.consumeContent();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                //Fail
            } else {
                parserJson(s);
            }
        }
    }
}

package kaede.me.howoldrobot.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import kaede.me.howoldrobot.activity.MainActivity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaede.me.howoldrobot.R;
import kaede.me.howoldrobot.model.Face;
import kaede.me.howoldrobot.util.AppUtil;
import kaede.me.howoldrobot.util.BitmapUtil;
import kaede.me.howoldrobot.util.FileUtil;
import kaede.me.howoldrobot.view.IPhotoView;

/**
 * Created by kaede on 2015/5/23.
 */
public class AnalysePresenterCompl implements IAnalysePresenter {
    private static final String TAG               = "AnalysePresenterCompl";
    public static final  int    TYPE_PICK_CAMERA  = 0;
    public static final  int    TYPE_PICK_GALLERY = 1;
    private static final String OUTPUT_IMAGE_JPG  = "output_image.jpg";
    private static final String OUTPUT_IMAGE_SMALL_JPG = "output_image_small.jpg";
    IPhotoView iPhotoView;
    File       appBaseDir;
    private Uri imageUri;

    public AnalysePresenterCompl(IPhotoView iPhotoView) {
        this.iPhotoView = iPhotoView;
        File dir = new File(FileUtil.getSdpath() + File.separator + "Moe Studio");
        dir.mkdir();
        appBaseDir = new File(dir.getAbsolutePath() + File.separator + "How Old Robot");
        appBaseDir.mkdir();
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
        File  outputImage;
        switch (type) {
            case TYPE_PICK_CAMERA:
                Intent takePicture = new Intent("android.media.action.IMAGE_CAPTURE");
                outputImage = new File(appBaseDir.getAbsolutePath() + File.separator + OUTPUT_IMAGE_JPG);
                if (!outputImage.exists()) try {
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                //takePicture.putExtra("return-data", false);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                try {
                    activity.startActivityForResult(takePicture, MainActivity.ACTIVITY_REQUEST_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                    iPhotoView.toast(activity.getResources().getString(R.string.main_pick_camera_fail));
                }
                break;
            case TYPE_PICK_GALLERY:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.putExtra("crop", true);//允许裁剪
                outputImage = new File(appBaseDir.getAbsolutePath() + File.separator + OUTPUT_IMAGE_JPG);
                if (!outputImage.exists()) try {
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                //takePicture.putExtra("return-data", false);
                pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                try {
                    activity.startActivityForResult(pickPhoto, MainActivity.ACTIVITY_REQUEST_GALLERY);
                } catch (Exception e) {
                    e.printStackTrace();
                    pickPhoto.putExtra("crop", false);//不允许裁剪
                    try {
                        activity.startActivityForResult(pickPhoto, MainActivity.ACTIVITY_REQUEST_GALLERY);
                    } catch (Throwable t) {
                    	t.printStackTrace();
                        iPhotoView.toast(activity.getResources().getString(R.string.main_pick_gallery_fail));
                    }
                }
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
            int widthMax = AppUtil.getScreenWitdh(context) - (context.getResources().getDimensionPixelSize(R.dimen.margin_main_left) + context.getResources().getDimensionPixelSize(R.dimen.border_main_photo) + context.getResources().getDimensionPixelSize(R.dimen.offset_main_photo)) * 2;
            int heightMax = iPhotoView.getPhotoContainer().getHeight() - (context.getResources().getDimensionPixelSize(R.dimen.border_main_photo) + context.getResources().getDimensionPixelSize(R.dimen.offset_main_photo)) * 2;
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
            String imgPath = appBaseDir.getAbsolutePath() + File.separator + OUTPUT_IMAGE_SMALL_JPG;
	        if (BitmapUtil.saveBitmapToSd(bitmap,80,imgPath))
		        iPhotoView.onGetImage(bitmap, imgPath);
        } catch (Exception e) {
            iPhotoView.toast(context.getResources().getString(R.string.main_get_img_fail));
            e.printStackTrace();
        }
    }

    private void postRequest(String imagePath) {
        new PostHandler().execute(imagePath);
    }

    private void parserJson(String string) {
        if (string == null) {
            iPhotoView.onGetFaces(null);
            return;
        }
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

        /*Iterator<Face> iterator = faceList.iterator();
        while (iterator.hasNext()) {
            Face item = iterator.next();
            Log.d(TAG, "Face : " + item.toString());
        }*/
        iPhotoView.onGetFaces(faceList);
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
                    resEntity.consumeContent();
                    Log.d(TAG, json);
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
	        iPhotoView.showProgressDialog(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parserJson(s);
        }
    }
}

/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.kaede.howoldrobot.Dispatcher;
import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.model.Face;
import me.kaede.howoldrobot.analyse.presenter.IAnalyse;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.utils.BitmapUtil;
import me.kaede.howoldrobot.utils.DeviceUtil;
import me.kaede.howoldrobot.utils.FileUtil;

class AnalyseImpl implements IAnalyse {

    static final int TYPE_PICK_CAMERA = 0;
    static final int TYPE_PICK_GALLERY = 1;
    private static final String OUTPUT_IMAGE_JPG = "output_image.jpg";
    private static final String OUTPUT_IMAGE_SMALL = "output_image_small.jpg";

    private Uri imageUri;
    private File mCacheDir;
    private IPhotoView mPhotoView;
    private final Handler mHandler;

    public AnalyseImpl(IPhotoView photoView) {
        mPhotoView = photoView;
        File cacheDir = mPhotoView.getContext().getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = mPhotoView.getContext().getCacheDir();
        }
        mCacheDir = cacheDir;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void pickPhoto(Activity activity, int type) {
        File outputImage;
        switch (type) {
            case TYPE_PICK_CAMERA:
                try {
                    Intent takePicture = new Intent("android.media.action.IMAGE_CAPTURE");
                    outputImage = new File(mCacheDir, OUTPUT_IMAGE_JPG);
                    FileUtil.checkCreateFile(outputImage);
                    imageUri = Uri.fromFile(outputImage);
                    //takePicture.putExtra("return-data", false);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    activity.startActivityForResult(takePicture, MainActivity.ACTIVITY_REQUEST_CAMERA);

                } catch (Exception e) {
                    Logger.w(e);
                    mPhotoView.toast(activity.getResources().getString(R.string.main_pick_camera_fail));
                }
                break;

            case TYPE_PICK_GALLERY:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                try {
                    pickPhoto.putExtra("crop", "true");
                    outputImage = new File(mCacheDir, OUTPUT_IMAGE_JPG);
                    FileUtil.checkCreateFile(outputImage);
                    imageUri = Uri.fromFile(outputImage);
                    //takePicture.putExtra("return-data", false);
                    pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    activity.startActivityForResult(pickPhoto, MainActivity.ACTIVITY_REQUEST_GALLERY);

                } catch (Exception e) {
                    Logger.w(e);
                    pickPhoto.putExtra("crop", false);
                    try {
                        activity.startActivityForResult(pickPhoto, MainActivity.ACTIVITY_REQUEST_GALLERY);
                    } catch (Throwable t) {
                        Logger.w(t);
                        mPhotoView.toast(activity.getResources().getString(R.string.main_pick_gallery_fail));
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void getImage(final Context context, Intent intent) {
        Dispatcher.instance().post(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                            .openInputStream(imageUri));

                    int widthBitmap = bitmap.getWidth();
                    int heightBitmap = bitmap.getHeight();

                    int widthMax = DeviceUtil.width(context)
                            - (context.getResources().getDimensionPixelSize(R.dimen.margin_main_left)
                            + context.getResources().getDimensionPixelSize(R.dimen.border_main_photo)
                            + context.getResources().getDimensionPixelSize(R.dimen.offset_main_photo)) * 2;
                    int heightMax = mPhotoView.getContainer().getHeight()
                            - (context.getResources().getDimensionPixelSize(R.dimen.border_main_photo)
                            + context.getResources().getDimensionPixelSize(R.dimen.offset_main_photo)) * 2;

                    if (widthBitmap > widthMax && heightBitmap > heightMax) {
                        float rateWidth = (float) widthBitmap / (float) widthMax;
                        float rateHeight = (float) heightBitmap / (float) heightMax;
                        if (rateWidth >= rateHeight) {
                            bitmap = BitmapUtil.zoomBitmapToWidth(bitmap, widthMax);
                        } else {
                            bitmap = BitmapUtil.zoomBitmapToHeight(bitmap, heightMax);
                        }
                    } else if (widthBitmap > widthMax) {
                        bitmap = BitmapUtil.zoomBitmapToWidth(bitmap, widthMax);
                    } else if (heightBitmap > heightMax) {
                        bitmap = BitmapUtil.zoomBitmapToHeight(bitmap, heightMax);
                    }

                    File smallImage = new File(mCacheDir, OUTPUT_IMAGE_SMALL);
                    final String imgPath = smallImage.getAbsolutePath();
                    final Bitmap finalBitmap = bitmap;

                    if (BitmapUtil.saveBitmap(finalBitmap, 80, imgPath)) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mPhotoView.onGetImage(finalBitmap, imgPath);
                            }
                        });
                    }

                } catch (Exception e) {
                    Logger.w(e);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoView.toast(context.getResources().getString(R.string.main_get_img_fail));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void doAnalyse(String imgPath) {
        File image = new File(imgPath);
        if (!image.exists()) {
            Logger.w("Image Not Exists!");
            mPhotoView.onGetFaces(null);
            return;
        }
        requestAges(image);
    }

    void requestAges(final File image) {
        Dispatcher.instance().post(new Runnable() {
            @Override
            public void run() {
                try {
                    String uri = "http://cn.how-old.net/Home/Analyze?isTest=False";
                    try {
                        HttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost(uri);
                        post.setHeader("Content-Type", "application/octet-stream");
                        post.setEntity(new FileEntity(image, "/"));
                        HttpResponse response = client.execute(post);

                        if (response != null) {
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode == 200) {
                                HttpEntity entity = response.getEntity();
                                String string = EntityUtils.toString(entity, "UTF-8");
                                String str1 = string.replaceAll("\\\\", "");
                                String str2 = str1.replaceAll("rn", "");
                                final String json = str2.substring(str2.indexOf("\"Faces\":[") + 8, str2.lastIndexOf("]") + 1);
                                Logger.d("[onSuccess] json = " + json);
                                // Success.
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        parserJson(json);
                                    }
                                });
                                return;
                            }

                            Logger.w("Fail, status code = " + statusCode);
                        } else {
                            Logger.w("Response is null.");
                        }
                    } catch (IOException e) {
                        Logger.w(e);
                    }
                } catch (Exception e) {
                    Logger.w(e);
                }
                // Fail.
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        parserJson(null);
                    }
                });
            }
        });
    }

    private void parserJson(String string) {
        if (string == null) {
            mPhotoView.onGetFaces(null);
            return;
        }

        List<Face> faceList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(string);
            if (jsonArray.length() <= 0) {
                Logger.w("No Face");
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

        mPhotoView.onGetFaces(faceList);
    }
}

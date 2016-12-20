/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.kaede.howoldrobot.Dispatcher;
import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.model.Face;
import me.kaede.howoldrobot.analyse.presenter.IAnalyse;
import me.kaede.howoldrobot.analyse.presenter.IAnimation;
import me.kaede.howoldrobot.analyse.presenter.IDraw;
import me.kaede.howoldrobot.analyse.presenter.IOptions;
import me.kaede.howoldrobot.analyse.presenter.IShare;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.utils.BitmapUtil;
import me.kaede.howoldrobot.utils.FileUtil;
import me.kaede.howoldrobot.widget.AgeIndicatorLayout;
import me.kaede.howoldrobot.widget.FaceImageView;

import static me.kaede.howoldrobot.analyse.AnalyseImpl.TYPE_PICK_CAMERA;
import static me.kaede.howoldrobot.analyse.AnalyseImpl.TYPE_PICK_GALLERY;

public class MainActivity extends AppCompatActivity implements IPhotoView, View.OnClickListener {

    static final int ACTIVITY_REQUEST_CAMERA = 0;
    static final int ACTIVITY_REQUEST_GALLERY = 1;
    private static final int REQ_PERMISSION_WRITE = 233;

    private Toolbar mToolbar;
    private View mContainer;
    private FaceImageView mFaceView;
    private ProgressDialog mProgress;
    private AgeIndicatorLayout mAgeLayout;
    private View mIntroduceView;

    private Context mContext;
    private Handler mHandler;
    private IDraw mDraw;
    private IAnalyse mAnalyse;
    private IAnimation mAnimation;
    private String mShareImgPath;
    private IShare mShare;
    private File mShareDir;
    private View mCameraBtn;
    private View mGalleryBtn;
    private View mShareBtn;
    private View mBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        init();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFaceView = (FaceImageView) findViewById(R.id.iv_main_face);
        mAgeLayout = (AgeIndicatorLayout) findViewById(R.id.layout_main_age);
        mContainer = findViewById(R.id.layout_main_photo);
        mIntroduceView = findViewById(R.id.layout_introduce);
        mCameraBtn = findViewById(R.id.btn_main_camera);
        mGalleryBtn = findViewById(R.id.btn_main_gallery);
        mShareBtn = findViewById(R.id.btn_main_share);
        mBorder = findViewById(R.id.layout_main_border);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
            mCameraBtn.setElevation(getResources().getDimension(R.dimen.btn_elevation));
            mGalleryBtn.setElevation(getResources().getDimension(R.dimen.btn_elevation));
            mShareBtn.setElevation(getResources().getDimension(R.dimen.btn_elevation));
        }
    }

    private void setListener() {
        mCameraBtn.setOnClickListener(this);
        mGalleryBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    private void init() {
        mContext = getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
        File fileDir = mContext.getExternalFilesDir("");
        if (fileDir == null) {
            fileDir = mContext.getFilesDir();
        }
        mShareDir = new File(fileDir, "share");
        try {
            FileUtil.checkCreateDir(mShareDir);
        } catch (IOException e) {
            Logger.w(e);
        }

        mAnalyse = new AnalyseImpl(this);
        mDraw = new DrawImpl(this);
        mAnimation = new AnimationImpl();
        mShare = new ShareImpl(this);
        mAnimation.doLogoAnimation(findViewById(R.id.iv_logo));
    }

    private void share() {
        if (!TextUtils.isEmpty(mShareImgPath)) {
            mShare.doShare(mShareImgPath);
        } else {
            showProgressDialog(true, "Compress image ...");
            Dispatcher.instance().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = BitmapUtil.getBitmapFromView(mContainer);
                        if (bitmap != null) {
                            File shareImage = File.createTempFile("age_", "_share.jpg", mShareDir);
                            String shareImgPath = shareImage.getAbsolutePath();
                            BitmapUtil.saveBitmap(bitmap, 80, shareImgPath);
                            mShareImgPath = shareImgPath;
                            mShare.doShare(mShareImgPath);
                        } else {
                            Logger.w("Can not get share bitmap.");
                        }
                    } catch (Exception e) {
                        Logger.w(e);
                    } finally {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showProgressDialog(false, null);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onGetImage(Bitmap bitmap, String imgPath) {
        mIntroduceView.setVisibility(View.GONE);
        mFaceView.clearFaces();
        mAgeLayout.clearAges();
        mFaceView.setImageBitmap(bitmap);
        mBorder.setBackgroundResource(R.drawable.shape_bg_photo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBorder.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
        }

        showProgressDialog(true, getResources().getString(R.string.main_loading));
        mAnalyse.doAnalyse(imgPath);
    }

    @Override
    public void onGetFaces(List<Face> faces) {
        if (faces == null) {
            toast(getResources().getString(R.string.main_analyze_fail));
            showProgressDialog(false, null);
        } else if (faces.size() <= 0) {
            toast(getResources().getString(R.string.main_analyze_no_face));
            showProgressDialog(false, null);

        } else {
            mDraw.drawFaces(mAgeLayout, mFaceView, faces);
            Dispatcher.instance().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = BitmapUtil.getBitmapFromView(mContainer);
                        if (bitmap != null) {
                            File shareImage = File.createTempFile("age_", "_share.jpg", mShareDir);
                            String shareImgPath = shareImage.getAbsolutePath();
                            BitmapUtil.saveBitmap(bitmap, 80, shareImgPath);
                            mShareImgPath = shareImgPath;
                        } else {
                            Logger.w("Can not get share bitmap.");
                        }
                    } catch (Exception e) {
                        Logger.w(e);
                    } finally {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showProgressDialog(false, null);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void showProgressDialog(Boolean isShow, String msg) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(false);
            mProgress.setCanceledOnTouchOutside(false);
            if (TextUtils.isEmpty(msg)) {
                mProgress.setMessage(msg);
            }
        }

        if (isShow) {
            if (!mProgress.isShowing())
                mProgress.show();
        } else {
            if (mProgress.isShowing())
                mProgress.dismiss();
        }
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public View getContainer() {
        return mContainer;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        IOptions optionsPresenter = new OptionsImpl();
        optionsPresenter.onOptionsItemClick(this, item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_camera:
                mAnalyse.pickPhoto(this, TYPE_PICK_CAMERA);
                break;

            case R.id.btn_main_gallery:
                mAnalyse.pickPhoto(this, TYPE_PICK_GALLERY);
                break;

            case R.id.btn_main_share:
                if (mIntroduceView.getVisibility() == View.VISIBLE) {
                    toast("Pick a photo to share :D");
                    break;
                }

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    share();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSION_WRITE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REQUEST_CAMERA:
            case ACTIVITY_REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    mAnalyse.getImage(this, data);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION_WRITE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                share();
            } else {
                toast("Can not share without permission");
            }
        }
    }
}

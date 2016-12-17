/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.model.Face;
import me.kaede.howoldrobot.analyse.presenter.IAnalyse;
import me.kaede.howoldrobot.analyse.presenter.IAnimation;
import me.kaede.howoldrobot.analyse.presenter.IDraw;
import me.kaede.howoldrobot.analyse.presenter.IOptions;
import me.kaede.howoldrobot.analyse.presenter.IShare;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.widget.AgeIndicatorLayout;
import me.kaede.howoldrobot.widget.FaceImageView;

import static me.kaede.howoldrobot.analyse.AnalyseImpl.TYPE_PICK_CAMERA;
import static me.kaede.howoldrobot.analyse.AnalyseImpl.TYPE_PICK_GALLERY;

public class MainActivity extends AppCompatActivity implements IPhotoView, View.OnClickListener {

    static final int ACTIVITY_REQUEST_CAMERA = 0;
    static final int ACTIVITY_REQUEST_GALLERY = 1;

    private Toolbar mToolbar;
    private View mContainer;
    private FaceImageView mFaceView;
    private ProgressDialog mProgress;
    private AgeIndicatorLayout mAgeLayout;
    private IAnalyse mAnalyse;
    private IDraw mDraw;
    private IAnimation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        injectView();
        setListener();
        init();
    }

    private void injectView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFaceView = (FaceImageView) findViewById(R.id.iv_main_face);
        mAgeLayout = (AgeIndicatorLayout) findViewById(R.id.layout_main_age);
        mContainer = findViewById(R.id.layout_main_photo);
    }

    private void setListener() {
        findViewById(R.id.btn_main_camera).setOnClickListener(this);
        findViewById(R.id.btn_main_gallery).setOnClickListener(this);
        findViewById(R.id.btn_main_share).setOnClickListener(this);
    }

    private void init() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mToolbar.setElevation(0);
//        }
        mAnalyse = new AnalyseImpl(this);
        mDraw = new DrawImpl(this);
        mAnimation = new AnimationImpl();
        mAnimation.doLogoAnimation(findViewById(R.id.iv_main_introduce_logo));
        mAnimation.doIntroduceAnimation(findViewById(R.id.layout_main_introduce_text));
    }

    @Override
    public void onGetFaces(List<Face> faces) {
        showProgressDialog(false);
        if (faces == null) {
            toast(getResources().getString(R.string.main_analyze_fail));
        } else if (faces.size() <= 0) {
            toast(getResources().getString(R.string.main_analyze_no_face));
        } else {
            mDraw.drawFaces(mAgeLayout, mFaceView, faces);
        }
    }

    @Override
    public void onGetImage(Bitmap bitmap, String imgPath) {
        mFaceView.clearFaces();
        mAgeLayout.clearAges();
        mFaceView.setImageBitmap(bitmap);
        findViewById(R.id.layout_main_introduce).setVisibility(View.GONE);
        findViewById(R.id.layout_main_border).setBackgroundResource(R.color.orange_500);
        mAnalyse.doAnalyse(imgPath);
    }

    @Override
    public void showProgressDialog(Boolean isShow) {
        if (mProgress == null) {
            mProgress = new ProgressDialog(this);
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(false);
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.setMessage(getResources().getString(R.string.main_loading));
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
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public View getContainer() {
        return mContainer;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
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
                IShare sharePresenter = new ShareImpl(this);
                sharePresenter.doShare(this, this.findViewById(android.R.id.content));
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


}

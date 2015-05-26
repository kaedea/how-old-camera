package kaede.me.howoldrobot.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import kaede.me.howoldrobot.R;
import kaede.me.howoldrobot.model.Face;
import kaede.me.howoldrobot.presenter.*;
import kaede.me.howoldrobot.view.IPhotoView;
import kaede.me.howoldrobot.widget.AgeIndicatorLayout;
import kaede.me.howoldrobot.widget.FaceImageView;

import java.util.List;


public class MainActivity extends ActionBarActivity implements IPhotoView, View.OnClickListener {

	public static final  int ACTVITY_REQUEST_CAMMERA = 0;
	private static final int ACTVITY_REQUEST_GALLERY = 1;
	private IAnalysePresenter analysePresenter;
	private IDrawPresenter    drawPresenter;
	private FaceImageView     faceImageView;
	private ProgressDialog progressDialog;
	private AgeIndicatorLayout ageIndicatorLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		analysePresenter = new AnalysePresenterCompl(this);
		drawPresenter = new DrawPresenterCompl(this, this);
		faceImageView = (FaceImageView) this.findViewById(R.id.iv_main_face);
		ageIndicatorLayout = (AgeIndicatorLayout) this.findViewById(R.id.layout_main_age);
		this.findViewById(R.id.btn_main_camera).setOnClickListener(this);
		this.findViewById(R.id.btn_main_gallery).setOnClickListener(this);
		this.findViewById(R.id.btn_main_share).setOnClickListener(this);
	}


	@Override
	public void onGetFaces(List<Face> faces) {
		showProgressDialog(false);
		drawPresenter.drawFaces(ageIndicatorLayout, faceImageView, faces);
	}

	@Override
	public void onGetImage(Bitmap bitmap, String imgPath) {
		faceImageView.clearFaces();
		ageIndicatorLayout.clearAges();
        faceImageView.setImageBitmap(bitmap);
        //drawPresenter.clearViews();
        analysePresenter.doAnalyse(imgPath);
    }

	@Override
	public void showProgressDialog(Boolean isShow) {
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage("Loading...");
		}
		if (isShow){
			if (!progressDialog.isShowing())
				progressDialog.show();
		}else {
			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}


	}


	@Override
    public void onClick(View v) {

        switch(v.getId()) {
        case R.id.btn_main_camera:
            analysePresenter.pickPhoto(this,AnalysePresenterCompl.TYPE_PICK_CAMERA);
            break;
        case R.id.btn_main_gallery:
            analysePresenter.pickPhoto(this,AnalysePresenterCompl.TYPE_PICK_GALLERY);
            break;
        case R.id.btn_main_share:
	       ISharePresenter sharePresenter  = new SharePresenterCompl(this);
	        sharePresenter.doShare(this,this.findViewById(android.R.id.content));
            break;
        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case ACTVITY_REQUEST_CAMMERA:
            case ACTVITY_REQUEST_GALLERY:
                if(resultCode==RESULT_OK){
                    analysePresenter.getImage(this,data);
                }
                break;
            default:
                break;
        }
    }


}

package kaede.me.howoldrobot.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import kaede.me.howoldrobot.R;
import kaede.me.howoldrobot.model.Face;
import kaede.me.howoldrobot.presenter.AnalysePresenterCompl;
import kaede.me.howoldrobot.presenter.DrawPresenterCompl;
import kaede.me.howoldrobot.presenter.IAnalysePresenter;
import kaede.me.howoldrobot.presenter.IDrawPresenter;
import kaede.me.howoldrobot.presenter.IOptionsPresenter;
import kaede.me.howoldrobot.presenter.ISharePresenter;
import kaede.me.howoldrobot.presenter.OptionsPresenter;
import kaede.me.howoldrobot.presenter.SharePresenterCompl;
import kaede.me.howoldrobot.view.IPhotoView;
import kaede.me.howoldrobot.widget.AgeIndicatorLayout;
import kaede.me.howoldrobot.widget.FaceImageView;


public class MainActivity extends ActionBarActivity implements IPhotoView, View.OnClickListener {

	public static final  int ACTIVITY_REQUEST_CAMERA = 0;
	private static final int ACTIVITY_REQUEST_GALLERY = 1;
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
        getSupportActionBar().setElevation(0);
		faceImageView = (FaceImageView) this.findViewById(R.id.iv_main_face);
		ageIndicatorLayout = (AgeIndicatorLayout) this.findViewById(R.id.layout_main_age);
		this.findViewById(R.id.btn_main_camera).setOnClickListener(this);
		this.findViewById(R.id.btn_main_gallery).setOnClickListener(this);
		this.findViewById(R.id.btn_main_share).setOnClickListener(this);
	}


	@Override
	public void onGetFaces(List<Face> faces) {
		showProgressDialog(false);
        if (faces==null){
            toast(getResources().getString(R.string.main_analyze_fail));
        }else if (faces.size()<=0){
            toast(getResources().getString(R.string.main_analyze_no_face));
        }else {
            drawPresenter.drawFaces(ageIndicatorLayout, faceImageView, faces);
        }
	}

	@Override
	public void onGetImage(Bitmap bitmap, String imgPath) {
		faceImageView.clearFaces();
		ageIndicatorLayout.clearAges();
        faceImageView.setImageBitmap(bitmap);
        analysePresenter.doAnalyse(imgPath);
    }

	@Override
	public void showProgressDialog(Boolean isShow) {
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage(getResources().getString(R.string.main_loading));
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
    public void toast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        IOptionsPresenter optionsPresenter = new OptionsPresenter();
        optionsPresenter.onOptionsItemClick(this,item.getItemId());
        return super.onOptionsItemSelected(item);
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
            case ACTIVITY_REQUEST_CAMERA:
            case ACTIVITY_REQUEST_GALLERY:
                if(resultCode==RESULT_OK){
                    analysePresenter.getImage(this,data);
                }
                break;
            default:
                break;
        }
    }


}

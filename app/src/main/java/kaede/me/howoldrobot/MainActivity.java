package kaede.me.howoldrobot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.util.List;

import kaede.me.howoldrobot.Model.Face;
import kaede.me.howoldrobot.presenter.AnalysePresenterCompl;
import kaede.me.howoldrobot.presenter.DrawPresenterCompl;
import kaede.me.howoldrobot.presenter.IAnalysePresenter;
import kaede.me.howoldrobot.presenter.IDrawPresenter;
import kaede.me.howoldrobot.view.IPhotoView;
import kaede.me.howoldrobot.widget.FaceImageView;


public class MainActivity extends ActionBarActivity implements IPhotoView, View.OnClickListener {

    public static final int ACTVITY_REQUEST_CAMMERA = 0;
    private static final int ACTVITY_REQUEST_GALLERY = 1;
    private IAnalysePresenter analysePresenter;
    private IDrawPresenter drawPresenter;
    private FaceImageView faceImageView;
    private View photoContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        analysePresenter = new AnalysePresenterCompl(this);
        drawPresenter = new DrawPresenterCompl(this,this);
        photoContainer = this.findViewById(R.id.layout_main_photo);
        faceImageView = (FaceImageView) this.findViewById(R.id.iv_main_face);

        this.findViewById(R.id.btn_main_camera).setOnClickListener(this);
        this.findViewById(R.id.btn_main_gallery).setOnClickListener(this);
    }




    @Override
    public void onGetFaces(List<Face> faces) {
        drawPresenter.drawFaces(this,faceImageView,faces);
    }

    @Override
    public void onGetImage(Bitmap bitmap,String imgPath) {
        faceImageView.clearFaces();
        faceImageView.setImageBitmap(bitmap);
        drawPresenter.clearViews();
        faceImageView.requestLayout();
        analysePresenter.doAnalyse(imgPath);
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

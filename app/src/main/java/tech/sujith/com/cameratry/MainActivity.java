package tech.sujith.com.cameratry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements SurfaceHolder.Callback, View.OnTouchListener{
    int RECTANGLE_HEIGHT; /*  Will be set in px */
    int RECTANGLE_WIDTH;
    float CONSTANT_HEIGHT_RATIO = (float) 0.0211;
    float CONSTANT_WIDTH_RATIO = (float) 0.1953;
    float CONSTANT_WIDTH_RATIO_ID = (float) 0.185185185;
    float CONSTANT_HEIGHT_RATIO_ID = (float) 0.063976378;

    float DEVICE_DENSITY; /* Pixel density*/
    int CAMERA_SCREEN_HEIGHT, CAMERA_SCREEN_WIDTH; /* px (int) */
    static int DEVICE_HEIGHT, DEVICE_WIDTH;
    LinearLayout ll1, ll2;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean preview = false;
    LayoutInflater controlInflater = null;
    RelativeLayout camera_container;
    FrameLayout rect_box_container;
    Button Button01;
    int width_ar;
    int height_ar;
    ImageView rect_box;
    FrameLayout.LayoutParams parms;
    LinearLayout.LayoutParams par;
    float dx=0,dy=0,x=0,y=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        camera_container = (RelativeLayout) findViewById(R.id.camera_container);
        rect_box_container = (FrameLayout) findViewById(R.id.rect_box_container);
        width_ar = 0;
        height_ar = 0;
        rect_box = (ImageView) findViewById(R.id.rect_box);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        controlInflater = LayoutInflater.from(getBaseContext());
        setup_constants();
        set_camera_container_params();
        set_rectangle_box_params();

        rect_box.setOnTouchListener(this);

        Log.v("CH", String.valueOf(camera_container.getLayoutParams().height));
        Log.v("CHW", String.valueOf(camera_container.getLayoutParams().width));
        View viewControl = controlInflater.inflate(R.layout.custom, null);
        Button01 = (Button) findViewById(R.id.btn1);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);


        Button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String file_path = get_new_image_path();
                CaptureImage_Background capture_image = new CaptureImage_Background(file_path);
                capture_image.execute();

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouch(View myView, MotionEvent event) {
         android.widget.FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) myView
                .getLayoutParams();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN :
            {
                parms = layoutParams;
                par = (LinearLayout.LayoutParams) getWindow().findViewById(Window.ID_ANDROID_CONTENT).getLayoutParams();
                dx = event.getRawX() - parms.leftMargin;
                dy = event.getRawY() - parms.topMargin;
            }
            break;
            case MotionEvent.ACTION_MOVE :
            {
                x = event.getRawX();
                y = event.getRawY();
                parms.leftMargin = (int) (x-dx);
                parms.topMargin = (int) (y - dy);
                myView.setLayoutParams(parms);
            }
            break;
            case MotionEvent.ACTION_UP :
            {

            }
            break;
        }
        return true;
    }

    private class CaptureImage_Background extends AsyncTask<String, String, String> {

        String file_path_inside;

        public CaptureImage_Background(String file_path) {
            this.file_path_inside = file_path;
        }

        @Override
        protected String doInBackground(String... strings) {

            camera.takePicture(null, null, new PhotoHandler(getApplicationContext(), file_path_inside));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
        }

    }

    private void set_camera_container_params() {

        surfaceView.getLayoutParams().height = CAMERA_SCREEN_HEIGHT ;
        surfaceView.getLayoutParams().width = CAMERA_SCREEN_WIDTH;
//         TODO:: Set Complete Relative Layout Height and width.
        rect_box_container.getLayoutParams().height = CAMERA_SCREEN_HEIGHT;
        rect_box_container.getLayoutParams().width = CAMERA_SCREEN_WIDTH;

    }

    private void setup_constants() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DEVICE_HEIGHT = displayMetrics.heightPixels;
        DEVICE_WIDTH = displayMetrics.widthPixels;
        float density  = getResources().getDisplayMetrics().density;
        DEVICE_DENSITY = density;
        float dpHeight = DEVICE_HEIGHT/DEVICE_DENSITY;
        float dpWidth  = DEVICE_WIDTH/DEVICE_DENSITY;
        float camera_screen_height = get_height_with_ratio(DEVICE_WIDTH);
        CAMERA_SCREEN_HEIGHT = Math.round(camera_screen_height);
        CAMERA_SCREEN_WIDTH = Math.round(DEVICE_WIDTH);
//        360dp
        Log.v("SUJITHScreenD W", String.valueOf(dpWidth));
//        640dp
        Log.v("SUJITHScreenD H", String.valueOf(dpHeight));
    }

    private void set_rectangle_box_params() {
//        device_height = 2160 ~ 2034 = 774dp
//        device_width = 1080 ~ 1080 = 411dp
//          4:3 height = 1452 ~ 1440 = 548dp
//        rect width = 211 = 211/density
//        rect height = 43 = 43/density
//        200 X 130


        float rect_width = DEVICE_WIDTH * CONSTANT_WIDTH_RATIO_ID;
        float rect_height = DEVICE_HEIGHT * CONSTANT_HEIGHT_RATIO_ID;
        RECTANGLE_HEIGHT = Math.round(rect_height);
        RECTANGLE_WIDTH = Math.round(rect_width);
        rect_box.getLayoutParams().height = RECTANGLE_HEIGHT;
        rect_box.getLayoutParams().width = RECTANGLE_WIDTH;

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        try{
            camera.setPreviewDisplay(surfaceHolder);
            if (camera!= null){
                camera.startPreview();
            }
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public float get_height_with_ratio(float width){
//        Ratio 3:4
        return (float) (width/0.75);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {

        if (preview){
            camera.stopPreview();
            preview = false;
        }

        surfaceView.getHolder().setFixedSize(width, height);

        if (camera!= null){
            try{
                camera.setPreviewDisplay(surfaceHolder);

//                Setting Up Camera Parameters: 1. Camera picture size to 3:4, 2. Camera preview size to 3:4 of screen, 3. AutoFocus
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                Camera.Size preview_size = previewSizes.get(0);
                Camera.Size required_size = pictureSizes.get(0); /* Looking for 3:4*/
                for (int j =0 ;j< pictureSizes.size();j++){
                    Log.v("picture_size", pictureSizes.get(j).toString());
                    Log.v("picture_sizeH", String.valueOf(pictureSizes.get(j).height));
                    Log.v("picture_sizeW", String.valueOf(pictureSizes.get(j).width));
                    if (check_ratio(pictureSizes.get(j).height, pictureSizes.get(j).width)){
                        Log.v("Checkckk11", String.valueOf(pictureSizes.get(j).height) + " " + String.valueOf(pictureSizes.get(j).width));
                        required_size = pictureSizes.get(j);
                        break;
                    }
                }
                for (int j =0 ;j< previewSizes.size();j++){
                    Log.v("preview_size", previewSizes.get(j).toString());
                    Log.v("preview_sizeH", String.valueOf(previewSizes.get(j).height));
                    Log.v("preview_sizeW", String.valueOf(previewSizes.get(j).width));
                    if (check_ratio(previewSizes.get(j).height, previewSizes.get(j).width)){
                        Log.v("Checkckk11", String.valueOf(previewSizes.get(j).height) + " " + String.valueOf(previewSizes.get(j).width));
                        preview_size = previewSizes.get(j);
                        break;
                    }
                }
                if (parameters.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                parameters.setPreviewSize(preview_size.width, preview_size.height);
                parameters.setPictureSize(required_size.width, required_size.height);
                camera.setParameters(parameters);
                camera.startPreview();
                preview = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean check_ratio(int height, int width) {
        int factor = gcd(width, height);
        int widthRatio = width / factor;
        int heightRatio = height / factor;
//        Specific for 3:4 ratio
        Log.v("Checkckk", String.valueOf(widthRatio)  + " " +  String.valueOf(heightRatio));
        if ((widthRatio == 4 && heightRatio == 3)|| (widthRatio == 3 && heightRatio == 4) ){
            return true;
        }
        else{
            return false;
        }
    }

    public String get_new_image_path(){

        File imageStorageDir = new File(Environment.getExternalStorageDirectory()+"/DCIM/", "Camera_Try");
        if (!imageStorageDir.exists()){
            imageStorageDir.mkdirs();
        }
        String file_path  = imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        return file_path;
    }

    public int gcd(int width, int height) {
        return (height == 0) ? width : gcd(height, width % height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.setPreviewCallback(null);
        camera.release();
        camera = null;
        preview = false;
    }

}

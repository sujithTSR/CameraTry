package tech.sujith.com.cameratry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ViewPhoto extends Activity {
    Button back_to_camera;
    ImageView view_image;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_layout);
        dialog = new ProgressDialog(this);
        final String file_path = getIntent().getStringExtra("image_path");
        back_to_camera = (Button) findViewById(R.id.back_to_camera);
        view_image = (ImageView) findViewById(R.id.view_iamge);
        dialog.setMessage("Saving Image");
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Bitmap bit_map = BitmapFactory.decodeFile(file_path);
                view_image.setImageBitmap(bit_map);
            }
        },3000);

        back_to_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to_camera_intent = new Intent(ViewPhoto.this, MainActivity.class);
                startActivity(to_camera_intent);
            }
        });

    }
}

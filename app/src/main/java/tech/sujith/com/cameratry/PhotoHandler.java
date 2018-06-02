package tech.sujith.com.cameratry;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoHandler implements Camera.PictureCallback {
    private final Context context;
    private final String file_path;

    public PhotoHandler(Context context, String file_path) {
        this.context = context;
        this.file_path = file_path;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        if (file_path == null || (file_path!=null && file_path.compareTo("")==0)){


        }
//        Log.v("gg","here");
//        File pictureFileDir = getDir();
//
//        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
//
//            Log.v("Debug","Can't create directory to save image." );
//            Toast.makeText(context, "Can't create directory to save image.",
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
//        String date = dateFormat.format(new Date());
//        String photoFile = "Picture_" + date + ".jpg";

        String filename = file_path;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(bytes);
            fos.close();
            Toast.makeText(context, "New Image saved:\n" + file_path,
                    Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Log.d("Debug", "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }

        try{
            Intent photo_intent = new Intent(context, ViewPhoto.class);
            photo_intent.putExtra("image_path", file_path);
            context.startActivity(photo_intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String aa = Environment.DIRECTORY_PICTURES;
        Log.v("aa",aa);
        return new File(sdDir, "CameraTry");
    }
}

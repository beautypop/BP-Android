package com.beautypop.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.image.crop.Crop;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;

import org.joda.time.DateTime;

import java.io.File;

public class SelectImageActivity extends Activity {
    final int SELECT_PICTURE = 1000;
    public String outputUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        File imageFolder = new File(ImageUtil.IMAGE_FOLDER_PATH);
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        String now = String.valueOf(new DateTime().getSecondOfDay());
        String fname = "Image-"+now+".jpg";
        File file = new File (imageFolder, fname);
        outputUrl = file.getAbsolutePath();

        Uri destination = Uri.fromFile(file);
        Crop.of(getIntent().getData(), destination).asSquare().start(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCELED){
            finish();
        }

        if (requestCode == SELECT_PICTURE) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.d(this.getClass().getSimpleName(), "handleCrop: outputUrl=" + outputUrl);

            // set activity result
            Intent intent = new Intent();
            intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, outputUrl);
            setResult(RESULT_OK, intent);
            finish();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        //finish();
    }
}

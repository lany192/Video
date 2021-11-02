package com.github.lany192.video.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.video.VideoCompress;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tv_input;
    private TextView tv_output;
    private TextView tv_indicator;
    private TextView tv_progress;
    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private String inputPath;
    private ProgressBar pb_compress;
    private long startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_input = (TextView) findViewById(R.id.tv_input);
        tv_output = (TextView) findViewById(R.id.tv_output);
        tv_indicator = (TextView) findViewById(R.id.tv_indicator);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        pb_compress = (ProgressBar) findViewById(R.id.pb_compress);
        Button btn_compress = (Button) findViewById(R.id.btn_compress);
        Button btn_select = (Button) findViewById(R.id.btn_select);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyPhotos.createAlbum(MainActivity.this, true, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .filter(Type.VIDEO)
                        .start(101);
            }
        });

        btn_compress.setOnClickListener(view -> {
            String destPath = tv_output.getText().toString() + File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".mp4";
            VideoCompress.compressVideoLow(tv_input.getText().toString(), destPath, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    tv_indicator.setText("Compressing..." + "\n"
                            + "Start at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                    pb_compress.setVisibility(View.VISIBLE);
                    startTime = System.currentTimeMillis();
                    Util.writeFile(MainActivity.this, "Start at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");
                }

                @Override
                public void onSuccess() {
                    String previous = tv_indicator.getText().toString();
                    tv_indicator.setText(previous + "\n"
                            + "Compress Success!" + "\n"
                            + "End at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                    pb_compress.setVisibility(View.INVISIBLE);
                    endTime = System.currentTimeMillis();
                    Util.writeFile(MainActivity.this, "End at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");
                    Util.writeFile(MainActivity.this, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n");
                    Util.writeFile(MainActivity.this);
                }

                @Override
                public void onFail() {
                    tv_indicator.setText("Compress Failed!");
                    pb_compress.setVisibility(View.INVISIBLE);
                    endTime = System.currentTimeMillis();
                    Util.writeFile(MainActivity.this, "Failed Compress!!!" + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                }

                @Override
                public void onProgress(float percent) {
                    tv_progress.setText(percent + "%");
                }
            });
        });
        tv_output.setText(outputDir);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            //相机或相册回调
            if (requestCode == 101) {
                //返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                ArrayList<Photo> resultPhotos =
                        data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);

                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal =
                        data.getBooleanExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, false);

                                                inputPath = resultPhotos.get(0).path;
                                                tv_input.setText(inputPath);


            }
        } else if (RESULT_CANCELED == resultCode) {
            Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
        }
    }

}
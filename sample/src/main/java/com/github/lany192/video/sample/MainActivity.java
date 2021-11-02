package com.github.lany192.video.sample;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.video.VideoCompress;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.text.SimpleDateFormat;
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

        btn_select.setOnClickListener(view -> Matisse.from(this)
                .choose(MimeType.ofVideo())
                .countable(true)
                .maxSelectable(9)
                .gridExpectedSize(10)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .showPreview(false) // Default is `true`
                .forResult(result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.i("OnActivityResult ", String.valueOf(Matisse.obtainOriginalState(result.getData())));
//                                inputPath = Util.getFilePath(this, data.getData());
//                                tv_input.setText(inputPath);
                    }
                }));

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
}
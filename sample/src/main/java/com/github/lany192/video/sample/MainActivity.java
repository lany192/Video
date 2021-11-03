package com.github.lany192.video.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.lany192.video.Compressor;
import com.github.lany192.video.sample.databinding.ActivityMainBinding;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    private String inputPath;
    private long startTime, endTime;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvOutput.setText(outputDir);
        binding.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyPhotos.createAlbum(MainActivity.this, true, true, new GlideEngine())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .filter(Type.VIDEO)
                        .start(101);
            }
        });
        binding.btnCompress.setOnClickListener(view -> {
            String outPath = binding.tvOutput.getText().toString() + File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".mp4";
//
//            Disposable disposable = Flowable.just(inputPath)
//                .observeOn(Schedulers.io())
//                .map(new Function<String, String>() {
//                    @Override
//                    public String apply(@NonNull String s) throws Exception {
//                        //处理压缩
//                        new Compressor().compress(sourcePath, destinationPath, mQuality, CompressTask.VideoCompressTask.this::publishProgress);
//
//                        return null;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnError(throwable -> Log.e("TAG", throwable.getMessage()))
//                .subscribe(new );

            CompressTask.compressVideoMedium(inputPath, outPath, new CompressTask.CompressListener() {
                @Override
                public void onStart() {
                    binding.tvIndicator.setText("Compressing..." + "\n"
                            + "Start at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                    binding.pbCompress.setVisibility(View.VISIBLE);
                    startTime = System.currentTimeMillis();
                    Util.writeFile(MainActivity.this, "Start at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");
                }

                @Override
                public void onSuccess() {
                    String previous = binding.tvIndicator.getText().toString();
                    binding.tvIndicator.setText(previous + "\n"
                            + "Compress Success!" + "\n"
                            + "End at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                    binding.pbCompress.setVisibility(View.INVISIBLE);
                    endTime = System.currentTimeMillis();
                    Util.writeFile(MainActivity.this, "End at: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");
                    Util.writeFile(MainActivity.this, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n");
                    Util.writeFile(MainActivity.this);
                }

                @Override
                public void onFail() {
                    binding.tvIndicator.setText("Compress Failed!");
                    binding.pbCompress.setVisibility(View.INVISIBLE);
                    endTime = System.currentTimeMillis();
                    Util.writeFile(MainActivity.this, "Failed Compress!!!" + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                }

                @Override
                public void onProgress(float percent) {
                    binding.tvProgress.setText(percent + "%");
                }
            });
        });
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
                binding.tvInput.setText(inputPath);
            }
        } else if (RESULT_CANCELED == resultCode) {
            Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
        }
    }

}
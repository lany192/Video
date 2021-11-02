package com.github.lany192.video.sample;

import android.os.AsyncTask;

import com.github.lany192.video.Compressor;

public class VideoCompress {

    public static VideoCompressTask compressVideoHigh(String srcPath, String destPath, CompressListener listener) {
        VideoCompressTask task = new VideoCompressTask(listener, Compressor.COMPRESS_QUALITY_HIGH);
        task.execute(srcPath, destPath);
        return task;
    }

    public static VideoCompressTask compressVideoMedium(String srcPath, String destPath, CompressListener listener) {
        VideoCompressTask task = new VideoCompressTask(listener, Compressor.COMPRESS_QUALITY_MEDIUM);
        task.execute(srcPath, destPath);
        return task;
    }

    public static VideoCompressTask compressVideoLow(String srcPath, String destPath, CompressListener listener) {
        VideoCompressTask task = new VideoCompressTask(listener, Compressor.COMPRESS_QUALITY_LOW);
        task.execute(srcPath, destPath);
        return task;
    }

    public interface CompressListener {
        void onStart();

        void onSuccess();

        void onFail();

        void onProgress(float percent);
    }

    private static class VideoCompressTask extends AsyncTask<String, Float, Boolean> {
        private final CompressListener mListener;
        private final int mQuality;

        public VideoCompressTask(CompressListener listener, int quality) {
            mListener = listener;
            mQuality = quality;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mListener != null) {
                mListener.onStart();
            }
        }

        @Override
        protected Boolean doInBackground(String... paths) {
            String sourcePath = paths[0];
            String destinationPath = paths[1];
            //处理压缩
            Compressor compressor = new Compressor();
            return compressor.compress(sourcePath, destinationPath, mQuality, VideoCompressTask.this::publishProgress);
        }

        @Override
        protected void onProgressUpdate(Float... percent) {
            super.onProgressUpdate(percent);
            if (mListener != null) {
                mListener.onProgress(percent[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (mListener != null) {
                if (result) {
                    mListener.onSuccess();
                } else {
                    mListener.onFail();
                }
            }
        }
    }
}

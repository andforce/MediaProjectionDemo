package com.cry.screenop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import java.nio.ByteBuffer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 截取屏幕的单利
 * Created by a2957 on 4/21/2018.
 */
public class RxScreenShot {
    private String TAG = "RxScreenShot";

    private Handler mCallBackHandler = new CallBackHandler();
    private MediaCallBack mMediaCallBack = new MediaCallBack();
    private MediaProjection mediaProjection;
    SurfaceFactory mSurfaceFactory;
    ImageReader mImageReader;

    public static final int MAX_IMAGE_HEIGHT = 480;
    public int width = 1080;
    public int height = 2280;
    public int dpi = 1;
    final DisplayMetrics metrics = new DisplayMetrics();


    private RxScreenShot(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
    }

    public static RxScreenShot of(MediaProjection mediaProjection) {
        return new RxScreenShot(mediaProjection);
    }

    public RxScreenShot createImageReader(WindowManager manager, int width, int height) {
        manager.getDefaultDisplay().getRealMetrics(metrics);

        this.width = width;
        this.height = height;
        this.dpi = metrics.densityDpi;

        //注意这里使用RGB565报错提示，只能使用RGBA_8888
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 5);
        mSurfaceFactory = new ImageReaderSurface(mImageReader);
        createProject(width, height, dpi);
        return this;
    }

    private void createProject(int width, int height, int dpi) {
        mediaProjection.registerCallback(mMediaCallBack, mCallBackHandler);
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

        mediaProjection.createVirtualDisplay(TAG + "-display", width, height, dpi,
                flags,
                mSurfaceFactory.getInputSurface(), null, null);
    }

    public Observable<Object> startCapture() {
        return ImageReaderAvailableObservable.of(mImageReader)
                .map(new Function<ImageReader, Object>() {
                    @Override
                    public Object apply(ImageReader imageReader) throws Exception {
                        String mImageName = System.currentTimeMillis() + ".jpeg";
                        Log.e(TAG, "image name is : " + mImageName);
                        Bitmap bitmap = null;
                        Bitmap result = null;
                        Image image = imageReader.acquireLatestImage();
                        if (image == null) {

                        } else {
                            int width = imageReader.getWidth();

                            int height = imageReader.getHeight();

                            final Image.Plane[] planes = image.getPlanes();

                            final ByteBuffer buffer = planes[0].getBuffer();

                            int pixelStride = planes[0].getPixelStride();

                            int rowStride = planes[0].getRowStride();

                            int rowPadding = rowStride - pixelStride * width;

                            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);

                            bitmap.copyPixelsFromBuffer(buffer);

                            result = Bitmap.createBitmap(bitmap, 0, 0, width, height);

                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                                bitmap = null;
                            }

                            image.close();
                        }


                        return result == null ? new Object() : result;
                    }
                });
    }

    public Observable<Object> startCaptureWithHW(int topStart, int totalHeight) {
        return ImageReaderAvailableObservable.of(mImageReader, mCallBackHandler)
                .observeOn(Schedulers.io())
                .map(new Function<ImageReader, Object>() {
                    @Override
                    public Object apply(ImageReader imageReader) throws Exception {

                        Image image = imageReader.acquireLatestImage();

                        int width = imageReader.getWidth();

                        int height = imageReader.getHeight();

                        final Image.Plane[] planes = image.getPlanes();

                        final ByteBuffer buffer = planes[0].getBuffer();

                        int pixelStride = planes[0].getPixelStride();

                        int rowStride = planes[0].getRowStride();

                        int rowPadding = rowStride - pixelStride * width;

                        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);

                        bitmap.copyPixelsFromBuffer(buffer);

                        bitmap = Bitmap.createBitmap(bitmap, 0, topStart, width, totalHeight);

                        image.close();

                        return bitmap == null ? new Object() : bitmap;
                    }
                });
    }

    public static Observable<Object> shoot(FragmentActivity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getRealMetrics(metrics);

        // 获取屏幕宽高
        float widthPixels = metrics.widthPixels;
        float heightPixels = metrics.heightPixels;
        if (heightPixels > MAX_IMAGE_HEIGHT) {
            // heightPixels        MAX_IMAGE_HEIGHT
            // ------------  =     ----------------
            // widthPixels         x
            widthPixels = widthPixels * (float) MAX_IMAGE_HEIGHT / heightPixels;
            heightPixels = MAX_IMAGE_HEIGHT;
        }

        int finalWidthPixels = (int) widthPixels;
        int finalHeightPixels = (int) heightPixels;
        return MediaProjectionHelper
                .requestCapture(activity)
                .map(mediaProjection -> RxScreenShot.of(mediaProjection).createImageReader(activity.getWindowManager(), finalWidthPixels, finalHeightPixels))
                .flatMap(RxScreenShot::startCapture)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    class MediaCallBack extends MediaProjection.Callback {
        @Override
        public void onStop() {
            super.onStop();
        }
    }

    static class CallBackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public interface SurfaceFactory {
        Surface getInputSurface();
    }

    class ImageReaderSurface implements SurfaceFactory {

        private ImageReader imageReader;

        public ImageReaderSurface(ImageReader imageReader) {
            this.imageReader = imageReader;
        }

        @Override
        public Surface getInputSurface() {
            return imageReader.getSurface();
        }
    }
}

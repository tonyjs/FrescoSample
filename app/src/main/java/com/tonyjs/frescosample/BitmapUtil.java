package com.tonyjs.frescosample;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.animated.base.AnimatedDrawable;
import com.facebook.imagepipeline.animated.factory.AnimatedDrawableFactory;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by tonyjs on 15. 12. 9..
 */
public class BitmapUtil {

    public static void loadPlainBitmap(Uri uri, final OnResourceReadyCallback onResourceReadyCallback) {
//        if (resizeOptions == null) {
//            int maximumBitmapSize = getMaximumBitmapSize();
//            LogUtil.d("BitmapUtil", "maximumBitmapSize = " + maximumBitmapSize);
//            resizeOptions = new ResizeOptions(maximumBitmapSize, maximumBitmapSize);
//        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setResizeOptions(resizeOptions)
                .setAutoRotateEnabled(true)
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, App.getContext());
        dataSource.subscribe(
                new BitmapDataSubscriber(onResourceReadyCallback), CallerThreadExecutor.getInstance());

    }

    public static class BitmapDataSubscriber
            extends BaseDataSubscriber<CloseableReference<CloseableImage>> {

        private OnResourceReadyCallback onResourceReadyCallback;

        public BitmapDataSubscriber(OnResourceReadyCallback onResourceReadyCallback) {
            this.onResourceReadyCallback = onResourceReadyCallback;
        }

        @Override
        public void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            if (!dataSource.isFinished()) {
                return;
            }
            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
            if (imageReference != null) {
                CloseableReference<CloseableImage> imageReferenceClone = imageReference.clone();
                try {
                    CloseableImage closeableImage = imageReferenceClone.get();
                    if (closeableImage instanceof CloseableBitmap) {
                        handleBitmap((CloseableBitmap) closeableImage);
                    } else if (closeableImage instanceof CloseableAnimatedImage) {
                        handleAnimateBitmap((CloseableAnimatedImage) closeableImage);
                    }
                } finally {
                    imageReference.close();
                    CloseableReference.closeSafely(imageReferenceClone);
                }
            }
        }

        private void handleAnimateBitmap(CloseableAnimatedImage animatedImage) {
            Log.i("BitmapUtil", "animatableImage loaded");
            AnimatedDrawableFactory animatedDrawableFactory =
                    Fresco.getImagePipelineFactory().getAnimatedDrawableFactory();
            AnimatedDrawable drawable =
                    animatedDrawableFactory.create(animatedImage.getImageResult());
            if (onResourceReadyCallback != null) {
                onResourceReadyCallback.onReady(drawable);
            } else {
                onResourceReadyCallback.onFail(new NullPointerException("Bitmap is empty."));
            }
        }

        private void handleBitmap(CloseableBitmap closeableBitmap) {
            Bitmap underlyingBitmap = closeableBitmap.getUnderlyingBitmap();
            if (onResourceReadyCallback != null && isValidateBitmap(underlyingBitmap)) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(
                        App.getContext().getResources(),
                        underlyingBitmap);
                onResourceReadyCallback.onReady(bitmapDrawable);
            } else {
                onResourceReadyCallback.onFail(new NullPointerException("Bitmap is empty."));
            }
        }

        private boolean isValidateBitmap(Bitmap bitmap) {
            return bitmap != null && !bitmap.isRecycled();
        }

        @Override
        public void onFailureImpl(DataSource dataSource) {
            // handle failure
            if (onResourceReadyCallback != null) {
                onResourceReadyCallback.onFail(dataSource.getFailureCause());
            }
        }

        @Override
        public void onProgressUpdate(DataSource<CloseableReference<CloseableImage>> dataSource) {
            if (!dataSource.isFinished() && onResourceReadyCallback != null) {
                onResourceReadyCallback.onProgressUpdate(dataSource.getProgress());
            }
        }
    }
}

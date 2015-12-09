package com.tonyjs.frescosample;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.animated.base.AnimatableDrawable;
import com.facebook.imagepipeline.image.ImageInfo;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity2 extends AppCompatActivity {

    public static final String URL =
            "https://s3-us-west-2.amazonaws.com/sample-images-tony/Screenshot_2015-01-27-16-20-58.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        String url = MainActivity.URL;
        Uri uri = Uri.parse(url);

        final ImageView draweeView = (ImageView) findViewById(R.id.image);
        draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });

        BitmapUtil.loadPlainBitmap(uri, new OnResourceReadyCallback() {
            @Override
            public void onReady(final Drawable drawable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        draweeView.setImageDrawable(drawable);

                        if (drawable instanceof AnimatableDrawable) {
                            ((AnimatableDrawable) drawable).start();
                        }
                    }
                });
            }

            @Override
            public void onFail(Throwable cause) {

            }

            @Override
            public void onProgressUpdate(final float progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress((int) progress);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

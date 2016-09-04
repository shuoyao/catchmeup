package com.shuoyao.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Context;

import java.io.InputStream;


/**
 * Activity which displays a registration screen to the user.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(new MYGIFView(this));
        setContentView(R.layout.activity_welcome);

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
                //startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
        });

        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
                finish();
                //startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
        });
    }

    private void setLayerType(int layerTypeSoftware, Object o) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private class MYGIFView extends View {
        Movie movie, movie1;
        InputStream is = null, is1 = null;
        int movieWidth, movieHeight;
        long moviestart;
        long movieDuration;

        public MYGIFView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            setFocusable(true);
            //is = context.getResources().openRawResource(R.drawable.welcome_circle);
            //movie = Movie.decodeStream(is);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            super.onDraw(canvas);
            long now = android.os.SystemClock.uptimeMillis();
            System.out.println("now=" + now);
            if (moviestart == 0) {//first time
                moviestart = now;
            }

            System.out.println("\tmoviestart=" + moviestart);
            int relTime = (int) ((now - moviestart) % movie.duration());
            System.out.println("time=" + relTime + "\treltime=" + movie.duration());
            movie.setTime(relTime);
            movie.draw(canvas, this.getWidth() / 2 - 20, this.getHeight() / 2 - 40);
            this.invalidate();
        }
    }
}
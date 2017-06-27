package com.example.raul.turnstylerun;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.World;

/**
 * Created by Raul on 01/05/2017.
 */

public class GameActivity extends Activity{

    private Surface mGLView;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            // Create a GLSurfaceView instance and set it
            // as the ContentView for this Activity.
            mGLView = new Surface(this);
            setContentView(mGLView);
    }

}

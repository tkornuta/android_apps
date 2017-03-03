package com.ibm.us.tkornuta.openglmicrophone;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import android.media.MediaRecorder;
import static android.Manifest.permission.RECORD_AUDIO;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLView;

    private MediaRecorder mRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout l = (LinearLayout) this.findViewById(R.id.layout_opengl);
        mGLView = new MyGLSurfaceView(this);
        l.addView(mGLView, 0);

    }

    public void btnStart_onClick(View v) {
        Button button = (Button) v;
        android.util.Log.i("Start", "onClick");
        if (mRecorder == null) {
            if(checkPermission()) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                try {
                    mRecorder.prepare();
                    mRecorder.start();
                    android.util.Log.i("mrecorder", "started!");
                } catch (Exception e) {
                    android.util.Log.e("Recorder", "Exception", e);
                    mRecorder = null;
                }
            } else {
                requestPermission();
            }
        }// if mRecorder is null

        if (mRecorder != null) {
            int amplitude = mRecorder.getMaxAmplitude();
            android.util.Log.i("Recorder", String.format("amplitude = %d", amplitude));

        }
    }

    public void btnStop_onClick(View v) {
        Button button = (Button) v;
        android.util.Log.i("Stop", "onClick");
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public static final int RequestPermissionCode = 1;

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{RECORD_AUDIO}, RequestPermissionCode);
    }



    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        if (mRecorder != null)
            mRecorder.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        if (mRecorder != null)
            mRecorder.start();
    }
}

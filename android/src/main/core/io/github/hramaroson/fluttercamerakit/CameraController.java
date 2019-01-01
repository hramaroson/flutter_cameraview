package io.github.hramaroson.fluttercamerakit;

import android.support.annotation.WorkerThread;

abstract class CameraController implements Thread.UncaughtExceptionHandler {

    static final int STATE_STOPPING = -1; // Camera is about to be stopped.
    static final int STATE_STOPPED = 0; // Camera is stopped.
    static final int STATE_STARTING = 1; // Camera is about to start.
    static final int STATE_STARTED = 2; // Camera is available and we can set parameters.

    protected final FlutterCamerakitPlugin.CameraCallbacks mCameraCallBacks;
    protected WorkerHandler mHandler;

    private int mDisplayOffset;

    protected int mState = STATE_STOPPED;

    CameraController (FlutterCamerakitPlugin.CameraCallbacks cameraCallbacks){
        this.mCameraCallBacks = cameraCallbacks;
        this.mHandler = WorkerHandler.get("CameraViewController");
        this.mHandler.getThread().setUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {

    }

    final void start(){
        mHandler.post(new Runnable() {
          @Override
          public void run(){
              if (mState >= STATE_STARTING) return;
              mState = STATE_STARTING;
              onStart();
              mState = STATE_STARTED;

          }
      });

  }
  final void stop(){
        mHandler.post(new Runnable() {
          @Override
          public void run(){
              if (mState <= STATE_STOPPED) return;
              mState = STATE_STOPPING;
              onStop();
              mState = STATE_STOPPED;
          }
        });
  }

  @WorkerThread
  abstract void onStart();

  @WorkerThread
  abstract void onStop();

  final void setDisplayOffset(int displayOffset) {
      mDisplayOffset = displayOffset;
  }

}

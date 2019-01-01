package io.github.hramaroson.fluttercamerakit;

import android.os.Handler;
import android.os.HandlerThread;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class holding a background handler.
 * We want them to survive configuration changes if there's still job to do.
 */
class WorkerHandler {
    private final static ConcurrentHashMap<String, WeakReference<WorkerHandler>> sCache = new ConcurrentHashMap<>(4);

    public static WorkerHandler get(String name) {
        if (sCache.containsKey(name)) {
            WorkerHandler cached = sCache.get(name).get();
            if (cached != null) {
                HandlerThread thread = cached.mThread;
                if (thread.isAlive() && !thread.isInterrupted()) {
                    return cached;
                }
            }
            sCache.remove(name);
        }
        WorkerHandler handler = new WorkerHandler(name);
        sCache.put(name, new WeakReference<>(handler));
        return handler;
    }

    // Handy util to perform action in a fallback thread.
    // Not to be used for long-running operations since they will
    // block the fallback thread.
    public static void run(Runnable action) {
        get("FallbackCameraThread").post(action);
    }

    private HandlerThread mThread;
    private Handler mHandler;

    private WorkerHandler(String name) {
        mThread = new HandlerThread(name);
        mThread.setDaemon(true);
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
    }

    public Handler get() {
        return mHandler;
    }

    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public Thread getThread() {
        return mThread;
    }

    public static void destroy() {
        for (String key : sCache.keySet()) {
            WeakReference<WorkerHandler> ref = sCache.get(key);
            WorkerHandler handler = ref.get();
            if (handler != null && handler.getThread().isAlive()) {
                handler.getThread().interrupt();
            }
            ref.clear();
        }
        sCache.clear();
    }
}
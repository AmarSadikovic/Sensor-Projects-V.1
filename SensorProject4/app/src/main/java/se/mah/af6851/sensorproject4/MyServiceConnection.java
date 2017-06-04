package se.mah.af6851.sensorproject4;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Amar on 2017-02-24.
 */

public class MyServiceConnection implements ServiceConnection{

    private final MainActivity mActivity;
    public MyServiceConnection(MainActivity a){
        this.mActivity = a;
    }
    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
        StepsService.LocalBinder binder = (StepsService.LocalBinder) service;
        mActivity.mService = binder.getService();
        mActivity.mBound = true;
        mActivity.mService.setListenerActivity(mActivity);
    }
    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mActivity.mBound = false;
    }
}

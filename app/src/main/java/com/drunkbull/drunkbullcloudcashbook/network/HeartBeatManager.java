package com.drunkbull.drunkbullcloudcashbook.network;

import android.os.Looper;
import android.util.Log;

import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;
import com.drunkbull.drunkbullcloudcashbook.singleton.GSignalManager;
import com.drunkbull.drunkbullcloudcashbook.singleton.NoSuchGSignalException;
import com.drunkbull.drunkbullcloudcashbook.utils.data.DateUtil;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class HeartBeatManager {

    static final int MAX_TIMEOUT_SECONDS = 8000;
    static final int INF = 10000000;
    static final int SLEEP_TIME = 2000;

    private boolean firstTime = true;

    private volatile static HeartBeatManager singleton;
    public static HeartBeatManager getSingleton() {
        if (singleton == null) {
            synchronized (HeartBeatManager.class) {
                if (singleton == null) {
                    singleton = new HeartBeatManager();
                }
            }
        }
        return singleton;
    }

    private HeartBeatManager(){
        GSignalManager.getSingleton().addGSignal(this, "timeout");
        lastHeartBeatTime = DateUtil.getTimeStamp();
        try {
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "responsed", this, "onResponsed", new Class[]{CBMessage.Response.class});
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        activate();
        threadHeatBeat.stopFlag = false;
        threadHeatBeat.start();
    }

    public void activate(){
        threadHeatBeat.active = true;
    }

    public void inactivate(){
        threadHeatBeat.active = false;
    }

    public void stop(){
        inactivate();
        threadHeatBeat.stopFlag = true;
        threadHeatBeat.interrupt();
    }

    private ThreadHeatBeat threadHeatBeat = new ThreadHeatBeat();

    private long lastHeartBeatTime = 0;

    static class ThreadHeatBeat extends Thread {
        boolean active = false;
        boolean stopFlag = false;
        public void run() {
            while (!stopFlag){
                try {
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!active) continue;
                ServerConnection.getSingleton().sendRequestHeartBeat();

                long time = DateUtil.getTimeStamp();
                long period = time - getSingleton().lastHeartBeatTime;

//                if (period > MAX_TIMEOUT_SECONDS && period < INF){
//                    Log.e(this.getClass().getSimpleName(), "timeout");
//                    try {
//                        GSignalManager.getSingleton().emitGSignal(HeartBeatManager.getSingleton(), "timeout");
//                    } catch (NoSuchGSignalException e) {
//                        e.printStackTrace();
//                    }
//                }

            }
        }
    }

    private void onResponsed(CBMessage.Response response){
        if (response.getType() == CBMessage.Type.HEARTBEAT){
            lastHeartBeatTime = DateUtil.getTimeStamp();
        }
    }
}

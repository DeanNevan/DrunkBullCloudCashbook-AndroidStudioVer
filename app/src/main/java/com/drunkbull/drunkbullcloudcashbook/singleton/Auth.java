package com.drunkbull.drunkbullcloudcashbook.singleton;

public class Auth {

    private volatile static Auth singleton;
    public static Auth getSingleton() {
        if (singleton == null) {
            synchronized (Auth.class) {
                if (singleton == null) {
                    singleton = new Auth();
                }
            }
        }
        return singleton;
    }

    private Auth(){

        GSignalManager.getSingleton().addGSignal(this, "已验证");

    }

    public String clientID = "";

    public boolean authenticated = false;




}

package com.drunkbull.drunkbullcloudcashbook.singleton;

import com.drunkbull.drunkbullcloudcashbook.network.ServerConnection;
import com.drunkbull.drunkbullcloudcashbook.pojo.CBGroup;
import com.drunkbull.drunkbullcloudcashbook.protobuf.CBMessage;

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

        GSignalManager.getSingleton().addGSignal(this, "authenticated");

        try {
            GSignalManager.getSingleton().connect(ServerConnection.getSingleton(), "responsed", this, "onResponsed", new Class[]{CBMessage.Response.class});
        } catch (NoSuchGSignalException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public String clientID = "";

    public boolean authenticated = false;

    public CBGroup cbGroup = new CBGroup();

    public CBGroup.CBGroupMember cbGroupMember = new CBGroup.CBGroupMember();

    private void onResponsed(CBMessage.Response response){
        if (response.getType() == CBMessage.Type.CONNECT){
            clientID = response.getClientId();
        }
    }


}

package com.drunkbull.drunkbullcloudcashbook.singleton;

import com.drunkbull.drunkbullcloudcashbook.utils.Reflex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GSignalManager {

    private volatile static GSignalManager singleton;
    public static GSignalManager getSingleton() {
        if (singleton == null) {
            synchronized (GSignalManager.class) {
                if (singleton == null) {
                    singleton = new GSignalManager();
                }
            }
        }
        return singleton;
    }

    private GSignalManager(){

    }

    private HashMap<Object, GSignalGroup> gSignalGroups = new HashMap<>();

    public boolean isGSignalExist(Object obj, String gSignalName){
        if (gSignalGroups.containsKey(obj)){
            if (gSignalGroups.get(obj).getGSignal(gSignalName) != null){
                return true;
            }
        }
        return false;
    }

    public void addGSignal(Object obj, String gSignalName){
        if (!gSignalGroups.containsKey(obj)){
            gSignalGroups.put(obj, new GSignalGroup(obj));
        }
        gSignalGroups.get(obj).addGSignal(gSignalName);
    }
    public void removeGSignal(Object obj, String gSignalName){
        if (gSignalGroups.containsKey(obj)){
            gSignalGroups.get(obj).removeGSignal(gSignalName);
        }
    }

    public void connect(Object target, String gSignalName, Object handler, String function) throws NoSuchGSignalException, NoSuchMethodException {
        if (!isGSignalExist(target, gSignalName)){
            throw new NoSuchGSignalException("GSignalManager:bad connect");
        }
        GSignalGroup gSignalGroup = gSignalGroups.get(target);
        GSignal gSignal = gSignalGroup.getGSignal(gSignalName);
        gSignal.addHandlerFunction(handler, function);
    }

    public void connect(Object target, String gSignalName, Object handler, String function, Class[] paraTypes) throws NoSuchGSignalException, NoSuchMethodException {
        if (!isGSignalExist(target, gSignalName)){
            throw new NoSuchGSignalException("GSignalManager:bad connect");
        }
        GSignalGroup gSignalGroup = gSignalGroups.get(target);
        GSignal gSignal = gSignalGroup.getGSignal(gSignalName);
        gSignal.addHandlerFunction(handler, function, paraTypes);
    }

    public void disconnect(Object target, String gSignalName, Object handler, String function) throws NoSuchGSignalException {
        if (!isGSignalExist(target, gSignalName)){
            throw new NoSuchGSignalException("GSignalManager:bad disconnect");
        }
        GSignalGroup gSignalGroup = gSignalGroups.get(target);
        GSignal gSignal = gSignalGroup.getGSignal(gSignalName);
        gSignal.removeHandlerFunction(handler, function);
    }

    public void emitGSignal(Object obj, String gSignalName, Class[] paraTypes, Object[] paraValues) throws NoSuchGSignalException {
        if (!isGSignalExist(obj, gSignalName)){
            throw new NoSuchGSignalException("GSignalManager:bad emitGSignal");
        }
        GSignalGroup gSignalGroup = gSignalGroups.get(obj);
        GSignal gSignal = gSignalGroup.getGSignal(gSignalName);
        gSignal.emit(paraTypes, paraValues);
    }

    public void emitGSignal(Object obj, String gSignalName, Class paraType, Object paraValue) throws NoSuchGSignalException {
        if (!isGSignalExist(obj, gSignalName)){
            throw new NoSuchGSignalException("GSignalManager:bad emitGSignal");
        }
        GSignalGroup gSignalGroup = gSignalGroups.get(obj);
        GSignal gSignal = gSignalGroup.getGSignal(gSignalName);
        gSignal.emit(paraType, paraValue);
    }

    public void emitGSignal(Object obj, String gSignalName) throws NoSuchGSignalException {
        if (!isGSignalExist(obj, gSignalName)){
            throw new NoSuchGSignalException("GSignalManager:bad emitGSignal");
        }
        GSignalGroup gSignalGroup = gSignalGroups.get(obj);
        GSignal gSignal = gSignalGroup.getGSignal(gSignalName);
        gSignal.emit();
    }

}

class GSignalGroup{
    private Object object = null;
    private HashMap<String, GSignal> gSignals = new HashMap<>();
    public GSignalGroup(Object object){
        this.object = object;
    }
    public void clearGSignals(){
        gSignals.clear();
    }

    public void addGSignal(String gSignalName){
        if (getGSignal(gSignalName) != null) {
            return;
        }
        gSignals.put(gSignalName, new GSignal(gSignalName));
    }

    public void removeGSignal(String gSignalName){
        gSignals.remove(gSignalName);
    }

    public GSignal getGSignal(String gSignalName){
        if (gSignals.containsKey(gSignalName)){
            return gSignals.get(gSignalName);
        }
        return null;
    }


}

class GSignal{
    private Object origin = null;
    private String gSignalName = "";
    private HashMap<Object, GSignalHandler> handlers = new HashMap<>();
    public GSignal(String gSignalName){
        this.gSignalName = gSignalName;
    }

    public void addHandlerFunction(Object handler, String function) throws NoSuchMethodException {
        if (!Reflex.hasFunction(handler.getClass(), function)){
            throw new NoSuchMethodException("GSignal:bad addHandlerFunction");
        }
        if (!handlers.containsKey(handler)){
            handlers.put(handler, new GSignalHandler(handler));
        }
        handlers.get(handler).addFunction(function);
    }

    public void addHandlerFunction(Object handler, String function, Class[] pareTypes) throws NoSuchMethodException {
        if (!Reflex.hasFunction(handler.getClass(), function, pareTypes)){
            throw new NoSuchMethodException("GSignal:bad addHandlerFunction");
        }
        if (!handlers.containsKey(handler)){
            handlers.put(handler, new GSignalHandler(handler));
        }
        handlers.get(handler).addFunction(function);
    }

    public void removeHandlerFunction(Object handler, String function){
        if (handlers.containsKey(handler)){
            handlers.get(handler).removeFunction(function);
        }
    }

    public void emit(Class[] paraTypes, Object[] paraValues){
        Iterator iterator = handlers.entrySet().iterator();
        while (iterator.hasNext()){
            HashMap.Entry entry = (HashMap.Entry) iterator.next();
            Object handler = entry.getKey();
            GSignalHandler gSignalHandler = (GSignalHandler) entry.getValue();
            gSignalHandler.callAll(paraTypes, paraValues);
        }
    }

    public void emit(Class paraType, Object paraValue){
        Iterator iterator = handlers.entrySet().iterator();
        while (iterator.hasNext()){
            HashMap.Entry entry = (HashMap.Entry) iterator.next();
            Object handler = entry.getKey();
            GSignalHandler gSignalHandler = (GSignalHandler) entry.getValue();
            gSignalHandler.callAll(paraType, paraValue);
        }
    }

    public void emit(){
        Iterator iterator = handlers.entrySet().iterator();
        while (iterator.hasNext()){
            HashMap.Entry entry = (HashMap.Entry) iterator.next();
            Object handler = entry.getKey();
            GSignalHandler gSignalHandler = (GSignalHandler) entry.getValue();
            gSignalHandler.callAll();
        }
    }

}

class GSignalHandler{
    private Object handler = null;
    private List<String> functions = new ArrayList<>();
    public GSignalHandler(Object handler){
        this.handler = handler;
    }
    public void addFunction(String function){
        if (functions.contains(function)){
            return;
        }
        functions.add(function);
    }
    public void removeFunction(String function){
        functions.remove(function);
    }

    public void callAll(Class[] paraTypes, Object[] paraValues){
        Iterator iterator = functions.listIterator();
        while (iterator.hasNext()){
            String function = (String) iterator.next();
            Reflex.invokeInstanceMethod(handler, function, paraTypes, paraValues);
        }
    }

    public void callAll(Class paraType, Object paraValue){
        Iterator iterator = functions.listIterator();
        while (iterator.hasNext()){
            String function = (String) iterator.next();
            Reflex.invokeInstanceMethod(handler, function, paraType, paraValue);
        }
    }

    public void callAll(){
        Iterator iterator = functions.listIterator();
        while (iterator.hasNext()){
            String function = (String) iterator.next();
            Reflex.invokeInstanceMethod(handler, function);
        }
    }
}

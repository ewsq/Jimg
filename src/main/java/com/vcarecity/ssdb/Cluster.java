package com.vcarecity.ssdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cluster {
    private String id;
    private SSDB master;
    private ConcurrentHashMap<String, SSDB> slaver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SSDB getMaster() {
        return master;
    }

    public void setMaster(SSDB master) {
        this.master = master;
    }

    public ConcurrentHashMap<String, SSDB> getSlaver() {
        return slaver;
    }

    public void setSlaver(ConcurrentHashMap<String, SSDB> slaver) {
        this.slaver = slaver;
    }

    public void release(){
        if(master!=null){
            master.release();
        }
        for(Map.Entry<String, SSDB> entry : slaver.entrySet()){
            String mapKey = entry.getKey();
            SSDB ssdb = entry.getValue();
            ssdb.release();
        }
    }

    public void destroy(){
        release();
    }
}

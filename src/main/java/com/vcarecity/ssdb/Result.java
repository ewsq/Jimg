package com.vcarecity.ssdb;

public class Result {
    private String clusterid;
    private String key;

    public Result(String clusterid,String key){
        this.clusterid=clusterid;
        this.key=key;
    }

    public String getClusterid() {
        return clusterid;
    }

    public void setClusterid(String clusterid) {
        this.clusterid = clusterid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "{" +
                "clusterid='" + clusterid + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

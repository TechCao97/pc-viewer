package cn.billycao.pcviewer.entity;

import lombok.Data;

@Data
public class ConfigPathItem {
    private String path;
    private boolean lock;

    public ConfigPathItem(String path, boolean lock) {
        this.path = path;
        this.lock = lock;
    }
}

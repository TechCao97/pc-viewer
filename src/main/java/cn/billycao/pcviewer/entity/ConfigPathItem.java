package cn.billycao.pcviewer.entity;

import lombok.Data;

@Data
public class ConfigPathItem {
    private String path;
    private Boolean lock;

    public ConfigPathItem(String path, Boolean lock) {
        this.path = path;
        this.lock = lock;
    }
}

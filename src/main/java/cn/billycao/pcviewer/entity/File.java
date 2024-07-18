package cn.billycao.pcviewer.entity;

import cn.billycao.pcviewer.enums.FileType;
import lombok.Data;

@Data
public class File {
    private String name;
    private FileType type;
    private boolean lock;

    public File(String name, FileType type) {
        this.name = name;
        this.type = type;
        this.lock = false;
    }

    public File(String name, FileType type, boolean lock) {
        this.name = name;
        this.type = type;
        this.lock = lock;
    }
}

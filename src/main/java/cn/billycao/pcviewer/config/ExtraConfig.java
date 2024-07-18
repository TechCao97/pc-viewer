package cn.billycao.pcviewer.config;

import cn.billycao.pcviewer.entity.ConfigPathItem;
import lombok.Data;
import java.util.List;

@Data
public class ExtraConfig {
    private List<ConfigPathItem> paths;
    private List<String> LegalPaths;
}

package cn.billycao.pcviewer.config;

import cn.billycao.pcviewer.entity.ConfigPathItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class Initializer {
    @Bean
    public ExtraConfig extraConfig() throws IOException {
        File config = new java.io.File("./config.json");
        ExtraConfig extraConfig;
        if (config.exists() && config.isFile()) {
            String json = Files.readAllLines(Paths.get("./config.json")).stream().collect(Collectors.joining(System.lineSeparator()));
            extraConfig = JSON.parseObject(json, ExtraConfig.class);
        } else {
            extraConfig = new ExtraConfig();
            extraConfig.setPaths(Collections.singletonList(new ConfigPathItem("C:", true)));
        }
        List<ConfigPathItem> paths = extraConfig.getPaths();
        List<String> list = new ArrayList<>();
        for (ConfigPathItem item : paths) {
            if (item.isLock()) {
                list.add(item.getPath());
            } else {
                String path = item.getPath();
                List<String> split = Stream.of(path.split("/")).filter(e -> !StringUtils.isEmpty(e)).collect(Collectors.toList());
                if (split.size() > 0) {
                    String cur = null;
                    for (String s : split) {
                        if (cur == null) {
                            cur = s;
                        } else {
                            cur = cur + "/" + s;
                        }
                        list.add(cur);
                    }
                }
            }
        }
        extraConfig.setLegalPaths(list);
        return extraConfig;
    }
}

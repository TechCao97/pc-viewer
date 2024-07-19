package cn.billycao.pcviewer.config;

import cn.billycao.pcviewer.entity.ConfigPathItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import org.apache.tomcat.util.net.WriteBuffer;
import org.thymeleaf.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class DirectoryConfig {
    private List<ConfigPathItem> paths;
    private List<String> LegalPaths;
    private static final String FILE_PATH = "./config.json";

    public DirectoryConfig(List<ConfigPathItem> paths) {
        this.paths = paths;
        List<String> list = new ArrayList<>();
        for (ConfigPathItem item : paths) {
            if (item.getLock()) {
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
        this.setLegalPaths(list);
    }

    public static DirectoryConfig init() {
        File config = new java.io.File(FILE_PATH);
        DirectoryConfig directoryConfig = null;
        if (config.exists() && config.isFile()) {
            try {
                String json = Files.readAllLines(Paths.get("./config.json")).stream().collect(Collectors.joining(System.lineSeparator()));
                JSONObject jo = JSON.parseObject(json);
                directoryConfig = new DirectoryConfig(JSON.parseObject(jo.getString("paths"), new TypeReference<List<ConfigPathItem>>() {
                }));
            } catch (IOException ignore) {
            }
        }
        if (directoryConfig == null) {
            directoryConfig = new DirectoryConfig(new ArrayList<>());
        }
        return directoryConfig;
    }

    public static void write(List<ConfigPathItem> paths) {
        JSONObject jo = new JSONObject();
        jo.put("paths", paths);
        File file = new java.io.File(FILE_PATH);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    throw new RuntimeException("create new file failed");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (OutputStreamWriter osw = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            osw.write(jo.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ConfigPathItem> copyData(List<ConfigPathItem> data) {
        return JSON.parseObject(JSON.toJSONString(data), new TypeReference<List<ConfigPathItem>>() {
        });
    }
}

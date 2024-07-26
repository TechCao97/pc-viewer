package cn.billycao.pcviewer.constant;

import cn.billycao.pcviewer.enums.FileType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileConstant {
    public static final List<String> IMAGE_EXTEND = Arrays.asList("jpg", "jpeg", "png", "gif");
    public static final List<String> VIDEO_EXTEND = Arrays.asList("mp4", "avi", "wmv", "mov", "flv", "mkv", "rmvb", "webm");

    public static Map<String, String> CONTENT_TYPE = new HashMap<String, String>() {{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("svg", "image/svg");
        put("bmp", "image/bmp");
        put("gif", "image/gif");
        put("webp", "image/webp");
        put("mp4", "video/mp4");
//        put("avi", "video/avi");
//        put("wmv", "video/wmv");
//        put("mov", "video/mov");
//        put("flv", "video/flv");
//        put("mkv", "video/mkv");
//        put("rmvb", "video/rmvb");
//        put("webm", "video/webm");
    }};

    public static final Map<FileType, Integer> TYPE_ORDER = new HashMap<FileType, Integer>() {{
        put(FileType.DIRECTORY, 0);
        put(FileType.IMAGE, 1);
        put(FileType.VIDEO, 2);
        put(FileType.FILE, 3);
    }};
}

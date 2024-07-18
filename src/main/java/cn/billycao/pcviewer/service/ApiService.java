package cn.billycao.pcviewer.service;

import cn.billycao.pcviewer.config.DirectoryConfig;
import cn.billycao.pcviewer.constant.FileConstant;
import cn.billycao.pcviewer.enums.FileType;
import cn.billycao.pcviewer.entity.File;
import cn.billycao.pcviewer.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Service
@AllArgsConstructor
public class ApiService {
    private DirectoryConfig config;

    public List<File> getRoots() {
        return config.getPaths().stream().map(e -> new File(e.getPath(), FileType.DIRECTORY, e.getLock())).collect(Collectors.toList());
    }

    public List<File> getMenu(String path) {
        java.io.File file = new java.io.File(path);
        if (!file.isDirectory()) {
            throw new RuntimeException("路径必须是文件夹");
        }
        java.io.File[] items = file.listFiles();
        List<File> list = new ArrayList<>();
        if (ArrayUtils.isEmpty(items)) return list;
        for (java.io.File item : items) {
            list.add(new File(item.getName(), getType(item)));
        }
        list.sort((next, cur) -> {
            Integer nextTypeOrder = FileConstant.TYPE_ORDER.get(next.getType());
            Integer curTypeOrder = FileConstant.TYPE_ORDER.get(cur.getType());
            if (nextTypeOrder < curTypeOrder) {
                return -1;
            } else if (nextTypeOrder > curTypeOrder) {
                return 1;
            } else {
                String nextName = next.getName();
                String curName = cur.getName();
                String pureNextName = nextName;
                String pureCurName = curName;
                if (this.getExtend(nextName) != null) {
                    pureNextName = nextName.replace("." + this.getExtend(nextName), "");
                }
                if (this.getExtend(curName) != null) {
                    pureCurName = curName.replace("." + this.getExtend(curName), "");
                }
                Integer nextInt = this.parseInt(pureNextName);
                Integer curInt = this.parseInt(pureCurName);
                if (nextInt == null && curInt == null) {
                    return nextName.compareTo(curName);
                } else if (nextInt == null) {
                    return 1;
                } else if (curInt == null) {
                    return -1;
                } else {
                    return nextInt.compareTo(curInt);
                }
            }
        });
        return list;
    }

    private Integer parseInt(String name) {
        try {
            return Integer.parseInt(name);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public FileType getType(java.io.File file) {
        if (file.isDirectory()) return FileType.DIRECTORY;
        String name = file.getName();
        String extend = getExtend(name);
        if (extend == null) {
            return FileType.FILE;
        }
        if (FileConstant.IMAGE_EXTEND.contains(extend.toLowerCase())) return FileType.IMAGE;
        if (FileConstant.VIDEO_EXTEND.contains(extend.toLowerCase())) return FileType.VIDEO;
        return FileType.FILE;
    }

    public String getExtend(String name) {
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return name.substring(index + 1);
    }

    public void download(String path, HttpServletResponse response, HttpServletRequest request) throws IOException {
        //普通文件输入流
        FileInputStream in = null;
        //视频文件使用随机输入流
        RandomAccessFile inRandom = null;
        OutputStream toClient = null;
        try {
            // path是指欲下载的文件的路径。
            java.io.File file = new java.io.File(path);
            // 取得文件名。
            String name = java.net.URLEncoder.encode(file.getName(), "UTF-8");

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");

            toClient = response.getOutputStream();
            String contentType = FileConstant.CONTENT_TYPE.get(this.getExtend(name).toLowerCase());
            if (contentType == null) {
                response.addHeader("Content-Disposition", "attachment;filename=" + name);
                contentType = "application/octet-stream";
            }

            //输入流缓冲数组
            byte[] buf = new byte[5120];
            //请求内容类型
            response.setContentType(contentType);

            //如果请求头包含range（比如视频），只返回对应位置的流并进行响应
            String rangeString = request.getHeader("Range");
            if (rangeString != null && rangeString.trim().length() > 0 && !"null".equals(rangeString)) {
                long start = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
                response.addHeader("Accept-Ranges", "bytes");
                response.addHeader("Content-Range", "bytes " + start + "-" + (file.length() - 1) + "/" + file.length());
                response.addHeader("Content-Length", "" + (file.length() - start));
                //断点传输下载视频返回206
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                inRandom = new RandomAccessFile(file, "r");
                inRandom.seek(start);
                int len;
                while ((len = inRandom.read(buf)) != -1) {
                    try {
                        toClient.write(buf, 0, len);
                    } catch (IOException e) {
                        break;
                    }
                }
            } else {
                response.setHeader("Content-Length", "" + file.length());
                in = FileUtils.openInputStream(file);
                int len;
                while ((len = in.read(buf)) != -1) {
                    try {
                        toClient.write(buf, 0, len);
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (inRandom != null) {
                inRandom.close();
            }
            if (toClient != null) {
                try {
                    toClient.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public boolean validate(String path) {
        if (StringUtils.isEmpty(path)) return true;
        List<String> legalPaths = config.getLegalPaths();
        for (String legalPath : legalPaths) {
            if (path.startsWith(legalPath)) {
                return true;
            }
        }
        return false;
    }
}

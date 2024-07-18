package cn.billycao.pcviewer.controller;

import cn.billycao.pcviewer.service.ApiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ApiController {
    private ApiService service;

    @GetMapping(value = "/index")
    public ModelAndView getIndex(@RequestParam(value = "path", required = false) String path, @RequestParam(value = "lock", required = false) String lock) {
        if (!service.validate(path)) throw new RuntimeException("非法路径");
        ModelAndView mv = new ModelAndView("index");
        Map<String, Object> model = mv.getModel();
        if (StringUtils.isEmpty(path)) {
            model.put("menu", service.getRoots());
        } else {
            model.put("menu", service.getMenu(path));
        }
        model.put("path", path == null ? "" : path);
        model.put("lock", lock == null ? "" : lock);
        return mv;
    }

    //文件下载：图片直接打开，视频断点播放，其他出现下载
    @GetMapping(value = "/download")
    public void download(@RequestParam("path") String path, HttpServletResponse response, HttpServletRequest request) throws IOException {
        service.download(path, response, request);
    }
}

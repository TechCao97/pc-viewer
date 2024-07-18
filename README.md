# 概述

这是一款方便 移动设备 查看同一局域网内 PC 文件的轻量化应用，使用java8 + springboot书写。

初衷是我下了很多媒体资源在电脑上，每次希望在手机上浏览这些图片或视频资源都极为不方便，还要拷来拷去，就算小米有自带的FTP服务也需要在电脑上进行拷贝操作，最后受不了脑子一拍干脆自己写一个，分享给有同样苦恼的朋友。

# 快速开始

你只需要双击jar包（需要java8环境）或运行 cn.billycao.pcviewer.frame.IndexFrame 下的主方法，就能启动一个简单的客户端（code by swing）。上方配置希望访问的电脑路径，点击启动按钮，将下方的http路径传给手机，最后在手机浏览器中打开，就可以简单的使用手机访问电脑目录。大部分图片以及MP4视频可以直接播放，其他格式文件（通过后缀名判断）只能提供下载。客户端如图：

![image-gui](https://github.com/TechCao97/pc-viewer/blob/main/assets/gui.png)

路径配置后方的lock选项表示是否锁定上级目录，例如 "C:/example/target" 勾选lock后，将不能直接访问 "C:" 或者 "C:/example"，否则允许访问。
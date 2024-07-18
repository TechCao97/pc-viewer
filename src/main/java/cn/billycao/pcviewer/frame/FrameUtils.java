package cn.billycao.pcviewer.frame;

import java.awt.*;
import java.util.function.BiFunction;

public class FrameUtils {
    public static Point screenSize;

    static  {
        // 获取默认工具包
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // 获取屏幕的尺寸
        Dimension screenSize = toolkit.getScreenSize();
        FrameUtils.screenSize = new Point((int) screenSize.getWidth(), (int) screenSize.getHeight());
    }

    public static Point getCenterPosition(Point size) {
        return  new Point((screenSize.x - size.x) / 2, (screenSize.y - size.y) / 2);
    }

    public static Point getCenterTopPosition(Point size, Integer percent) {
        int spare = (screenSize.y - size.y) / 2;
        int offsetY = (int) (spare * (1 - percent / 100.0));
        return new Point((screenSize.x - size.x) / 2, offsetY);
    }
}

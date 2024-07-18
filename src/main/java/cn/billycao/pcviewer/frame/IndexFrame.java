package cn.billycao.pcviewer.frame;

import cn.billycao.pcviewer.PcViewerApplication;
import cn.billycao.pcviewer.config.DirectoryConfig;
import cn.billycao.pcviewer.entity.ConfigPathItem;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class IndexFrame extends JFrame {
    private DirectoryConfig config;
    public static int contentWith = 500;
    public static int contentHeight = 40;
    public static int contentGap = 20;
    private StartButton btnStart;
    private ConfigurableApplicationContext springContext;

    public IndexFrame(DirectoryConfig config) {
        this.config = config;
        this.initFrame();
    }

    public void initFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("PcViewer");
        JPanel panel = new JPanel();
        Point size = new Point(contentWith + contentGap * 2, 380);
        panel.setPreferredSize(new Dimension(size.x, size.y));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, contentGap, contentGap));

        ConfigTable tblConfig = new ConfigTable(this);
        tblConfig.addTo(panel);

        JTextField txfIp = new JTextField(getPath());
        txfIp.setEditable(false);
        txfIp.setPreferredSize(new Dimension(contentWith, contentHeight));
        panel.add(txfIp);

        btnStart = new StartButton(e -> {
            // 使用 SpringApplicationBuilder 构建 Spring Boot 应用程序
            this.springContext = new SpringApplicationBuilder()
                    .sources(PcViewerApplication.class) // 指定启动类
                    .initializers((applicationContext) -> applicationContext.getBeanFactory().registerSingleton("directoryConfig", config)) // 将 config 对象注册到容器中
                    .run(); // 启动 Spring Boot 应用程序
            SwingUtilities.invokeLater(() -> this.btnStart.setLoading(false));
        }, e -> {
            this.springContext.close();
            SwingUtilities.invokeLater(() -> this.btnStart.setLoading(false));
        });
        btnStart.setPreferredSize(new Dimension(contentWith, contentHeight));
        panel.add(btnStart);

        Point position = FrameUtils.getCenterTopPosition(size, 15);
        this.setLocation(position);
        this.add(panel);
        this.pack();
    }

    public String getPath() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            return "http://" + ip + ":8100/viewer/index";
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public void setConfig(DirectoryConfig config) {
        this.config = config;
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        //加载swing皮肤
        FlatLightLaf.setup();
        UIManager.setLookAndFeel(new FlatDarkLaf());
        //统一设置字体
        FontUIResource fontRes = new FontUIResource(new Font("微软雅黑", Font.PLAIN, 16));
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }

        IndexFrame frame = new IndexFrame(DirectoryConfig.init());
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}

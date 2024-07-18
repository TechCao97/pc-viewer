package cn.billycao.pcviewer.frame;

import cn.billycao.pcviewer.PcViewerApplication;
import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class IndexFrame extends JFrame {
    private JTextField txfIp;
    private StartButton btnStart;
    private ConfigurableApplicationContext springContext;

    public IndexFrame() {
        this.initFrame();
    }

    public void initFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("PcViewer");
        JPanel panel = new JPanel();
        Point size = new Point(360, 140);
        panel.setPreferredSize(new Dimension(size.x, size.y));
        panel.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 20));

        txfIp = new JTextField(getPath());
        txfIp.setEditable(false);
        btnStart = new StartButton(e -> {
            this.springContext = SpringApplication.run(PcViewerApplication.class);
            SwingUtilities.invokeLater(() -> this.btnStart.setLoading(false));
        }, e -> {
            this.springContext.close();
            SwingUtilities.invokeLater(() -> this.btnStart.setLoading(false));
        });
        txfIp.setPreferredSize(new Dimension(320, 40));
        btnStart.setPreferredSize(new Dimension(320, 40));
        panel.add(txfIp);
        panel.add(btnStart);

        Point position = FrameUtils.getCenterTopPosition(size, 20);
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

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        //加载swing皮肤
        FlatLightLaf.install();
        UIManager.setLookAndFeel(new FlatLightLaf());
        //统一设置字体
        FontUIResource fontRes = new FontUIResource(new Font("微软雅黑", Font.PLAIN, 16));
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }

        IndexFrame frame = new IndexFrame();
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}

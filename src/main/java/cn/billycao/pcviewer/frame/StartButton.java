package cn.billycao.pcviewer.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.function.Consumer;

public class StartButton extends JButton {
    private final ImageIcon icon;
    private Status status;

    public StartButton(Consumer<ActionEvent> startEvent, Consumer<ActionEvent> endEvent) {
        super("启动");
        this.status = Status.START;
        this.icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/static/image/loading.gif")));
        this.icon.setImage(this.icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        this.setDisabledIcon(icon);
        this.addActionListener(event -> {
            SwingUtilities.invokeLater(() -> this.setLoading(true));
            new Thread(() -> {
                if (this.status == Status.START) {
                    startEvent.accept(event);
                } else {
                    endEvent.accept(event);
                }
            }).start();
        });
    }

    public void setLoading(boolean flag) {
        if (flag) {
            this.setText(null);
            this.setIcon(this.icon);
            this.setDisabledIcon(icon);
            this.setEnabled(false);
        } else {
            Status status = getNextStatus();
            this.setStatus(status);
            this.setText(status.name);
            this.setIcon(null);
            this.setEnabled(true);
        }
    }

    public Status getNextStatus() {
        return this.status == Status.START ? Status.END : Status.START;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private static enum Status {
        START("启动"), END("终止");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

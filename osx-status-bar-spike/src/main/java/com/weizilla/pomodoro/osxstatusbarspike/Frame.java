package com.weizilla.pomodoro.osxstatusbarspike;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Frame extends JFrame
{
    private int num = 0;
    private JPanel mainPanel;
    private BufferedImage image;
    private Graphics2D g2d;
    private SystemTray systemTray;
    private TrayIcon trayIcon;

    public Frame()
    {
        super("Hello");
        try
        {
            createPanel();
            createButton();
            createImage();
            createTray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createPanel()
    {
        mainPanel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private void createButton()
    {
        JButton button = new JButton("Increase");
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                increment();
            }
        });
        mainPanel.add(button);
    }

    private void increment()
    {
        num++;
        System.out.println("increment " + num);
        refreshImage();
        repaint();
        refreshTray();
    }

    private void createImage() throws Exception
    {
        image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        refreshImage();
    }

    private void refreshImage()
    {
        g2d = image.createGraphics();
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, 100, 100);
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setPaint(Color.black);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2d.drawString(String.valueOf(num), 0, 12);
        g2d.dispose();
    }

    private void createTray() throws Exception
    {
        systemTray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        systemTray.add(trayIcon);
        refreshTray();
    }

    private void refreshTray()
    {
        trayIcon.setImage(image);
    }


}

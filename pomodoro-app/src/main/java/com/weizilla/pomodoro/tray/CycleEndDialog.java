package com.weizilla.pomodoro.tray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CycleEndDialog extends JFrame
{
    public enum Result
    {
        YES, NO
    }

    private final ResultsListener listener;
    private final JLabel label;

    public CycleEndDialog(ResultsListener listener)
    {
        super("Pomodoro");
        this.listener = listener;
        label = new JLabel();

        setAlwaysOnTop(true);

        getContentPane().add(label, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        getContentPane().add(buttons, BorderLayout.SOUTH);

        JButton yes = new JButton("Yes");
        buttons.add(yes);
        yes.setSelected(true);
        yes.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                returnResults(Result.YES);
            }
        });

        JButton no = new JButton("No");
        no.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                returnResults(Result.NO);
            }
        });
        buttons.add(no);
    }

    public void showMessage(String message)
    {
        label.setText(message);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void returnResults(Result result)
    {
        listener.dialogResult(result);
    }

    public interface ResultsListener
    {
        void dialogResult(Result result);
    }

}

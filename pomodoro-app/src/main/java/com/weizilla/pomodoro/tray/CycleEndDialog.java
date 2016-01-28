package com.weizilla.pomodoro.tray;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

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
        yes.addActionListener(e -> returnResults(Result.YES));

        JButton no = new JButton("No");
        no.addActionListener(e -> returnResults(Result.NO));
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

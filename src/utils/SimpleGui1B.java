package utils;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleGui1B implements ActionListener {
    JButton mButton;
    public static void main(String[] args) {
        SimpleGui1B gui = new SimpleGui1B();
        gui.go();
    }

    private void go() {
        JFrame frame = new JFrame();
        mButton = new JButton("click me");
        mButton.addActionListener(this);
        frame.getContentPane().add(mButton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mButton.setText("I've been clicked!");
    }
}

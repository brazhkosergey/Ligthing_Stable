package ui.setting;

import entity.Storage.Storage;
import ui.main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SarcophagusSettingPanel extends JPanel {
    private JPanel mainPanel;

    private JTextField firstCameraLeftRightTextField;
    private JTextField firstCameraUpDownTextField;

    private JTextField secondCameraLeftRightTextField;
    private JTextField secondCameraUpDownTextField;

    private JTextField thirdCameraLeftRightTextField;
    private JTextField thirdCameraUpDownTextField;

    private JTextField fourthCameraLeftRightTextField;
    private JTextField fourthCameraUpDownTextField;
    MyKeyAdapter keyAdapter;

    private int[][] position;

    public SarcophagusSettingPanel(int[][] position) {
        this.position = position;
        this.setLayout(new BorderLayout(2, 2));
        mainPanel = new JPanel(new BorderLayout(3, 3));
        keyAdapter = new MyKeyAdapter();
        buildPanel();
        this.add(mainPanel);
    }

    private void buildPanel() {
        Font commonFont = new Font(null, Font.BOLD, 25);
        JPanel northOutPanel = new JPanel(new BorderLayout());
        JPanel firstCameraPanel = new JPanel(new GridLayout(2, 2));
        firstCameraPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel firstCameraLabel = new JLabel("Camera 1");
        firstCameraLabel.setPreferredSize(new Dimension(100, 100));
        firstCameraLabel.setHorizontalAlignment(SwingConstants.CENTER);
        firstCameraLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        firstCameraPanel.add(firstCameraLabel);

        JLabel firstCameraLeft = new JLabel("\u21D0");
        JLabel firstCameraRight = new JLabel("\u21D2");
        JLabel firstCameraUp = new JLabel("\u21D1");
        JLabel firstCameraDown = new JLabel("\u21D3");

        firstCameraLeft.setFont(commonFont);
        firstCameraRight.setFont(commonFont);
        firstCameraUp.setFont(commonFont);
        firstCameraDown.setFont(commonFont);

        firstCameraUp.setAlignmentX(CENTER_ALIGNMENT);
        firstCameraDown.setAlignmentX(CENTER_ALIGNMENT);

        firstCameraLeftRightTextField = new JTextField();
        firstCameraLeftRightTextField.setMaximumSize(new Dimension(40, 25));
        firstCameraLeftRightTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel firstCameraLeftRightTextFieldPanel = new JPanel();
        firstCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(firstCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        firstCameraLeftRightTextFieldPanel.add(firstCameraLeft);
        firstCameraLeftRightTextFieldPanel.add(firstCameraLeftRightTextField);
        firstCameraLeftRightTextFieldPanel.add(firstCameraRight);

        firstCameraUpDownTextField = new JTextField();
        firstCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        firstCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel firstCameraUpDownTextFieldPanel = new JPanel();
        firstCameraUpDownTextFieldPanel.setLayout(new BoxLayout(firstCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        firstCameraUpDownTextFieldPanel.add(firstCameraUp);
        firstCameraUpDownTextFieldPanel.add(firstCameraUpDownTextField);
        firstCameraUpDownTextFieldPanel.add(firstCameraDown);
        JLabel firstCameraLastLabel = new JLabel("\u21D8");
        firstCameraLastLabel.setFont(commonFont);
        firstCameraLastLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        firstCameraLastLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        firstCameraPanel.add(firstCameraLabel);
        firstCameraPanel.add(firstCameraLeftRightTextFieldPanel);
        firstCameraPanel.add(firstCameraUpDownTextFieldPanel);
        firstCameraPanel.add(firstCameraLastLabel);

//================================================================================
        JPanel secondCameraPanel = new JPanel(new GridLayout(2, 2));
        secondCameraPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel secondCameraLabel = new JLabel("Camera 2");
        secondCameraLabel.setPreferredSize(new Dimension(100, 100));
        secondCameraLabel.setHorizontalAlignment(SwingConstants.CENTER);
        secondCameraLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        secondCameraPanel.add(secondCameraLabel);

        JLabel secondCameraLeft = new JLabel("\u21D0");
        JLabel secondCameraRight = new JLabel("\u21D2");
        JLabel secondCameraUp = new JLabel("\u21D1");
        JLabel secondCameraDown = new JLabel("\u21D3");

        secondCameraLeft.setFont(commonFont);
        secondCameraRight.setFont(commonFont);
        secondCameraUp.setFont(commonFont);
        secondCameraDown.setFont(commonFont);

        secondCameraUp.setAlignmentX(CENTER_ALIGNMENT);
        secondCameraDown.setAlignmentX(CENTER_ALIGNMENT);

        secondCameraLeftRightTextField = new JTextField();
        secondCameraLeftRightTextField.setMaximumSize(new Dimension(40, 25));
        secondCameraLeftRightTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel secondCameraLeftRightTextFieldPanel = new JPanel();
        secondCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(secondCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        secondCameraLeftRightTextFieldPanel.add(secondCameraLeft);
        secondCameraLeftRightTextFieldPanel.add(secondCameraLeftRightTextField);
        secondCameraLeftRightTextFieldPanel.add(secondCameraRight);


        secondCameraUpDownTextField = new JTextField();
        secondCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        secondCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);


        JPanel secondCameraUpDownTextFieldPanel = new JPanel();
        secondCameraUpDownTextFieldPanel.setLayout(new BoxLayout(secondCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        secondCameraUpDownTextFieldPanel.add(secondCameraUp);
        secondCameraUpDownTextFieldPanel.add(secondCameraUpDownTextField);
        secondCameraUpDownTextFieldPanel.add(secondCameraDown);
        JLabel secondCameraLastLabel = new JLabel("\u21D9");
        secondCameraLastLabel.setFont(commonFont);
        secondCameraLastLabel.setHorizontalAlignment(SwingConstants.LEFT);
        secondCameraLastLabel.setVerticalAlignment(SwingConstants.BOTTOM);


        secondCameraPanel.add(secondCameraLeftRightTextFieldPanel);
        secondCameraPanel.add(secondCameraLabel);
        secondCameraPanel.add(secondCameraLastLabel);
        secondCameraPanel.add(secondCameraUpDownTextFieldPanel);

        northOutPanel.add(firstCameraPanel, BorderLayout.WEST);
        northOutPanel.add(secondCameraPanel, BorderLayout.EAST);


//        ========================================================================
//        ========================================================================
        JPanel southOutPanel = new JPanel(new BorderLayout());

        JPanel thirdCameraPanel = new JPanel(new GridLayout(2, 2));
        thirdCameraPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel thirdCameraLabel = new JLabel("Camera 3");
        thirdCameraLabel.setPreferredSize(new Dimension(100, 100));
        thirdCameraLabel.setHorizontalAlignment(SwingConstants.CENTER);
        thirdCameraLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        thirdCameraPanel.add(thirdCameraLabel);

        JLabel thirdCameraLeft = new JLabel("\u21D0");
        JLabel thirdCameraRight = new JLabel("\u21D2");
        JLabel thirdCameraUp = new JLabel("\u21D1");
        JLabel thirdCameraDown = new JLabel("\u21D3");

        thirdCameraLeft.setFont(commonFont);
        thirdCameraRight.setFont(commonFont);
        thirdCameraUp.setFont(commonFont);
        thirdCameraDown.setFont(commonFont);

        thirdCameraUp.setAlignmentX(CENTER_ALIGNMENT);
        thirdCameraDown.setAlignmentX(CENTER_ALIGNMENT);

        thirdCameraLeftRightTextField = new JTextField();
        thirdCameraLeftRightTextField.setMaximumSize(new Dimension(40, 25));
        thirdCameraLeftRightTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel thirdCameraLeftRightTextFieldPanel = new JPanel();
        thirdCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(thirdCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        thirdCameraLeftRightTextFieldPanel.add(thirdCameraLeft);
        thirdCameraLeftRightTextFieldPanel.add(thirdCameraLeftRightTextField);
        thirdCameraLeftRightTextFieldPanel.add(thirdCameraRight);


        thirdCameraUpDownTextField = new JTextField();
        thirdCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        thirdCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel thirdCameraUpDownTextFieldPanel = new JPanel();
        thirdCameraUpDownTextFieldPanel.setLayout(new BoxLayout(thirdCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        thirdCameraUpDownTextFieldPanel.add(thirdCameraUp);
        thirdCameraUpDownTextFieldPanel.add(thirdCameraUpDownTextField);
        thirdCameraUpDownTextFieldPanel.add(thirdCameraDown);
        JLabel thirdCameraLastLabel = new JLabel("\u21D7");
        thirdCameraLastLabel.setFont(commonFont);
        thirdCameraLastLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        thirdCameraLastLabel.setVerticalAlignment(SwingConstants.TOP);


        thirdCameraPanel.add(thirdCameraUpDownTextFieldPanel);
        thirdCameraPanel.add(thirdCameraLastLabel);
        thirdCameraPanel.add(thirdCameraLabel);
        thirdCameraPanel.add(thirdCameraLeftRightTextFieldPanel);

//================================================================================
        JPanel fourthCameraPanel = new JPanel(new GridLayout(2, 2));
        fourthCameraPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel fourthCameraLabel = new JLabel("Camera 4");
        fourthCameraLabel.setPreferredSize(new Dimension(100, 100));
        fourthCameraLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fourthCameraLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        fourthCameraPanel.add(fourthCameraLabel);

        JLabel fourthCameraLeft = new JLabel("\u21D0");
        JLabel fourthCameraRight = new JLabel("\u21D2");
        JLabel fourthCameraUp = new JLabel("\u21D1");
        JLabel fourthCameraDown = new JLabel("\u21D3");

        fourthCameraLeft.setFont(commonFont);
        fourthCameraRight.setFont(commonFont);
        fourthCameraUp.setFont(commonFont);
        fourthCameraDown.setFont(commonFont);

        fourthCameraUp.setAlignmentX(CENTER_ALIGNMENT);
        fourthCameraDown.setAlignmentX(CENTER_ALIGNMENT);

        fourthCameraLeftRightTextField = new JTextField();
        fourthCameraLeftRightTextField.setMaximumSize(new Dimension(40, 25));
        fourthCameraLeftRightTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel fourthCameraLeftRightTextFieldPanel = new JPanel();
        fourthCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(fourthCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        fourthCameraLeftRightTextFieldPanel.add(fourthCameraLeft);
        fourthCameraLeftRightTextFieldPanel.add(fourthCameraLeftRightTextField);
        fourthCameraLeftRightTextFieldPanel.add(fourthCameraRight);


        fourthCameraUpDownTextField = new JTextField();
        fourthCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        fourthCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel fourthCameraUpDownTextFieldPanel = new JPanel();
        fourthCameraUpDownTextFieldPanel.setLayout(new BoxLayout(fourthCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        fourthCameraUpDownTextFieldPanel.add(fourthCameraUp);
        fourthCameraUpDownTextFieldPanel.add(fourthCameraUpDownTextField);
        fourthCameraUpDownTextFieldPanel.add(fourthCameraDown);
        JLabel fourthCameraLastLabel = new JLabel("\u21D6");
        fourthCameraLastLabel.setFont(commonFont);
        fourthCameraLastLabel.setHorizontalAlignment(SwingConstants.LEFT);
        fourthCameraLastLabel.setVerticalAlignment(SwingConstants.TOP);


        fourthCameraPanel.add(fourthCameraLastLabel);
        fourthCameraPanel.add(fourthCameraUpDownTextFieldPanel);
        fourthCameraPanel.add(fourthCameraLeftRightTextFieldPanel);
        fourthCameraPanel.add(fourthCameraLabel);

        southOutPanel.add(thirdCameraPanel, BorderLayout.WEST);
        southOutPanel.add(fourthCameraPanel, BorderLayout.EAST);


        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel sarcophagusLabel = new JLabel();
        sarcophagusLabel.setText(Storage.getBundle().getString("sarcophagus"));
        sarcophagusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sarcophagusLabel.setVerticalAlignment(SwingConstants.CENTER);
        sarcophagusLabel.setFont(commonFont);
        centerPanel.add(sarcophagusLabel, BorderLayout.CENTER);


        Font buttonFont = new Font(null, Font.BOLD, 50);

        JButton saveButton = new JButton("<html>&#128190</html>");
        saveButton.setFont(buttonFont);
        saveButton.addActionListener((af) -> {
            int[][] position = new int[][]{new int[2],new int[2],new int[2],new int[2]};
            position[0][0]=Integer.parseInt(firstCameraLeftRightTextField.getText());
            position[0][1]=Integer.parseInt(firstCameraUpDownTextField.getText());
            position[1][0]=Integer.parseInt(secondCameraLeftRightTextField.getText());
            position[1][1]=Integer.parseInt(secondCameraUpDownTextField.getText());

            position[2][0]=Integer.parseInt(thirdCameraLeftRightTextField.getText());
            position[2][1]=Integer.parseInt(thirdCameraUpDownTextField.getText());

            position[3][0]=Integer.parseInt(fourthCameraLeftRightTextField.getText());
            position[3][1]=Integer.parseInt(fourthCameraUpDownTextField.getText());
            Storage.getAddressSaver().setCamerasPosition(position);
            saveButton.setForeground(new Color(47, 123, 21));
        });

        JButton backButton = new JButton("<html>&#11178</html>");
        backButton.setFont(buttonFont);
        backButton.addActionListener((as) -> {
            Setting.getSetting().backToSetting();
            saveButton.setForeground(Color.DARK_GRAY);
        });


        firstCameraLeftRightTextField.addKeyListener(new MyKeyAdapter());

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setPreferredSize(new Dimension(200, 40));
        backPanel.add(backButton);




        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        savePanel.setPreferredSize(new Dimension(200, 40));
        savePanel.add(saveButton);

        setPosition(position);
        mainPanel.add(northOutPanel, BorderLayout.NORTH);
        mainPanel.add(southOutPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(backPanel, BorderLayout.WEST);
        mainPanel.add(savePanel, BorderLayout.EAST);
    }

    public void setPosition(int[][] position) {
        firstCameraLeftRightTextField.setText(String.valueOf(position[0][0]));
        firstCameraUpDownTextField.setText(String.valueOf(position[0][1]));

        secondCameraLeftRightTextField.setText(String.valueOf(position[1][0]));
        secondCameraUpDownTextField.setText(String.valueOf(position[1][1]));

        thirdCameraLeftRightTextField.setText(String.valueOf(position[2][0]));
        thirdCameraUpDownTextField.setText(String.valueOf(position[2][1]));

        fourthCameraLeftRightTextField.setText(String.valueOf(position[3][0]));
        fourthCameraUpDownTextField.setText(String.valueOf(position[3][1]));

        firstCameraLeftRightTextField.addKeyListener(keyAdapter);
        firstCameraUpDownTextField.addKeyListener(keyAdapter);
        secondCameraLeftRightTextField.addKeyListener(keyAdapter);
        secondCameraUpDownTextField.addKeyListener(keyAdapter);
        thirdCameraLeftRightTextField.addKeyListener(keyAdapter);
        thirdCameraUpDownTextField.addKeyListener(keyAdapter);
        fourthCameraLeftRightTextField.addKeyListener(keyAdapter);
        fourthCameraUpDownTextField.addKeyListener(keyAdapter);
    }


    private class MyKeyAdapter extends KeyAdapter {
        char[] chars;

        public MyKeyAdapter() {
            chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char keyChar = e.getKeyChar();
            boolean contain = false;
            for (char c : chars) {
                if (c == keyChar) {
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                e.consume();
            }
        }
    }

}

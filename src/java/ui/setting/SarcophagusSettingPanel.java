package ui.setting;

import entity.Storage.Storage;

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


    private JTextField firstCameraHideZoneDistanceTextField;
    private JTextField secondCameraHideZoneDistanceTextField;
    private JTextField thirdCameraHideZoneDistanceTextField;
    private JTextField fourthCameraHideZoneDistanceTextField;


    private MyKeyAdapter keyAdapter;

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

        JLabel h1Label = new JLabel("h1");
        h1Label.setPreferredSize(new Dimension(40, 25));
        h1Label.setHorizontalAlignment(SwingConstants.CENTER);
        h1Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel firstCameraLeftRightInnerTextFieldPanel = new JPanel();
        firstCameraLeftRightInnerTextFieldPanel.setLayout(new BoxLayout(firstCameraLeftRightInnerTextFieldPanel, BoxLayout.Y_AXIS));
        firstCameraLeftRightInnerTextFieldPanel.add(h1Label);
        firstCameraLeftRightInnerTextFieldPanel.add(firstCameraLeftRightTextField);


        JPanel firstCameraLeftRightTextFieldPanel = new JPanel();
        firstCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(firstCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        firstCameraLeftRightTextFieldPanel.add(firstCameraLeft);
        firstCameraLeftRightTextFieldPanel.add(firstCameraLeftRightInnerTextFieldPanel);
        firstCameraLeftRightTextFieldPanel.add(firstCameraRight);

        firstCameraUpDownTextField = new JTextField();
        firstCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        firstCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel v1Label = new JLabel("v1 ");
        v1Label.setHorizontalAlignment(SwingConstants.CENTER);
        v1Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel firstCameraUpDownInnerTextFieldPanel = new JPanel();
        firstCameraUpDownInnerTextFieldPanel.setLayout(new BoxLayout(firstCameraUpDownInnerTextFieldPanel, BoxLayout.X_AXIS));
        firstCameraUpDownInnerTextFieldPanel.add(v1Label);
        firstCameraUpDownInnerTextFieldPanel.add(firstCameraUpDownTextField);


        JPanel firstCameraUpDownTextFieldPanel = new JPanel();
        firstCameraUpDownTextFieldPanel.setLayout(new BoxLayout(firstCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        firstCameraUpDownTextFieldPanel.add(firstCameraUp);
        firstCameraUpDownTextFieldPanel.add(firstCameraUpDownInnerTextFieldPanel);
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


        JLabel h2Label = new JLabel("h2");
        h2Label.setPreferredSize(new Dimension(40, 25));
        h2Label.setHorizontalAlignment(SwingConstants.CENTER);
        h2Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel secondCameraLeftRightInnerTextFieldPanel = new JPanel();
        secondCameraLeftRightInnerTextFieldPanel.setLayout(new BoxLayout(secondCameraLeftRightInnerTextFieldPanel, BoxLayout.Y_AXIS));
        secondCameraLeftRightInnerTextFieldPanel.add(h2Label);
        secondCameraLeftRightInnerTextFieldPanel.add(secondCameraLeftRightTextField);

        JPanel secondCameraLeftRightTextFieldPanel = new JPanel();
        secondCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(secondCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        secondCameraLeftRightTextFieldPanel.add(secondCameraLeft);
        secondCameraLeftRightTextFieldPanel.add(secondCameraLeftRightInnerTextFieldPanel);
        secondCameraLeftRightTextFieldPanel.add(secondCameraRight);


        secondCameraUpDownTextField = new JTextField();
        secondCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        secondCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel v2Label = new JLabel("v2 ");
        v2Label.setHorizontalAlignment(SwingConstants.CENTER);
        v2Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel secondCameraUpDownInnerTextFieldPanel = new JPanel();
        secondCameraUpDownInnerTextFieldPanel.setLayout(new BoxLayout(secondCameraUpDownInnerTextFieldPanel, BoxLayout.X_AXIS));
        secondCameraUpDownInnerTextFieldPanel.add(v2Label);
        secondCameraUpDownInnerTextFieldPanel.add(secondCameraUpDownTextField);

        JPanel secondCameraUpDownTextFieldPanel = new JPanel();
        secondCameraUpDownTextFieldPanel.setLayout(new BoxLayout(secondCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        secondCameraUpDownTextFieldPanel.add(secondCameraUp);
        secondCameraUpDownTextFieldPanel.add(secondCameraUpDownInnerTextFieldPanel);
        secondCameraUpDownTextFieldPanel.add(secondCameraDown);
        JLabel secondCameraLastLabel = new JLabel("\u21D9");
        secondCameraLastLabel.setFont(commonFont);
        secondCameraLastLabel.setHorizontalAlignment(SwingConstants.LEFT);
        secondCameraLastLabel.setVerticalAlignment(SwingConstants.BOTTOM);


        secondCameraPanel.add(secondCameraLeftRightTextFieldPanel);
        secondCameraPanel.add(secondCameraLabel);
        secondCameraPanel.add(secondCameraLastLabel);
        secondCameraPanel.add(secondCameraUpDownTextFieldPanel);


        JLabel northLabel = new JLabel(Storage.getBundle().getString("north"));
        northLabel.setFont(commonFont);
        northLabel.setHorizontalAlignment(SwingConstants.CENTER);
        northLabel.setVerticalAlignment(SwingConstants.CENTER);


        northOutPanel.add(firstCameraPanel, BorderLayout.WEST);
        northOutPanel.add(northLabel, BorderLayout.CENTER);
        northOutPanel.add(secondCameraPanel, BorderLayout.EAST);


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


        JLabel h3Label = new JLabel("h3");
        h3Label.setPreferredSize(new Dimension(40, 25));
        h3Label.setHorizontalAlignment(SwingConstants.CENTER);
        h3Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel thirdCameraLeftRightInnerTextFieldPanel = new JPanel();
        thirdCameraLeftRightInnerTextFieldPanel.setLayout(new BoxLayout(thirdCameraLeftRightInnerTextFieldPanel, BoxLayout.Y_AXIS));
        thirdCameraLeftRightInnerTextFieldPanel.add(h3Label);
        thirdCameraLeftRightInnerTextFieldPanel.add(thirdCameraLeftRightTextField);

        JPanel thirdCameraLeftRightTextFieldPanel = new JPanel();
        thirdCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(thirdCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        thirdCameraLeftRightTextFieldPanel.add(thirdCameraLeft);
        thirdCameraLeftRightTextFieldPanel.add(thirdCameraLeftRightInnerTextFieldPanel);
        thirdCameraLeftRightTextFieldPanel.add(thirdCameraRight);


        thirdCameraUpDownTextField = new JTextField();
        thirdCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        thirdCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel v3Label = new JLabel("v3 ");
        v3Label.setHorizontalAlignment(SwingConstants.CENTER);
        v3Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel thirdCameraUpDownInnerTextFieldPanel = new JPanel();
        thirdCameraUpDownInnerTextFieldPanel.setLayout(new BoxLayout(thirdCameraUpDownInnerTextFieldPanel, BoxLayout.X_AXIS));
        thirdCameraUpDownInnerTextFieldPanel.add(v3Label);
        thirdCameraUpDownInnerTextFieldPanel.add(thirdCameraUpDownTextField);


        JPanel thirdCameraUpDownTextFieldPanel = new JPanel();
        thirdCameraUpDownTextFieldPanel.setLayout(new BoxLayout(thirdCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        thirdCameraUpDownTextFieldPanel.add(thirdCameraUp);
        thirdCameraUpDownTextFieldPanel.add(thirdCameraUpDownInnerTextFieldPanel);
        thirdCameraUpDownTextFieldPanel.add(thirdCameraDown);
        JLabel thirdCameraLastLabel = new JLabel("\u21D6");
        thirdCameraLastLabel.setFont(commonFont);
        thirdCameraLastLabel.setHorizontalAlignment(SwingConstants.LEFT);
        thirdCameraLastLabel.setVerticalAlignment(SwingConstants.TOP);

        thirdCameraPanel.add(thirdCameraLastLabel);
        thirdCameraPanel.add(thirdCameraUpDownTextFieldPanel);
        thirdCameraPanel.add(thirdCameraLeftRightTextFieldPanel);
        thirdCameraPanel.add(thirdCameraLabel);
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

        JLabel h4Label = new JLabel("h4");
        h4Label.setPreferredSize(new Dimension(40, 25));
        h4Label.setHorizontalAlignment(SwingConstants.CENTER);
        h4Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel fourthCameraLeftRightInnerTextFieldPanel = new JPanel();
        fourthCameraLeftRightInnerTextFieldPanel.setLayout(new BoxLayout(fourthCameraLeftRightInnerTextFieldPanel, BoxLayout.Y_AXIS));
        fourthCameraLeftRightInnerTextFieldPanel.add(h4Label);
        fourthCameraLeftRightInnerTextFieldPanel.add(fourthCameraLeftRightTextField);

        JPanel fourthCameraLeftRightTextFieldPanel = new JPanel();
        fourthCameraLeftRightTextFieldPanel.setLayout(new BoxLayout(fourthCameraLeftRightTextFieldPanel, BoxLayout.X_AXIS));
        fourthCameraLeftRightTextFieldPanel.add(fourthCameraLeft);
        fourthCameraLeftRightTextFieldPanel.add(fourthCameraLeftRightInnerTextFieldPanel);
        fourthCameraLeftRightTextFieldPanel.add(fourthCameraRight);

        fourthCameraUpDownTextField = new JTextField();
        fourthCameraUpDownTextField.setMaximumSize(new Dimension(40, 25));
        fourthCameraUpDownTextField.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel v4Label = new JLabel("v4 ");
        v4Label.setHorizontalAlignment(SwingConstants.CENTER);
        v4Label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel fourthCameraUpDownInnerTextFieldPanel = new JPanel();
        fourthCameraUpDownInnerTextFieldPanel.setLayout(new BoxLayout(fourthCameraUpDownInnerTextFieldPanel, BoxLayout.X_AXIS));
        fourthCameraUpDownInnerTextFieldPanel.add(v4Label);
        fourthCameraUpDownInnerTextFieldPanel.add(fourthCameraUpDownTextField);

        JPanel fourthCameraUpDownTextFieldPanel = new JPanel();
        fourthCameraUpDownTextFieldPanel.setLayout(new BoxLayout(fourthCameraUpDownTextFieldPanel, BoxLayout.Y_AXIS));
        fourthCameraUpDownTextFieldPanel.add(fourthCameraUp);
        fourthCameraUpDownTextFieldPanel.add(fourthCameraUpDownInnerTextFieldPanel);
        fourthCameraUpDownTextFieldPanel.add(fourthCameraDown);
        JLabel fourthCameraLastLabel = new JLabel("\u21D7");
        fourthCameraLastLabel.setFont(commonFont);
        fourthCameraLastLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fourthCameraLastLabel.setVerticalAlignment(SwingConstants.TOP);

        fourthCameraPanel.add(fourthCameraUpDownTextFieldPanel);
        fourthCameraPanel.add(fourthCameraLastLabel);
        fourthCameraPanel.add(fourthCameraLabel);
        fourthCameraPanel.add(fourthCameraLeftRightTextFieldPanel);

        JLabel southLabel = new JLabel(Storage.getBundle().getString("south"));
        southLabel.setFont(commonFont);
        southLabel.setHorizontalAlignment(SwingConstants.CENTER);
        southLabel.setVerticalAlignment(SwingConstants.CENTER);

        southOutPanel.add(thirdCameraPanel, BorderLayout.EAST);
        southOutPanel.add(southLabel, BorderLayout.CENTER);
        southOutPanel.add(fourthCameraPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel hideZoneLinePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hideZoneLinePanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel hideZoneLabel = new JLabel(Storage.getBundle().getString("hidezoneborder"));
        hideZoneLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hideZoneLabel.setVerticalAlignment(SwingConstants.CENTER);
        hideZoneLabel.setFont(commonFont);
        hideZoneLinePanel.add(hideZoneLabel);


        JPanel northHideZonePanel = new JPanel(new BorderLayout(2, 2));
        JPanel firstCameraNorthHideZOnePAnel = new JPanel(new BorderLayout(2, 2));
        firstCameraHideZoneDistanceTextField = new JTextField();
        firstCameraHideZoneDistanceTextField.setPreferredSize(new Dimension(40, 25));
        JLabel firstCameraDownLabel = new JLabel("\u2193");
        firstCameraDownLabel.setFont(commonFont);
        firstCameraDownLabel.setHorizontalAlignment(SwingConstants.CENTER);
        firstCameraDownLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        firstCameraNorthHideZOnePAnel.add(firstCameraHideZoneDistanceTextField, BorderLayout.WEST);
        firstCameraNorthHideZOnePAnel.add(firstCameraDownLabel, BorderLayout.EAST);
        firstCameraNorthHideZOnePAnel.add(new JLabel("d1"));


        JPanel secondCameraNorthHideZOnePAnel = new JPanel(new BorderLayout(2, 2));
        secondCameraHideZoneDistanceTextField = new JTextField();
        secondCameraHideZoneDistanceTextField.setPreferredSize(new Dimension(40, 25));
        JLabel secondCameraDownLabel = new JLabel("\u2193");
        secondCameraDownLabel.setFont(commonFont);
        secondCameraDownLabel.setHorizontalAlignment(SwingConstants.CENTER);
        secondCameraDownLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        secondCameraNorthHideZOnePAnel.add(secondCameraHideZoneDistanceTextField, BorderLayout.EAST);
        secondCameraNorthHideZOnePAnel.add(secondCameraDownLabel, BorderLayout.WEST);
        secondCameraNorthHideZOnePAnel.add(new JLabel("d2"));


        northHideZonePanel.add(firstCameraNorthHideZOnePAnel, BorderLayout.WEST);
        northHideZonePanel.add(secondCameraNorthHideZOnePAnel, BorderLayout.EAST);

        JPanel southHideZonePanel = new JPanel(new BorderLayout(2, 2));
        JPanel thirdCameraNorthHideZOnePAnel = new JPanel(new BorderLayout(2, 2));
        thirdCameraHideZoneDistanceTextField = new JTextField();
        thirdCameraHideZoneDistanceTextField.setPreferredSize(new Dimension(40, 25));
        JLabel thirdCameraDownLabel = new JLabel("\u2191");
        thirdCameraDownLabel.setFont(commonFont);
        thirdCameraDownLabel.setHorizontalAlignment(SwingConstants.CENTER);
        thirdCameraDownLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        thirdCameraNorthHideZOnePAnel.add(thirdCameraHideZoneDistanceTextField, BorderLayout.EAST);
        thirdCameraNorthHideZOnePAnel.add(thirdCameraDownLabel, BorderLayout.WEST);
        thirdCameraNorthHideZOnePAnel.add(new JLabel("d3"), BorderLayout.CENTER);

        JPanel fourthCameraNorthHideZOnePAnel = new JPanel(new BorderLayout(2, 2));
        fourthCameraHideZoneDistanceTextField = new JTextField();
        fourthCameraHideZoneDistanceTextField.setPreferredSize(new Dimension(40, 25));
        JLabel fourthCameraDownLabel = new JLabel("\u2191");
        fourthCameraDownLabel.setFont(commonFont);
        fourthCameraDownLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fourthCameraDownLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        fourthCameraNorthHideZOnePAnel.add(fourthCameraHideZoneDistanceTextField, BorderLayout.WEST);
        fourthCameraNorthHideZOnePAnel.add(fourthCameraDownLabel, BorderLayout.EAST);
        fourthCameraNorthHideZOnePAnel.add(new JLabel("d4"));

        southHideZonePanel.add(thirdCameraNorthHideZOnePAnel, BorderLayout.EAST);
        southHideZonePanel.add(fourthCameraNorthHideZOnePAnel, BorderLayout.WEST);

        centerPanel.add(northHideZonePanel, BorderLayout.NORTH);
        centerPanel.add(hideZoneLinePanel, BorderLayout.CENTER);
        centerPanel.add(southHideZonePanel, BorderLayout.SOUTH);

        Font buttonFont = new Font(null, Font.BOLD, 50);

        JButton saveButton = new JButton("<html>&#128190</html>");
        saveButton.setFont(buttonFont);
        saveButton.addActionListener((af) -> {
            int[][] position = new int[][]{new int[3], new int[3], new int[3], new int[3]};
            position[0][0] = Integer.parseInt(firstCameraLeftRightTextField.getText());
            position[0][1] = Integer.parseInt(firstCameraUpDownTextField.getText());
            position[0][2] = Integer.parseInt(firstCameraHideZoneDistanceTextField.getText());

            position[1][0] = Integer.parseInt(secondCameraLeftRightTextField.getText());
            position[1][1] = Integer.parseInt(secondCameraUpDownTextField.getText());
            position[1][2] = Integer.parseInt(secondCameraHideZoneDistanceTextField.getText());

            position[2][0] = Integer.parseInt(thirdCameraLeftRightTextField.getText());
            position[2][1] = Integer.parseInt(thirdCameraUpDownTextField.getText());
            position[2][2] = Integer.parseInt(thirdCameraHideZoneDistanceTextField.getText());

            position[3][0] = Integer.parseInt(fourthCameraLeftRightTextField.getText());
            position[3][1] = Integer.parseInt(fourthCameraUpDownTextField.getText());
            position[3][2] = Integer.parseInt(fourthCameraHideZoneDistanceTextField.getText());

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

    private void setPosition(int[][] position) {

        firstCameraLeftRightTextField.setText(String.valueOf(position[0][0]));
        firstCameraUpDownTextField.setText(String.valueOf(position[0][1]));

        secondCameraLeftRightTextField.setText(String.valueOf(position[1][0]));
        secondCameraUpDownTextField.setText(String.valueOf(position[1][1]));

        thirdCameraLeftRightTextField.setText(String.valueOf(position[2][0]));
        thirdCameraUpDownTextField.setText(String.valueOf(position[2][1]));

        fourthCameraLeftRightTextField.setText(String.valueOf(position[3][0]));
        fourthCameraUpDownTextField.setText(String.valueOf(position[3][1]));

        firstCameraHideZoneDistanceTextField.setText(String.valueOf(position[0][2]));
        secondCameraHideZoneDistanceTextField.setText(String.valueOf(position[1][2]));
        thirdCameraHideZoneDistanceTextField.setText(String.valueOf(position[2][2]));
        fourthCameraHideZoneDistanceTextField.setText(String.valueOf(position[3][2]));

        firstCameraLeftRightTextField.addKeyListener(keyAdapter);
        firstCameraUpDownTextField.addKeyListener(keyAdapter);
        secondCameraLeftRightTextField.addKeyListener(keyAdapter);
        secondCameraUpDownTextField.addKeyListener(keyAdapter);
        thirdCameraLeftRightTextField.addKeyListener(keyAdapter);
        thirdCameraUpDownTextField.addKeyListener(keyAdapter);
        fourthCameraLeftRightTextField.addKeyListener(keyAdapter);
        fourthCameraUpDownTextField.addKeyListener(keyAdapter);

        firstCameraHideZoneDistanceTextField.addKeyListener(keyAdapter);
        secondCameraHideZoneDistanceTextField.addKeyListener(keyAdapter);
        thirdCameraHideZoneDistanceTextField.addKeyListener(keyAdapter);
        fourthCameraHideZoneDistanceTextField.addKeyListener(keyAdapter);
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

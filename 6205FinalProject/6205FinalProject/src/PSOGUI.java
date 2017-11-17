import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;


public class PSOGUI {

    private JButton start;
    private JPanel jPanel;
    private JPanel displayPanel;

    private JLabel CNum;
    private JLabel MAX_GEN;
    private JLabel W;
    private JLabel Begin;
    private JLabel bestLengthLabel;

    private JTextField CNumText;
    private JTextField ScaleText;
    private JTextField MAX_GENText;
    private JTextField WText;
    private JTextField BeginText;

    // set cNum
    int cNum = Integer.parseInt(CNumText.getText().trim());

    // read data
    String tspData = System.getProperty("user.dir") + "/resources/cNum.txt";

    int[] bestTour;
    int bestLength;
    private int[] x = new int[cNum];
    private int[] y = new int[cNum];


    // button start
    private Boolean isStarted = false;

    private GUICanvas GUICanvas;

    public PSOGUI() {
        // initialize panel
        displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());

        // initialize GUIcanvas, add it into panel
        GUICanvas= new GUICanvas();
        displayPanel.add(GUICanvas);

        // X-coordinate, Y-coordinate
        try {
            x = ReadFile.getX(cNum, tspData);
            y = ReadFile.getY(cNum, tspData);
            for (int i = 0; i < cNum; i++) {
                x[i] += 30;
                y[i] += 200;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ActionListener
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // get parameter from JTextField
                    int cNum = Integer.parseInt(CNumText.getText().trim());
                    int scale = Integer.parseInt(ScaleText.getText().trim());
                    int max_GEN = Integer.parseInt(MAX_GENText.getText().trim());
                    float w = Float.parseFloat(WText.getText().trim());
                    int begin = Integer.parseInt(BeginText.getText().trim());

                    // pso class
                    PSO pso = new PSO(cNum, scale, max_GEN, w, begin -1);
                    pso.init(tspData);
                    pso.solve();
                    bestTour = pso.getgBest();
                    bestLength = pso.geteG();

                    bestLengthLabel.setText("Best Length: " + bestLength);

                    displayPanel.add(GUICanvas);

                    isStarted = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    class GUICanvas extends Canvas {
        public GUICanvas() {
            // set background
            setBackground(Color.WHITE);
        }

        public void paint(Graphics graphics) {
            try {
                graphics.setColor(Color.PINK);
                for (int i = 0; i < cNum; i++) {
                    graphics.fillOval(x[i] / 10, y[i] / 10, 10, 10);
                    graphics.drawString(String.valueOf(i + 1), x[i] / 10, y[i] / 10);
                }

                if (isStarted == true) {
                    graphics.setColor(Color.BLACK);
                    for (int j = 0; j < cNum - 1; j++) {
                        try {
                            Thread.sleep(200);
                            graphics.drawLine(x[bestTour[j]] / 10, y[bestTour[j]] / 10, x[bestTour[j + 1]] / 10, y[bestTour[j + 1]] / 10);

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    graphics.drawLine(x[bestTour[0]]/10,y[bestTour[0]] / 10, x[bestTour[cNum - 1]] / 10, y[bestTour[cNum - 1]] / 10);

                    graphics.setColor(Color.BLUE);
                    graphics.fillOval(x[bestTour[0]] / 10, y[bestTour[0]] / 10, 6, 6);
                    graphics.setColor(Color.GREEN);
                    graphics.fillOval(x[bestTour[cNum - 1]] / 10, y[bestTour[cNum - 1]] / 10, 6, 6);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PSO"); // 设置标题
        PSOGUI myGUI = new PSOGUI();

        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(myGUI.jPanel, BorderLayout.WEST);
        container.add(myGUI.displayPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1024, 600);
        frame.setVisible(true);
    }
}

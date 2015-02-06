package teamcomm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.media.opengl.GLContext;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import jogamp.opengl.gl4.GL4bcImpl;
import teamcomm.Main;
import teamcomm.data.RobotData;
import teamcomm.data.RobotState;

/**
 * @author Felix Thielke
 */
public class RobotView extends JFrame implements Runnable {

    private static final long serialVersionUID = 6549981924840180076L;
    private final GLCanvas fieldView = new GLCanvas();
    private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
    private final JPanel[] teamPanels = new JPanel[]{new JPanel(), new JPanel(), new JPanel()};
    private final Map<String, JPanel> robotPanels = new HashMap<String, JPanel>();

    public RobotView() {
        super("Robots");

        // Setup window
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Main.shutdown();
            }
        });

        // Setup team panels
        teamPanels[0].setLayout(new BoxLayout(teamPanels[0], BoxLayout.Y_AXIS));
        //teamPanels[0].add(new JLabel("No blue team found", SwingConstants.CENTER));
        teamPanels[1].setLayout(new BoxLayout(teamPanels[1], BoxLayout.Y_AXIS));
        //teamPanels[1].add(new JLabel("No red team found", SwingConstants.CENTER));
        teamPanels[2].setLayout(new BoxLayout(teamPanels[2], BoxLayout.X_AXIS));

        // Setup content pane
        final JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(teamPanels[0], BorderLayout.WEST);
        contentPane.add(teamPanels[1], BorderLayout.EAST);
        contentPane.add(teamPanels[2], BorderLayout.SOUTH);
        contentPane.add(fieldView, BorderLayout.CENTER);
        setContentPane(contentPane);

        // Display window
        pack();
        setVisible(true);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            RobotData.getInstance().lockForReading();
            updateView();
            RobotData.getInstance().unlockForReading();
            try {
                Thread.sleep(1000 / 4);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void updateView() {
        final int[] teamNumbers = RobotData.getInstance().getTeamNumbers();
        final Iterator<RobotState> otherRobots = RobotData.getInstance().getOtherRobots();
        if (teamNumbers == null) {
            if (otherRobots.hasNext()) {

            } else {

            }
        } else {
            if (getContentPane() != tabbedPane) {

            }
        }

        for (int team = 0; team < 3; team++) {
            final Iterator<RobotState> robots;
            if (team < 2) {
                if (teamNumbers == null) {
                    continue;
                }
                robots = RobotData.getInstance().getRobotsForTeam(team);
            } else {
                robots = otherRobots;
            }

            int i = 0;
            while (robots.hasNext()) {
                final RobotState robot = robots.next();

                JPanel panel = robotPanels.get(robot.getAddress());
                if (panel == null) {
                    panel = createRobotPanel(robot);
                    robotPanels.put(robot.getAddress(), panel);
                } else {
                    updateRobotPanel(panel, robot);
                }

                if (teamPanels[team].getComponentCount() <= i) {
                    teamPanels[team].add(panel);
                    panel.revalidate();
                } else if (panel != teamPanels[team].getComponent(i)) {
                    teamPanels[team].remove(panel);
                    teamPanels[team].add(panel, i);
                    panel.revalidate();
                }

                i++;
            }
        }

        repaint();
    }

    private JPanel createRobotPanel(final RobotState robot) {
        final DecimalFormat df = new DecimalFormat("#.#####");
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), robot.getAddress(), TitledBorder.CENTER, TitledBorder.TOP));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMinimumSize(new Dimension(150, 90));
        panel.setMaximumSize(new Dimension(150, 90));
        panel.setPreferredSize(new Dimension(150, 90));

        panel.add(new JLabel("Player no: " + robot.getLastMessage().playerNum, JLabel.LEFT));
        panel.add(new JLabel("Messages: " + robot.getMessageCount(), JLabel.LEFT));
        panel.add(new JLabel("Per second: " + df.format(robot.getMessagesPerSecond()), JLabel.LEFT));
        panel.add(new JLabel("Illegal: " + robot.getIllegalMessageCount(), JLabel.LEFT));
        /*panel.add(new JLabel(" ", JLabel.LEFT));
         panel.add(new JLabel(robot.getLastMessage().fallen ? "fallen" : "upright", JLabel.LEFT));
         panel.add(new JLabel("Pos.X: " + df.format(robot.getLastMessage().pose[0]), JLabel.LEFT));
         panel.add(new JLabel("Pos.Y: " + df.format(robot.getLastMessage().pose[1]), JLabel.LEFT));
         panel.add(new JLabel("Pos.T: " + df.format(robot.getLastMessage().pose[2]), JLabel.LEFT));
         panel.add(new JLabel("Target.X: " + df.format(robot.getLastMessage().walkingTo[0]), JLabel.LEFT));
         panel.add(new JLabel("Target.Y: " + df.format(robot.getLastMessage().walkingTo[1]), JLabel.LEFT));
         panel.add(new JLabel("Shot.X: " + df.format(robot.getLastMessage().shootingTo[0]), JLabel.LEFT));
         panel.add(new JLabel("Shot.Y: " + df.format(robot.getLastMessage().shootingTo[1]), JLabel.LEFT));
         panel.add(new JLabel("BallRel.X: " + df.format(robot.getLastMessage().ball[0]), JLabel.LEFT));
         panel.add(new JLabel("BallRel.Y: " + df.format(robot.getLastMessage().ball[1]), JLabel.LEFT));
         panel.add(new JLabel("BallVel.X: " + df.format(robot.getLastMessage().ballVel[0]), JLabel.LEFT));
         panel.add(new JLabel("BallVel.Y: " + df.format(robot.getLastMessage().ballVel[1]), JLabel.LEFT));
         panel.add(new JLabel("BallAge: " + robot.getLastMessage().ballAge, JLabel.LEFT));*/

        return panel;
    }

    private void updateRobotPanel(final JPanel panel, final RobotState robot) {
        final DecimalFormat df = new DecimalFormat("#.#####");
        ((JLabel) panel.getComponent(0)).setText("Player no: " + robot.getLastMessage().playerNum);
        ((JLabel) panel.getComponent(1)).setText("Messages: " + robot.getMessageCount());
        ((JLabel) panel.getComponent(2)).setText("Per second: " + df.format(robot.getMessagesPerSecond()));
        ((JLabel) panel.getComponent(3)).setText("Illegal: " + robot.getIllegalMessageCount());
        /*((JLabel)panel.getComponent(5)).setText(robot.getLastMessage().fallen ? "fallen" : "upright");
         ((JLabel)panel.getComponent(6)).setText("Pos.X: " + df.format(robot.getLastMessage().pose[0]));
         ((JLabel)panel.getComponent(7)).setText("Pos.Y: " + df.format(robot.getLastMessage().pose[1]));
         ((JLabel)panel.getComponent(8)).setText("Pos.T: " + df.format(robot.getLastMessage().pose[2]));
         ((JLabel)panel.getComponent(11)).setText("Target.X: " + df.format(robot.getLastMessage().walkingTo[0]));
         ((JLabel)panel.getComponent(12)).setText("Target.Y: " + df.format(robot.getLastMessage().walkingTo[1]));
         ((JLabel)panel.getComponent(13)).setText("Shot.X: " + df.format(robot.getLastMessage().shootingTo[0]));
         ((JLabel)panel.getComponent(14)).setText("Shot.Y: " + df.format(robot.getLastMessage().shootingTo[1]));
         ((JLabel)panel.getComponent(15)).setText("BallRel.X: " + df.format(robot.getLastMessage().ball[0]));
         ((JLabel)panel.getComponent(16)).setText("BallRel.Y: " + df.format(robot.getLastMessage().ball[1]));
         ((JLabel)panel.getComponent(17)).setText("BallVel.X: " + df.format(robot.getLastMessage().ballVel[0]));
         ((JLabel)panel.getComponent(18)).setText("BallVel.Y: " + df.format(robot.getLastMessage().ballVel[1]));
         ((JLabel)panel.getComponent(19)).setText("BallAge: " + robot.getLastMessage().ballAge);*/
    }

}

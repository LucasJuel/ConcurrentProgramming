//Implementation of Graphical User Interface class
//Mandatory assignment 3
//Course 02158 Concurrent Programming, DTU, Fall 2024

//Hans Henrik Lovengreen       Nov 1, 2024

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;

// import Cars.SetThread;

class Tile {

    final static int edge = 36;

    // Colors
    final static Color defcolor = Color.blue;
    final static Color symbolcolor = new Color(200, 200, 200);
    final static Color blockcolor = new Color(180, 180, 180);
    final static Color bgcolor = new Color(250, 250, 250); // Light grey
    final static Color slowcolor = new Color(255, 200, 80); // Amber
    final static Color bridgecolor = new Color(210, 210, 255); // Light blue
    final static Color overloadcolor = new Color(255, 210, 240); // Pink
    final static Color opencolor = new Color(0, 200, 0); // Dark green
    final static Color closedcolor = Color.red;
    final static Color barriercolor = Color.black;
    final static Color barriercolor2 = new Color(255, 70, 70); // Emphasis
                                                               // colour - a
                                                               // kind of orange

    final static Font f = new Font("SansSerif", Font.BOLD, 12);

    final static int maxstaints = 10;

    static Color currentBridgeColor = bridgecolor;

    private Cars cars;

    // Model of tile status
    // Modified through event-thread (except for Car no. 0)

    private volatile int users = 0; // No. of current users of tile
    private char symbol = ' ';

    private boolean isblocked = false; // Tile can be used
    private boolean hadcrash = false; // Car crash has occurred
    private boolean keepcrash = false; // For detecting crashes
    private boolean slowdown = false; // Slow tile
    private boolean onbridge = false; // Bridge tile

    private boolean isstartpos = false;
    private int startposno = 0;
    private boolean startposopen = false;

    private int staintx = 0;
    private int stainty = 0;
    private int staintd = 0;

    @SuppressWarnings("unused")
    private static boolean light(Color c) {
        return (c.getRed() + 2 * c.getGreen() + c.getBlue()) > 600;
    }

    public Tile(Pos p, Cars c) {
        cars = c;
    }

    public void onMousePressed(MouseEvent e) {
        if (isstartpos) {
            if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) > 0)
                cars.removeCar(startposno);
            else if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0)
                cars.restoreCar(startposno);
            else
                cars.startTileClick(startposno);
        }
    }

    public void use() {
        users++;
        if (users > 1 && keepcrash && !hadcrash) {
            hadcrash = true;
            // Define a staint
            int dia = 7;
            staintx = (edge - 1 - dia) / 2 + (int) Math.round(Math.random() * 4) - 2;
            stainty = (edge - 1 - dia) / 2 + (int) Math.round(Math.random() * 4) - 2;
            staintd = dia;
        }
    }

    public void exit() {
        users--;
    }

    public void reset() {
        users = 0;
    }

    public void clean() {
        hadcrash = false;
    }

    public void setSymbol(char c) {
        symbol = c;
    }

    public void setBlocked() {
        isblocked = true;
    }

    public void setStartPos(int no, boolean open) {
        setSymbol((char) (no + (int) '0'));
        isstartpos = true;
        startposno = no;
        startposopen = open;
    }

    public void setStartPos(boolean open) {
        startposopen = open;
    }

    public void showBarrier(boolean active) {
    }

    public void emphasizeBarrier(boolean emph) {
    }

    public void setBarrierPos(boolean top) {
    }

    public void setKeep(boolean keep) {
        keepcrash = keep;
        if (!keep && hadcrash)
            clean();
    }

    public void setSlow(boolean slowdown) {
        this.slowdown = slowdown;
    }

    public void setBridge(boolean onbridge) {
        this.onbridge = onbridge;
    }

    public static void setOverload(boolean overloaded) {
        currentBridgeColor = (overloaded ? overloadcolor : bridgecolor);
    }

    public void paintComponent(Graphics g) {
        g.setColor(isblocked ? blockcolor : (slowdown ? slowcolor : onbridge ? currentBridgeColor : bgcolor));
        g.fillRect(0, 0, edge, edge);

        if (symbol != ' ') {
            g.setColor(symbolcolor);
            g.setFont(f);
            FontMetrics fm = g.getFontMetrics(f);
            int w = fm.charWidth(symbol);
            int h = fm.getHeight();
            g.drawString("" + symbol, ((edge - w) / 2), ((edge + h / 2) / 2 + 1));
        }

        if (hadcrash) {
            g.setColor(Color.red);
            g.fillOval(staintx, stainty, staintd, staintd);
        }

        if (users > 1 || (users > 0 && isblocked)) {
            g.setColor(Color.red);
            g.fillRect(0, 0, edge, edge);
        }

        if (users < 0) {
            System.out.println("Users : " + users);
            g.setColor(Color.yellow);
            g.fillRect(0, 0, edge, edge);
        }

        if (isstartpos) {
            g.setColor(startposopen ? opencolor : closedcolor);
            g.drawRect(1, 1, edge - 2, edge - 2);

        }

    }

}

@SuppressWarnings("serial")
class Ground extends JPanel {

    private final int n = Layout.ROWS; // Rows
    private final int m = Layout.COLS; // Columns

    final static int edge = Tile.edge; // Tile size
    final static int bordersize = 1; // Thickness of playground border

    public final static int carSize = 24; // Car size

    public final static double margin = ((double) (edge - carSize)) / (edge * 2);

    final static int offsetx = edge / 2;
    final static int offsety = edge / 2;

    final static Font f = new Font("SansSerif", Font.BOLD, 12);

    final static int fps = 50;

    private int frames = 0;

    private Tile[][] area;

    private boolean barrieractive = false;
    private boolean barrieremph = false;

    final static int barrierStartX = (Layout.getBarrierUpperPos(0).col) * edge;
    final static int barrierStartY = (Layout.getBarrierUpperPos(0).row + 1) * edge;

    final static int barrierThickness = 2; // Half of real thickness
    final static Color barriercolor = Color.black;
    final static Color barriercolor2 = new Color(255, 70, 70); // Emphasis
                                                               // colour - a
                                                               // kind of orange

    // Checking for bridge overload is done in this class
    // Initial values must correspond to control panel defaults
    private int onbridge = 0;
    private boolean checkBridge = false;
    private int limit = Cars.initialBridgeLimit;

    private Simulator<CarModel> sim;

    private static boolean light(Color c) {
        return (c.getRed() + 2 * c.getGreen() + c.getBlue()) > 600;
    }

    public Ground(Cars c, Simulator<CarModel> sim) {
        this.sim = sim;
        setPreferredSize(new Dimension(edge * m + 2 * bordersize, edge * n + 2 * bordersize));
        setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), bordersize));
        setOpaque(true);

        area = new Tile[n][m];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                area[i][j] = new Tile(new Pos(i, j), c);
            }

        // Define Hut and Shed areas
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                Pos pos = new Pos(i, j);

                if (Layout.isHutPos(pos))
                    area[i][j].setBlocked();

                if (Layout.isShedPos(pos))
                    area[i][j].setBlocked();
            }

        // Set start/gate positions
        for (int no = 0; no < 9; no++) {
            Pos startpos = Layout.getStartPos(no);
            area[startpos.row][startpos.col].setStartPos(no, false);
        }

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                // Compensate for border
                int x = p.x - bordersize;
                int y = p.y - bordersize;
                if (y >= n * edge || x >= m * edge)
                    return;
                Tile tile = area[y / edge][x / edge];
                tile.onMousePressed(e);
            }
        });

        // Use periodic repaint of whole playground at fps frames-per-second to
        // eliminate platform differences
        Timer t = new Timer(1000 / fps, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                repaint();
                // Do periodic statistics
                frames = (frames + 1) % fps;
                if (frames == 0) {
                    // Waypoint.printStats();
                }
            }
        });
        t.start();
    }

    boolean isOnBridge(Pos pos) {
        return pos.col >= Layout.bridgeLeftCol && pos.col <= Layout.bridgeRightCol && pos.row >= Layout.bridgeUpperRow
                && pos.row <= Layout.bridgeLowerRow;
    }

    // The following internal graphical methods are only called via the
    // GUI-thread

    void setOpen(int no) {
        Pos p = Layout.getStartPos(no);
        area[p.row][p.col].setStartPos(true);
    }

    void setClosed(int no) {
        Pos p = Layout.getStartPos(no);
        area[p.row][p.col].setStartPos(false);
    }

    void showBarrier(boolean active) {
        barrieractive = active;
    }

    void setBarrierEmphasis(boolean emph) {
        barrieremph = emph;
    }

    void setKeep(boolean keep) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                area[i][j].setKeep(keep);
    }

    void setSlow(boolean slowdown) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                Pos pos = Layout.getPos(i, j);
                if (Layout.isSlowPos(pos))
                    area[i][j].setSlow(slowdown);
            }

        CarModel.setSlow(slowdown);
    }

    void showBridge(boolean active) {
        for (int i = Layout.bridgeUpperRow; i <= Layout.bridgeLowerRow; i++)
            for (int j = Layout.bridgeLeftCol; j <= Layout.bridgeRightCol; j++)
                area[i][j].setBridge(active);
        checkBridge = active;
        bridgeCheck();
    }

    void setLimit(int max) {
        limit = max;
        bridgeCheck();
    }

    void bridgeCheck() {
        if (checkBridge) {
            Tile.setOverload(onbridge > limit);
        }
    }

    public void paintComponent(Graphics g) {
        // Eliminate borders to get the field proper
        final Graphics field = g.create(bordersize, bordersize, edge * m, edge * n);

        // Paint tiles
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++) {
                Graphics tile = field.create(j * edge, i * edge, edge, edge);
                area[i][j].paintComponent(tile);
                area[i][j].reset();
            }

        // Paint Barrier
        if (barrieractive) {
            field.setColor(!barrieremph ? barriercolor : barriercolor2);
            field.fillRect(barrierStartX, barrierStartY - barrierThickness, 9 * edge, 2 * barrierThickness);
        }

        // Paint cars (within simulator's critical region)
        // Determine bridge overload (shown in next frame)
        onbridge = 0;
        sim.applyToAll((car) -> {
            paintCar(field, car);
        });
        bridgeCheck();
        
    }

    public void paintCar(Graphics g, CarModel car) {
        int no = car.getNo();
        char id = (char) (no + (int) '0');
        Color col = car.getColor();
        long loc = car.getLocation();
        Pos pos = Location.position(loc);
        int x = offsetx + (int) (edge * Location.getY(loc));
        int y = offsety + (int) (edge * Location.getX(loc));

        g.setColor(col);
        g.fillOval(x - carSize / 2, y - carSize / 2, carSize, carSize);
        if (id != ' ') {
            if (light(col))
                g.setColor(Color.black);
            else
                g.setColor(Color.white);
            g.setFont(f);
            FontMetrics fm = getFontMetrics(f);
            int w = fm.charWidth(id);
            int h = fm.getHeight();
            g.drawString("" + id, x - w / 2, y + (h / 4) + 1); // (edge-w)/2),((edge+h/2)/2+1));
        }

     
        // Count tile usage for collision detection (shown in next frame).
        // All tiles should already have been reset.
        // Cars must be on track for this simple detection to work properly.
        // Also count bridge load
        if (!Location.onTrack(loc)) {
            System.err.println("Car no. " + no + " not on track");
            return;
        }

        // Register use of current tile.
        area[pos.row][pos.col].use();

        long diff = Location.sub(loc, Location.location(pos));
        double l = Location.len(diff);

        if (l > margin) {
            // Extends into adjacent tile
            long dir = Location.direction(diff);
            Pos adj = Location.position(Location.add(Location.location(pos), dir));
            area[adj.row][adj.col].use();
            if (isOnBridge(pos)|| isOnBridge(adj)) { onbridge++; }
        } else {
            if (isOnBridge(pos)) { onbridge++; }
        }
    }

}

@SuppressWarnings("serial")
class ControlPanel extends JPanel {

    private int test_count = 20;

    Cars cars;

    JPanel button_panel = new JPanel();

    JCheckBox keep = new JCheckBox("Keep", false);
    JCheckBox slow = new JCheckBox("Slowdown", false);

    JButton freeze = new JButton("||");

    JPanel barrier_panel = new JPanel();

    // JCheckBox barrier_on = new JCheckBox("Active", false);

    JButton barrier_on = new JButton("On");
    JButton barrier_off = new JButton("Off");
    // JButton barrier_shutdown = new JButton("Shut down");
    // JPanel bridge_panel = new JPanel(); // Combined with barrier panel

    //    JCheckBox bridge_on = new JCheckBox("Show", false);
    // JCheckBox bridge_on = new JCheckBox("", false);

    JLabel     threshold_label = new JLabel("Threshold:");
    JComboBox<String> barrier_threshold = new JComboBox<String>();
    int currentThreshold = 9;

    // int currentLimit = Cars.initialBridgeLimit;
    // JLabel     limit_label  = new JLabel("Bridge limit:");
    // JComboBox<String>  bridge_limit = new JComboBox<String>();

    JPanel test_panel = new JPanel();

    JComboBox<String> test_choice = new JComboBox<String>();

    public ControlPanel(Cars c) {
        cars = c;

        Insets bmargin = new Insets(2, 5, 2, 5);

        setLayout(new GridLayout(3, 1));

        JButton start_all = new JButton("Start all");
        start_all.setMargin(bmargin);
        start_all.addActionListener((event) -> {
            cars.startAll();
        });

        JButton stop_all = new JButton("Stop all");
        stop_all.setMargin(bmargin);
        stop_all.addActionListener((event) -> {
            cars.stopAll();
        });

        keep.addItemListener((event) -> {
            cars.setKeep(keep.isSelected());
        });
        slow.addItemListener((event) -> {
            cars.setSlow(slow.isSelected());
        });

        freeze.setFont(new Font("Monospaced", Font.BOLD, 12));
        freeze.setMargin(bmargin);
        freeze.addActionListener((event) -> {
            boolean frozen = !freeze.isSelected();
            freeze.setSelected(frozen);
            freeze.setText(frozen ? "|>" : "||");
            cars.setFreeze(frozen);
        });

        button_panel.add(start_all);
        button_panel.add(stop_all);
        button_panel.add(new JLabel(" "));
        button_panel.add(keep);
        button_panel.add(new JLabel(""));
        button_panel.add(slow);
        button_panel.add(new JLabel(""));
        button_panel.add(freeze);

        add(button_panel);

        barrier_panel.add(new JLabel("Barrier:"));

        barrier_on.setMargin(bmargin);
        barrier_off.setMargin(bmargin);
        // barrier_shutdown.setMargin(bmargin);

        barrier_on.addActionListener((event) -> {
            cars.barrierOn();
        });

        barrier_off.addActionListener((event) -> {
            cars.barrierOff();
        });

        /*
        barrier_shutdown.addActionListener((event) -> {
                cars.barrierShutDown(null);
        });
        */
        
        barrier_panel.add(barrier_on);
        barrier_panel.add(barrier_off);
        // barrier_panel.add(barrier_shutdown);

        
        barrier_panel.add(new JLabel("   "));

        barrier_threshold.setMaximumRowCount(9);
        for (int i = 0; i <= 8; i++) {
            barrier_threshold.addItem("" + (i + 1));
        }
        barrier_threshold.setSelectedIndex(currentThreshold - 1);

        barrier_threshold.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int t = barrier_threshold.getSelectedIndex() + 1; // Ignore
                                                                  // internal
                                                                  // changes
                if (t != currentThreshold) {
                    cars.barrierSet(t, null);
                }
            }
        });
        
        barrier_panel.add(threshold_label);
        barrier_panel.add(barrier_threshold);         


        barrier_panel.add(new JLabel("   "));

        /*
        barrier_panel.add(new JLabel("Bridge:"));
        barrier_panel.add(bridge_on);

        bridge_on.addItemListener( (event) -> {
                cars.showBridge(bridge_on.isSelected());
                
        });

        for (int i = 0; i < 7; i++) 
            bridge_limit.addItem(""+(i));
        bridge_limit.setSelectedIndex(currentLimit);

        bridge_limit.addActionListener( (event) -> {
                int i = bridge_limit.getSelectedIndex();
                // System.out.println("Select event " + i);
                // Ignore internal changes
                if (i!= currentLimit) {
                    // System.out.println("Calling setLimit");
                    cars.setLimit(i);
                }
        });

        barrier_panel.add(bridge_limit);
         */        

        add(barrier_panel);

        for (int i = 0; i < test_count; i++)
            test_choice.addItem("" + i);

        JButton run_test = new JButton("Run test no.");
        run_test.setMargin(bmargin);
        run_test.addActionListener((event) -> {
            int i = test_choice.getSelectedIndex();
            cars.runTest(i);
        });

        test_panel.add(run_test);
        test_panel.add(test_choice);
        add(test_panel);

    }

    public void barrierSetBegin() {
        // barrier_on.setEnabled(false);
        // barrier_off.setEnabled(false);
        barrier_threshold.setEnabled(false);
    }

    public void barrierSetEnd(int k) { 
        // barrier_on.setEnabled(true); //
        // barrier_off.setEnabled(true);
        if (k != currentThreshold) {
            currentThreshold = k;
            barrier_threshold.setSelectedIndex(k - 1);
        }
        barrier_threshold.setEnabled(true);
    }     
    
    public void shutDownBegin() {
        barrier_on.setEnabled(false);
        barrier_off.setEnabled(false);
        // barrier_shutdown.setEnabled(false);
    }

    public void shutDownEnd() {
        barrier_on.setEnabled(true);
        barrier_off.setEnabled(true);
        // barrier_shutdown.setEnabled(true);
    }    

    /*
    public void disableBridge() {
        bridge_limit.setEnabled(false);
    }

    public void enableBridge() {
        bridge_limit.setEnabled(true);
    }

    public void setLimit(int k) {
        currentLimit = k;
        if (k != bridge_limit.getSelectedIndex())
            bridge_limit.setSelectedIndex(k);
    }

    public void setBridge(boolean active) {
        // Precaution to avoid infinite event sequence of events
        if (active != bridge_on.isSelected()) {
            bridge_on.setSelected(active);
        }
    }
    */
}

@SuppressWarnings({ "serial" })
public class Cars extends JFrame implements CarDisplayI {

    static final int width = 30; // Width of text area
    static final int minhistory = 50; // Min no. of lines kept

    static final String version = "Man3.1";

    public static final int initialBridgeLimit = 6;

    // Model
    private boolean[] gateopen = new boolean[9];
    private Pos[] startpos = new Pos[9];
    private Pos[] barrierpos = new Pos[9];

    private boolean barrieractive = false;
    private CarControlI ctr;
    private CarTestWrapper testwrap;
    private Thread test;

    private Ground gnd;
    private JPanel gp;
    private ControlPanel cp;
    private JTextArea txt;
    private JScrollPane log;

    private Simulator<CarModel> sim;

    public CarControlI getControl() {
        return ctr;
    }

    class LinePrinter implements Runnable {
        String m;

        public LinePrinter(String line) {
            m = line;
        }

        public void run() {
            int lines = txt.getLineCount();
            if (lines > 2 * minhistory) {
                try {
                    int cutpos = txt.getLineStartOffset(lines / 2);
                    txt.replaceRange("", 0, cutpos);
                } catch (Exception e) {
                }
            }
            txt.append(m + "\n");
        }
    }

    /*
     * Thread to carry out barrier threshold setting since it may be blocked by
     * CarControl
     */

    class SetThread extends Thread {
        int newval;

        public SetThread(int newval) {
            this.newval = newval;
        }

        public void run() {
            try {
                ctr.barrierSet(newval);
                EventQueue.invokeLater(() -> {
                    barrierSetDone();
                });

            } catch (Exception e) {
                System.err.println("Exception in threshold setting thread");
                e.printStackTrace();
            }

        }
    }



    /*
     *  NO blocking limit setting in this version
     *
        // Variables used during limit setting
        private SetLimitThread limitThread; 
        private Semaphore      limitDone;
        private int            limitValue;
        
        
         * Thread to carry out change of bridge limit since
         * it may be blocked by CarControl
         
        class SetLimitThread extends Thread {
            int newmax;

            public SetLimitThread(int newmax) {
                this.newmax =  newmax;

            }

            public void run() {
                // ctr.setLimit(newmax);
                
                System.out.println("SetLimit returned");
                EventQueue.invokeLater(new Runnable() {
                    public void run() { endSetLimit(); }}
                );
                
            }
        }
    */
 
    /* No barrier shutdown in this version

    // Variables used during barrier shut down 
    private ShutDownThread shutDownThread = null;
    private Semaphore shutDownSem;

    // Thread to carry out barrier off shut down since it may be blocked by CarControl

    class ShutDownThread extends Thread { 
        int newmax;

        public void run() { 
            try { 
                ctr.barrierShutDown();

                //System.out.println("Shut down returned"); 
                EventQueue.invokeLater( () -> { shutDownDone(); } );

            } catch (Exception e) { 
                System.err.println("Exception in shut down thread");
                e.printStackTrace(); 
            }

        }
    }
    */

    void buildGUI(final Cars cars, final Simulator<CarModel> sim) {
        try {

            EventQueue.invokeAndWait(() -> {

                gnd = new Ground(cars, sim);
                gp = new JPanel();
                cp = new ControlPanel(cars);
                txt = new JTextArea("", 8, width);
                txt.setEditable(false);
                log = new JScrollPane(txt);
                log.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                setTitle("Cars");
                setBackground(new Color(200, 200, 200));

                gp.setLayout(new FlowLayout(FlowLayout.CENTER));
                gp.add(gnd);

                setLayout(new BorderLayout());
                add("North", gp);
                add("Center", cp);
                add("South", log);

                addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(1);
                    }
                });

                // cp.setBridge(false);
                // cp.disableBridge();
                
                pack();
                setBounds(100, 100, getWidth(), getHeight());
                setTitle("Cars " + version);
                setVisible(true);
            });

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Cars() {

        for (int no = 0; no < 9; no++) {
            startpos[no] = Layout.getStartPos(no);
            barrierpos[no] = Layout.getBarrierPos(no);
            gateopen[no] = false;
        }

        sim = new Simulator<CarModel>();

        buildGUI(this, sim);

        // Add control
        testwrap = new CarTestWrapper(this);

        ctr = new CarControl(this);
    }

    public static void main(String[] args) {
        try {
            new Cars().getControl();

            Thread.sleep(100 * 365 * 24 * 60 * 60 * 1000); // Sleep here for a
                                                           // hundred years
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // High-level event handling -- to be called by gui thread only
    // The test thread activates these through the event queue via the
    // CarTestWrapper

    public void barrierOn() {
        gnd.showBarrier(true);
        barrieractive = true;
        ctr.barrierOn();
    }

    public void barrierOff() {
        ctr.barrierOff();
        gnd.showBarrier(false);
        barrieractive = false;
    }

    /* 
    void barrierShutDown(Semaphore done) {

        if (shutDownThread != null ) {
            println("WARNING: Barrier shut down already in progress");
            if (done != null) done.V();
            return;
        }

        gnd.setBarrierEmphasis(true);
        cp.shutDownBegin();
        // Hold values for post-processing
        shutDownSem = done;
        shutDownThread = new ShutDownThread();
        shutDownThread.start();
    }
    
    // Called when Shut Down Thread has ended
    void shutDownDone() {

        cp.shutDownEnd();
 
        if (shutDownSem != null ) shutDownSem.V();
        shutDownThread = null;
        shutDownSem = null;

        gnd.setBarrierEmphasis(false);
        gnd.showBarrier(false);
        barrieractive = false;
    }
    */
    
    // Variables used during threshold setting
    private SetThread setThread = null;
    private Semaphore setSem = null;
    private int setVal;

    void  barrierSet(int k, Semaphore done) {

        if (setThread != null ) {
            println("WARNING: Threshold setting already in progress");
            if (done != null) done.V();
            return;
        }

        if (k < 1 || k > 9) {
            println("WARNING: Threshold value out of range: " + k + " (ignored)");
            if (done != null) done.V();
            return;
        }

        gnd.setBarrierEmphasis(true);
        cp.barrierSetBegin();
        // Hold values for post-processing
        setSem = done;
        setVal = k;
        setThread = new SetThread(k);
        setThread.start();
    }

    // Called when Set Thread has ended
    void barrierSetDone() {

        cp.barrierSetEnd(setVal);
        gnd.setBarrierEmphasis(false);

        if (setSem != null ) setSem.V();
        setThread = null;
        setSem = null;
    }

    
     void barrierClicked(boolean on) {
        if (on)
            barrierOn();
        else
            barrierOff();
    }
    
    public void setSlow(final boolean slowdown) {
        gnd.setSlow(slowdown);
    }

    public void setFreeze(boolean freeze) {
        if (freeze)
            sim.pause();
        else
            sim.resume();
    }

    public void startAll() {
        int first = barrieractive ? 0 : 1;
        // Should not start no. 0 if no barrier
        for (int no = first; no < 9; no++)
            startCar(no);
    }

    public void stopAll() {
        for (int no = 0; no < 9; no++)
            stopCar(no);
    }

    void runTest(int i) {
        if (test != null && test.isAlive()) {
            println("Test already running");
            return;
        }
        println("Run of test " + i);
        test = new CarTest(testwrap, i);
        test.start();
    }

    public void setKeep(final boolean keep) {
        gnd.setKeep(keep);
    }

    /*
    void showBridge(boolean active) {
        gnd.showBridge(active);
        if (active) cp.enableBridge(); else cp.disableBridge();
     }
    */
    
    void startTileClick(int no) {
        if (gateopen[no])
            stopCar(no);
        else
            startCar(no);
    }

    public void startCar(final int no) {
        if (!gateopen[no]) {
            gnd.setOpen(no);
            gateopen[no] = true;
            ctr.startCar(no);
        }
    }

    public void stopCar(final int no) {
        if (gateopen[no]) {
            ctr.stopCar(no);
            gnd.setClosed(no);
            gateopen[no] = false;
        }
    }

    public void setSpeed(int no, double speed) {
        ctr.setSpeed(no, speed);
    }

    public void setVariation(int no, int var) {
        ctr.setVariation(no, var);
    }

    public void removeCar(int no) {
        ctr.removeCar(no);
    }

    public void restoreCar(int no) {
        ctr.restoreCar(no);
    }

    /*
    void setLimit(int max) {

        if (max < 0 || max > 6) {
            println("ERROR: Illegal limit value");
            return;
        }

        ctr.setLimit(max);
        gnd.setLimit(max);
        cp.setLimit(max);
    }
    */

    /*
     * No blocking of setLimit in this version
     * 
     * void setLimit(int max, Semaphore done) {
     * 
     * if (! bridgepresent) { println("ERROR: No bridge at this playground!");
     * if (done != null) done.V(); return; }
     * 
     * if (max < 1 || max > 6) { println("ERROR: Illegal limit value"); if (done
     * != null) done.V(); return; }
     * 
     * if (limitThread != null ) {
     * println("WARNING: Limit setting already in progress"); if (done != null)
     * done.V(); return; }
     * 
     * cp.disableLimit(); // Hold values for post-processing limitValue = max;
     * limitDone = done; limitThread = new SetLimitThread(max);
     * limitThread.start(); }
     * 
     * // Called when SetLimitThread has ended void endSetLimit() {
     * 
     * System.out.println("endSetLimit start"); if (limitDone != null )
     * limitDone.V();
     * 
     * gnd.setLimit(limitValue); cp.enableLimit(limitValue); limitThread = null;
     * limitDone = null; System.out.println("endSetLimit end"); }
     */

    /*
     * Implementation of Car Display Interface
     * 
     */

    public Pos getStartPos(int no) { // Identify start position of Car no.
        return startpos[no];
    }

    public Pos getBarrierPos(int no) { // Identify pos. right before barrier
                                       // line
        return barrierpos[no];
    }

    public Pos nextPos(int no, Pos pos) {
        // Get from Layout
        return Layout.nextPos(no, pos);
    }

    public void println(String m) {
        // Print (error) message on screen
        Runnable job = new LinePrinter(m);
        EventQueue.invokeLater(job);
    }

    /* The car operations work directly upon the simulator */

    public CarI newCar(int no, Color col, Pos pos) {
        return new CarModel(no, col, pos, sim);
    }

    public void register(CarI car) {
        sim.add((CarModel) car);
    }

    public boolean deregister(CarI car) {
        return sim.remove((CarModel) car);
    }

}

/**
 * For the methods of the CarTestI interface this class wraps them to similar
 * events to be processed by the gui thread.
 */
class CarTestWrapper implements CarTestingI {

    Cars cars;

    public CarTestWrapper(Cars cars) {
        this.cars = cars;
    }

    public void startCar(final int no) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                cars.startCar(no);
            }
        });
    }

    public void stopCar(final int no) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                cars.stopCar(no);
            }
        });
    }

    public void startAll() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                cars.startAll();
            }
        });
    }

    public void stopAll() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                cars.stopAll();
            }
        });
    }

    public void barrierOn() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                cars.barrierOn();
            }
        });
    }

    public void barrierOff() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                cars.barrierOff();
            }
        });
    }
    
    /*
    private Semaphore doneSem;
    
    // Start barrier shut down
    public void startBarrierShutDown() {
        if (doneSem != null) {
            cars.println("WARNING: Shut down already active when startBarrierShutDown() called");
            return;
        }
        final Semaphore done = new Semaphore(0);
        doneSem = done;
        EventQueue.invokeLater( () -> { cars.barrierShutDown(done); }); 
    }  

    public void awaitBarrierShutDown() {
        try {
             if (doneSem != null) {
                doneSem.P();
                doneSem = null;
             } else 
                cars.println("WARNING: no active shut down when awaitBarrierShutDown() called");
         } catch (InterruptedException e) {}
    }
    */
    
    Semaphore setDoneSem;

    // Start setting of threshold (asynchronously) 
    public void startBarrierSet(final int k) {
        if (setDoneSem != null) {
            cars.println("WARNING: setting alread active when startBarrierSet(k) called");
            return;
        }

        final Semaphore done = new Semaphore(0);
        setDoneSem = done;
        EventQueue.invokeLater(new Runnable() {
            public void run() { cars.barrierSet(k, done); }}
                );
    }

    public void awaitBarrierSet() {
        try {
            if (setDoneSem != null) {
                setDoneSem.P();
                setDoneSem = null;
            } else 
                cars.println("WARNING: no active setting when awaitBarrierSet() called");
        } catch (InterruptedException e) {}

    }

    /*
    public void setLimit(final int k) { 
        EventQueue.invokeLater( () -> {
          cars.setLimit(k); 
        });  
    }
    */
    
    public void setSlow(final boolean slowdown) {
        EventQueue.invokeLater(() -> {
            cars.setSlow(slowdown);
        });
    }

    public void removeCar(final int no) {
        EventQueue.invokeLater(() -> {
            cars.removeCar(no);
        });
    }

    public void restoreCar(final int no) {
        EventQueue.invokeLater(() -> {
            cars.restoreCar(no);
        });
    }

    public void setSpeed(final int no, final double speed) {
        EventQueue.invokeLater(() -> {
            cars.setSpeed(no, speed);
        });
    }

    public void setVariation(final int no, final int var) {
        EventQueue.invokeLater(() -> {
            cars.setVariation(no, var);
        });
    }

    /*
     * // This should wait until limit change carried out // For this, a
     * one-time semaphore is used (as simple Future) public void setLimit(final
     * int k) { final Semaphore done = new Semaphore(0);
     * EventQueue.invokeLater(new Runnable() { public void run() {
     * cars.setLimit(k, done); }} ); try { done.P(); } catch
     * (InterruptedException e) {}
     * 
     * }
     */

    // Println already wrapped in Cars
    public void println(final String message) {
        cars.println(message);
    }

}

interface ClockI {
    public long getTime();
}

class Clock implements ClockI {

    long time;
    long base;
    long stamp;
    double rate;
    int active;

    public Clock() {
        stamp = System.currentTimeMillis();
        base = 0;
        rate = 1.0;
        active = 1;
        update();
    }

    synchronized public void update() {
        long now = System.currentTimeMillis();
        time = base + (long) ((now - stamp) * active * rate);
    }

    synchronized void updateBase() {
        long now = System.currentTimeMillis();
        time = base + (long) ((now - stamp) * active * rate);
        base = time;
        stamp = now;
    }

    synchronized public void setRate(double r) {
        if (r < 0.0)
            throw new IllegalArgumentException("Negative Clock rate");

        rate = r;
        updateBase();
    }

    synchronized public void pause() {
        if (active != 0) {
            updateBase();
            active = 0;
        }
    }

    synchronized public void resume() {
        if (active == 0) {
            updateBase();
            active = 1;
        }
    }

    synchronized public long getTime() {
        return time;
    }

}


/*
 * A waypoint represents a position on the track that a car must pass
 * Once reached, an optional callback is executed asynchronously
 * 
 * A waypoint points the next one to head for (if any)
 */
class Waypoint {
    public Pos pos;
    public long id;
    public long loc;
    public long dir;
    public Runnable cb;
    public Waypoint next;

    // We maintain a common pool of waypoints 
    // in order to avoid ultra-fast cars exhausting memory.
    static Waypoint freeList = null;
    static int created = 0;
    static int inFreeList = 0;

    synchronized static Waypoint allocate(Pos pos, long id) {

        if (freeList == null) {
            freeList = new Waypoint();
            created++;
            inFreeList++;
        }

        Waypoint wp = freeList;
        freeList = freeList.next;
        inFreeList--;

        wp.pos = pos;
        wp.id = id;
        wp.loc = Location.location(pos);
        wp.dir = Location.encode(0, 0);
        wp.cb = null;
        wp.next = null;

        return wp;
    }

    synchronized static void free(Waypoint wp) {
        if (wp == null) {
            throw new IllegalArgumentException("Waypoint.free() called with null argument");
        }

        wp.pos = null;
        wp.id = 0L;
        wp.loc = 0L;
        wp.dir = 0L;
        wp.cb = null;

        wp.next = freeList;
        freeList = wp;
        inFreeList++;
    }

    synchronized static void printStats() {
        System.out.println("Waypoints created: " + created + ", free: " + inFreeList);
    }

}

class CarModel implements CarI, Tickable {

    /*
     * CarModel models the movement of a car along a route of waypoints
     * (destinations)
     * 
     * The movement is driven by calls of tick() from the virtual clock
     * provided.
     * 
     * The route is represented by a linked list of waypoints identified 
     * by monotonically increasing integers. 
     * 
     * Important: All positions used by this class are canonical and hence may
     * be compared using ==.
     */

    static final double accel = 200.0;  // Unit: tiles/(s*s)
    static final double decel =  50.0;  // Unit: tiles/(s*s)
    
    static final double slowFactor = 0.3;

    /* Using a common background server thread for call-backs */
    static ExecutorService exec = Executors.newSingleThreadExecutor();

    int no;      // Immutable
    Color color; // Immutable

    Waypoint start;      // Start of current linear stretch
    Waypoint last;       // Last way-point visited
    Waypoint cur;        // Way-point headed for -- current goal
    Waypoint target;     // Target of current linear stretch
    Waypoint dest;       // Latest way-point added -- current final destination

    double nominalSpeed; // Tiles per sec. Negative ~ infinite
    double currentSpeed;

    boolean running;

    long baseStamp;
    long saveElapsed;
    long baseLoc;       // Last starting location
    long loc;           // current interpolated location
    long step;          // Vector from baseLoc to current goal
    double len;         // Length of step vector

    static volatile boolean slowdown = false;  // Go slow in slowdown area (shared
                                               // by all cars)

    ClockI clk;
    Simulator<CarModel> sim;

    long wpCounter = 0;
    long lastArrival = 0;

    public CarModel(int no, Color col, Pos pos, Simulator<CarModel> sim) {
        this.color = col;
        this.no = no;

        // Get canonical position object (checking validity)
        Pos canPos = Layout.canonical(pos);

        Waypoint wp = Waypoint.allocate(canPos, wpCounter);

        start  = wp;
        last   = wp;
        cur    = wp;
        target = wp;
        dest   = wp;

        this.sim = sim;
        clk = sim.getClock();

        nominalSpeed = 4.0; // Fairly slow

        loc = Location.location(wp.pos);
        baseLoc = loc;
        baseStamp = clk.getTime();
        saveElapsed = 0;
        running = false;
    }

    public int getNo() {
        return no;
    }

    public Color getColor() {
        return color;
    }

    public void driveTo(Pos pos) throws InterruptedException {
        long id = addDestination(pos);
        awaitArrivalAt(id);
    }

    public long addDestination(Pos pos) {
        return addDestination(pos, null);
    }

    public long addDestination(Pos pos, Runnable callback) {
        // Do not add while being updated/painted
        synchronized (sim) {
            return simLockedAddDestination(pos, callback);
        }
    }

    synchronized public long simLockedAddDestination(Pos pos, Runnable callback) {
        // Get canonical position object (checking validity)
        Pos canPos = Layout.canonical(pos);

        if ((dest.pos.col - canPos.col) * (dest.pos.row - canPos.row) != 0) {
            throw new IllegalArgumentException("Illegal drive from " + dest.pos + " to " + canPos);
        }

        wpCounter++;

        // If position not changed, coalesce with previous.
        if (dest.pos == canPos) {
            if (callback != null) {
                throw new IllegalArgumentException("Callback not allowed for repeated destinations");
            }

            dest.id = wpCounter;
            if (lastArrival == wpCounter - 1) {
                arriveAt(dest);
            }
            return wpCounter;
        }

        /* A non-zero move is requested */
        Waypoint wp = Waypoint.allocate(canPos, wpCounter);

        /* Set direction *towards* new destination */ 
        long v = Location.sub(Location.location(canPos), Location.location(dest.pos));
        long dir = Location.direction(v);
        wp.dir = dir;

        wp.cb = callback;

        // Make it the new final destination
        dest.next = wp;
        dest      = wp;

        // See if current linear stretch can be extended
        extendTarget();

        updateLocation();

        return wpCounter;
    }

    void extendTarget() {
        while (target.next != null) {
            if (!sameDir(target.dir, target.next.dir))
                break;
            target = target.next;
        }
    }

    boolean sameDir(long v1, long v2) {
        long v2t = Location.rot(v2);
        return Location.prod(v1, v2t) == 0.0 && Location.prod(v1, v2) > 0;
    }

    synchronized public void tick() {
        if (running) {
            updateLocation();
        } else {
            System.err.println("Car ticked while not running");
        }
    }

    // Called before ticking starts. Restore elapsed simulation time.
    synchronized public void start() {
        if (!running) {
            baseStamp = clk.getTime() - saveElapsed;
            running = true;
        }
    }

    // Called when ticking ceases. Save elapsed simulation time.
    synchronized public void stop() {
        if (running) {
            saveElapsed = clk.getTime() - baseStamp;
            running = false;
        }
    }

    synchronized public long getLocation() {
        return loc;
    }

    synchronized public boolean hasArrivedAt(long id) {
        if (id > wpCounter) {
            throw new IllegalArgumentException("Destination id: " + id + " wpCounter: " + wpCounter);
        }

        return lastArrival >= id;
    }

    synchronized public void awaitArrivalAt(long id) throws InterruptedException {
        if (id > wpCounter) {
            throw new IllegalArgumentException("Destination id: " + id + " wpCounter: " + wpCounter);
        }

        while (lastArrival < id) {
            wait();
        }
    }

    void arriveAt(Waypoint wp) {
        if (lastArrival > wp.id) { 
            throw new IllegalArgumentException("arriveAt waypoint out of order"); 
        }
        
        lastArrival = wp.id;
        if (wp.cb != null) {
            exec.submit(wp.cb);
        }
        notifyAll();
    }

    public static void setSlow(boolean slowdown) {
        CarModel.slowdown = slowdown;
    }

    boolean arriving(long l) {
        return Location.dist(l, cur.loc) <= Ground.margin;
    }

    boolean arrival(long l1, long l2) {
        return !arriving(l1) && arriving(l2);
    }

    /*
     * Discrete 3-step approximation of a braking/starting parabola
     */
    double parabola(double dist, double accel) {
        double fullDist = (nominalSpeed*nominalSpeed) / (2*accel);
        double frac = dist/fullDist;
        if (frac < 0.10) { return nominalSpeed * 0.05; } 
        if (frac < 0.30) { return nominalSpeed * 0.25; } 
        if (frac < 0.75) { return nominalSpeed * 0.80; } 
        return nominalSpeed;
    }

   boolean isSlow() {
        return (CarModel.slowdown && Layout.isSlowPos(Location.position(loc)));
    }

    public void setSpeed(double speed) {
        // Do not change while being updated/painted
        synchronized (sim) {
            simLockedSetSpeed(speed);
        }
    }

    public synchronized void simLockedSetSpeed(double speed) {
        updateLocation();
        this.nominalSpeed = speed;
        updateLocation();
    }

    double calcSpeed() {
        if (nominalSpeed < 0.0) { return 0.0; }
                
        double distFromStart = Location.dist(loc, start.loc);
        double distToTarget  = Location.dist(loc, target.loc);
        double leaveSpeed    = parabola(distFromStart,accel);
        double approachSpeed = parabola(distToTarget, decel);
        
        double standardSpeed = Math.min(approachSpeed, leaveSpeed);
        return standardSpeed * (isSlow() ? slowFactor : 1.0);

        // double standardSpeed = Math.min(nominalSpeed, Math.min(arrivalCap(), departureCap()));
        // return standardSpeed * (isSlow() ? slowFactor : 1.0) * (nearTarget(loc) ? nearFactor : 1.0);
    }

    /*
     * While current goal not reached, the step is a vector
     * starting at baseLoc ending at the current goal.
     * The actual location is given by the current speed times
     * the time elapsed from baseStamp
     */
    void fixStep() {
        if (cur == last)
            return;

        baseStamp = clk.getTime();
        baseLoc = loc;
        step = Location.sub(cur.loc, baseLoc);
        len = Location.len(step);
        currentSpeed = calcSpeed();
        // System.out.println("Changing speed of " + no + " to " + currentSpeed);
    }

    /*
     * updateLocation calculates the actual location along the current
     * step vector. 
     * 
     * If the current goal is reached, this is recorded and a new goal defined.
     */
    void updateLocation() {

        if (!running)
            return;

        if (last != cur) {
            long oldLoc = loc;
            if (nominalSpeed < 0.0) {
                last = cur;
                loc  = cur.loc;
            } else {
                long now = clk.getTime();
                long deltaTime = now - baseStamp;
                double travelled = currentSpeed * deltaTime / 1000;
                if (travelled >= len) {
                    // Current goal fully reached
                    last = cur;
                    loc = cur.loc;

                } else {
                    long move = Location.scale(step, travelled / len);
                    loc = Location.add(baseLoc, move);
                }
            }

            if (arrival(oldLoc, loc)) {
                arriveAt(cur);
            }

            if (currentSpeed != calcSpeed()) {
                fixStep();
            }

        }

        while (last == cur) {

            if (cur == target) {
                // Linear stretch ended -- free way-points from start
                while (start != target) {
                    Waypoint wp = start;
                    start = start.next;
                    Waypoint.free(wp);
                }
            }

            if (dest == cur)
                break;

            // More way-points ahead
            cur = cur.next;
            if (last == target) {
                target = cur;
                extendTarget();
            }

            if (nominalSpeed < 0) {
                last = cur;
                loc  = cur.loc;
                arriveAt(cur);
            } else {
                fixStep();
            }
        }

    }
}

interface Procedure<T> {
    void apply(T t);
}

interface Tickable {
    public void tick();

    public void start();

    public void stop();
}

class Simulator<Item extends Tickable> {

    public static final int tickInterval = 5; // Milliseconds

    private List<Item> items = new ArrayList<Item>();

    private java.util.Timer timer;

    private final Clock clock = new Clock();

    public Simulator() {
        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            public void run() {
                tick();
            }
        }, tickInterval, tickInterval);
    }

    public Clock getClock() {
        return clock;
    }

    synchronized public void pause() {
        clock.pause();
    }

    synchronized public void resume() {
        clock.resume();
    }

    synchronized public void add(Item x) {
        int i = items.indexOf(x);
        if (i >= 0)
            return;
        x.start();
        items.add(x);
    }

    synchronized public boolean remove(Item x) {
        int i = items.indexOf(x);
        if (i < 0)
            return false;
        items.remove(i);
        x.stop();
        return true;
    }

    synchronized void tick() {
        clock.update();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            items.get(i).tick();
        }
    }

    synchronized void applyToAll(Procedure<Item> f) {
        int size = items.size();
        for (int i = 0; i < size; i++) {
            f.apply(items.get(i));
        }
    }

}

class Location {

    /*
     * Locations are fine-grained positions within the playground represented as
     * fixed decimal values of the Pos coordinate system. (I.e. the x-axis
     * points downwards and the y axis to the right.)
     * 
     * Locations are encoded as two ints within a long for efficient
     * communication to the GUI.
     * 
     * This class provides various conversion functions for this encoding as
     * well as some vector operations.
     */

    final public static int resolution = 10000;

    static long encode(double x, double y) {
        int xdec = (int) (x * resolution);
        int ydec = (int) (y * resolution);

        return encode(xdec, ydec);
    }

    static long location(Pos pos) {
        int xdec = pos.row * resolution;
        int ydec = pos.col * resolution;

        return encode(xdec, ydec);
    }

    static Pos position(long loc) {
        int row = (locX(loc) + resolution / 2) / resolution;
        int col = (locY(loc) + resolution / 2) / resolution;

        return Layout.getPos(row, col);
    }

    static long encode(int xdec, int ydec) {
        return (((long) xdec) << 32) | (((long) ydec) & (0xFFFFFFFFL));
    }

    static int locX(long loc) {
        return (int) (loc >> 32);
    }

    static int locY(long loc) {
        return (int) loc;
    }

    static public double getX(long loc) {
        return ((double) locX(loc)) / resolution;
    }

    static public double getY(long loc) {
        return ((double) locY(loc)) / resolution;
    }

    // Add l1 and l2
    static long add(long l1, long l2) {
        return encode(locX(l1) + locX(l2), locY(l1) + locY(l2));
    }

    // Subtract l2 from l1
    static long sub(long l1, long l2) {
        return encode(locX(l1) - locX(l2), locY(l1) - locY(l2));
    }

    // Vector product of l1 and l2
    static double prod(long l1, long l2) {
        return (((double) locX(l1)) / resolution) * (((double) locX(l2)) / resolution)
                + (((double) locY(l1)) / resolution) * (((double) locY(l2)) / resolution);
    }

    // Rotate l1 90 degrees
    static long rot(long l) {
        return encode(-locY(l), locX(l));
    }

    // Scale l with f
    static long scale(long l, double f) {
        return encode((int) (locX(l) * f), (int) (locY(l) * f));
    }

    // (Kind of) unit-vector
    static long direction(long v) {
        double len = len(v);
        if (len == 0.0) { throw new IllegalArgumentException("direction called with null vector"); }
        return scale(v, 1.0 / len);
    }

    // Manhattan length of vector
    static double len(long v) {
        return ((double) Math.abs(locX(v))) / resolution + ((double) Math.abs(locY(v))) / resolution;
    }

    // Manhattan distance between two locations
    static double dist(long l1, long l2) {
        return len(sub(l1, l2));
    }

    static boolean onTrack(long loc) {
        return (locX(loc) % resolution) * (locY(loc) % resolution) == 0;
    }

    static String toString(long loc) {
        return "(" + locX(loc) + "," + locY(loc) + ")";
    }

}

/*
 * The static Layout class defines the concrete topology of the playground
 * including car tracks
 */
class Layout {

    public static final int ROWS = 11;
    public static final int COLS = 12;

    static final int upperBarRow = 5;
    static final int lowerBarRow = 6;

    private static Pos[][] posCache = new Pos[ROWS][COLS];
    private static Pos[][] tracksCW = new Pos[ROWS][COLS];
    private static Pos[][] tracksCCW = new Pos[ROWS][COLS];

    private static Pos[] branchCW = new Pos[4];
    private static Pos[] branchCCW = new Pos[4];

    /* Bridge */
    static final int bridgeUpperRow = 9;
    static final int bridgeLowerRow = 10;
    static final int bridgeLeftCol = 1;
    static final int bridgeRightCol = 3;

    public static Pos getPos(int i, int j) {
        return posCache[i][j];
    }

    public static Pos canonical(Pos pos) {
        try {
            return posCache[pos.row][pos.col];
        } catch (Exception e) {
            throw new IllegalArgumentException("Position out of grounds: " + pos);
        }
    }

    static {
        // Fill position cache
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                posCache[i][j] = new Pos(i, j);
            }
        }

        // Fill next position with stall values.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                tracksCW[i][j] = getPos(i, j);
                tracksCCW[i][j] = getPos(i, j);
            }
        }

        // Define next position of all counter-clockwise tracks (except
        // branching points)

        // Alley (upwards)
        for (int i = 1; i <= 10; i++) {
            tracksCW[i][0] = getPos(i - 1, 0);
        }
        // Private lanes
        for (int no = 5; no <= 8; no++) {
            for (int i = 1; i <= 9; i++) {
                tracksCW[i][no + 3] = getPos(i + 1, no + 3);
            }
        }
        // Upper lane (without shed)
        for (int j = 0; j <= 10; j++) {
            tracksCW[0][j] = getPos(0, j + 1);
        }
        // Lower lane
        for (int j = 1; j <= 11; j++) {
            tracksCW[10][j] = getPos(10, j - 1);
        }

        // Shed avoidance
        // tracksCW[1][0] = getPos(1, 1);
        // tracksCW[1][1] = getPos(1, 2);
        // tracksCW[1][2] = getPos(0, 2);

        // CW branching points
        for (int no = 5; no <= 8; no++) {
            branchCW[no - 5] = getPos(1, no + 3);
        }

        // Define next position of all clockwise tracks (except branching
        // points)

        // Toddler's special path
        tracksCCW[5][2] = getPos(6, 2);
        tracksCCW[6][2] = getPos(6, 3);
        tracksCCW[6][3] = getPos(5, 3);
        tracksCCW[5][3] = getPos(5, 2);

        // Alley (downwards)
        for (int i = 1; i <= 8; i++) {
            tracksCCW[i][0] = getPos(i + 1, 0);
        }
        // Private lanes (some parts overwritten below)
        for (int no = 1; no <= 4; no++) {
            for (int i = 2; i <= 8; i++) {
                tracksCCW[i][no + 3] = getPos(i - 1, no + 3);
            }
        }
        // Upper lane (car nos. 3,4)
        for (int j = 1; j <= 7; j++) {
            tracksCCW[1][j] = getPos(1, j - 1);
        }
        // Upper lane (car nos. 1,2)
        for (int j = 1; j <= 5; j++) {
            tracksCCW[2][j] = getPos(2, j - 1);
        }
        // Lower lane
        for (int j = 0; j <= 6; j++) {
            tracksCCW[9][j] = getPos(9, j + 1);
        }

        // CCW branching points
        for (int no = 1; no <= 4; no++) {
            branchCCW[no - 1] = getPos(8, no + 3);
        }
    }

    public static boolean isBridgePos(Pos p) {
        if (p.row < bridgeUpperRow || p.row > bridgeLowerRow)
            return false;
        return (p.col >= bridgeLeftCol && p.col <= bridgeRightCol);
    }

    public static boolean isShedPos(Pos p) {
        return false; // (p.row == 0 && p.col < 2); // Upper left corner
    }

    public static boolean isHutPos(Pos p) {
        if (p.row < 3 || p.row > 8 || p.col < 1 || p.col > 3)
            return false;
        return (p.row < 5 || p.row > 6 || p.col < 2);
    }

    public static boolean isToddlerPos(Pos p) {
        return (p.col >= 2 || p.col <= 3 || p.row >= upperBarRow || p.row <= lowerBarRow);
    }

    public static boolean isSlowPos(Pos p) {
        return (p.col == 0 && p.row > 0);
    }

    public static Pos getStartPos(int no) {
        if (no == 0)
            return getPos(upperBarRow, 2);
        if (no >= 5)
            return getPos(upperBarRow - 1, 3 + no);
        return getPos(lowerBarRow + 1, 3 + no);
    }

    public static Pos getBarrierPos(int no) {
        return getPos(no < 5 ? lowerBarRow : upperBarRow, 3 + no);
    }

    public static Pos getBarrierUpperPos(int no) {
        return getPos(upperBarRow, 3 + no);
    }

    public static Pos getBarrierLowerPos(int no) {
        return getPos(lowerBarRow, 3 + no);
    }

    public static Pos nextPos(int no, Pos pos) {

        int mycol = 3 + no;

        if (no < 5) {
            // CCW
            if (pos.row == 9 && pos.col == mycol) {
                // Branching point
                return branchCCW[no - 1];
            } else {
                return tracksCCW[pos.row][pos.col];
            }
        } else {
            // CW
            if (pos.row == 0 && pos.col == mycol) {
                // Branching point
                return branchCW[no - 5];
            } else {
                return tracksCW[pos.row][pos.col];
            }
        }
    }

}

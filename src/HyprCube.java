// | Java Source Code for Stereoscopic Animated HyperCube applet
// | This is a Java 1.0 applet.
// | Copyright (c) Mark Newbold, 1996-2014
// | Last modified May 28, 2014
// |   (Changed name of "object" parameter to "obj" due to conflict with Java 1.3 Plug-in.)
// | Usable by permission for noncommercial purposes.
// | http://dogfeathers.com
// |
// | Optional Parameters:
// |   NAME=obj         VALUE=cube or VALUE=24cell or VALUE=crosspoly or VALUE=simplex
// |   NAME=projection  VALUE=n    where n is a number between 0 and 95, divisible by 5
// |   NAME=speed       VALUE=n    where n is a number between 1 and 100

import java.awt.*;
import java.lang.Math;
import java.util.*;

public class HyprCube extends java.applet.Applet
{
    private HyprCubeFrame myFrame;
    private Container myParent;
    private HyprCubePnl3 pnlDraw;
    private Panel pnlCtl;

    private Label  lblProj;
    private Label  lblSpeed;
    private Button btnProjMinus;
    private TextField txProj;
    private Button btnProjPlus;
    private Button btnSpeedMinus;
    private TextField txSpeed;
    private Button btnSpeedPlus;

    private Button btnStartStop;
    private Button btnDetach;
    private Button btnStereo;
    private Dimension dimOrigSize;

    boolean bStandalone = false;

    // Parameters
    private int objnum = 1;  // 1=cube 2=24-cell 3=cross-polytope 4=simplex
    private int proj = 0;    // current projection factor
    private int speed = 10;  // current speed
    private int maxspeed = 100;   // max allowed speed
    private int stereo_opt = 0;   // 0=red-blue  1=red-green  2=look-crossed

    double getProj() { return Math.sqrt(proj/100.0); }
    double getSpeed() { return Math.sqrt(speed / 10.0); }
    int getStereoOpt() { return stereo_opt; }
    int getObjnum() { return objnum; }

    private void putInFrame()
    {
        // hang onto the size for when we removeFromFrame
        dimOrigSize = new Dimension(size());

        myParent = getParent();

        String strFrameName;

        switch (objnum) {
            case 2:  strFrameName = "24-Cell"; break;
            case 3:  strFrameName = "Cross Polytope"; break;
            case 4:  strFrameName = "Simplex"; break;
            default: strFrameName = "Hypercube"; break;
        }

        myFrame = new HyprCubeFrame(this, strFrameName);
        Dimension siz = new Dimension(dimOrigSize);
        myFrame.resize(siz);
        myFrame.add("Center",this);
        myFrame.show();
        if (btnDetach != null) btnDetach.setLabel(" Attach ");
    }

    void removeFromFrame()
    {
        myFrame.remove(this);
        myFrame.dispose();
        myFrame = null;
        myParent.add("Center",this);
        resize(dimOrigSize);
        myParent.show();
        if (btnDetach != null) btnDetach.setLabel(" Detach ");
    }

    static public void main(String args[])
    {
        HyprCube hc = new HyprCube();

        int len = args.length;
        if (len >= 1) hc.parseObjParam(args[0]);
        if (len >= 2) hc.parseProjParam(args[1]);
        if (len >= 3) hc.parseSpeedParam(args[2]);
        hc.bStandalone = true;
        hc.resize(new Dimension(400,458));
        hc.init();
        hc.putInFrame();
        hc.start();
    }

    private void parseObjParam(String paramString)
    {
        String objNames[] = { "cube", "24cell", "crosspoly", "simplex" };

        int m;
        for (m=0; m < 4; m++)
            if (paramString.equalsIgnoreCase(objNames[m])) { objnum = m+1; return; }
    }

    private void parseProjParam(String paramString)
    {
        Integer newproj = new Integer(0);   // Integer wrapper
        int newp;
        newp = newproj.parseInt(paramString);
        if (newp < 0) newp = 0;
        if (newp > 95) newp = 95;
        proj = newp - (newp % 5);
    }

    private void parseSpeedParam(String paramString)
    {
        Integer newspeed = new Integer(0);   // Integer wrapper
        int newsp;
        newsp = newspeed.parseInt(paramString);
        if (newsp < 1) newsp = 1;
        if (newsp > maxspeed) newsp = maxspeed;
        speed = newsp;
    }

    public void init()
    {
        System.out.println(getAppletInfo());

        if (!bStandalone) {
            // Check parameters
            String paramString;

            paramString = getParameter("obj");
            if (paramString != null) parseObjParam(paramString);

            paramString = getParameter("projection");
            if (paramString != null) parseProjParam(paramString);

            paramString = getParameter("speed");
            if (paramString != null) parseSpeedParam(paramString);
        }

        setLayout(new BorderLayout());

        pnlDraw = new HyprCubePnl3(this);
        add("Center",pnlDraw);

        pnlCtl = new Panel();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        pnlCtl.setLayout(gridbag);

        lblProj = new Label("Projection:");
        lblSpeed = new Label("Speed:");

        btnProjMinus = new Button(" - ");
        txProj = new TextField("XXXX");
        txProj.setEditable(false);
        btnProjPlus = new Button(" + ");

        btnSpeedMinus = new Button(" - ");
        txSpeed = new TextField("XXXX");
        txSpeed.setEditable(false);
        btnSpeedPlus = new Button(" + ");

        btnStartStop = new Button(" Stop ");
        btnStereo = new Button(" Stereo ");

        c.insets = new Insets(5,0,0,0);
        c.weightx = 0.1;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(lblProj, c);
        pnlCtl.add(lblProj);

        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(lblSpeed, c);
        pnlCtl.add(lblSpeed);

        if (!bStandalone) {
            btnDetach = new Button(" Detach ");
            c.gridx = 7;
            c.weightx = 4.0;
            c.anchor = GridBagConstraints.EAST;
            gridbag.setConstraints(btnDetach, c);
            pnlCtl.add(btnDetach);
        }

        c.insets = new Insets(0,0,5,0);
        c.gridy = 1;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridwidth = 1;
        c.weightx = 0.4;
        c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(btnProjMinus, c);
        pnlCtl.add(btnProjMinus);

        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(txProj, c);
        pnlCtl.add(txProj);

        c.weightx = 3.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(btnProjPlus, c);
        pnlCtl.add(btnProjPlus);

        c.gridx = GridBagConstraints.RELATIVE;
        c.gridwidth = 1;
        c.weightx = 0.4;
        c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(btnSpeedMinus, c);
        pnlCtl.add(btnSpeedMinus);

        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(txSpeed, c);
        pnlCtl.add(txSpeed);

        c.weightx = 3.0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints(btnSpeedPlus, c);
        pnlCtl.add(btnSpeedPlus);

        c.gridwidth = 1;
        c.weightx = 4.0;
        c.gridx = 6;
        c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(btnStartStop, c);
        pnlCtl.add(btnStartStop);

        c.gridx = 7;
        c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(btnStereo, c);
        pnlCtl.add(btnStereo);

        add("South",pnlCtl);

        setTxProj();
        setTxSpeed();

        pnlDraw.init();
        pnlDraw.show();
        start();
    }

    public void start()
    {
        btnStartStop.setLabel(" Stop ");
        pnlDraw.start();
    }

    public void stop()
    {
        btnStartStop.setLabel(" Start ");
        pnlDraw.stop();
    }

    public boolean action(Event evt, Object arg)
    {
        if (evt.id == evt.ACTION_EVENT) {
            int newproj = proj;

            if ((evt.target == btnProjMinus) && (newproj >= 5)) newproj -= 5;
            if ((evt.target == btnProjPlus) && (newproj <= 90)) newproj += 5;
            if (newproj != proj) {
                proj = newproj;
                setTxProj();
                pnlDraw.repaint();
                return true;
            }

            int newspeed = speed;
            if ((evt.target == btnSpeedMinus) && (newspeed > 1)) newspeed--;
            if ((evt.target == btnSpeedPlus) && (newspeed < maxspeed)) newspeed++;
            if (newspeed != speed) {
                speed = newspeed;
                setTxSpeed();
                return true;
            }

            if (evt.target == btnStartStop) {
                if (arg == " Stop ") stop();
                else
                if (arg == " Start ") start();
                return true;
            }

            if ((evt.target == btnDetach) && (btnDetach != null)) {
                if (myFrame == null) putInFrame();
                else removeFromFrame();
                return true;
            }

            if (evt.target == btnStereo) {
                stereo_opt++;
                if (stereo_opt > 2) stereo_opt = 0;
                pnlDraw.makecolors();
                pnlDraw.repaint();
            }
        }

        return false;
    }

    private void setTxProj()
    {
        Double dbl = new Double(proj/100.0);  // Double wrapper
        txProj.setText(dbl.toString());
    }

    private void setTxSpeed()
    {
        Integer spd = new Integer(speed);  // Integer wrapper
        txSpeed.setText(spd.toString());
    }

    public String getAppletInfo()
    {
        return "HyprCube applet v1.3 Copyright 1996-2014 Mark Newbold\nLast updated: May 28, 2014\nAuthor: Mark Newbold\nhttp://dogfeathers.com";
    }
}


class HyprCubePnl3 extends Panel implements Runnable
{
    private Image offscreenImg;
    private Graphics offscreenG;
    private Dimension offscreensize;
    private Thread runner;
    private Random rand = new Random();

    private final double velmax = .03;  // max velocity, radians per cycle
    private final double velinc = .006; // velocity increment, radians
    private final int    delay  = 50;   // sleep milliseconds

    private double vertices[][];  // vertex coords in 4-space
    private int vert2xR[];   // screen coords of vertices
    private int vert2xL[];
    private int vert2y[];

    private byte edges[][];       // "from" and "to" vertex indices
    private double vel[][] = new double[4][4];
    private double m1[][] = new double[4][4];
    private double m2[][] = new double[4][4];
    private double rot4[][] = new double[4][4];
    private double ROT4[][] = new double[4][4];
    private double ROT4A[][] = new double[4][4];
    private double newROT[][] = new double[4][4];
    private double ROTM[][] = new double[4][4];
    private double holdROT[][];
    private double rotvert[] = new double[4];
    private double vec1[] = new double[3];
    private double vec2[] = new double[3];
    private double vec3[] = new double[3];

    private double R4;     // radius in 4-space
    private int dx,dy;     // screen width,height
    private int dx_offset; // dx shift for stereo opt 2 (look-crossed)
    private int xbase,ybase;  // center of screen, in pixels

    // projection parameters
    // Calculated by "calcProjParms"
    private double fac,dfac,deps,deltar,vpfR,R3,epsfac;

    private boolean bLeftFirst;
    private boolean bTracking = false;
    private boolean bShiftDown;
    private int mouseX, mouseY;  // last mouse X and Y

    private Color leftColor,rightColor,backgColor;

    private HyprCube owner;

    HyprCubePnl3(HyprCube own)  // constructor
    {
        owner = own;
    }

    private void defineCube()
    {
        int i,j,k,dif,ct;

        vertices = new double[16][4];
        edges = new byte[32][2];

        // create the vertices
        for (i=0; i < 16; i++) {
            for (j=0; j < 4; j++) vertices[i][j] = ((i >> (3-j)) & 1) - 0.5;
        }

        // Create the edges
        // Considering each vertex to be a 4-bit bit-pattern, there
        //   is an edge between each pair of vertices that differ in only
        //   one bit.
        k = 0;
        for (i=0; i < 15; i++) {
            for (j=i+1; j < 16; j++) {
                ct = 0;
                for (dif=i^j; dif != 0; dif >>= 1) if ((dif&1) != 0) ct++;
                if (ct == 1) {
                    edges[k][0] = (byte)i;
                    edges[k][1] = (byte)j;
                    k++;
                }
            }
        }
    }

    private void define24Cell()
    {
        byte bitss[] = new byte[24];  // 4-bit values labelling the
                                      //    hypercube squares
        byte masks[] = new byte[24];  // masks indicating which
                                      //    particular 2 bits are significant
        int mask,bits;
        int i,j,k,m,n,d,e;

        vertices = new double[24][4];  // vertex coords in 4-space
        edges = new byte[96][2];    // "from" and "to" vertex indices

        // We construct the 24-Cell by making 3-d octahedra in each of the 3-cubes
        //   that form a hypercube.  The 6 vertices of the each octahedron touch
        //   in the center of each face of the 3-cube.
        // Hypercube vertices are labeled by 4-bit values.
        // A 3-cube within a hypercube is formed from all vertices
        //    that have the same value of a particular bit.
        // A 2-square is formed from all vertices that have the
        //    same values of 2 particular distinct bits.
        // We use the bitss and maskss arrays to label the vertices according to
        //   which 2-square they lie on.
        i = 0;   // vertex index
        for (m=0; m < 4; m++) {
            for (n=0; n < m; n++) {
                // m and n are a pair of distinct bit indexes
                mask = (1 << m) | (1 << n);
                for (j=0; j < 2; j++) {
                    for (k=0; k < 2; k++) {
                        bits = (j << m) | (k << n);
                        masks[i] = (byte)mask;
                        bitss[i] = (byte)bits;

                        for (d=0; d < 4; d++) {
                            vertices[i][d]
                              = ((mask >> d) & 1) != 0
                              ? 2 * ((bits >> d) & 1) - 1
                              : 0;
                        }
                        i++;
                    }
                }
            }
        }


        // Construct the 96
        // Loop thru the 3-cubes
        // A 3-cube is all vertices that have a single particular bit value in common.
        // We make an edge from the center of each 2-square of the 3-cube to
        //   the center of each adjacent 2-square.
        e = 0;
        for (m=0; m < 4; m++) {  // for each particular bit
            mask = (1 << m);
            for (n=0; n < 2; n++) {  // for each value of that bit
                bits = (n << m);

                // Loop thru all pairs of adjacent squares of the 3-cube.
                for (j=0; j < 24; j++) {
                    if ((mask & masks[j]) == 0)
                        continue;  // square doesn't belong to cube

                    if ((bits & mask) != (bitss[j] & mask))
                        continue;  // square doesn't belong to cube

                    for (k=0; k < j; k++) {
                        if ((mask & masks[k]) == 0)
                            continue;  // square doesn't belong to cube

                        if ((bits & mask) != (bitss[k] & mask))
                            continue;  // square doesn't belong to cube

                        if (masks[j] == masks[k])
                            continue;  // skip opposing squares

                        edges[e][0] = (byte)j;
                        edges[e][1] = (byte)k;
                        e++;
                    }
                }
            }
        }
    }

    private void defineCrossPoly()
    {
        vertices = new double[8][4];  // vertex coords in 4-space

        byte edges[][] = {    // "from" and "to" vertex indices
          { 0, 2 },
          { 0, 3 },
          { 1, 3 },
          { 1, 2 },
          { 0, 4 },
          { 1, 4 },
          { 2, 4 },
          { 3, 4 },
          { 0, 5 },
          { 1, 5 },
          { 2, 5 },
          { 3, 5 },
          { 0, 6 },
          { 1, 6 },
          { 2, 6 },
          { 3, 6 },
          { 4, 6 },
          { 5, 6 },
          { 0, 7 },
          { 1, 7 },
          { 2, 7 },
          { 3, 7 },
          { 4, 7 },
          { 5, 7 }
        };

        int i,j,k;

        // Create the vertices
        j = k = 0;
        for (i=0; i < 4; i++) {
            vertices[j++][k] = -1;
            vertices[j++][k] = 1;
            k++;
        }

        this.edges = edges;
    }

    private void defineSimplex()
    {
        vertices = new double[5][4];   // vertex coords in 4-space

        byte edges[][] = {    // "from" and "to" vertex indices
            { 0, 1 },
            { 0, 2 },
            { 0, 3 },
            { 0, 4 },
            { 1, 2 },
            { 1, 3 },
            { 1, 4 },
            { 2, 3 },
            { 2, 4 },
            { 3, 4 },
        };

        int i,j,k;

        // Create the vertices
        // vertices[0][?] is initialized to zeros (default initialization)
        // This represents a single point at the origin
        double dist,sumsq,avg;

        // Add additional dimensions 1, 2, 3 and 4
        for (i=1; i < 5; i++) {
            sumsq = 0.0;
            for (j=0; j < i; j++) {
                avg = 0.0;
                for (k=0; k < i; k++) avg += vertices[k][j];
                avg /= i;
                dist = (vertices[0][j] - avg);
                sumsq += (dist * dist);
                vertices[i][j] = avg;
            }
            vertices[i][i-1] = Math.sqrt(1.0 - sumsq);
        }

        centerTheObject();

        this.edges = edges;
    }

    private void centerTheObject()
    {
        int i,j;
        int len = vertices.length;
        double avg;
        for (i=0; i < 4; i++) {
            avg = 0.0;
            for (j=0; j < len; j++) avg += vertices[j][i];
            avg /= len;
            for (j=0; j < len; j++) vertices[j][i] -= avg;
        }
    }

    void makecolors()
    {
        int redval;
        int blueval;
        int backgval;

        switch (owner.getStereoOpt()) {
            case 0: // red-blue
                redval   = 188;
                blueval  = 255;
                backgval = 84;
                leftColor   = new Color(redval,backgval,0);
                rightColor  = new Color(0,backgval,blueval);
                backgColor = new Color(0,backgval,0);       // To minimize "ghosts" where one eye sees the other eye's image
                break;
            case 1: // red-green
                // Use Stefan Scheller's recommended colors:
                leftColor  = new Color(0,239,0);
                rightColor   = new Color(255,0,0);
                backgColor = new Color(222,222,222);
                break;
            case 2: // look-crossed
                backgval = 198;
                leftColor   = new Color(0,0,0);
                rightColor  = new Color(0,0,0);
                backgColor = new Color(backgval,backgval,backgval);
                break;
        }
    }

    public void init()
    {

        makecolors();

        // Seed the random number generator.
        Date date = new Date();
        rand.setSeed(date.getTime());

        switch (owner.getObjnum()) {
            case 1:
                defineCube();
                break;
            case 2:
                define24Cell();
                break;
            case 3:
                defineCrossPoly();
                break;
            case 4:
                defineSimplex();
                break;
            default:
                defineCube();
                break;
        }

        // Calculate the radius of the figure in 4-space.
        // Since all the vertices have the same radius, we just look at the first vertex.
        double sum = 0.0;
        int k;
        for (k=0; k < 4; k++) sum += vertices[0][k] * vertices[0][k];
        R4 = Math.sqrt(sum);

        // Alloc arrays for screen coords of vertices.
        k = vertices.length;
        vert2xR = new int[k];
        vert2xL = new int[k];
        vert2y  = new int[k];

        for (k=0; k < 4; k++) ROT4[k][k] = 1.0;
        for (k=0; k < 4; k++) ROTM[k][k] = 1.0;
    }

    public void run()
    {
        setBackground(backgColor);
        while (true) {
            rotate();
            repaint();
            try { Thread.sleep((int)(delay / owner.getSpeed())); }
            catch (InterruptedException e) { }
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }


    public void start() {
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }

    public void stop() {
        if (runner != null) {
            runner.stop();
            runner = null;
        }
    }

    public boolean mouseDown(Event evt, int x, int y)
    {
        if (!bTracking) {
            owner.stop();
            bShiftDown = evt.shiftDown();
            bTracking = true;
            mouseX = x;
            mouseY = y;
            repaint();
        }

        return true;
    }

    private double normalize3Vec(double vec[])
    {
        int m;
        double len,sq;

        len = 0.0;
        for (m=0; m < 3; m++) {
            sq = vec[m] * vec[m];
            len += sq;
        }
        len = Math.sqrt(len);
        for (m=0; m < 3; m++) vec[m] /= len;
        return len;
    }

    public boolean mouseDrag(Event evt, int x, int y)
    {
        if ((mouseX != x) || (mouseY != y)) {

            double s,c;
            int i,j,k;

            // Vector from center to previous mouse position
            vec1[0] = mouseX - xbase;
            vec1[1] = mouseY - ybase;
            vec1[2] = -epsfac;

            // Vector from center to current mouse position
            vec2[0] = x - xbase;
            vec2[1] = y - ybase;
            vec2[2] = -epsfac;

            normalize3Vec(vec1);
            normalize3Vec(vec2);

            // Get the dot product (the cosine of the angle)
            c = 0.0;
            for (k=0; k < 3; k++) c += vec1[k] * vec2[k];

            // The cross product:
            vec3[0] = vec1[1] * vec2[2] - vec1[2] * vec2[1];
            vec3[1] = vec1[2] * vec2[0] - vec1[0] * vec2[2];
            vec3[2] = vec1[0] * vec2[1] - vec1[1] * vec2[0];

            s = normalize3Vec(vec3);   // Returns the sine of the angle

            // Make vec2 perpendicular to vec1 by subtracting off
            //    the part which is parallel.
            for (k=0; k < 3; k++) vec2[k] -= c * vec1[k];
            normalize3Vec(vec2);

            // Now vec1, vec2 and vec3 are an orthonormal basis

            // Build the 3-rotation matrix
            for (i=0; i < 3; i++) {
                for (j=0; j < 3; j++) {
                    rot4[i][j] = vec3[i] * vec3[j]
                               + c * (vec1[i] * vec1[j] + vec2[i] * vec2[j])
                               + s * (vec2[i] * vec1[j] - vec1[i] * vec2[j]);
                }
            }
            for (k=0; k < 3; k++) rot4[3][k] = rot4[k][3] = 0.0;
            rot4[3][3] = 1.0;

            // If shift key was held down, swap the w and z axes in the rotation matrix.
            if (bShiftDown) {
                for (k=0; k < 4; k++) {   // swap rows 2 and 3
                    c = rot4[2][k];
                    rot4[2][k] = rot4[3][k];
                    rot4[3][k] = c;
                }
                for (k=0; k < 4; k++) {   // swap columns 2 and 3
                    c = rot4[k][2];
                    rot4[k][2] = rot4[k][3];
                    rot4[k][3] = c;
                }
            }

            // Apply the small 3-rotation rot4 to the cumulative manual rotation ROTM:
            for (i=0; i < 4; i++) {
                for (j=0; j < 4; j++) {
                  newROT[i][j] = 0;
                  for (k=0; k < 4; k++) newROT[i][j] += rot4[i][k] * ROTM[k][j];
                }
            }

            // swap newROT with ROTM
            holdROT = ROTM;
            ROTM = newROT;
            newROT = holdROT;
        }

        mouseX = x;
        mouseY = y;
        repaint();

        return true;
    }

    public boolean mouseUp(Event evt, int x, int y)
    {
        if (bTracking) {
            bTracking = false;
            repaint();
        }
        return true;
    }

    // rotate
    // increment velocity vector and rotate the object
    private void rotate()
    {
        double angl,sinangl,cosangl,d,dsq,max,veli,vmax;
        int i,j,k,abi,abj;

        max = 0.0;
        abi = 1;
        abj = 2;

        veli = velinc * owner.getSpeed();
        vmax = velmax * owner.getSpeed();

        // The velocity matrix represents the rotation that is to be performed every cycle.
        // It is a 4x4 antisymmetric matrix, with a determinant of zero.
        // We now change it by a small antisymmetric amount (which generally makes the determinant non-zero).
        for (i=0; i < 3; i++) {
            for (j=i+1; j < 4; j++) {
                d = vel[i][j] + veli * (rand.nextDouble() - 0.5);
                vel[i][j] = d;
                vel[j][i] = -d;
                dsq = d * d;
                if (dsq > max) {      // hang onto the indices of the biggest element
                    max = dsq;
                    abi = i;
                    abj = j;
                }
            }
        }

        if (max < 1.0E-10) return;   // no rotation

        // calculate the square root of the determinant
        d =  vel[0][3] * vel[1][2]
            -vel[0][2] * vel[1][3]
            +vel[0][1] * vel[2][3];

        // We need to adjust so that the determinant is zero
        // (abi,abj) are the indices of the largest element
        // Determine the indices of that element's cofactor:
        switch (abi*10+abj) {
            case 1:  i=2; j=3; break;
            case 2:  i=3; j=1; break;
            case 3:  i=1; j=2; break;
            case 12: i=0; j=3; break;
            case 13: i=2; j=0; break;
            case 23: i=0; j=1; break;
            default: i=0; j=1; break;
        }

        // Adjust the cofactor to make the determinant zero.
        vel[i][j] -= d / vel[abi][abj];
        vel[j][i] = -vel[i][j];

        // Calculate the rotation angle (the sum of the squares of the vel elements)
        angl = 0;
        for (i=0; i < 3; i++) {
            for (j=i+1; j < 4; j++) angl += vel[i][j] * vel[i][j];
        }
        angl = Math.sqrt(angl);
        if (angl < 1.0E-5) return;    // no rotation

        // If the angle is too great, reduce all components of the velocity.
        // (Don't want it to rotate too fast.)
        if (angl > vmax) {
            d = vmax / angl;
            angl = vmax;
            for (i=0; i < 3; i++) {
                for (j=i+1; j < 4; j++) {
                    vel[i][j] *= d;
                    vel[j][i] = -vel[i][j];
                }
            }
        }

        // Now we need to build a rotation matrix from "vel".
        // The rotation matrix can be expressed symbolically as
        // R = lim          (I + (vel/n))^n
        //     (n->infinity)
        //
        // Where I is the identity matrix and  "^n" represents the operation
        //   of multiplying the matrix by itself n times.
        // We expand the exponential as a power series in the matrix "vel", noting that R = exp(vel)
        //   and using the standard power series expansion of the exponential.
        // The "vel" matrix has the property that vel . vel . vel = -angl * angl * vel
        //  (where "." is matrix multiplication)
        // Define a matrix m1 as vel / angl.
        // Then m1 . m1 . m1 = -m1
        // Odd powers of m1 can be written as m1^(2n+1) = (-1)^n * m1
        // Even powers of m1 can be written as m1^(2n+2) = (-1)^n * (m1 . m1)
        // Define m2 as m1 . m1
        // Odd powers of vel can be rewritten:  vel^(2n+1) = angl^(2n+1) * (-1)^n * m1
        // Even powers > 0 of vel can be rewritten: vel^(2n+2) = angl^(2n+2) * (-1)^n * m2
        // Rewrite the power series using m1 and m2.
        // The odd terms are a series expansion of sin(angl) * m1
        // The even terms with n > 0 are a series expansion of (1 - cos(angl)) * m2
        // The zero-order term is the identity matrix.

        // Build m1 by scaling vel by an appropriate factor.
        // m1 has the property that m1 . m1 . m1 = -m1
        // (where the "." is matrix multiplication)
        for (i=0; i < 4; i++) m1[i][i] = 0;
        for (i=0; i < 3; i++) {
            for (j=i+1; j < 4; j++) {
                m1[i][j] = vel[i][j] / angl;
                m1[j][i] = -m1[i][j];
            }
        }

        // Build m2, the square of m1:
        for (i=0; i < 4; i++) {
            for (j=i; j < 4; j++) {
                m2[i][j] = 0.0;
                for (k=0; k < 4; k++) m2[i][j] += (m1[i][k] * m1[k][j]);
                m2[j][i] = m2[i][j];
            }
        }

        // Build the rotation matrix
        cosangl = 1.0 - Math.cos(angl);
        sinangl = Math.sin(angl);
        for (i=0; i < 4; i++) {
            for (j=0; j < 4; j++) rot4[i][j] = sinangl*m1[i][j] + cosangl*m2[i][j];
        }
        for (i=0; i < 4; i++) rot4[i][i] += 1.0;

        // Apply the small rotation "rot4" to the cumulative rotation "ROT4"
        for (i=0; i < 4; i++) {
            for (j=0; j < 4; j++) {
                newROT[i][j] = 0.0;
                for (k=0; k < 4; k++) newROT[i][j] += rot4[i][k] * ROT4[k][j];
            }
        }

        // swap newROT with ROT4
        holdROT = ROT4;
        ROT4 = newROT;
        newROT = holdROT;
    }


    // calcProjParms
    // Calculate the following parameters which control the
    //    projection from 4D onto the 2D screen:
    //      fac
    //      dfac
    //      deps
    //      deltar
    //      vpfR
    //      R3
    //      epsfac

    private void calcProjParms()
    {
        // 3-D parameters:
        // These parameters are in units of r, the radius of the 3-sphere
        //   that encloses the 3-space projection of the 4-space object.
        double d = 8.0;       // distance from eyes to screen
        double eps = 1.5;     // distance from screen to center of 3-sphere
        double clip = 0.95;   // fraction of the panel to use

        double q,sx,sy,vpf;

        double delta = (owner.getStereoOpt() == 2) ? 0.5 : 0.3;  // distance from eye to nose

        vpf = owner.getProj(); // viewpoint factor  (0 <= vpf < 1)
                               // inverse of viewpoint 4-distance in units of R

        R3 = R4 / Math.sqrt(1 - vpf * vpf);      // radius in 3-space

        deps = d - eps;

        // Calculate projected size of the 2D image (sx,sy) for R3 == 1.
        q = Math.sqrt(deps*deps + delta*delta);
        sx = 2.0 * (d * Math.tan(Math.asin(1/q)+Math.atan(delta/deps)) - delta);
        if (owner.getStereoOpt() == 2) sx *= 2;

        q = Math.sqrt(deps*deps + delta*delta);
        sy = 2.0 * d * Math.tan(Math.asin(1/deps));

        if (dx * sy < dy * sx) fac = dx/sx;   // window is too tall, constrained by dx
        else                   fac = dy/sy;   // constrained by dy

        fac *= clip;
        epsfac = eps * fac;

        dx_offset = 0;
        if (owner.getStereoOpt() == 2) dx_offset = -(int)(fac * sx / 4);

        fac /= R3;

        deltar = delta * R3;
        vpfR = vpf / R4;
        dfac = d * fac;
    }


    public void paint(Graphics g)
    {
        Dimension dim = size();

        dx = dim.width;
        dy = dim.height;

        if ((dx < 1) || (dy < 1)) return;

        xbase = dx / 2;
        ybase = dy / 2;

        // For double buffering:
        if ((offscreenImg == null) || (dx != offscreensize.width) || (dy != offscreensize.height)) {
            offscreenImg = createImage(dx, dy);
            offscreensize = new Dimension(dim);
            offscreenG = offscreenImg.getGraphics();
            offscreenG.setFont(getFont());
        }

        // Draw background
        offscreenG.setColor(backgColor);
        offscreenG.fillRect(0,0,dx,dy);

        // Calculate projection parameters
        calcProjParms();

        int i,j,k,v,v1;
        double q,fac2,fac3,delt;

        if (owner.getStereoOpt() == 2) delt = -dx_offset;
        else                           delt = (fac-dfac/deps)*deltar;

        if (owner.getStereoOpt() != 2) {  // Draw a little square to focus on:
            offscreenG.setColor(rightColor);
            offscreenG.drawRect(xbase-2 + (int)delt, ybase-2, 4, 4);
            offscreenG.setColor(leftColor);
            offscreenG.drawRect(xbase-2 - (int)delt, ybase-2, 4, 4);
        }

        // Draw vector from center of object to mouse position
        if (bTracking) {
            offscreenG.setColor(rightColor);
            offscreenG.drawLine(xbase+(int)delt, ybase, mouseX, mouseY);
            offscreenG.setColor(leftColor);
            offscreenG.drawLine(xbase-(int)delt, ybase, mouseX, mouseY);
        }

        // Combine the manual rotation ROTM with the randomly-generated 4-rotation ROT4
        for (i=0; i < 4; i++) {
            for (j=0; j < 4; j++) {
                ROT4A[i][j] = 0.0;
                for (k=0; k < 4; k++) ROT4A[i][j] += ROTM[i][k] * ROT4[k][j];
            }
        }

        // Build 2d coords of all vertices
        int len = vertices.length;
        for (v=0; v < len; v++) {
            // Rotate the vertex
            for (j=0; j < 4; j++) {
                rotvert[j] = 0.0;
                for (k=0; k < 4; k++) rotvert[j] += (ROT4A[j][k] * vertices[v][k]);
            }
            fac2 = 1.0 / (1.0 - vpfR * rotvert[3]);
            for (k=0; k < 3; k++) rotvert[k] *= fac2;
            fac3 = dfac / (deps-rotvert[2]/R3);
            vert2y[v] = ybase + (int)(fac3*rotvert[1]);
            q = fac3*rotvert[0];
            delt = (fac-fac3)*deltar + dx_offset;
            vert2xR[v] = xbase + (int)(q+delt);
            vert2xL[v] = xbase + (int)(q-delt);
        }


        // Draw all the edges
        len = edges.length;
        for (i=0; i < len; i++) {
            v  = edges[i][0];   // vertex indices
            v1 = edges[i][1];
            if (bLeftFirst) {
                offscreenG.setColor(leftColor);
                offscreenG.drawLine(vert2xL[v],vert2y[v],vert2xL[v1],vert2y[v1]);
            }
            offscreenG.setColor(rightColor);
            offscreenG.drawLine(vert2xR[v],vert2y[v],vert2xR[v1],vert2y[v1]);
            if (!bLeftFirst) {
                offscreenG.setColor(leftColor);
                offscreenG.drawLine(vert2xL[v],vert2y[v],vert2xL[v1],vert2y[v1]);
            }
            bLeftFirst = !bLeftFirst;
        }

        g.drawImage(offscreenImg,0,0,this);
    }
}

class HyprCubeFrame extends Frame
{
    private HyprCube ghc;

    public HyprCubeFrame(HyprCube hc, String strName)
    {
        super(strName);
        ghc = hc;
    }


    public boolean handleEvent(Event evt)
    {
        if (evt.id == evt.WINDOW_DESTROY) {
            if (ghc.bStandalone) System.exit(0);
            else ghc.removeFromFrame();
            return true;
        }
        return false;
    }
}
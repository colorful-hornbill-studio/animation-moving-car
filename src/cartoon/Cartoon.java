package cartoon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class Cartoon extends JPanel {
    private static final GeneralPath MOUNTAIN = new GeneralPath(); // Field for paint mountain 
    private static final GeneralPath VANE = new GeneralPath(); // Field for paint vane of windmill
    private static final GeneralPath STAR = new GeneralPath(); // Field for paint star

    private static final double[] LIMITS = new double[] {0, 7, 4, -1};

    private static int number1 = 0; // Number for move cars and plane
    private static int number2 = 0; // Numbers (number2, number3, number4, and number5) for change day and night (Possible values is 0 or 1)
    private static int number3 = 375;
    private static int number4 = 1;
    private static int number5 = 0;

    private static float size = 0;

    public Cartoon() {
        /**
         * Set window size
         */
        this.setPreferredSize(new Dimension(720, 500));

        /**
         * Paint star
         */
        Cartoon.STAR.moveTo(4.5, 2.0); // Set starting point to center
        Cartoon.STAR.lineTo(4.75, 2.03);
        Cartoon.STAR.lineTo(4.78, 2.28);
        Cartoon.STAR.lineTo(4.81, 2.03);
        Cartoon.STAR.lineTo(5.06, 2.0);
        Cartoon.STAR.lineTo(4.81, 1.97);
        Cartoon.STAR.lineTo(4.78, 1.72);
        Cartoon.STAR.lineTo(4.75, 1.97);
        Cartoon.STAR.closePath();

        /**
         * Paint mountain
         */
        Cartoon.MOUNTAIN.moveTo(0, -1); // Set starting point to bottom left
        Cartoon.MOUNTAIN.lineTo(0, 0.7);
        Cartoon.MOUNTAIN.lineTo(1.5, 1.60);
        Cartoon.MOUNTAIN.lineTo(1.8, 1.3);
        Cartoon.MOUNTAIN.lineTo(3, 2.1);
        Cartoon.MOUNTAIN.lineTo(4.7, 0.7);
        Cartoon.MOUNTAIN.lineTo(6.1, 1.2);
        Cartoon.MOUNTAIN.lineTo(9, 0.8);
        Cartoon.MOUNTAIN.lineTo(9, -1);
        Cartoon.MOUNTAIN.closePath();

        /**
         * Paint vane for windmill
         */
        Cartoon.VANE.moveTo(0, 0); // Set starting point to top left
        Cartoon.VANE.lineTo(1, 0.1);
        Cartoon.VANE.lineTo(1.5, 0);
        Cartoon.VANE.lineTo(0.5, -0.1);
        Cartoon.VANE.closePath();

        /**
         * Set timer for repaint
         */
        new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ++Cartoon.number1;

                if(0 == (Cartoon.number1 % 750)) {
                    switch(Cartoon.number2) {
                        case 0:
                            Cartoon.number2 = 1;
                            break;
                        case 1:
                            Cartoon.number2 = 0;
                            break;
                    }
                }
                
                if(Cartoon.number3 <= 1) {
                    ++Cartoon.number4;
                    
                    Cartoon.number5 = Cartoon.number4;
                } else {
                    --Cartoon.number3;

                    Cartoon.number5 = Cartoon.number3;
                }
                
                if(Cartoon.number4 >= 375) {
                    Cartoon.number3 = 375;
                    Cartoon.number4 = 1;
                }

                repaint();
            }
        }).start();

        /**
         * Set timer for background sound
         */
        new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Cartoon.playSound();
            }
        }).start();
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        controlLimits(g2D, getWidth(), getHeight(), Cartoon.LIMITS);

        /**
         * Set color for day and night sky
         */
        if(Cartoon.number2 == 0) {
            g2D.setColor(new Color(154, 243, 252)); // Set color for day sky 
        } else {
            g2D.setColor(new Color(54, 0, 152)); // Set color for night sky
        }

        g2D.fillRect(0, 0, 7, 4); // Paint sky

        /**
         * Set color for star in night sky
         */
        if(Cartoon.number2 == 1) {
            g2D.setColor(new Color(0, 200, 200));
        }

        g2D.fill(Cartoon.STAR); // Paint star

        /**
         * Fill land and mountain with green color
         */
        g2D.setColor(new Color(51, 204, 51));
        g2D.fill(Cartoon.MOUNTAIN);

        /**
         * Paint highway
         */
        g2D.setColor(new Color(119, 119, 165));
        g2D.fill(new Rectangle2D.Double(0, -0.4, 7, 0.8));

        /**
         * Paint white stroke in highway
         */
        g2D.setStroke(new BasicStroke(0.1F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[] {0.2F, 0.2F}, 1));
        g2D.setColor(Color.WHITE);
        g2D.drawLine(0, 0, 7, 0);
        g2D.setStroke(new BasicStroke(size));

        AffineTransform transform = g2D.getTransform();

        g2D.translate(-0.5, 3.3); // Set starting point for sun and moon

        /**
         * Set change between sun and moon
         */
        if(Cartoon.number2 == 0) {
            g2D.translate(3 * (Cartoon.number1 % 750) / 300.0, -(Cartoon.number5 / 300.0)); // Move sun

            paintSun(g2D); // Paint sun
        } else {
            g2D.translate(3 * (Cartoon.number1 % 750) / 300.0,  -(Cartoon.number5 / 300.0)); // Move moon

            paintMoon(g2D); // Paint moon
        }

        g2D.setTransform(transform);
        g2D.translate(10 * (Cartoon.number1 % 300) / 300.0, 0); // Move cars and plane
        g2D.scale(0.3, 0.3); // Make cars and plane to be bigger

        paintCarsAndPlane(g2D); // Paint cars and plane

        g2D.setTransform(transform);
        g2D.translate(0.9, 1); // Place first windmill
        g2D.scale(0.6, 0.6); // Make first windmill to be bigger

        paintWindmill(g2D); // Paint first windmill

        g2D.setTransform(transform);
        g2D.translate(2.3, 1.5); // Place second windmill
        g2D.scale(0.4, 0.4); // Make second windmill to be bigger

        paintWindmill(g2D); // Paint second windmill

        g2D.setTransform(transform);
        g2D.translate(4.0, 0.8); // Place third windmill
        g2D.scale(0.7, 0.7); // Make third windmill to be bigger

        paintWindmill(g2D); // Paint third windmill
    }

    private static void controlLimits(Graphics2D g2D, int width, int height, double[] limits) {
        double pixelWidth = Math.abs((limits[1] - limits[0]) / width);
        double pixelHeight = Math.abs(( limits[3] - limits[2]) / height);

        Cartoon.size = (float) Math.min(pixelWidth, pixelHeight);

        g2D.scale(width / (limits[1] - limits[0]), height / (limits[3] - limits[2]));
        g2D.translate(-limits[0], -limits[2]);
    }

    /**
     * Method for paint sun
     */
    private static void paintSun(Graphics2D g2D) {
        /**
         * Set color for sun
         */
        g2D.setColor(Color.YELLOW);

        /**
         * Paint light of sun
         */
        for (int i = 1; i <= 15; i++) { 
            g2D.rotate(2 * Math.PI / 15 );
            g2D.draw(new Line2D.Double(0, 0, 0.75, 0) ); 
        }

        /**
         * Paint sun
         */
        g2D.fill(new Ellipse2D.Double(-0.5, -0.5, 1, 1) );
    }

    /**
     * Method for paint moon 
     */
    private static void paintMoon(Graphics2D g2D) {
        g2D.setColor(Color.LIGHT_GRAY); // Set color for moon
        g2D.fill(new Ellipse2D.Double(-0.5, -0.5, 1, 1)); // Paint moon
    }

    /**
     * Method for paint Windmill 
     */
    private static void paintWindmill(Graphics2D g2D) {
        g2D.setColor(Color.BLACK); // Set color of windmill stick
        g2D.fill(new Rectangle2D.Double(-0.05, 0.2, 0.1, 2.8)); // Paint windmill stick
        g2D.translate(0, 3); // Place windmill
        g2D.rotate(-Cartoon.number1); // Rotate vane of windmill

        /**
         * Set color for vane of windmill
         */
        g2D.setColor(new Color(158, 93, 226));

        /**
         * Paint vane
         */
        for (int i = 1; i <= 6; i++) {
            g2D.rotate(Math.PI / 3);
            g2D.fill(Cartoon.VANE);
        }
    }

    /**
     * Method for paint cars and plane
     */
    private static void paintCarsAndPlane(Graphics2D g2D) {
        AffineTransform transform = g2D.getTransform();

        /**
         * Paint wheels for first car
         */
        g2D.translate(-1.5, -0.1);
        g2D.scale(0.8, 0.8);

        paintWheel(g2D);

        g2D.setTransform(transform);
        g2D.translate(5.0, -0.1);
        g2D.scale(0.8, 0.8);

        paintWheel(g2D);

        g2D.setTransform(transform);

        /**
         * Set color of first car for day and night
         */
        if(Cartoon.number2 == 0) {
            g2D.setColor(Color.RED);
        } else {
            g2D.setColor(new Color(150, 0, 0));
        }

        /**
         * Paint body of first car
         */
        g2D.fill(new Rectangle2D.Double(-2.5, 0, 7, 3));
        g2D.fill(new Rectangle2D.Double(4, 0, 2, 2));

        /**
         * Paint window of first car
         */
        g2D.setColor(Color.WHITE);
        g2D.fill(new Rectangle2D.Double(-2.0,1.5,2,1) );
        g2D.fill(new Rectangle2D.Double(1.5,1.5,2,1) );

        /**
         * Paint wheels for second car
         */
        g2D.translate(-8.5,-0.1);
        g2D.scale(0.8,0.8);

        paintWheel(g2D);

        g2D.setTransform(transform);
        g2D.translate(-11.5,-0.1);
        g2D.scale(0.8,0.8);

        paintWheel(g2D);

        g2D.setTransform(transform);

        /**
         * Set color of second car for day and night
         */
        if(Cartoon.number2 == 0) {
            g2D.setColor(Color.BLUE);
        } else {
            g2D.setColor(new Color(0, 175, 0));
        }

        /**
         * Paint body of second car
         */
        g2D.fill(new Rectangle2D.Double(-10.5, 1, 2, 1.5) );
        g2D.fill(new Rectangle2D.Double(-12.5, 0, 5, 1.5) );

        /**
         * Paint window of second car
         */
        g2D.setColor(Color.WHITE);
        g2D.fill(new Rectangle2D.Double(-9.7, 1, 0.8, 1) );

        /**
         * Set color of plane for day and night
         */
        if(Cartoon.number2 == 0) {
            g2D.setColor(new Color(0, 0, 128));
        } else {
            g2D.setColor(new Color(175, 0, 0));
        }

        
        g2D.translate(0.0, -0.1);
        /**
         * Paint body of plane
         */
        g2D.fill(new Ellipse2D.Double(-10.5, 12, 4, 0.5) );
        g2D.fill(new Rectangle2D.Double(-8.4, 11.8, 0.25, 0.7));
        g2D.fill(new Rectangle2D.Double(-10.3, 12.1, 0.25, 0.5));
    }

    /**
     * Method for paint wheel
     */
    private static void paintWheel(Graphics2D g2D) {
        /**
         * Paint tires
         */
        g2D.setColor(Color.BLACK);
        g2D.fill(new Ellipse2D.Double(-1, -1, 2, 2));

        g2D.setColor(Color.LIGHT_GRAY);
        g2D.fill(new Ellipse2D.Double(-0.8, -0.8, 1.6, 1.6));
        
        g2D.setColor(Color.BLACK);
        g2D.fill(new Ellipse2D.Double(-0.2, -0.2, 0.4, 0.4) );

        /**
         * Rotate tires
         */
        g2D.rotate(Cartoon.number1);

        for (int i = 1; i <= 15; i++) {
            g2D.rotate(2 * Math.PI / 15);
            g2D.draw(new Rectangle2D.Double(0, -0.1, 1, 0.2));
        }
    }
    
    public static void playSound() {
        try {
            File VAWFile = new File("A:/Downloads/BMW+DRIVEBY.wav");

            AudioInputStream sound = AudioSystem.getAudioInputStream(VAWFile);
            AudioFormat format = sound.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip audioClip = (Clip) AudioSystem.getLine(info);
            
            audioClip.open(sound);
            audioClip.start();
        } catch(UnsupportedAudioFileException | IOException | LineUnavailableException err) {
            System.out.println(err);
        }
    }

    public static void main(String[] args) throws Exception {
        JFrame window = new JFrame();

        window.setContentPane(new Cartoon());
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Cartoon.playSound();
    }
}


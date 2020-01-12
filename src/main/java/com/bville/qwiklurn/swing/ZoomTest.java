/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bville.qwiklurn.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Bart
 */
public class ZoomTest {

    public static void main(String[] args) throws IOException {
        ImagePanel panel = new ImagePanel(new FileImageInputStream(new File("D:\\JavaDev\\NetBeansProjects\\qwiklurn\\Pictures\\Esdoorn_vrucht.jpg")));
        ImageZoom zoom = new ImageZoom(panel);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(zoom.getUIPanel(), "North");
        f.getContentPane().add(new JScrollPane(panel));
        f.setSize(400, 400);
        f.setLocation(200, 200);
        f.setVisible(true);
    }
}

class ImagePanel extends JPanel {

    BufferedImage image;
    double scale;

    public ImagePanel(ImageInputStream imgIS) throws IOException {
        loadImage(imgIS);
        scale = 1.0;
        setBackground(Color.BLACK);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        int w = getWidth();
        int h = getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double x = (w - scale * imageWidth) / 2;
        double y = (h - scale * imageHeight) / 2;
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.scale(scale, scale);
        g2.drawRenderedImage(image, at);
    }

    /**
     * For the scroll pane.
     */
    public Dimension getPreferredSize() {
        int w = (int) (scale * image.getWidth());
        int h = (int) (scale * image.getHeight());
        return new Dimension(w, h);
    }

    public void setScale(double s) {
        scale = s;
        revalidate();      // update the scroll pane
        repaint();
    }

    private void loadImage(ImageInputStream imgStream) throws IOException {

        image = ImageIO.read(imgStream);

    }
}

class ImageZoom {
    private JSpinner spinner;
    ;
    ImagePanel imagePanel;

    public ImageZoom(ImagePanel ip) {
        imagePanel = ip;
    }
    
    public void setImage(ImagePanel ip){
        imagePanel = ip;
    }
    
    public void reset(){
        spinner.setValue(1.0);
    }

    public JPanel getUIPanel() {
        SpinnerNumberModel model = new SpinnerNumberModel(1.0, 0.1, 1.4, .01);
        spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(45, spinner.getPreferredSize().height));
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float scale = ((Double) spinner.getValue()).floatValue();
                imagePanel.setScale(scale);
            }
        });
        JPanel panel = new JPanel();
        panel.add(new JLabel("scale"));
        panel.add(spinner);
        return panel;
    }
}

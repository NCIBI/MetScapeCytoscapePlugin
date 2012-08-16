/*************************************************************************
 * Copyright 2012 Regents of the University of Michigan 
 * 
 * NCIBI - The National Center for Integrative Biomedical Informatics (NCIBI)
 *         http://www.ncib.org.
 * 
 * This product may includes software developed by others; in that case see specific notes in the code. 
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, 
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, 
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details, http://www.gnu.org/licenses/.
 * 
 * This work was supported in part by National Institutes of Health Grant #U54DA021519
 *
 ******************************************************************/
package org.ncibi.cytoscape.metscape.multidisplay.gui.mulltislider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxValueModel;

@SuppressWarnings("serial")
public class MinMaxTwoThumbDisplayAndControl extends JPanel
    implements MouseListener, MouseMotionListener, MinMaxChangeListener {

	private static enum DragMode {
		DRAG_NOTHING, DRAG_START, DRAG_END;
	}
	
	private static final double aSmallNumber = 1e-6;

	private static final Color COLOR_SMALL_GRID = Color.DARK_GRAY;
	private static final Color COLOR_LARGE_GRID = Color.BLACK;
	private static final Color COLOR_MARKERS = new Color(0x6666FF); 
	private static final Color COLOR_DRAG = new Color(0xFF6666);
    
	private static final int PADDING = 6;

	private String message = null;

	private Dimension minDim = new Dimension(300, 22);

	private double grid = 0.1;
	private DragMode currentMode = DragMode.DRAG_NOTHING;

	private TickMark tm = new TickMark();

    private int pressed, min, max, dragMin, dragMax;

    private Image offScreen;
    private Graphics offGC;
    private Dimension offSize;

    private final ControlInterface control;
    
    private double currentMinValue, minValue, currentMaxValue, maxValue;

    /**
     * Initialize and return the JPanel for this GUI
     * 
     * @param model the model that is linked to this GUI.
     */
    public MinMaxTwoThumbDisplayAndControl(ControlInterface control) {
        addMouseListener(this);
        addMouseMotionListener(this);
		this.control = control;
		control.addMinMaxListener(this);
	    initView();
    }
    
    public void initView() {
    	currentMinValue = control.getMinValue();
    	minValue = control.getMinMinValue();
    	currentMaxValue = control.getMaxValue();
    	maxValue = control.getMaxMaxValue();
    	updateGraphics();
    }
    
	@Override
	public void valuesChanged(MinMaxValueModel t) {
    	currentMinValue = control.getMinValue();
    	currentMaxValue = control.getMaxValue();
    	updateGraphics();
	}

    private double snapToGrid(double v) {
        return grid * Math.round(v / grid);
    }

    private void paintUnderTriangleMarker(Graphics gc, int x) {
        gc.drawLine(x, 14, x, 14);
        gc.drawLine(x - 1, 15, x + 1, 15);
        gc.drawLine(x - 2, 16, x + 2, 16);
        gc.drawLine(x - 3, 17, x + 3, 17);
        gc.drawLine(x - 4, 18, x + 4, 18);
    }

    private void paintGrid(Graphics gc, double minValue, double maxValue, Color c, double g, int y1, int y2)
    {
        gc.setColor(c);
        double x1 = g * Math.ceil(minValue / g - aSmallNumber);
        double x2 = g * Math.floor(maxValue / g + aSmallNumber);
        for (double i = x1; i < x2 + aSmallNumber; i += g) {
            double t = (i - minValue) / (maxValue - minValue);
            int x = (int)Math.round(min + t * (max - min));
            gc.drawLine(x, y1, x, y2);
        }
    }
        
    public Graphics getOffscreenGraphics() {
        Dimension d = getSize();

        if ((offScreen == null) || !offSize.equals(d)) {
            offSize = d;
            offScreen = createImage(offSize.width, offSize.height);
            offGC = offScreen.getGraphics();
            offGC.setFont(getFont());
        }
        return offGC;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics gr;
        gr = getOffscreenGraphics();
        if (isOpaque())
            paintBackground(gr);
        paintSurface(gr);
        paintOffScreenGraphics(g);
    }

    private void paintBackground(Graphics g) {
        Dimension d = getSize();
        g.setColor(getBackground());
        g.fillRect(0, 0, d.width, d.height);
    }

    private void paintOffScreenGraphics(Graphics g) {
        g.drawImage(offScreen, 0, 0, null);
    }

    private void updateGraphics() {
        revalidate();
        repaint();
    }

    private void paintSurface(Graphics gc) {
        Dimension extent = getSize();
        gc.setColor(Color.black);

        FontMetrics fm = gc.getFontMetrics();
        
        if (message != null) {
            gc.setColor(Color.red);
            int x = (extent.width - fm.stringWidth(message)) / 2;
            int y = extent.height / 2;
            gc.drawString(message, x, y);
        } else {
            
            Insets border = getInsets();
            min = border.left + PADDING;
            max = extent.width - 1 - border.right - PADDING;

            tm.set(minValue, maxValue, 8);
            grid = 0.1 * tm.getStep();

            // paint grid
            paintGrid(gc, minValue, maxValue, COLOR_SMALL_GRID, grid, 6, 11);
            paintGrid(gc, minValue, maxValue, COLOR_LARGE_GRID, 10.0 * grid, 4, 13);

            // paint main bar
            gc.setColor(Color.darkGray);
            gc.drawLine(min, 8, max, 8);
            gc.drawLine(min, 9, max, 9);

            // paint start marker
            gc.setColor(currentMode == DragMode.DRAG_START ? COLOR_DRAG : COLOR_MARKERS);
            double t = (currentMinValue - minValue) / (maxValue - minValue);
            int x = (int)Math.round(min + t * (max - min));
            paintUnderTriangleMarker(gc, x);

            // paint end  marker
            gc.setColor(currentMode == DragMode.DRAG_END ? COLOR_DRAG : COLOR_MARKERS);
            t = (currentMaxValue - minValue) / (maxValue - minValue);
            x = (int)Math.round(min + t * (max - min));
            paintUnderTriangleMarker(gc, x);

        }
    }

    //1.1 event handling
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        //if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
        //stupid SGI bug :(
        if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == 0
            && (e.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
            pressed = e.getX();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
            if (currentMode != DragMode.DRAG_NOTHING) {
                currentMode = DragMode.DRAG_NOTHING;
                // the value does not change
                updateGraphics();
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        Dimension extent = getSize();
        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
            Insets border = getInsets();
            min = border.left + PADDING;
            max = extent.width - 1 - border.right - PADDING;

            // check if a marker should be dragged
            if (currentMode == DragMode.DRAG_NOTHING) {
                double t =
                    (currentMinValue - minValue) / (maxValue - minValue);
                int xStart = (int)Math.round(min + t * (max - min));

                t = (currentMaxValue - minValue) / (maxValue - minValue);
                int xEnd = (int)Math.round(min + t * (max - min));

                if (Math.abs(xStart - pressed) < 4) {
                    currentMode = DragMode.DRAG_START;
                    dragMin = min;
                    dragMax = xEnd - 9;
                }

                if (Math.abs(xEnd - pressed) < 4) {
                    currentMode = DragMode.DRAG_END;
                    dragMin = xStart + 9;
                    dragMax = max;
                }
            }

            // do the actual dragging
            if (currentMode != DragMode.DRAG_NOTHING) {
                double x = e.getX();
                if (x < dragMin)
                    x = dragMin;
                if (x > dragMax)
                    x = dragMax;

                double t =
                    minValue + (x - min) / (max - min) * (maxValue - minValue);
                if (e.isControlDown())
                    t = snapToGrid(t);

                if (currentMode == DragMode.DRAG_START) {
                    control.setMinValue(t);
                }
                if (currentMode == DragMode.DRAG_END) {
                    control.setMaxValue(t);
                }
            }
        }
    }

    public Dimension getPreferredSize() {
        return minDim;
    }
    public Dimension getMinimumSize() {
        return minDim;
    }
    public Dimension getMaximumSize() {
        return minDim;
    }

    /** @param set the message */
    public void setMessage(String string) {
        message = string;
    }
    /** @return the message String*/
    public String getMessage() {
        return message;
    }
}

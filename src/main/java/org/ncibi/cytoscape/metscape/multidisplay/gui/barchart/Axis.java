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
package org.ncibi.cytoscape.metscape.multidisplay.gui.barchart;

// Copied from http://www.superliminal.com/sources/Axis.java.html
// removed Log scaling
//Axis.java

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Original Comments (edited): includes a static function for selecting and
 * labeling graph axis tic labels. given a numeric range and a maximum number of
 * tics, this class can produce a list of labels with the nicest round numbers
 * not exceeding a given maximum number of labels. the label generation code was
 * extracted from the public domain <a
 * href="http://ptolemy.eecs.berkeley.edu/">Ptolomy project</a> at UC Berkeley,
 * taken from ptolemy/plot/PlotBox.java.
 * 
 * i added another static method to compute and draw an axis into a given AWT
 * Graphics object. i extracted the code for producing linear labels and threw
 * out the vast majority of code that attempted to produce log scale labels
 * since that code was very broken.
 * 
 * (And I removed the log code; there is no need for log axis in this
 * application - Terry, Oct 11, 2010).
 * 
 * @author Melinda Green
 */
public class Axis  {
    private static final boolean DEBUG = false;
    public final static int
        X_AXIS = 0,
        Y_AXIS = 1;
    // For use in calculating log base 10. A log times this is a log base 10.
    private static final double LOG10SCALE = 1/Math.log(10);
    // handy static methods
    private static double log10(double val) { return Math.log(val) * LOG10SCALE; }

    /**
     * this is the central method of this class.
     * takes axis range parameters and produces a list of string
     * representations of nicely rounded numbers within the given range.
     * these strings are intended for use as axis tic labels.
     * note: to find out where to plot each tic label simply
     * use <br><code>float ticval = Float.parseFloat(ticstring);</code>
     * @param ticMinVal no tics will be created for less than this value.
     * @param ticMaxVal no tics will be created for greater than this value.
     * @param maxTics returned vector will contain no more labels than this number.
     * @return a Vector containing formatted label strings which should also
     * be parsable into floating point numbers (in order to plot them).
     */
    public static Vector<String> computeTicks(double ticMinVal, double ticMaxVal, int maxTicks)  {
        double xStep = roundUp((ticMaxVal-ticMinVal)/maxTicks);
        int numfracdigits = numFracDigits(xStep);

        // Compute x starting point so it is a multiple of xStep.
        double xStart = xStep*Math.ceil(ticMinVal/xStep);
        Vector<String> labels = new Vector<String>();
        // Label the axis.  The labels are quantized so that
        // they don't have excess resolution.
        for (double xpos=xStart; xpos<=ticMaxVal; xpos+=xStep)
            labels.addElement(formatNum(xpos, numfracdigits));
        return labels;
    }
    
    /**
     * high-level method for drawing a chart axis line plus labeled tic marks.
     * introduces a dependancy on AWT because it takes a Graphics parameter.
     * perhaps this method belongs in some higher-level class but i added it
     * here since it's highly related with the tic lable generation code.
     * 
     * @author Melinda Green
     *
     * @param axis is one of Axis.X_AXIS or Axis.Y_AXIS.
     * @param maxTics is the maximum number of labeled tics to draw.
     * note: the actual number drawn may be less.
     * @param lowVal is the smallest value tic mark that may be drawn.
     * note: the lowest valued tic label may be greater than this limit.
     * @param highVal is the largest value tic mark that may be drawn.
     * note: the highest valued tic label may be less than this limit.
     * @param screenStart is the coordinate in the low valued direction.
     * @param screenEnd is the coordinate in the high valued direction.
     * @param offset is the coordinate in the direction perpendicular to
     * the specified direction.
     * @param logScale is true if a log scale axis is to be drawn,
     * false for a linear scale.
     * @param screenHeight is needed to flip Y coordinates.
     * @param g is the AWT Graphics object to draw into.
     * note: all drawing will be done in the current color of the given
     * Graphics object.
     */
    public static void drawAxis(
        int axis, int maxTics, int ticLength,
        float lowVal, float highVal, 
        int screenStart, int screenEnd, 
        int screenOffset, int screenHeight, Graphics g) 
    {
        if(axis == X_AXIS) // horizontal baseline
            g.drawLine(screenStart, screenHeight-screenOffset, screenEnd, screenHeight-screenOffset);
        else // vertical baseline
            g.drawLine(screenOffset, screenStart-screenOffset, screenOffset, screenEnd-screenOffset);    
        Vector<?> tics = Axis.computeTicks(lowVal, highVal, maxTics); // nice round numbers for tic labels
        int last_label_end = axis == X_AXIS ? -88888 : 88888;     
        String dbgstr = "tics:    ";
        for(Enumeration<?> e=tics.elements(); e.hasMoreElements(); ) {
            String ticstr = (String)e.nextElement();
            if(DEBUG)
                dbgstr += ticstr + ", ";
            float ticval = Float.parseFloat(ticstr);
            int tic_coord = screenStart;
            Dimension str_size = stringSize(ticstr, g);
            tic_coord += plotValue(ticval, lowVal, highVal, screenStart, screenEnd, screenHeight);
            if (axis == X_AXIS) { // horizontal axis == vertical tics
                g.drawLine(
                    tic_coord, screenHeight-screenOffset, 
                    tic_coord, screenHeight-screenOffset+ticLength);
                if (tic_coord-str_size.width/2 > last_label_end) {          
                    g.drawString(ticstr, tic_coord-str_size.width/2, screenHeight-screenOffset+str_size.height+5);
                    last_label_end = tic_coord + str_size.width/2 + str_size.height/2;
                }
            }
            else { // vertical axis == horizontal tics
                tic_coord = screenHeight - tic_coord; // flips Y coordinates
                g.drawLine(
                    screenOffset-ticLength, tic_coord, 
                    screenOffset,           tic_coord);
                if (tic_coord-str_size.height/3 < last_label_end) {
                    g.drawString(ticstr, screenOffset-ticLength-str_size.width-5, tic_coord+str_size.height/3);
                    last_label_end = tic_coord - str_size.height;
                }
            }
        }
        if(DEBUG)
            System.out.println(dbgstr);
    } // end drawAxis

    /**
     * lower level method to determine a screen location where a given value
     * should be plotted given range, type, and screen information.
     * the "val" parameter is the data value to be plotted
     * @author Melinda Green
     * @param val is a data value to be plotted.
     * @return pixel offset (row or column) to draw a screen representation
     * of the given data value. i.e. <i>where</i>  along an axis 
     * in screen coordinates the caller should draw a representation of
     * the given value.
     * @see drawAxis(int,int,int,float,float,int,int,int,boolean,int,Graphics)
     */
    public static int plotValue(float val, float lowVal, float highVal, 
        int screenStart, int screenEnd, int screenHeight) 
    {
        int screen_range = screenEnd - screenStart; // in pixels        
        float value_range = highVal - lowVal; // in data value units
        float pixels_per_unit = screen_range / value_range;
        return (int)((val-lowVal) * pixels_per_unit + .5);
    }    

    /*
     * Given a number, round up to the nearest power of ten
     * times 1, 2, or 5.
     *
     * Note: The argument must be strictly positive.
     */
    private static double roundUp(double val) {
        int exponent = (int) Math.floor(log10(val));
        val *= Math.pow(10, -exponent);
        if (val > 5.0) val = 10.0;
        else if (val > 2.0) val = 5.0;
        else if (val > 1.0) val = 2.0;
        val *= Math.pow(10, exponent);
        return val;
    }

    /*
     * Return the number of fractional digits required to display the
     * given number.  No number larger than 15 is returned (if
     * more than 15 digits are required, 15 is returned).
     */
    private static int numFracDigits(double num) {
        int numdigits = 0;
        while (numdigits <= 15 && num != Math.floor(num)) {
            num *= 10.0;
            numdigits += 1;
        }
        return numdigits;
    }

    // Number format cache used by formatNum.
    // Note: i'd have put the body of the formatNum method below into
    // a synchronized block for complete thread safety but that causes
    // an abscure null pointer exception in the awt event thread.
    // go figure.
    private static NumberFormat numberFormat = null;

    /*
     * Return a string for displaying the specified number
     * using the specified number of digits after the decimal point.
     * NOTE: java.text.NumberFormat is only present in JDK1.1
     * We use this method as a wrapper so that we can cache information.
     */
    private static String formatNum(double num, int numfracdigits) {
        if (numberFormat == null) {
            // Cache the number format so that we don't have to get
            // info about local language etc. from the OS each time.
            numberFormat = NumberFormat.getInstance();
            // force to not include commas because we want the strings
            // to be parsable back into numeric values. - DRG
            numberFormat.setGroupingUsed(false);
        }
        numberFormat.setMinimumFractionDigits(numfracdigits);
        numberFormat.setMaximumFractionDigits(numfracdigits);
        return numberFormat.format(num);
    }
    

    /**
     * handy little utility for determining the length in pixels the
     * given string will use if drawn into the given Graphics object.
     * Note: perhaps belongs in some utility package.
     */
    public static Dimension stringSize(String str, Graphics g) {
        if (g instanceof Graphics2D) {
            java.awt.geom.Rectangle2D bounds = g.getFont().getStringBounds(str, ((Graphics2D)g).getFontRenderContext());
            return new Dimension(
                (int)(bounds.getWidth()+.5),
                (int)(bounds.getHeight()+.5));
        }        
        else
            return new Dimension(g.getFontMetrics().stringWidth(str), g.getFontMetrics().getHeight());
    }
    
}
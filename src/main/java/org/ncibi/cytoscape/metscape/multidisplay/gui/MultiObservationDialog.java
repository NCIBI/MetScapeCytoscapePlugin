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
package org.ncibi.cytoscape.metscape.multidisplay.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ncibi.cytoscape.metscape.multidisplay.MultiNet;
import org.ncibi.cytoscape.metscape.multidisplay.Study;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AlignViewsListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AnimationPostionChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.AnimationValueListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ColorRangeChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.ControlInterface;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MinMaxChangeListener;
import org.ncibi.cytoscape.metscape.multidisplay.gui.model.MultiNetChangeListener;

public class MultiObservationDialog extends JDialog implements WindowListener,MultiNetChangeListener, AnimationPostionChangeListener {

	private static final long serialVersionUID = 1L;

	private final ControlInterface control;
	
	private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>(); // @jve:decl-index=0:
	private MultiNetSlider slider = null;
	private JPanel jContentPane = null; 
	private JPanel buttonPanel = null;
	private JButton makeCongruentButton = null;
	private JPanel topPanel = null;
	private JPanel titlePanel = null;
	private JLabel titleLabel = null;
	private JPanel viewLinkPanel = null;
	private JLabel viewLinkLabel = null;
	private JPanel viewLinkListPanel = null;
	private Box checkBoxHolder = null;
	private HistogramColorScaleHolder histogramColorScaleHolder = null;
	private JPanel controlButtonPanel = null;
	private JButton playButton = null;
	private JButton closeButton = null;

	private boolean playing = false;
	private int numberOfSteps;
	private Thread playThread = null;  //  @jve:decl-index=0:
	private static final long PLAY_SLEEP_INTERVAL = 200; // a fifth of a second
	private static final long MIN_SLEEP_INTERVAL = 100; // a tenth of a second	
	private static final double PLAY_INCREMENT = 0.1;  // proportion of the range between values, per step
	
	@Override
	public void updateFromMultiNet(MultiNet m) {
		for (String l: m.getTimeSeriesLabels()){
			System.out.println(l);
		}
		setNumberOfSteps(m.getTimeSeriesLabels().size());
		getSlider().setScoreLabels(m.getTimeSeriesLabels());
		titleLabel.setText(m.getName());
		validate();
		repaint();
	}

	public void setSelectionList(List<Study> studyList) {
		ActionListener l = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// for (JCheckBox cb: checkBoxes){
				// // if (cb.isSelected()) System.out.println(cb.getText());
				// }
			}
		};
		for (Study study: studyList) {
			JCheckBox cbox = new JCheckBox(study.getLabel());
			cbox.setEnabled(true);
			cbox.addActionListener(l);
			cbox.setSelected(true);
			getCheckBoxHolder().add(cbox);
			checkBoxes.add(cbox);
		}
		validate();
		repaint();
	}

	public void setSliderPosition(int scorePos) {
		getSlider().setScorePosition(scorePos);
	}

	@Override
	public void setProportionalPosition(double proportionalPosition) {
		// note this methood may be called from non-graphics threads; hence the invokeLater 
		final double pos = proportionalPosition;
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
					getSlider().jumpToProportionalPosition(pos);
			    }
			});
		} catch (InterruptedException e) {
			System.out.println("Slider update task, interrupted.");
		} catch (InvocationTargetException e) {
			System.out.println("SLider update task, invocation exception.");
		}
	}

	private void makeViewsMatch() {
		control.alignNetworkViews();
	}

	private void closeWithConfirm(){

		stopPlayingAnimation();
		
		int status = JOptionPane.showConfirmDialog(this,
			    "<html><b>Did you really want to quit?</b><br />The animation networks will be deleted.<br /> " +
			    "You can rebuild then with the 'Animate Data' Menu item.</html>");
		
		if (status == JOptionPane.CANCEL_OPTION){
			return;
		}
		if (status == JOptionPane.NO_OPTION){
			return;
		}
		this.setVisible(false);
		control.disposeOfMultiNet();
	}
	
	private void togglePlay(){
		if (playing) stopPlayingAnimation();
		else startPlayingAnimation();
	}
	
	private void startPlayingAnimation() {
		if (playing) return;
		playThread = new Thread(new Runnable(){
			@Override
			public void run() {
				playing = true;
				updatePlayButton();
				System.out.println("Starting play");
				double fractionalIncrement = PLAY_INCREMENT/((double)(getNumberOfSteps() - 1));
				while(playing == true) {
					long time = animateOneTick(fractionalIncrement);
					if (time < PLAY_SLEEP_INTERVAL) {
						try {
							Thread.sleep(PLAY_SLEEP_INTERVAL-time);
						} catch (InterruptedException ignore) {
						}
					} else {
						try {
							Thread.sleep(MIN_SLEEP_INTERVAL);
						} catch (InterruptedException ignore) {
						}
					}
				}
				System.out.println("Stop play.");
				playing = false;
				updatePlayButton();
			}});
		playThread.start();
	}

	public void stopPlayingAnimation() {
		if (!playing) return;
		playing = false;
		updatePlayButton();
		if (playThread != null)
			playThread.interrupt();
		playThread = null;
	}
	
	private long animateOneTick(double fractionalIncrement){
		long start = System.currentTimeMillis();
		control.updateAnimatePosition(fractionalIncrement);
		long end = System.currentTimeMillis();
		return end-start;
	}

	private void updatePlayButton() {
		if (playing) {
			getPlayButton().setText("Stop");
		} else {
			getPlayButton().setText("Play");
		}
	}

	/**
	 * 
	 */
	public MultiObservationDialog(ControlInterface control) {
		this.control = control;
		initialize();
	}

	
	private void setNumberOfSteps(int n) {
		numberOfSteps = n;
	}

	private int getNumberOfSteps(){
		return numberOfSteps;
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(550, 450);
		this.setTitle("Animation Controls");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setContentPane(getJContentPane());
		this.addWindowListener(this);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.setSize(new Dimension(392, 249));
			jContentPane.add(getTopPanel(), BorderLayout.NORTH);
			jContentPane.add(getControlPanel(), BorderLayout.WEST);
			jContentPane.add(getHistogramColorScaleHolder(),
					BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes slider
	 * 
	 * @return 
	 *         org.ncibi.cytoscape.plugin.metabolomics.multidisplay.gui.StickySlider
	 */
	private MultiNetSlider getSlider() {
		if (slider == null) {
			slider = new MultiNetSlider(control);
			slider.setEnabled(false);
		}
		return slider;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getMakeCongruentButton(), gridBagConstraints);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes makeCongruentButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getMakeCongruentButton() {
		if (makeCongruentButton == null) {
			makeCongruentButton = new JButton();
			makeCongruentButton.setText("Realign All");
			makeCongruentButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							makeViewsMatch();
						}
					});
		}
		return makeCongruentButton;
	}

	/**
	 * This method initializes topPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new BorderLayout());
			topPanel.add(getSlider(), BorderLayout.CENTER);
			topPanel.add(getTitlePanel(), BorderLayout.NORTH);
			topPanel.add(getControlButtonPanel(), BorderLayout.SOUTH);
		}
		return topPanel;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titleLabel = new JLabel();
			titleLabel.setText("JLabel");
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.add(titleLabel, new GridBagConstraints());
		}
		return titlePanel;
	}

	/**
	 * This method initializes controlPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getControlPanel() {
		if (viewLinkPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = GridBagConstraints.SOUTHWEST;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.NORTH;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			viewLinkLabel = new JLabel();
			viewLinkLabel
					.setText("<html>Views To Link<br>(select views)</html>");
			viewLinkPanel = new JPanel();
			viewLinkPanel.setLayout(new GridBagLayout());
			viewLinkPanel.add(getButtonPanel(), gridBagConstraints2);
			viewLinkPanel.add(viewLinkLabel, gridBagConstraints1);
			viewLinkPanel.add(getControlListPanel(), gridBagConstraints3);
		}
		return viewLinkPanel;
	}

	/**
	 * This method initializes controlListPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getControlListPanel() {
		if (viewLinkListPanel == null) {
			viewLinkListPanel = new JPanel();
			viewLinkListPanel.setLayout(new GridBagLayout());
			viewLinkListPanel.add(getCheckBoxHolder(), new GridBagConstraints());
		}
		return viewLinkListPanel;
	}

	/**
	 * This method initializes checkBoxHolder
	 * 
	 * @return javax.swing.Box
	 */
	private Box getCheckBoxHolder() {
		if (checkBoxHolder == null) {
			checkBoxHolder = new Box(BoxLayout.Y_AXIS);
		}
		return checkBoxHolder;
	}

	/**
	 * This method initializes histogramColorScaleHolder
	 * 
	 * @return 
	 *         org.ncibi.cytoscape.metscape.multidisplay.gui.HistogramColorScaleHolder
	 */
	private HistogramColorScaleHolder getHistogramColorScaleHolder() {
		if (histogramColorScaleHolder == null) {
			histogramColorScaleHolder = new HistogramColorScaleHolder(control);
		}
		return histogramColorScaleHolder;
	}
	/**
	 * This method initializes controlButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getControlButtonPanel() {
		if (controlButtonPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = GridBagConstraints.NONE;
			gridBagConstraints4.gridy = 0;
			controlButtonPanel = new JPanel();
			controlButtonPanel.setLayout(new GridBagLayout());
			controlButtonPanel.add(getPlayButton(), gridBagConstraints6);
			controlButtonPanel.add(getCloseButton(), gridBagConstraints4);
		}
		return controlButtonPanel;
	}

	/**
	 * This method initializes playButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPlayButton() {
		if (playButton == null) {
			playButton = new JButton();
			playButton.setText("Play");
			playButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					togglePlay();
				}
			});
			updatePlayButton();
		}
		return playButton;
	}

	/**
	 * This method initializes closeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("Close");
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeWithConfirm();
				}
			});
		}
		return closeButton;
	}

	// set up listeners
	public List<MinMaxChangeListener> getMinMaxChangeListenerList() {
		List<MinMaxChangeListener> ret = histogramColorScaleHolder.getMinMaxChangeListenerList();
		return ret;
	}

	public List<MultiNetChangeListener> getMultiNetChangeListenerList() {
		return histogramColorScaleHolder.getMultiNetChangeListenerList();
	}

	public List<ColorRangeChangeListener> getColorRangeChangeListenerList() {
		return histogramColorScaleHolder.getColorRangeChangeListenerList();
	}

	public List<AnimationValueListener> getAnimationValueListenerList() {
		return histogramColorScaleHolder.getAnimationValueListenerList();
	}

	public List<AlignViewsListener> getAlignViewsListenerList() {
		return histogramColorScaleHolder.getAlignViewsListenerList();
	}

	@Override
	public void windowActivated(WindowEvent e) { //ignore
	}

	@Override
	public void windowClosed(WindowEvent e) { //ignore
	}

	@Override
	public void windowClosing(WindowEvent e) {
		closeWithConfirm();
	}

	@Override
	public void windowDeactivated(WindowEvent e) { //ignore
	}

	@Override
	public void windowDeiconified(WindowEvent e) { //ignore
	}

	@Override
	public void windowIconified(WindowEvent e) { //ignore
	}

	@Override
	public void windowOpened(WindowEvent e) { //ignore
	}

} 

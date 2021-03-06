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

// copied from http://www.superliminal.com/sources/SelectionSet.java.html
// SelectionSet.java

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * a "set" container which notifies listeners of membership changes
 * for example as a central container for coordinated selection 
 * between a group of viewer/listeners.<br>
 *
 * this class is somewhat similar to javax.swing.DefaultListSelectionModel
 * except that its for sets instead of lists. in other words, this class
 * makes up for swing's lack of a DefaultSetSelectionModel class.<br>
 *
 * editing methods all take a "byWhom" parameter taken to be the
 * object performing the editing. this value is used to catch
 * illegal states when more than one object attempts to edit the set
 * at the same time. the "byWhom" parameter is also the object
 * sent to any registered SelectionSetListeners as the "source"
 * parameter.<br>
 *
 * another purpose for having an editing mode is so that listener
 * notification can be delayed until all edits are completed.<br>
 *
 * all editing methods may be called atomically where "atomically"
 * means that the there is no current editor. in these cases
 * all listeners are notified immediately rather then only when 
 * endEditing() is called. this is mostly meant as a convienience
 * for cases when only a single editing operation is needed.<br>
 *
 * @author Melinda Green
 */
public class SelectionSet {
    private Set<Object> selectedObjects = new HashSet<Object>();
    private List<SelectionSetListener> selectionSetListeners = new ArrayList<SelectionSetListener>();
    private Object currentEditor = null;
    private Class<?> contentType;

    /**
     * constructs a SelectionSet capable of holding objects
     * of the given type.
     */
    public SelectionSet(Class<?> contentType) {
        this.contentType = contentType;
    }

    /**
     * no-arg constructor for convienience. the resulting
     * selection set will be capable of holding objects
     * of any type.
     */
    public SelectionSet() {
        this(Object.class);
    }

    //
    // Editing mode control methods
    //

    /**
     * reserves the selection set for editing only by the given object.
     * @param byWhom the editor-to-be
     * @param clearFirst an optional flag which when true will remove any
     * previous content.
     * @throws IllegalStateException if another object has reserved the
     * selection set.
     */
    public void beginEditing(Object byWhom, boolean clearFirst) throws IllegalStateException {
        if (currentEditor != null)
            throw new IllegalStateException("SelectionSet: already being edited");
        currentEditor = byWhom;    
        if(clearFirst)
            clear(byWhom);
    }

    /**
     * convenience method of the two-argument version which always
     * clears the selection set contents.
     */
    public void beginEditing(Object byWhom) throws IllegalStateException {
        beginEditing(byWhom, true);
    }

    /**
     * releases a previously reserved selection set.
     * @throws IllegalStateException the selection set 
     * is not already reserved by the given object.
     */
    public void endEditing(Object byWhom) throws IllegalStateException {
        if (currentEditor != byWhom)
            throw new IllegalStateException("SelectionSet: not owner");
        Object oldEditor = currentEditor;
        currentEditor = null;
        fireSelectionChanged(oldEditor); // notify all listeners
    } 

    //
    // Editing methods
    //

    public void addElement(Object element, Object byWhom) throws IllegalStateException, IllegalArgumentException {
        preEdit(byWhom);
        testType(element);
        selectedObjects.add(element);
        postEdit(byWhom);
    }
    
    public void clear(Object byWhom) throws IllegalStateException  {
        preEdit(byWhom);
        selectedObjects.clear();
        postEdit(byWhom);
    }

    public Object getCurrentEditor() {
        return currentEditor;
    }

    /**
     * convienience method sets all selected elements in one shot 
     * and notifies any selection listeners.<br>
     */
    public void setElements(Object newElements[], Object byWhom) throws IllegalStateException, IllegalArgumentException {
        preEdit(byWhom);
        selectedObjects.clear();
        for(int i=0; i<newElements.length; i++) {
            testType(newElements[i]);
            selectedObjects.add(newElements[i]);
        }
        postEdit(byWhom);
    }
    /**
     * convienience method sets the list to contain only the given element
     * and notifies listeners.<br>
     */
    public void setElements(Object element, Object byWhom) throws IllegalStateException {
        // any argument exceptions are generated by array version called below
        setElements(new Object[] { element }, byWhom);
    }

    public void removeElement(Object element, Object byWhom) throws IllegalStateException, IllegalArgumentException {
        preEdit(byWhom);
        testType(element);
        selectedObjects.remove(element);
        postEdit(byWhom);
    }

    /**
     * adds the given element if not already contained,
     * removes it if it is. possibly useful for Window's style 
     * <code><ctrl>+<left click></code> toggling.
     */
    public void toggle(Object element, Object byWhom) throws IllegalStateException, IllegalArgumentException {
        preEdit(byWhom);
        testType(element);
        if(selectedObjects.contains(element))
            selectedObjects.remove(element);
        else
            selectedObjects.add(element);
        postEdit(byWhom);
    }

    //
    // Atomic editing and type support.
    // callable by subclasses when adding new editing methods.
    //

    /**
     * called at the beginning of all editing methods capable of
     * being called atomically.
     */
    protected void preEdit(Object byWhom) {
        if (currentEditor != null && currentEditor != byWhom)
            throw new IllegalStateException("SelectionSet: not owner");
    }
    /**
     * called at the end of all editing methods capable of
     * being called atomically.
     */
    protected void postEdit(Object byWhom) {
        if(currentEditor == null)
            fireSelectionChanged(byWhom);
    }

    /**
     * @throws an IllegalArgumentException if the given object
     * is null or the class of the given object is not assignment 
     * compatable with the type of this selection set's content type.
     */
    protected void testType(Object obj) throws IllegalArgumentException {
        if(obj == null)
            throw new IllegalArgumentException("SelectionSet: attempt to add null element");
        if( ! contentType.isAssignableFrom(obj.getClass()))
            throw new IllegalArgumentException(
                "SelectionSet: " + obj.getClass().getName() + 
                " is not compatable with " + contentType.getName());
    }

    //
    // Selection query methods
    //

    public int getNumElements() {
        return selectedObjects.size();
    }
    
    public Object[] getElements() {
        return selectedObjects.toArray();
    }

    public boolean contains(Object obj) {
        return selectedObjects.contains(obj);
    }

    //
    // Selection Listener Methods
    //
    
    public void addSelectionSetListener(SelectionSetListener listener) {
        selectionSetListeners.add(listener);
    }
    public void removeSelectionSetListener(SelectionSetListener listener) {
        selectionSetListeners.remove(listener);
    }

    protected void fireSelectionChanged(Object byWhom) {
        for(Iterator<SelectionSetListener> it=selectionSetListeners.iterator(); it.hasNext(); )
            it.next().selectionSetChanged(this, byWhom);
    }
    
} // end class SelectionSet
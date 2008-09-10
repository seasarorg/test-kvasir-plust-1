/**
 * 
 */
package org.seasar.kvasir.plust.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author shidat
 *
 */
public abstract class PlustModel
{

    private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    private String description;


    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }


    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }


    protected void firePropertyChange(String name, Object newValue)
    {
        PropertyChangeEvent event = new PropertyChangeEvent(this, name, null,
            newValue);
        for (Iterator<PropertyChangeListener> iter = listeners.iterator(); iter
            .hasNext();) {
            PropertyChangeListener listener = iter.next();
            listener.propertyChange(event);
        }
    }


    public String getDescription()
    {
        return description != null ? description : "";
    }


    public void setDescription(String description)
    {
        this.description = description;
        firePropertyChange("description", description);
    }
}

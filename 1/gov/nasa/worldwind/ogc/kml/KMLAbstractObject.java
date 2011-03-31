/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.ogc.kml;

import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.xml.*;

import java.beans.*;

/**
 * The abstract base class for most KML classes. Provides parsing and access to the <i>id</i> and <i>targetId</i> fields
 * of KML elements.
 *
 * @author tag
 * @version $Id: KMLAbstractObject.java 14308 2010-12-27 21:27:19Z pabercrombie $
 */
public abstract class KMLAbstractObject extends AbstractXMLEventParser implements PropertyChangeListener
{
    protected PropertyChangeSupport propertyChangeSupport;

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    protected KMLAbstractObject(String namespaceURI)
    {
        super(namespaceURI);
    }

    /**
     * Returns the id of this object, if any.
     *
     * @return the id of this object, or null if it's not specified in the element.
     */
    public String getId()
    {
        return (String) this.getField("id");
    }

    /**
     * Returns the target-id of this object, if any.
     *
     * @return the targetId of this object, or null if it's not specified in the element.
     */
    public String getTargetId()
    {
        return (String) this.getField("targetId");
    }

    @Override
    public KMLRoot getRoot()
    {
        XMLEventParser root = super.getRoot();
        return root instanceof KMLRoot ? (KMLRoot) root : null;
    }

    //**********************************************************************
    //********************* Property change support ************************
    //**********************************************************************

    /**
     * Adds the specified property change listener that will be called for all list changes.
     *
     * @param listener the listener to call.
     *
     * @throws IllegalArgumentException if <code>listener</code> is null
     * @see java.beans.PropertyChangeSupport
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener)
    {
        if (listener == null)
        {
            String msg = Logging.getMessage("nullValue.ListenerIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.getChangeSupport().addPropertyChangeListener(listener);
    }

    /**
     * Removes the specified property change listener.
     *
     * @param listener the listener to remove.
     *
     * @throws IllegalArgumentException if <code>listener</code> is null.
     * @see java.beans.PropertyChangeSupport
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener)
    {
        if (listener == null)
        {
            String msg = Logging.getMessage("nullValue.ListenerIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.getChangeSupport().addPropertyChangeListener(listener);
    }

    protected synchronized void firePropertyChange(java.beans.PropertyChangeEvent propertyChangeEvent)
    {
        if (propertyChangeEvent == null)
        {
            String msg = Logging.getMessage("nullValue.PropertyChangeEventIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.getChangeSupport().firePropertyChange(propertyChangeEvent);
    }

    protected synchronized void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if (propertyName == null)
        {
            String msg = Logging.getMessage("nullValue.PropertyNameIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.getChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Get the PropertyChangeSupport object for this KML object. The support object will be created if it does not
     * already exist.
     *
     * @return PropertyChangeSupport for this KML object.
     */
    protected PropertyChangeSupport getChangeSupport()
    {
        if (this.propertyChangeSupport == null)
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        return this.propertyChangeSupport;
    }

    /**
     * Forward property change events to the objects listening for property changes on this instance.
     *
     * @param evt Property change event.
     *
     * @throws IllegalArgumentException if <code>propertyChangeEvent</code> is null
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt == null)
        {
            String msg = Logging.getMessage("nullValue.PropertyChangeEventIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        // Notify all *my* listeners of the change that I caught
        this.firePropertyChange(evt);
    }
}

/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.util.webview;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;

/**
 * Abstract base class for {@link WebView} implementations.
 *
 * @author pabercrombie
 * @version $Id: AbstractWebView.java 14636 2011-02-03 17:38:31Z dcollins $
 */
public abstract class AbstractWebView extends WWObjectImpl implements WebView, WebNavigationPolicy, Disposable
{
    /** The size of the WebView frame in pixels. Initially null, indicating the default size is used. */
    protected Dimension frameSize;
    /** The WebView's current texture representation. Lazily created in {@link #getTextureRepresentation}. */
    protected WWTexture textureRep;
    /** The object to use as the picked object of generated select events, or {@code null} to use the WebView itself. */
    protected Object delegateOwner;
    /** The list of listeners that receive select events from the WebView. */
    protected EventListenerList eventListeners = new EventListenerList();
    /** The last input event sent to the WebView. */
    protected InputEvent lastEvent;
    /** Indicates whether the WebView is active. */
    protected boolean active;

    /**
     * Overridden to ensure that the WebView's native resources are disposed when the WebView is reclaimed by the
     * garbage collector. This does nothing if the WebView's owner has already called {@link #dispose()}.
     */
    @Override
    protected void finalize() throws Throwable
    {
        this.dispose();
        super.finalize();
    }

    /** {@inheritDoc} */
    public Dimension getFrameSize()
    {
        return this.frameSize;
    }

    /** {@inheritDoc} */
    public void setFrameSize(Dimension size)
    {
        if (size == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Setting the frame size requires a call into native code, and requires us to regenerate the texture. Only
        // do this if the size has actually changed.
        if (this.frameSize.equals(size))
            return;

        this.frameSize = size;
        // The texture needs to be regenerated because the frame size changed.
        this.textureRep = null;
    }

    /** {@inheritDoc} */
    public WWTexture getTextureRepresentation(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.textureRep == null)
            this.textureRep = this.createTextureRepresentation(dc);

        return this.textureRep;
    }

    /** {@inheritDoc} */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /** {@inheritDoc} */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
    }

    /** {@inheritDoc} */
    public void addSelectListener(SelectListener listener)
    {
        if (listener != null)
            this.eventListeners.add(SelectListener.class, listener);
    }

    /** {@inheritDoc} */
    public void removeSelectListener(SelectListener listener)
    {
        if (listener != null)
            this.eventListeners.remove(SelectListener.class, listener);
    }

    /** {@inheritDoc} */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /** {@inheritDoc} */
    public boolean isActive()
    {
        return this.active;
    }

    /** {@inheritDoc} */
    public String decidePolicyForNavigation(AVList params)
    {
        if (params == null)
        {
            String message = Logging.getMessage("nullValue.ParametersIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (AVKey.NAVIGATION_TYPE_BROWSER_INITIATED.equals(params.getValue(AVKey.NAVIGATION_TYPE)))
            return this.handleBrowserInitiatedNavigation(params);

        else if (AVKey.NAVIGATION_TYPE_LINK_ACTIVATED.equals(params.getValue(AVKey.NAVIGATION_TYPE)))
            return this.handleLinkActivatedNavigation(params);

        else
        {
            Logging.logger().warning(
                Logging.getMessage("generic.UnrecognizedNavigationType", params.getValue(AVKey.NAVIGATION_TYPE)));

            return AVKey.ALLOW; // Allow any unrecognized navigation type.
        }
    }

    /**
     * Call the select listeners that are listening to this WebView.
     *
     * @param event event to pass to listeners
     */
    protected void callSelectListeners(SelectEvent event)
    {
        for (SelectListener listener : this.eventListeners.getListeners(SelectListener.class))
        {
            listener.selected(event);
        }
    }

    /**
     * Create a texture representation of the WebView.
     *
     * @param dc draw context.
     *
     * @return A texture representation of the WebView contents.
     */
    protected abstract WWTexture createTextureRepresentation(DrawContext dc);

    @Override
    public void propertyChange(final PropertyChangeEvent event)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    propertyChange(event);
                }
            });
        }
        else
        {
            this.firePropertyChange(AVKey.REPAINT, null, this);
        }
    }

    //**********************************************************************//
    //********************  Navigation Policy  *****************************//
    //**********************************************************************//

    /**
     * Decide the navigation policy for a navigation event that was initiated by the browser (due to a redirection, a
     * script on the page, etc). This implementation ignores the event if it targets a new browser window. Otherwise,
     * the navigation is allowed.
     *
     * @param params navigation parameters
     *
     * @return {@link AVKey#ALLOW} to allow the navigation to proceed, or {@link AVKey#IGNORE} to ignore the event.
     */
    protected String handleBrowserInitiatedNavigation(AVList params)
    {
        if (AVKey.NAVIGATION_TARGET_NEW_BROWSER.equals(params.getValue(AVKey.TARGET)))
        {
            return AVKey.IGNORE;
        }
        else
        {
            return AVKey.ALLOW;
        }
    }

    /**
     * Decide the navigation policy for a navigation event that was initiated by the user. This implementation ignores
     * the event if it targets a new browser window. Otherwise, this method calls this WebView's select listeners. If
     * the select event is consumed by one of the listeners, then the navigation is ignored. Otherwise, the navigation
     * is allowed to proceed.
     *
     * @param params navigation parameters
     *
     * @return {@link AVKey#ALLOW} to allow the navigation to proceed, or {@link AVKey#IGNORE} to ignore the event.
     */
    protected String handleLinkActivatedNavigation(AVList params)
    {
        SelectEvent event = this.createLinkActivatedSelectEvent(params);
        this.callSelectListeners(event);

        if (AVKey.NAVIGATION_TARGET_NEW_BROWSER.equals(params.getValue(AVKey.TARGET)))
        {
            return AVKey.IGNORE;
        }
        else if (event.isConsumed())
        {
            return AVKey.IGNORE;
        }
        else
        {
            return AVKey.ALLOW;
        }
    }

    /**
     * Create a SelectEvent for a link activation event.
     *
     * @param params link parameters to attach to the PickedObject in the new SelectEvent.
     *
     * @return A new select event.
     */
    protected SelectEvent createLinkActivatedSelectEvent(AVList params)
    {
        PickedObject pickedObject = new PickedObject(0,
            this.getDelegateOwner() != null ? this.getDelegateOwner() : this);
        pickedObject.setOnTop();

        if (params != null)
            pickedObject.setValues(params);

        PickedObjectList list = new PickedObjectList();
        list.add(pickedObject);

        if (this.lastEvent instanceof MouseEvent)
        {
            String eventAction = null;

            if (this.lastEvent.getID() == MouseEvent.MOUSE_CLICKED
                && ((MouseEvent) this.lastEvent).getButton() == MouseEvent.BUTTON1)
            {
                eventAction = SelectEvent.LEFT_CLICK;
            }
            else if (this.lastEvent.getID() == MouseEvent.MOUSE_CLICKED
                && ((MouseEvent) this.lastEvent).getButton() == MouseEvent.BUTTON2)
            {
                eventAction = SelectEvent.RIGHT_CLICK;
            }

            return new SelectEvent(this.lastEvent.getSource(), eventAction, (MouseEvent) this.lastEvent, list);
        }
        else if (this.lastEvent != null)
        {
            return new SelectEvent(this.lastEvent.getSource(), null, (MouseEvent) null, list);
        }
        else
        {
            return new SelectEvent(null, null, (MouseEvent) null, list);
        }
    }
}

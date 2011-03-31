/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.examples.kml;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.tree.TreeNode;

import java.awt.*;
import java.beans.*;

/**
 * A controller that maps KML events to changes in a World Wind application. This controller animates the view to a KML
 * feature when the feature is clicked in the feature tree, and animates the view to KML network links when they are
 * refreshed.
 *
 * @author pabercrombie
 * @version $Id: KMLApplicationController.java 14686 2011-02-13 00:13:00Z dcollins $
 */
public class KMLApplicationController implements SelectListener, PropertyChangeListener
{
    /**
     * Indicates the <code>WorldWindow</code> this controller listens to for select events and property change events.
     * Initialized during construction.
     */
    protected WorldWindow wwd;
    /** Indicates the <code>TreeNode</code> currently under the cursor. Initially <code>null</code>. */
    protected TreeNode highlightedNode;

    /**
     * Creates a new <code>KMLApplicationController</code> with the specified <code>WorldWindow</code>. The new
     * <code>KMLApplicationController</code> listens KML tree node select events, and <code>KMLNetworkLink</code>
     * refresh property change events.
     *
     * @param wwd the <code>WorldWindow</code> this listens to, and who's <code>View</code> is moved upon a refresh
     *            event.
     *
     * @throws IllegalArgumentException if the <code>wwd</code> is <code>null</code>.
     */
    public KMLApplicationController(WorldWindow wwd)
    {
        if (wwd == null)
        {
            String message = Logging.getMessage("nullValue.WorldWindow");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.wwd = wwd;
        this.wwd.addSelectListener(this);
        this.wwd.getSceneController().addPropertyChangeListener(this);
    }

    /**
     * Animate the globe to a KML feature when the feature is clicked in the tree.
     *
     * @param event Select event.
     */
    public void selected(SelectEvent event)
    {
        if (event.isLeftClick())
        {
            Object topObject = event.getTopObject();
            if (topObject instanceof TreeNode)
            {
                // The KML feature should be attached to the node as the CONTEXT
                Object context = ((TreeNode) topObject).getValue(AVKey.CONTEXT);
                if (context instanceof KMLAbstractFeature)
                {
                    this.moveTo((KMLAbstractFeature) context);
                }
            }
        }
        else if (event.isRollover())
        {
            Object topObject = event.getTopObject();

            if (this.highlightedNode == topObject)
            {
                return; // Same thing selected
            }

            if (this.highlightedNode != null) // Something different selected
            {
                this.highlightedNode = null;
                this.setCursor(null); // Reset to default
            }

            if (topObject instanceof TreeNode)
            {
                // The KML feature should be attached to the node as the CONTEXT
                TreeNode treeNode = (TreeNode) topObject;

                Object context = treeNode.getValue(AVKey.CONTEXT);
                if (context instanceof KMLAbstractFeature)
                {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    this.highlightedNode = treeNode;
                }
            }
        }
    }

    /**
     * Handles property change events sent from <code>KMLNetworkLink</code> objects to the <code>SceneController</code>.
     * Upon receiving a <code>{@link gov.nasa.worldwind.avlist.AVKey#RETRIEVAL_STATE_SUCCESSFUL}</code> event from a
     * <code>KMLNetworkLink</code>, this attempts to fly to a <code>KMLAbstractView</code> associated with the link's
     * KML resource.
     * <p/>
     * If the <code>KMLNetworkLink</code>'s <code>flyToView</code> property is <code>0</code> or <code>false</code>,
     * this ignores the event. Otherwise, this attempts to get a <code>KMLAbstractView</code> from features in the
     * link's KML resource as follows: <ol> <li><code>NetworkLinkControl</code> child of link's KML resource.</li>
     * <li><code>AbstractFeature</code> child of link's KML resource.</li> </ol> If neither of the above features
     * contain a view, this ignores the event. Otherwise, this causes the <code>WorldWindow</code>'s <code>View</code>
     * to fly to the feature's <code>KMLAbstractView</code> using a <code>{@link gov.nasa.worldwind.examples.kml.KMLViewController}</code>
     *
     * @param event a property change event from the <code>SceneController</code>.
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        try
        {
            if (AVKey.RETRIEVAL_STATE_SUCCESSFUL.equals(event.getPropertyName())
                && event.getSource() instanceof KMLNetworkLink)
            {
                this.onNetworkLinkRefreshed((KMLNetworkLink) event.getSource());
            }
        }
        catch (Exception e)
        {
            // Wrap the handler in a try/catch to keep exceptions from bubbling up.
            Logging.logger().warning(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    /**
     * Called from <code>propertyChange</code> when a <code>KMLNetworkLink</code> sends a <code>{@link
     * gov.nasa.worldwind.avlist.AVKey#RETRIEVAL_STATE_SUCCESSFUL}</code> property change event. This attempts to fly to
     * a view associated with the link's KML resource.
     * <p/>
     * This does nothing if the <code>networkLink</code> is <code>null</code>.
     *
     * @param networkLink the <code>KMLNetworkLink</code> that has been refreshed.
     */
    protected void onNetworkLinkRefreshed(KMLNetworkLink networkLink)
    {
        if (networkLink == null)
            return;

        KMLRoot kmlRoot = networkLink.getNetworkResource();
        if (kmlRoot == null)
            return;

        if (Boolean.TRUE.equals(networkLink.getFlyToView()))
        {
            if (kmlRoot.getNetworkLinkControl() != null
                && kmlRoot.getNetworkLinkControl().getView() != null)
            {
                this.moveTo(kmlRoot.getNetworkLinkControl().getView());
            }
            else if (kmlRoot.getFeature() != null
                && kmlRoot.getFeature().getView() != null)
            {
                this.moveTo(kmlRoot.getFeature().getView());
            }
        }
    }

    /**
     * Smoothly moves the <code>WorldWindow</code>'s <code>View</code> to the specified
     * <code>KMLAbstractFeature</code>.
     *
     * @param feature the <code>KMLAbstractFeature</code> to move to.
     */
    protected void moveTo(KMLAbstractFeature feature)
    {
        KMLViewController viewController = KMLViewController.create(this.wwd);
        if (viewController == null)
            return;

        viewController.goTo(feature);
    }

    /**
     * Smoothly moves the <code>WorldWindow</code>'s <code>View</code> to the specified <code>KMLAbstractView</code>.
     *
     * @param view the <code>KMLAbstractView</code> to move to.
     */
    protected void moveTo(KMLAbstractView view)
    {
        KMLViewController viewController = KMLViewController.create(this.wwd);
        if (viewController == null)
            return;

        viewController.goTo(view);
    }

    /**
     * Set the mouse cursor.
     *
     * @param cursor New cursor. Pass {@code null} to reset the default cursor.
     */
    protected void setCursor(Cursor cursor)
    {
        if (this.wwd instanceof Component)
            ((Component) this.wwd).setCursor(cursor);
    }
}

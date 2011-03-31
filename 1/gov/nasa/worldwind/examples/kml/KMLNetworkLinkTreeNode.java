/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.examples.kml;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.util.tree.TreeNode;

import java.beans.PropertyChangeEvent;

/**
 * A <code>KMLFeatureTreeNode</code> that represents a KML network link defined by a <code>{@link
 * gov.nasa.worldwind.ogc.kml.KMLNetworkLink}</code>.
 * <p/>
 * <code>KMLNetworkLinkTreeNode</code>  automatically repopulates its hierarchy when its <code>KMLNetworkLink</code> is
 * refreshed, and notifies its listeners when this happens.
 *
 * @author dcollins
 * @version $Id: KMLNetworkLinkTreeNode.java 14684 2011-02-13 00:08:17Z dcollins $
 */
public class KMLNetworkLinkTreeNode extends KMLFeatureTreeNode
{
    /**
     * Creates a new <code>KMLNetworkLinkTreeNode</code> from the specified <code>networkLink</code>. The node's name is
     * set to the network link's name, and the node's hierarchy is populated from the network link's KML features.
     *
     * @param networkLink the KML network link this node represents.
     *
     * @throws IllegalArgumentException if the <code>networkLink</code> is <code>null</code>.
     */
    public KMLNetworkLinkTreeNode(KMLNetworkLink networkLink)
    {
        super(networkLink);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Additionally, this node's hierarchy is populated from the KML features in its <code>KMLNetworkLink</code>, and
     * this registers a <code>REFRESH</code> property change listener on the <code>KMLNetworkLink</code>.
     */
    @Override
    protected void initialize()
    {
        super.initialize();

        this.addNetworkResourceNodes();
        this.getFeature().addPropertyChangeListener(this);
    }

    /**
     * Indicates the KML network link this node represents.
     *
     * @return this node's KML network link.
     */
    public KMLNetworkLink getFeature()
    {
        return (KMLNetworkLink) super.getFeature();
    }

    /**
     * Handles property change events sent from the <code>KMLNetworkLink</code>.
     * <p/>
     * Upon receiving an <code>REFRESH</code> event from the <code>KMLNetworkLink</code>, this repopulates this node's
     * hierarchy with the KML features in its <code>KMLNetworkLink</code> and fires a <code>REFRESH</code> on this
     * node.
     * <p/>
     * If this receives any other type of event, it forwards that event to this node's listeners. This ensures that
     * property change events meant for the tree or the application are properly delivered.
     *
     * @param propertyChangeEvent a property change event from the <code>KMLNetworkLink</code>.
     */
    public void propertyChange(PropertyChangeEvent propertyChangeEvent)
    {
        if (propertyChangeEvent == null)
            return;

        if (AVKey.RETRIEVAL_STATE_SUCCESSFUL.equals(propertyChangeEvent.getPropertyName()))
        {
            this.onNetworkLinkRefresh();
            this.firePropertyChange(AVKey.RETRIEVAL_STATE_SUCCESSFUL, null, this);
        }
        else
        {
            super.propertyChange(propertyChangeEvent);
        }
    }

    /**
     * Called when this node's <code>KMLNetworkLink</code> refreshes. Clears this node's hieararchy by removing its
     * children, then adds a new <code>KMLFeatureTreeNode</code> to this node for each KML feature in the
     * <code>KMLNetworkLink</code>.
     */
    protected void onNetworkLinkRefresh()
    {
        this.removeAllChildren();
        this.addNetworkResourceNodes();
    }

    /**
     * Adds a new <code>KMLFeatureTreeNode</code> to this node for each KML feature in the <code>KMLNetworkLink</code>.
     * <p/>
     * If the <code>KMLNetworkLink</code>'s top level feature is a <code>KMLDocument</code>, this ignores this document
     * and adds its children directly to this node. Creating a node for the document adds an extra level to the tree
     * node that doesn't provide any meaningful grouping.
     * <p/>
     * This does nothing if the <code>KMLNetworkLink</code>'s top level feature is <code>null</code>.
     */
    protected void addNetworkResourceNodes()
    {
        KMLRoot kmlRoot = this.getFeature().getNetworkResource();
        if (kmlRoot == null || kmlRoot.getFeature() == null)
            return;

        // A KML document has only one top-level feature. Except for very simple files, this top level is typically a
        // Document. In this case we skip the top level document, and attach tree nodes for the features beneath that
        // document. Attaching the document as a tree node would add an extra level to the tree that doesn't provide any
        // meaningful grouping.

        if (kmlRoot.getFeature() instanceof KMLDocument)
        {
            KMLDocument doc = (KMLDocument) kmlRoot.getFeature();
            for (KMLAbstractFeature child : doc.getFeatures())
            {
                if (child != null)
                    this.addFeatureNode(child);
            }
        }
        else
        {
            this.addFeatureNode(kmlRoot.getFeature());
        }
    }

    /**
     * Adds the a new <code>KMLFeatureTreeNode</code> created with the specified <code>feature</code> to this node.
     *
     * @param feature the KML feature to add.
     */
    protected void addFeatureNode(KMLAbstractFeature feature)
    {
        TreeNode featureNode = KMLFeatureTreeNode.fromKMLFeature(feature);
        if (featureNode != null)
            this.addChild(featureNode);
    }
}

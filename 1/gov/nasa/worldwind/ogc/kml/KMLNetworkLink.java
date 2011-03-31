/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.ogc.kml;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.ogc.kml.impl.KMLTraversalContext;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.WWUtil;

/**
 * Represents the KML <i>NetworkLink</i> element and provides access to its contents.
 * <p/>
 * During rendering, <code>KMLNetworkLink</code> retrieves and loads its network resource whenever necessary. Upon a
 * successful retrieval, <code>KMLNetworkLink</code> sends an <code>{@link gov.nasa.worldwind.avlist.AVKey#RETRIEVAL_STATE_SUCCESSFUL}</code>
 * property change event to this link's property change listeners. Once retrieved and loaded,
 * <code>KMLNetworkLink</code> stores its network resource by calling <code>{@link #setNetworkResource(KMLRoot)}</code>,
 * draws its network resource during preRendering and rendering, and forwards property change events from the network
 * resource to its property change listeners.
 * <p/>
 * During retrieval, <code>KMLNetworkLink</code> attempts to use either the <code>Link</code> or the <code>Url</code>.
 * The <code>Link</code> is the preferred method for encoding a KML NetworkLink's address since KML version 2.1,
 * therefore we give it priority over <code>Url</code>.
 *
 * @author tag
 * @version $Id: KMLNetworkLink.java 14717 2011-02-15 19:28:53Z dcollins $
 */
public class KMLNetworkLink extends KMLAbstractFeature
{
    /** Indicates the network resource referenced by this <code>KMLNetworkLink</code>. Initially <code>null</code>. */
    protected KMLRoot networkResource;

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLNetworkLink(String namespaceURI)
    {
        super(namespaceURI);
    }

    public Boolean getRefreshVisibility()
    {
        return (Boolean) this.getField("refreshVisibility");
    }

    public Boolean getFlyToView()
    {
        return (Boolean) this.getField("flyToView");
    }

    public KMLLink getNetworkLink()
    {
        return (KMLLink) this.getField("Link");
    }

    public KMLLink getUrl()
    {
        return (KMLLink) this.getField("Url");
    }

    /**
     * Indicates the network resource referenced by this <code>KMLNetworkLink</code>. This returns <code>null</code> if
     * this link has no resource.
     *
     * @return this link's network resource, or <code>null</code> to indicate that this link has no resource.
     *
     * @see #setNetworkResource(KMLRoot)
     */
    public KMLRoot getNetworkResource()
    {
        return networkResource;
    }

    /**
     * Specifies the network resource referenced by this <code>KMLNetworkLink</code>, or <code>null</code> if this link
     * has no resource. If the specified <code>kmlRoot</code> is not <code>null</code, this link draws the
     * <code>kmlRoot</code> during preRendering and rendering, and forwards property change events from the
     * <code>kmlRoot</code> to this link's property change listeners.
     *
     * @param kmlRoot the network resource referenced by this <code>KMLNetworkLink</code>. May be <code>null</code>.
     *
     * @see #getNetworkResource()
     */
    public void setNetworkResource(KMLRoot kmlRoot)
    {
        // Remove any property change listeners previously set on the KMLRoot. This eliminates dangling references from
        // the KMLNetworkLink to its previous KMLRoot.
        if (this.networkResource != null)
            this.networkResource.removePropertyChangeListener(this);

        this.networkResource = kmlRoot;

        // Set up to listen for property change events on the KMLRoot. KMLNetworkLink must forward REPAINT and REFRESH
        // property change events from its internal KMLRoot to its parent KMLRoot to support BrowserBalloon repaint
        // events and recursive KMLNetworkLink elements.
        if (this.networkResource != null)
            this.networkResource.addPropertyChangeListener(this);
    }

    /**
     * Pre-render's the network resource referenced by this <code>KMLNetworkLink</code>. If this link must retrieve its
     * network resource, this initiates a retrieval and does nothing until the resource is retrieved and loaded. Once
     * the network resource is retrieved and loaded, this calls <code>{@link #setNetworkResource(KMLRoot)}</code> to
     * specify this link's new network resource, and sends an <code>{@link gov.nasa.worldwind.avlist.AVKey#RETRIEVAL_STATE_SUCCESSFUL}</code>
     * property change event to this link's property change listeners.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     *
     * @see #getNetworkResource()
     */
    @Override
    protected void doPreRender(KMLTraversalContext tc, DrawContext dc)
    {
        if (this.mustRetrieveNetworkResource())
            this.retrieveNetworkResource(dc);

        if (this.getNetworkResource() != null)
            this.getNetworkResource().preRender(tc, dc);
    }

    /**
     * Render's the network resource referenced by this <code>KMLNetworkLink</code>. This does nothing if this link has
     * no network resource.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     */
    @Override
    protected void doRender(KMLTraversalContext tc, DrawContext dc)
    {
        if (this.getNetworkResource() != null)
            this.getNetworkResource().render(tc, dc);
    }

    /**
     * Returns whether this <code>KMLNetworkLink</code> must retrieve its network resource. This always returns
     * <code>false</code> if this <code>KMLNetworkLink</code> has no <code>KMLLink</code>.
     *
     * @return <code>true</code> if this <code>KMLNetworkLink</code> must retrieve its network resource, otherwise
     *         <code>false</code>.
     */
    protected boolean mustRetrieveNetworkResource()
    {
        return (this.getNetworkLink() != null || this.getUrl() != null) && this.getNetworkResource() == null;
    }

    /**
     * Initiates a retrieval of the network resource referenced by this <code>KMLNetworkLink</code>. Once the network
     * resource is retrieved and loaded, this calls <code>{@link #setNetworkResource(KMLRoot)}</code> to specify this
     * link's new network resource, and sends an <code>{@link gov.nasa.worldwind.avlist.AVKey#RETRIEVAL_STATE_SUCCESSFUL}</code>
     * property change event to this link's property change listeners.
     * <p/>
     * This does nothing if this <code>KMLNetworkLink</code> has no <code>KMLLink</code>.
     *
     * @param dc the current draw context. Used to substitute <code>viewFormat</code> parameters in the
     *           <code>KMLLink</code> with the <code>DrawContext</code>'s view parameters.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void retrieveNetworkResource(DrawContext dc)
    {
        // Attempt to use the NetworkLink's Link property. This is the preferred method for encoding a NetworkLink
        // since KML 2.1, therefore we give it priority.
        KMLLink link = this.getNetworkLink();

        // If the Link property is null, attempt to use the deprecated Url property. Url was deprecated in KML 2.1, but
        // still appears in many KML documents containing NetworkLinks.
        if (link == null)
            link = this.getUrl();

        // If both the Link and the Url are null, then there's nothing to retrieve.
        if (link == null)
            return;

        // TODO: Implement support for NetworkLink refresh behavior. See refreshVisibility, KMLLink.refreshMode,
        // KMLLink.refreshInterval, KMLLink.viewRefreshMode, and KMLLink.viewRefreshTime.

        // TODO: Once NetworkLink refresh behavior is implemented, replace this with NetworkLink.getAddress(DrawContext).
        String address = link.getHref();
        if (address != null)
            address = address.trim();

        if (WWUtil.isEmpty(address))
            return;

        // Treat the address as either a path to a local document, or as an absolute URL to a remote document. If the
        // address references a remote document, this attempt to retrieve it and loads the document once retrieval
        // succeeds. This does not handle absolute local file paths; absolute local file paths are not supported by the
        // KML specification. However, a NetworkLink may reference an absolute local file by specifying an absolute
        // URL with the "file:" protocol.
        Object o = this.getRoot().resolveReference(address);
        if (o != null && o instanceof KMLRoot)
        {
            KMLRoot oldContentRoot = this.getNetworkResource();
            KMLRoot newContentRoot = (KMLRoot) o;
            this.setNetworkResource(newContentRoot);
            this.firePropertyChange(AVKey.RETRIEVAL_STATE_SUCCESSFUL, oldContentRoot, newContentRoot);
        }
    }
}

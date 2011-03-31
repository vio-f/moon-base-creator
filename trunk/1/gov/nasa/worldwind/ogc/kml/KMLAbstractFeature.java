/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.ogc.kml;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.ogc.kml.impl.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.xml.XMLEventParserContext;
import gov.nasa.worldwind.util.xml.atom.*;
import gov.nasa.worldwind.util.xml.xal.XALAddressDetails;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Represents the KML <i>Feature</i> element and provides access to its contents.
 * <p/>
 * <code>KMLAbstractFeature</code> implements the <code>KMLRenderable</code> interface, but does not actually render
 * anything. Subclasses should override the methods <code>{@link #doPreRender(gov.nasa.worldwind.ogc.kml.impl.KMLTraversalContext,
 * gov.nasa.worldwind.render.DrawContext)}</code> and <code>{@link #doRender(gov.nasa.worldwind.ogc.kml.impl.KMLTraversalContext,
 * gov.nasa.worldwind.render.DrawContext)}</code> to render their contents. If the <code>visibility</code> property is
 * set to <code>false</code>, this does not call <code>doPreRender</code> and <code>doRender</code> during rendering.
 *
 * @author tag
 * @version $Id: KMLAbstractFeature.java 14681 2011-02-12 23:51:12Z dcollins $
 */
public abstract class KMLAbstractFeature extends KMLAbstractObject implements KMLRenderable
{
    protected ArrayList<KMLAbstractStyleSelector> styleSelectors = new ArrayList<KMLAbstractStyleSelector>();

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    protected KMLAbstractFeature(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof KMLAbstractView)
            this.setView((KMLAbstractView) o);
        else if (o instanceof KMLAbstractTimePrimitive)
            this.setTimePrimitive((KMLAbstractTimePrimitive) o);
        else if (o instanceof KMLAbstractStyleSelector)
            this.addStyleSelector((KMLAbstractStyleSelector) o);
        else
            super.doAddEventContent(o, ctx, event, args);
    }

    public String getName()
    {
        return (String) this.getField("name");
    }

    /**
     * Indicates whether this <code>KMLAbstractFeature</code> is enabled for rendering. This returns <code>null</code>
     * if no visibility is specified. This indicates the default visibility of <code>true</code> should be used.
     *
     * @return <code>true</code> or <code>null</code> to draw feature shape, otherwise <code>false</code>. The default
     *         value is <code>true</code>.
     *
     * @see #setVisibility(Boolean)
     */
    public Boolean getVisibility()
    {
        return (Boolean) this.getField("visibility");
    }

    /**
     * Specifies whether this <code>KMLAbstractFeature</code> is enabled for rendering.
     *
     * @param visibility <code>true</code> or <code>null</code> to draw this feature, otherwise <code>false</code>. The
     *                   default value is <code>true</code>.
     *
     * @see #getVisibility()
     */
    public void setVisibility(Boolean visibility)
    {
        this.setField("visibility", visibility);
    }

    public Boolean getOpen()
    {
        return (Boolean) this.getField("open");
    }

    public AtomPerson getAuthor()
    {
        return (AtomPerson) this.getField("author");
    }

    public AtomLink getLink()
    {
        return (AtomLink) this.getField("link");
    }

    public String getAddress()
    {
        return (String) this.getField("address");
    }

    public XALAddressDetails getAddressDetails()
    {
        return (XALAddressDetails) this.getField("AddressDetails");
    }

    public String getPhoneNumber()
    {
        return (String) this.getField("phoneNumber");
    }

    public Object getSnippet()
    {
        Object o = this.getField("snippet");
        if (o != null)
            return o;

        return this.getField("Snippet");
    }

    public String getSnippetText()
    {
        Object o = this.getField("snippet");
        if (o != null)
            return (String) o;

        KMLSnippet snippet = (KMLSnippet) this.getField("Snippet");
        if (snippet != null)
            return snippet.getCharacters();

        return null;
    }

    public String getDescription()
    {
        return (String) this.getField("description");
    }

    protected void setView(KMLAbstractView o)
    {
        this.setField("AbstractView", o);
    }

    public KMLAbstractView getView()
    {
        return (KMLAbstractView) this.getField("AbstractView");
    }

    protected void setTimePrimitive(KMLAbstractTimePrimitive o)
    {
        this.setField("AbstractTimePrimitive", o);
    }

    public KMLAbstractTimePrimitive getTimePrimitive()
    {
        return (KMLAbstractTimePrimitive) this.getField("AbstractTimePrimitive");
    }

    public KMLStyleUrl getStyleUrl()
    {
        return (KMLStyleUrl) this.getField("styleUrl");
    }

    protected void addStyleSelector(KMLAbstractStyleSelector o)
    {
        this.styleSelectors.add(o);
    }

    public List<KMLAbstractStyleSelector> getStyleSelectors()
    {
        return this.styleSelectors;
    }

    public KMLRegion getRegion()
    {
        return (KMLRegion) this.getField("Region");
    }

    public KMLExtendedData getExtendedData()
    {
        return (KMLExtendedData) this.getField("ExtendedData");
    }

    /**
     * Set the balloon associated with this feature.
     *
     * @param balloon New balloon.
     */
    public void setBalloon(Balloon balloon)
    {
        // Stop listening for property changes on the previous balloon.
        Balloon oldBalloon = this.getBalloon();
        if (oldBalloon != null)
            oldBalloon.removePropertyChangeListener(this);

        this.setField(AVKey.BALLOON, balloon);

        if (balloon != null)
            balloon.addPropertyChangeListener(this);
    }

    /**
     * Get the balloon associated with this feature.
     *
     * @return The balloon associated with the feature. Returns null if there is no balloon.
     */
    public Balloon getBalloon()
    {
        return (Balloon) this.getField(AVKey.BALLOON);
    }

    /** {@inheritDoc} */
    public void preRender(KMLTraversalContext tc, DrawContext dc)
    {
        if (tc == null)
        {
            String message = Logging.getMessage("nullValue.TraversalContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!this.isFeatureActive())
            return;

        this.doPreRender(tc, dc);
    }

    /** {@inheritDoc} */
    public void render(KMLTraversalContext tc, DrawContext dc)
    {
        if (tc == null)
        {
            String message = Logging.getMessage("nullValue.TraversalContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!this.isFeatureActive())
            return;

        this.doRender(tc, dc);
    }

    /**
     * Indicates whether this <code>KMLAbstractFeature</code> is active and should be rendered. This returns
     * <code>true</code> if the this <code>KMLAbstractFeature</code>'s <code>visibility</code> is unspecified
     * (<code>null</code>) or is set to <code>true</code>.
     *
     * @return <code>true</code> to indicate that this feature should be rendered, otherwise <code>false</code>.
     */
    protected boolean isFeatureActive()
    {
        return this.getVisibility() == null || this.getVisibility();
    }

    /**
     * Called from <code>preRender</code> if this <code>KMLAbstractFeature</code>'s <code>visibility</code> is not set
     * to <code>false</code>. Subclasses should override this method to pre-render their content.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     */
    protected void doPreRender(KMLTraversalContext tc, DrawContext dc)
    {
        // Subclasses override to implement render behavior.
    }

    /**
     * Called from <code>render</code> if this <code>KMLAbstractFeature</code>'s <code>visibility</code> is not set to
     * <code>false</code>. Subclasses should override this method to render their content.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     */
    protected void doRender(KMLTraversalContext tc, DrawContext dc)
    {
        // Subclasses override to implement render behavior.
    }

    /**
     * Obtains the effective values for a specified sub-style (<i>IconStyle</i>, <i>ListStyle</i>, etc.) and state
     * (<i>normal</i> or <i>highlight</i>). The returned style is the result of merging values from this feature
     * instance's style selectors and its styleUrl, if any, with precedence given to style selectors.
     * <p/>
     * A remote <i>styleUrl</i> that has not yet been resolved is not included in the result. In this case the returned
     * sub-style is marked with the value {@link gov.nasa.worldwind.avlist.AVKey#UNRESOLVED}. The same is true when a
     * StyleMap style selector contains a reference to an external Style and that reference has not been resolved.
     *
     * @param styleState the style mode, either \"normal\" or \"highlight\".
     * @param subStyle   an instance of the sub-style desired, such as {@link gov.nasa.worldwind.ogc.kml.KMLIconStyle}.
     *                   The effective sub-style values are accumulated and merged into this instance. The instance
     *                   should not be one from within the KML document because its values are overridden and augmented;
     *                   it's just an independent variable in which to return the merged attribute values. For
     *                   convenience, the instance specified is returned as the return value of this method.
     *
     * @return the sub-style values for the specified type and state. The reference returned is the one passed in as the
     *         <code>subStyle</code> argument.
     */
    public KMLAbstractSubStyle getSubStyle(KMLAbstractSubStyle subStyle, String styleState)
    {
        return KMLAbstractStyleSelector.mergeSubStyles(this.getStyleUrl(), this.getStyleSelectors(), styleState,
            subStyle);
    }
}

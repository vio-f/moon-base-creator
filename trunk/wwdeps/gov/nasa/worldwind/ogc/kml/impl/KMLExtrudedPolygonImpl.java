/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.ogc.kml.impl;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;

/**
 * @author tag
 * @version $Id: KMLExtrudedPolygonImpl.java 14742 2011-02-16 23:40:13Z pabercrombie $
 */
public class KMLExtrudedPolygonImpl extends ExtrudedPolygon implements KMLRenderable
{
    protected final KMLAbstractFeature parent;
    protected boolean highlightAttributesResolved = false;
    protected boolean normalAttributesResolved = false;

    /**
     * Create an instance.
     *
     * @param tc        the current {@link KMLTraversalContext}.
     * @param placemark the <i>Placemark</i> element containing the <i>LineString</i>.
     * @param geom      the {@link KMLPolygon} geometry.
     *
     * @throws NullPointerException     if the geomtry is null.
     * @throws IllegalArgumentException if the parent placemark or the traversal context is null.
     */
    public KMLExtrudedPolygonImpl(KMLTraversalContext tc, KMLPlacemark placemark, KMLAbstractGeometry geom)
    {
        if (tc == null)
        {
            String msg = Logging.getMessage("nullValue.TraversalContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (placemark == null)
        {
            String msg = Logging.getMessage("nullValue.ParentIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.parent = placemark;

        KMLPolygon polygon = (KMLPolygon) geom;
        this.setEnableSides(polygon.isExtrude());

        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // KML default
        String altMode = polygon.getAltitudeMode();
        if (!WWUtil.isEmpty(altMode))
        {
            if ("clampToGround".equals(altMode))
                this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
            else if ("relativeToGround".equals(altMode))
                this.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            else if ("absolute".equals(altMode))
                this.setAltitudeMode(WorldWind.ABSOLUTE);
        }

        KMLLinearRing outerBoundary = polygon.getOuterBoundary();
        if (outerBoundary != null)
        {
            Position.PositionList coords = outerBoundary.getCoordinates();
            if (coords != null && coords.list != null)
                this.setOuterBoundary(outerBoundary.getCoordinates().list);
        }

        Iterable<? extends KMLLinearRing> innerBoundaries = polygon.getInnerBoundaries();
        if (innerBoundaries != null)
        {
            for (KMLLinearRing ring : innerBoundaries)
            {
                Position.PositionList coords = ring.getCoordinates();
                if (coords != null && coords.list != null)
                    this.addInnerBoundary(ring.getCoordinates().list);
            }
        }

        if (placemark.getName() != null)
            this.setValue(AVKey.DISPLAY_NAME, placemark.getName());

        if (placemark.getDescription() != null)
            this.setValue(AVKey.DESCRIPTION, placemark.getDescription());

        if (placemark.getSnippetText() != null)
            this.setValue(AVKey.SHORT_DESCRIPTION, placemark.getSnippetText());

        this.setValue(AVKey.CONTEXT, this.parent);
    }

    public void preRender(KMLTraversalContext tc, DrawContext dc)
    {
        // Intentionally left blank; KML extruded polygon does nothing during the preRender phase.
    }

    public void render(KMLTraversalContext tc, DrawContext dc)
    {
        // If the attributes are not inline or internal then they might not be resolved until the external KML
        // document is resolved. Therefore check to see if resolution has occurred.

        Balloon balloon = (Balloon) this.parent.getField(AVKey.BALLOON);

        if (this.isHighlighted())
        {
            if (!this.highlightAttributesResolved)
            {
                if (balloon == null)
                {
                    KMLBalloonStyle balloonStyle = (KMLBalloonStyle) this.parent.getSubStyle(new KMLBalloonStyle(null),
                        KMLConstants.HIGHLIGHT);

                    if (balloonStyle.hasStyleFields())
                    {
                        balloon = this.createBalloon(balloonStyle);
                        this.parent.setBalloon(balloon);
                    }
                }

                ShapeAttributes a = this.getCapHighlightAttributes();
                if (a == null || a.isUnresolved())
                {
                    a = this.makeAttributesCurrent(KMLConstants.HIGHLIGHT);
                    if (a != null)
                    {
                        this.setCapHighlightAttributes(a);
                        this.setSideHighlightAttributes(a);
                        if (!a.isUnresolved())
                            this.highlightAttributesResolved = true;
                    }
                }
            }
        }
        else
        {
            if (!this.normalAttributesResolved)
            {
                if (balloon == null)
                {
                    KMLBalloonStyle balloonStyle = (KMLBalloonStyle) this.parent.getSubStyle(new KMLBalloonStyle(null),
                        KMLConstants.NORMAL);

                    if (balloonStyle.hasStyleFields())
                    {
                        balloon = this.createBalloon(balloonStyle);
                        this.parent.setBalloon(balloon);
                    }
                }

                ShapeAttributes a = this.getCapAttributes();
                if (a == null || a.isUnresolved())
                {
                    a = this.makeAttributesCurrent(KMLConstants.NORMAL);
                    if (a != null)
                    {
                        this.setCapAttributes(a);
                        this.setSideAttributes(a);
                        if (!a.isUnresolved())
                            this.normalAttributesResolved = true;
                    }
                }
            }
        }

        if (this.isVisible() && balloon != null)
            balloon.render(dc);

        this.render(dc);
    }

    /** {@inheritDoc} */
    @Override
    protected PickedObject createPickedObject(DrawContext dc, Color pickColor)
    {
        PickedObject po = super.createPickedObject(dc, pickColor);

        // Add the KMLPlacemark to the picked object as the context of the picked object.
        po.setValue(AVKey.CONTEXT, this.parent);
        return po;
    }

    /**
     * If there is a balloon attached to the placemark, highlight the balloon when the placemark is highlighted.
     *
     * @param highlighted true to highlight the shape, otherwise false.
     */
    @Override
    public void setHighlighted(boolean highlighted)
    {
        Balloon balloon = (Balloon) this.parent.getField(AVKey.BALLOON);
        if (balloon != null)
        {
            // Highlight the balloon when the placemark is highlighted.
            balloon.setHighlighted(highlighted);

            // Set the delegate owner of the balloon to the currently highlighted placemark renderable. 
            balloon.setDelegateOwner(this);
        }

        super.setHighlighted(highlighted);
    }    

    /**
     * Determine and set the {@link Path} highlight attributes from the KML <i>Feature</i> fields.
     *
     * @param attrType the type of attributes, either {@link KMLConstants#NORMAL} or {@link KMLConstants#HIGHLIGHT}.
     *
     * @return the new attributes.
     */
    protected ShapeAttributes makeAttributesCurrent(String attrType)
    {
        ShapeAttributes attrs = this.getInitialAttributes(
            this.isHighlighted() ? KMLConstants.HIGHLIGHT : KMLConstants.NORMAL);

        // Get the KML sub-style for Line attributes. Map them to Shape attributes.

        KMLAbstractSubStyle lineSubStyle = this.parent.getSubStyle(new KMLLineStyle(null), attrType);
        if (!this.isHighlighted() || KMLUtil.isHighlightStyleState(lineSubStyle))
        {
            KMLUtil.assembleLineAttributes(attrs, (KMLLineStyle) lineSubStyle);
            if (lineSubStyle.hasField(AVKey.UNRESOLVED))
                attrs.setUnresolved(true);
        }

        // Get the KML sub-style for interior attributes. Map them to Shape attributes.

        KMLAbstractSubStyle fillSubStyle = this.parent.getSubStyle(new KMLPolyStyle(null), attrType);
        if (!this.isHighlighted() || KMLUtil.isHighlightStyleState(lineSubStyle))
        {
            KMLUtil.assembleInteriorAttributes(attrs, (KMLPolyStyle) fillSubStyle);
            if (fillSubStyle.hasField(AVKey.UNRESOLVED))
                attrs.setUnresolved(true);

            attrs.setDrawInterior(((KMLPolyStyle) fillSubStyle).isFill());
            attrs.setDrawOutline(((KMLPolyStyle) fillSubStyle).isOutline());
        }

        return attrs;
    }

    protected ShapeAttributes getInitialAttributes(String attrType)
    {
        ShapeAttributes attrs = new BasicShapeAttributes();

        if (KMLConstants.HIGHLIGHT.equals(attrType))
        {
            attrs.setOutlineMaterial(Material.RED);
            attrs.setInteriorMaterial(Material.PINK);
        }
        else
        {
            attrs.setOutlineMaterial(Material.WHITE);
            attrs.setInteriorMaterial(Material.LIGHT_GRAY);
        }

        return attrs;
    }

    /**
     * Create a balloon for this feature.
     *
     * @param style Feature's balloon style.
     *
     * @return New balloon for the feature.
     */
    protected Balloon createBalloon(KMLBalloonStyle style)
    {
        return this.parent.getRoot().createBalloon(this.parent, style, AVKey.GLOBE);
    }
}

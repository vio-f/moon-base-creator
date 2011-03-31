/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.ogc.kml.impl;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import gov.nasa.worldwind.render.*;

/**
 * Executes the mapping from KML to World Wind. Traverses a parsed KML document and creates the appropriate World Wind
 * object to represent the KML.
 *
 * @author tag
 * @version $Id: KMLController.java 14308 2010-12-27 21:27:19Z pabercrombie $
 */
public class KMLController extends WWObjectImpl implements PreRenderable, Renderable
{
    protected KMLRoot kmlRoot;
    protected KMLTraversalContext tc;

    public KMLController(KMLRoot root)
    {
        this.setKmlRoot(root);
        this.setTraversalContext(new KMLTraversalContext());
    }

    public KMLRoot getKmlRoot()
    {
        return this.kmlRoot;
    }

    public void setKmlRoot(KMLRoot kmlRoot)
    {
        // Stop listening for property changes in previous KMLRoot
        KMLRoot oldRoot = this.getKmlRoot();
        if (oldRoot != null)
            oldRoot.removePropertyChangeListener(this);

        this.kmlRoot = kmlRoot;

        if (kmlRoot != null)
            kmlRoot.addPropertyChangeListener(this);
    }

    public void setTraversalContext(KMLTraversalContext tc)
    {
        this.tc = tc;
    }

    public KMLTraversalContext getTraversalContext()
    {
        return this.tc;
    }

    public void preRender(DrawContext dc)
    {
        this.kmlRoot.preRender(this.getTraversalContext(), dc);
    }

    public void render(DrawContext dc)
    {
        this.kmlRoot.render(this.getTraversalContext(), dc);
    }
}

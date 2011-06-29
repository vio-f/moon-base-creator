/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>Data</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id: KMLData.java 13396 2010-05-25 21:02:14Z tgaskins $
 */
public class KMLData extends KMLAbstractObject
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLData(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getName()
    {
        return (String) this.getField("name");
    }

    public String getDisplayName()
    {
        return (String) this.getField("displayName");
    }

    public String getValue()
    {
        return (String) this.getField("value");
    }
}
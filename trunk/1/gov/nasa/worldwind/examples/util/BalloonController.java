/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.examples.util;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.examples.kml.KMLViewController;
import gov.nasa.worldwind.exception.WWTimeoutException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.ogc.kml.*;
import gov.nasa.worldwind.ogc.kml.impl.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * Controller to display a {@link Balloon} and handle balloon events. The controller does the following: <ul>
 * <li>Display a balloon when an object is selected</li> <li>Handle URL selection events in balloons</li> <li>Resize
 * BrowserBalloons</li> <li>Handle close, back, and forward events in BrowserBalloon</li> </ul>
 * <p/>
 * <h2>Displaying a balloon for a selected object</h2>
 * <p/>
 * When a object is clicked, the controller looks for a Balloon attached to the object. The controller includes special
 * logic for handling balloons attached to KML features.
 * <p/>
 * <h3>KML Features</h3>
 * <p/>
 * The KMLAbstractFeature is attached to the top PickedObject under AVKey.CONTEXT. The controller looks for the balloon
 * in the KMLAbstractFeature under key AVKey.BALLOON.
 * <p/>
 * <h3>Other objects</h3>
 * <p/>
 * If the top object is an instance of AVList, the controller looks for a Balloon under AVKey.BALLOON.
 * <p/>
 * <h2>URL events</h2>
 * <p/>
 * The controller looks for a value under AVKey.URL attached to either the top PickedObject or the top user object. If
 * the URL refers to a KML or KMZ document, the document is loaded into a new layer. If the link includes a reference to
 * a KML feature, controller will animate the view to that feature and/or open the feature balloon.
 * <p/>
 * If the link should open in a new window (determined by an AVKey.TARGET of AVKey.NAVIGATION_TARGET_NEW_BROWSER), the
 * controller will launch the system web browser and navigate to the link. Otherwise it will allow the BrowserBalloon to
 * navigate to the link.
 * <p/>
 * <h2>BrowserBalloon control events</h2>
 * <p/>
 * {@link gov.nasa.worldwind.render.AbstractBrowserBalloon} identifies its controls by attaching a value to the
 * PickedObject's AVList under AVKey.ACTION. The controller reads this value and performs the appropriate action. The
 * possible actions are AVKey.RESIZE, AVKey.BACK, AVKey.FORWARD, and AVKey.CLOSE.
 *
 * @author pabercrombie
 * @version $Id: BalloonController.java 14687 2011-02-13 00:42:50Z dcollins $
 */
public class BalloonController extends MouseAdapter implements SelectListener
{
    public static final String FLY_TO = "flyto";
    public static final String BALLOON = "balloon";
    public static final String BALLOON_FLY_TO = "balloonFlyto";

    protected WorldWindow wwd;

    protected Object lastSelectedObject;
    protected Balloon balloon;

    /**
     * Timeout to use when requesting remote documents. If the document does not load within this many milliseconds the
     * controller will stop trying and report an error.
     */
    protected long retrievalTimeout = 30 * 1000; // 30 seconds
    /** Interval between periodic checks for completion of asynchronous document retrieval (in milliseconds). */
    protected long retrievalPollInterval = 1000; // 1 second

    /**
     * A resize controller is created when the mouse enters a resize control on the balloon. The controller is destroyed
     * when the mouse exits the resize control.
     */
    protected BalloonResizeController resizeController;

    /**
     * Create a new balloon controller.
     *
     * @param wwd WorldWindow to attach to.
     */
    public BalloonController(WorldWindow wwd)
    {
        if (wwd == null)
        {
            String message = Logging.getMessage("nullValue.WorldWindow");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.wwd = wwd;
        this.wwd.addSelectListener(this);
        this.wwd.getInputHandler().addMouseListener(this);
    }

    /**
     * Handle a mouse click. If the top picked object has a balloon attached to it the balloon will be made visible. A
     * balloon may be attached to a KML feature, or to any picked object though {@link AVKey#BALLOON}.
     *
     * @param e Mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        // Implementation note: handle the balloon with a mouse listener instead of a select listener so that the balloon
        // can be turned off if the user clicks on the terrain.
        try
        {
            if (this.isBalloonTrigger(e))
            {
                PickedObjectList pickedObjects = this.wwd.getObjectsAtCurrentPosition();
                if (pickedObjects == null || pickedObjects.getTopPickedObject() == null)
                    return;

                Object topObject = pickedObjects.getTopObject();
                PickedObject topPickedObject = pickedObjects.getTopPickedObject();

                // Do nothing if the same thing is selected.
                if (this.lastSelectedObject == topObject || this.balloon == topObject)
                {
                    return;
                }

                // Hide the active balloon if the selection has changed, or if terrain was selected.
                if (this.lastSelectedObject != null || topPickedObject.isTerrain())
                {
                    this.hideBalloon(); // Something else selected
                }

                Balloon balloon = this.getBalloon(topPickedObject);

                // Don't change balloons that are already visible
                if (balloon != null && !balloon.isVisible())
                {
                    this.lastSelectedObject = topObject;
                    this.showBalloon(balloon, e.getPoint());
                }
            }
        }
        catch (Exception ex)
        {
            // Wrap the handler in a try/catch to keep exceptions from bubbling up
            Logging.logger().warning(ex.getMessage() != null ? ex.getMessage() : ex.toString());
        }
    }

    public void selected(SelectEvent event)
    {
        try
        {
            PickedObject pickedObject = event.getTopPickedObject();
            if (pickedObject == null)
                return;
            Object topObject = event.getTopObject();

            // Handle balloon resize events. Create a resize controller when the mouse enters the resize area.
            // While the mouse is in the resize area, the resize controller will handle select events to resize the
            // balloon. The controller will be destroyed when the mouse exists the resize area.  
            if (AVKey.RESIZE.equals(pickedObject.getStringValue(AVKey.ACTION)) && topObject instanceof Balloon)
            {
                this.createResizeController((Balloon) topObject, event);
            }
            else if (this.resizeController != null && !this.resizeController.isResizing())
            {
                // Destroy the resize controller if the mouse is out of the resize area and the controller
                // is not resizing the balloon. The mouse is allowed to move out of the resize area during the resize
                // operation.
                this.destroyResizeController(event);
            }

            // Check to see if the event is a link activation or other balloon event
            if (event.isLeftClick())
            {
                String url = this.getUrl(pickedObject);
                if (url != null)
                {
                    this.onLinkActivated(event, url);
                }
                else if (pickedObject.hasKey(AVKey.ACTION) && topObject instanceof AbstractBrowserBalloon)
                {
                    this.onBalloonAction((AbstractBrowserBalloon) topObject, pickedObject.getStringValue(AVKey.ACTION));
                }
            }
        }
        catch (Exception e)
        {
            // Wrap the handler in a try/catch to keep exceptions from bubbling up
            Logging.logger().warning(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    /**
     * Get the URL attached to a PickedObject. This method looks for a URL attached to the PickedObject under {@link
     * AVKey#URL}.
     *
     * @param pickedObject PickedObject to inspect. May not be null.
     *
     * @return The URL attached to the PickedObject, or null if there is no URL.
     */
    protected String getUrl(PickedObject pickedObject)
    {
        return pickedObject.getStringValue(AVKey.URL);
    }

    /**
     * Get the KML feature that is the context of a picked object. The context is associated with either the
     * PickedObject or the user object under the key {@link AVKey#CONTEXT}.
     *
     * @param pickedObject PickedObject to inspect for context. May not be null.
     *
     * @return The KML feature associated with the picked object, or null if no KML feature is found.
     */
    protected KMLAbstractFeature getContext(PickedObject pickedObject)
    {
        Object topObject = pickedObject.getObject();

        Object context = pickedObject.getValue(AVKey.CONTEXT);

        // If there was no context in the PickedObject, look for it in the top user object.
        if (context == null && topObject instanceof AVList)
        {
            context = ((AVList) topObject).getValue(AVKey.CONTEXT);
        }

        if (context instanceof KMLAbstractFeature)
            return (KMLAbstractFeature) context;
        else
            return null;
    }

    /**
     * Called when a {@link gov.nasa.worldwind.render.AbstractBrowserBalloon} control is activated (Close, Back, or
     * Forward).
     *
     * @param browserBalloon Balloon involved in action.
     * @param action         Identifier for the action that occurred.
     */
    protected void onBalloonAction(AbstractBrowserBalloon browserBalloon, String action)
    {
        if (AVKey.CLOSE.equals(action))
        {
            // If the balloon closing is the balloon we manage, call hideBalloon to clean up state.
            // Otherwise just make the balloon invisible.
            if (browserBalloon == this.balloon)
                this.hideBalloon();
            else
                browserBalloon.setVisible(false);
        }
        else if (AVKey.BACK.equals(action))
            browserBalloon.goBack();

        else if (AVKey.FORWARD.equals(action))
            browserBalloon.goForward();
    }

    //********************************************************************//
    //***********************  Resize events *****************************//
    //********************************************************************//

    /**
     * Create a resize controller and attach it to the WorldWindow. Has no effect if there is already an active resize
     * controller.
     *
     * @param balloon Balloon to resize.
     * @param event   Event that initiated the resize. The top picked object must contain the balloon bounds in its
     *                {@link AVList} under the key {@link AVKey#BOUNDS}.
     */
    protected void createResizeController(Balloon balloon, SelectEvent event)
    {
        // If a resize controller is already active, don't start another one.
        if (this.resizeController != null)
            return;

        Object bounds = event.getTopPickedObject().getValue(AVKey.BOUNDS);
        if (bounds instanceof Rectangle)
        {
            this.resizeController = new BalloonResizeController(this.wwd, balloon, (Rectangle) bounds);
            this.wwd.addSelectListener(this.resizeController);
        }
    }

    /**
     * Destroy the active resize controller.
     *
     * @param event Event that triggered the controller to be destroyed.
     */
    protected void destroyResizeController(SelectEvent event)
    {
        if (this.resizeController != null)
        {
            // Pass the last event to the controller so that it can clean up internal state if it needs to.
            this.resizeController.selected(event);

            this.wwd.removeSelectListener(this.resizeController);
            this.resizeController = null;

            // Reset the cursor to default. The resize controller may have changed it.
            if (this.wwd instanceof Component)
            {
                ((Component) this.wwd).setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    //**********************************************************************//
    //***********************  Hyperlink events  ***************************//
    //**********************************************************************//

    /**
     * Called when a URL in a balloon is activated. This method handles links to KML documents, features in KML
     * documents, and links that target a new browser window.
     * <p/>
     * The possible cases are:
     * <p/>
     * <b>KML/KMZ document</b> - Load the document in a new layer.<br> <b>Feature in KML/KMZ document</b> - Load the
     * document, navigate to the feature and/or open feature balloon.<br> <b>Feature in currently open KML/KMZ
     * document</b> - Navigate to the feature and/or open feature balloon. <br> <b>HTML document, target current
     * window</b> - No action, let the BrowserBalloon navigate to the URL. <br> <b>HTML document, target new window</b>
     * - Launch the system web browser and navigate to the URL.
     * <p/>
     * If this method takes action on the URL, the SelectEvent will be marked as consumed. Marking the event as consumed
     * prevents BrowserBalloon from handling the navigation event.
     *
     * @param event SelectEvent for the URL activation.
     * @param url   URL that was activated.
     */
    protected void onLinkActivated(SelectEvent event, String url)
    {
        PickedObject pickedObject = event.getTopPickedObject();
        String type = pickedObject.getStringValue(AVKey.MIME_TYPE);

        // Break URL into base and reference
        String linkBase;
        String linkRef;

        int hashSign = url.indexOf("#");
        if (hashSign != -1)
        {
            linkBase = url.substring(0, hashSign);
            linkRef = url.substring(hashSign);
        }
        else
        {
            linkBase = url;
            linkRef = null;
        }

        KMLRoot targetDoc; // The document to load and/or fly to
        KMLRoot contextDoc = null; // The local KML document that initiated the link
        KMLAbstractFeature kmlFeature;

        boolean isKmlUrl = this.isKmlUrl(linkBase, type);
        boolean foundLocalFeature = false;

        // Look for a KML feature attached to the picked object. If present, the link will be interpreted relative
        // to this feature.
        kmlFeature = this.getContext(pickedObject);
        if (kmlFeature != null)
            contextDoc = kmlFeature.getRoot();

        // If this link is to a KML or KMZ document we will load the document into a new layer.
        if (isKmlUrl)
        {
            targetDoc = this.findOpenKmlDocument(linkBase);
            if (targetDoc == null)
            {
                // Asynchronously request the document.
                this.requestDocument(linkBase, contextDoc, linkRef);

                // We are opening a document, consume the event to prevent balloon from trying to load the document.
                event.consume();
                return;
            }
        }
        else
        {
            // URL does not refer to a remote KML document, assume that it refers to a feature in the currect doc
            targetDoc = contextDoc;
        }

        // If the link also has a feature reference, we will move to the feature
        if (linkRef != null)
        {
            if (this.onFeatureLinkActivated(targetDoc, linkRef))
            {
                foundLocalFeature = true;
                event.consume(); // Consume event if the target feature was found
            }
        }

        // If the link is not to a KML file or feature, and the link targets a new browser window, launch the system web
        // browser. BrowserBalloon ignores link events that target new windows, so we need to handle them here.
        if (!isKmlUrl && !foundLocalFeature)
        {
            String target = pickedObject.getStringValue(AVKey.TARGET);
            if (AVKey.NAVIGATION_TARGET_NEW_BROWSER.equals(target))
            {
                this.openInNewBrowser(event, url);
            }
        }
    }

    /**
     * Open a URL in a new web browser. Launch the system web browser and navigate to the URL.
     *
     * @param event SelectEvent that triggered navigation. The event is consumed if URL can be parsed.
     * @param url   URL to open.
     */
    protected void openInNewBrowser(SelectEvent event, String url)
    {
        try
        {
            BrowserOpener.browse(new URL(url));
            event.consume();
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("generic.ExceptionAttemptingToInvokeWebBrower", url);
            Logging.logger().warning(message);
        }
    }

    /**
     * Called when a link to a KML feature is activated.
     *
     * @param doc          Document to search for the feature.
     * @param linkFragment Reference to the feature. The fragment may contain a display directive. For example
     *                     "#myPlacemark", or "#myPlacemark;balloon".
     *
     * @return True if a feature matching the reference was found and some action was taken.
     */
    protected boolean onFeatureLinkActivated(KMLRoot doc, String linkFragment)
    {
        // Split the reference into the feature id and the display directive (flyto, balloon, etc)
        String[] parts = linkFragment.split(";");
        String featureId = parts[0];
        String directive = parts.length > 1 ? parts[1] : FLY_TO;

        if (!WWUtil.isEmpty(featureId) && doc != null)
        {
            Object o = doc.resolveReference(featureId);
            if (o instanceof KMLAbstractFeature)
            {
                this.doFeatureLinkActivated((KMLAbstractFeature) o, directive);
                return true;
            }
        }
        return false;
    }

    /**
     * Handle activation of a KML feature link. Depending on the display directive, this method will either move the
     * view to the feature, open the balloon for the feature, or both. See the KML specification for details on links to
     * features in the KML description balloon.
     *
     * @param feature   Feature to navigate to.
     * @param directive Display directive, one of {@link #FLY_TO}, {@link #BALLOON}, or {@link #BALLOON_FLY_TO}.
     */
    protected void doFeatureLinkActivated(KMLAbstractFeature feature, String directive)
    {
        if (FLY_TO.equals(directive) || BALLOON_FLY_TO.equals(directive))
        {
            this.moveToFeature(feature);
        }

        if (BALLOON.equals(directive) || BALLOON_FLY_TO.equals(directive))
        {
            Object balloonObj = feature.getField(AVKey.BALLOON);
            if (balloonObj instanceof Balloon)
            {
                Balloon b = (Balloon) balloonObj;

                // Don't change balloons that are already visible
                if (!b.isVisible())
                {
                    this.lastSelectedObject = b.getDelegateOwner();

                    Position pos = this.getBalloonPosition(feature);
                    if (pos != null)
                    {
                        this.hideBalloon(); // Hide previously displayed balloon, if any
                        this.showBalloon(b, pos);
                    }
                    else
                    {
                        // The feature may be attached to the screen, not the globe
                        Point point = this.getBalloonPoint(feature);
                        if (point != null)
                        {
                            this.hideBalloon(); // Hide previously displayed balloon, if any
                            this.showBalloon(b, point);
                        }
                    }
                }
            }
        }
    }

    /**
     * Does a URL refer to a KML or KMZ document?
     *
     * @param url         URL to test.
     * @param contentType Mime type of the URL content. May be null.
     *
     * @return Return true if the URL refers to a file with a ".kml" or ".kmz" extension, or if the {@code contentType}
     *         is the KML or KMZ mime type.
     */
    protected boolean isKmlUrl(String url, String contentType)
    {
        if (WWUtil.isEmpty(url))
            return false;

        String suffix = WWIO.getSuffix(url);

        return "kml".equalsIgnoreCase(suffix)
            || "kmz".equalsIgnoreCase(suffix)
            || KMLConstants.KML_MIME_TYPE.equals(contentType)
            || KMLConstants.KMZ_MIME_TYPE.equals(contentType);
    }

    /**
     * Move the view to look at a KML feature. The view will be adjusted to look at the bounding sector that contains
     * all of the feature's points.
     *
     * @param feature Feature to look at.
     */
    protected void moveToFeature(KMLAbstractFeature feature)
    {
        KMLViewController viewController = KMLViewController.create(this.wwd);
        viewController.goTo(feature);
    }

    //**********************************************************************//
    //**********************  Show/Hide Balloon  ***************************//
    //**********************************************************************//

    /**
     * Inspect a mouse event to see if it should make a balloon visible.
     *
     * @param e Event to inspect.
     *
     * @return {@code true} if the event is a balloon trigger. This implementation returns {@code true} if the event is
     *         a left click.
     */
    protected boolean isBalloonTrigger(MouseEvent e)
    {
        // Handle only left click
        return (e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() % 2 == 1);
    }

    /**
     * Get the balloon attached to a PickedObject. If the PickedObject represents a KML feature, then the balloon will
     * be retrieved from the feature's field {@link AVKey#BALLOON}. Otherwise, the balloon will be retrieved from the
     * user object's field AVKey.BALLOON.
     *
     * @param pickedObject PickedObject to inspect. May not be null.
     *
     * @return The balloon attached to the picked object, or null if there is no balloon. Returns null if {@code
     *         pickedObject} is null.
     */
    protected Balloon getBalloon(PickedObject pickedObject)
    {
        Object topObject = pickedObject.getObject();
        Object balloonObj = null;

        // Look for a KMLAbstractFeature context. If the top picked object is part of a KML feature, the
        // feature will determine the balloon.
        if (pickedObject.hasKey(AVKey.CONTEXT))
        {
            Object contextObj = pickedObject.getValue(AVKey.CONTEXT);
            if (contextObj instanceof KMLAbstractFeature)
                balloonObj = ((KMLAbstractFeature) contextObj).getField(AVKey.BALLOON);
        }

        // If we didn't find a balloon on the KML feature, look for a balloon in the AVList
        if (balloonObj == null && topObject instanceof AVList)
        {
            AVList avList = (AVList) topObject;
            balloonObj = avList.getValue(AVKey.BALLOON);
        }

        if (balloonObj instanceof Balloon)
            return (Balloon) balloonObj;
        else
            return null;
    }

    /**
     * Show a balloon at a screen point.
     *
     * @param balloon Balloon to make visible.
     * @param point   Point where mouse was clicked.
     */
    protected void showBalloon(Balloon balloon, Point point)
    {
        // If the balloon is attached to the screen rather than the globe, move it to the
        // current point. Otherwise move it to the position under the current point.
        if (balloon instanceof ScreenBalloon)
            ((ScreenBalloon) balloon).setScreenLocation(point);
        else if (balloon instanceof GlobeBalloon)
            ((GlobeBalloon) balloon).setPosition(wwd.getView().computePositionFromScreenPoint(point.x, point.y));

        this.balloon = balloon;
        this.balloon.setVisible(true);
    }

    /**
     * Show a balloon at a globe position.
     *
     * @param balloon  Balloon to make visible.
     * @param position Position on the globe to locate the balloon. If the balloon is attached to the screen, it will be
     *                 position at the screen point currently over this position.
     */
    protected void showBalloon(Balloon balloon, Position position)
    {
        // If the balloon is attached to the screen rather than the globe, move it to the
        // current point. Otherwise move it to the position under the current point.
        if (balloon instanceof ScreenBalloon)
        {
            Vec4 screenPoint = this.wwd.getView().project(
                this.wwd.getModel().getGlobe().computePointFromPosition(position));
            ((ScreenBalloon) balloon).setScreenLocation(new Point((int) screenPoint.x, (int) screenPoint.y));
        }
        else
        {
            ((GlobeBalloon) balloon).setPosition(position);
        }

        this.balloon = balloon;
        this.balloon.setVisible(true);
    }

    /** Hide the active balloon. Does nothing if there is no active balloon. */
    protected void hideBalloon()
    {
        if (this.balloon != null)
        {
            this.balloon.setVisible(false);
            this.balloon = null;
        }
        this.lastSelectedObject = null;
    }

    //**********************************************************************//
    //***********  Methods to determine where to but the balloon  **********//
    //**********************************************************************//

    /**
     * Get the position of the balloon for a KML feature attached to the globe. This method applies to KML features that
     * area attached to the globe, rather than to the screen (for example, this method applies to GroundOverlay, but not
     * to ScreenOverlay). This method determines the type of feature, and calls a more specific method to handle
     * features of that type.
     *
     * @param feature Feature to find balloon position for.
     *
     * @return Position at which to place the Placemark balloon.
     *
     * @see #getBalloonPositionForPlacemark
     * @see #getBalloonPositionForGroundOverlay
     * @see #getBalloonPoint
     */
    protected Position getBalloonPosition(KMLAbstractFeature feature)
    {
        if (feature instanceof KMLPlacemark)
        {
            return this.getBalloonPositionForPlacemark((KMLPlacemark) feature);
        }
        else if (feature instanceof KMLGroundOverlay)
        {
            return this.getBalloonPositionForGroundOverlay(((KMLGroundOverlay) feature));
        }
        return null;
    }

    /**
     * Get the position of the balloon for a KML placemark. For a point placemark, this method returns the placemark
     * point. For all other placemarks, this method returns the centroid of the sector that bounds all of the points in
     * the placemark. Note that the centroid of the sector may not actually fall on the visible area of the shape.
     *
     * @param placemark Placemark for which to find a balloon position.
     *
     * @return Position for the balloon, or null if a position cannot be determined.
     *
     * @see #getBalloonPosition
     */
    protected Position getBalloonPositionForPlacemark(KMLPlacemark placemark)
    {
        List<Position> positions = new ArrayList<Position>();

        KMLAbstractGeometry geometry = placemark.getGeometry();
        KMLUtil.getPositions(this.wwd.getModel().getGlobe(), geometry, positions);

        return this.getBalloonPosition(positions);
    }

    /**
     * Get the position of the balloon for a KML GroundOverlay. This method returns the centroid of the sector that
     * bounds all of the points in the overlay.
     *
     * @param overlay Ground overlay for which to find a balloon position.
     *
     * @return Position for the balloon, or null if a position cannot be determined.
     *
     * @see #getBalloonPosition
     */
    protected Position getBalloonPositionForGroundOverlay(KMLGroundOverlay overlay)
    {
        Position.PositionList positionsList = overlay.getPositions();
        return this.getBalloonPosition(positionsList.list);
    }

    /**
     * Get the position of the balloon for a list of positions that bound a feature. This method returns a position at
     * the centroid of the sector that bounds all of the points in the list, and at the maximum altitude of the points
     * in the list.
     *
     * @param positions List of positions to find a balloon position.
     *
     * @return Position for the balloon, or null if a position cannot be determined.
     */
    protected Position getBalloonPosition(List<? extends Position> positions)
    {
        if (positions.size() == 1) // Only one point, just return the point
        {
            return positions.get(0);
        }
        else if (positions.size() > 1)// Many points, find center point of bounding sector
        {
            Sector sector = Sector.boundingSector(positions);

            return new Position(sector.getCentroid(), this.findMaxAltitude(positions));
        }
        return null;
    }

    /**
     * Get the screen point for a balloon for a KML feature attached to the screen. This method applies only to KML
     * features that area attached to the screen, rather than to the globe (for example, ScreenOverlay, but not
     * GroundOverlay). This method determines the type of feature, and then calls a more specific method to handle
     * features of that type.
     *
     * @param feature Feature for which to find a balloon point.
     *
     * @return Point for the balloon, or null if a point cannot be determined.
     *
     * @see #getBalloonPointForScreenOverlay
     * @see #getBalloonPosition
     */
    protected Point getBalloonPoint(KMLAbstractFeature feature)
    {
        if (feature instanceof KMLScreenOverlay)
        {
            return this.getBalloonPointForScreenOverlay((KMLScreenOverlay) feature);
        }
        return null;
    }

    /**
     * Get the screen point for a balloon for a ScreenOverlay.
     *
     * @param overlay ScreenOverlay for which to find a balloon position.
     *
     * @return Point for the balloon, or null if a point cannot be determined.
     *
     * @see #getBalloonPoint
     */
    protected Point getBalloonPointForScreenOverlay(KMLScreenOverlay overlay)
    {
        KMLVec2 xy = overlay.getScreenXY();
        Offset offset = new Offset(xy.getX(), xy.getY(), KMLUtil.kmlUnitsToWWUnits(xy.getXunits()),
            KMLUtil.kmlUnitsToWWUnits(xy.getYunits()));

        Rectangle viewport = this.wwd.getView().getViewport();
        Point2D point2D = offset.computeOffset(viewport.width, viewport.height, 1d, 1d);

        return new Point((int) point2D.getX(), (int) point2D.getY());
    }

    /**
     * Get the maximum altitude in a list of positions.
     *
     * @param positions List of positions to search for max altitude.
     *
     * @return The maximum elevation in the list of positions. Returns {@code -Double.MAX_VALUE} if {@code positions} is
     *         empty.
     */
    protected double findMaxAltitude(List<? extends Position> positions)
    {
        double maxAltitude = -Double.MAX_VALUE;
        for (Position p : positions)
        {
            double altitude = p.getAltitude();
            if (altitude > maxAltitude)
                maxAltitude = altitude;
        }

        return maxAltitude;
    }

    //**********************************************************************//
    //******************  Remote document retrieval  ***********************//
    //**********************************************************************//

    /**
     * Search for a KML document that has already been opened. This method looks in the session cache for a parsed
     * KMLRoot.
     *
     * @param url URL of the KML document.
     *
     * @return KMLRoot for an already-parsed document, or null if the document was not found in the cache.
     */
    protected KMLRoot findOpenKmlDocument(String url)
    {
        Object o = WorldWind.getSessionCache().get(url);
        if (o instanceof KMLRoot)
            return (KMLRoot) o;
        else
            return null;
    }

    /**
     * Asynchronously load a KML document. When the document is available, {@link #onDocumentLoaded} will be called on
     * the Event Dispatch Thread (EDT). If the document fails to load, {@link #onDocumentFailed} will be called. Failure
     * will be reported if the document does not load within {@link #retrievalTimeout} milliseconds.
     *
     * @param url        URL of KML doc to open.
     * @param context    Context of the URL, used to resolve local references.
     * @param featureRef A reference to a feature in the remote file to animate the globe to once the file is
     *                   available.
     *
     * @see #onDocumentLoaded(String, gov.nasa.worldwind.ogc.kml.KMLRoot, String)
     * @see #onDocumentFailed(String, Exception)
     */
    protected void requestDocument(String url, KMLRoot context, String featureRef)
    {
        Timer docLoader = new Timer("BalloonController document retrieval");

        // Schedule a task that will request the document periodically until the document becomes available or the
        // request timeout is reached.
        docLoader.scheduleAtFixedRate(new DocumentRetrievalTask(url, context, featureRef, this.retrievalTimeout),
            0, this.retrievalPollInterval);
    }

    /**
     * Called when a KML document has been loaded. This implementation creates a new layer and adds the new document to
     * the layer.
     *
     * @param url        URL of the document that has been loaded.
     * @param document   Parsed document.
     * @param featureRef Reference to a feature that must be activated (fly to or open balloon).
     */
    protected void onDocumentLoaded(String url, KMLRoot document, String featureRef)
    {
        // Use the URL as the document's DISPLAY_NAME. This field is used by addDocumentLayer to determine the layer's
        // name.
        document.setField(AVKey.DISPLAY_NAME, url);
        this.addDocumentLayer(document);

        if (featureRef != null)
            this.onFeatureLinkActivated(document, featureRef);
    }

    /**
     * Called when a KML file fails to load due to a network timeout or parsing error. This implementation simply logs a
     * warning.
     *
     * @param url URL of the document that failed to load.
     * @param e   Exception that caused the failure.
     */
    protected void onDocumentFailed(String url, Exception e)
    {
        String message = Logging.getMessage("generic.ExceptionWhileReading", url + ": " + e.getMessage());
        Logging.logger().warning(message);
    }

    /**
     * Adds the specified <code>document</code> to this controller's <code>WorldWindow</code> as a new
     * <code>Layer</code>.
     * <p/>
     * This expects the <code>kmlRoot</code>'s <code>AVKey.DISPLAY_NAME</code> field to contain a display name suitable
     * for use as a layer name.
     *
     * @param document the KML document to add a <code>Layer</code> for.
     */
    protected void addDocumentLayer(KMLRoot document)
    {
        KMLController controller = new KMLController(document);

        // Load the document into a new layer.
        RenderableLayer kmlLayer = new RenderableLayer();
        kmlLayer.setName((String) document.getField(AVKey.DISPLAY_NAME));
        kmlLayer.addRenderable(controller);

        this.wwd.getModel().getLayers().add(kmlLayer);
    }

    /**
     * A TimerTask that will request a resource from the {@link gov.nasa.worldwind.cache.FileStore} until it becomes
     * available, or until a timeout is exceeded. When the task finishes it will trigger a callback on the Event
     * Dispatch Thread (EDT) to either {@link BalloonController#onDocumentLoaded} or {@link
     * BalloonController#onDocumentFailed}.
     * <p/>
     * This task is designed to be repeated periodically. The task will cancel itself when the document becomes
     * available, or the timeout is exceeded.
     */
    protected class DocumentRetrievalTask extends TimerTask
    {
        /** URL of the KML document to load. */
        protected String docUrl;
        /** The document that contained the link this document. */
        protected KMLRoot context;
        /**
         * Reference to a feature in the remote document, with an action (for example, "myFeature;flyto"). The action
         * will be carried out when the document becomes available.
         */
        protected String featureRef;
        /**
         * Task timeout. If the document has not been loaded after this many milliseconds, the task will cancel itself
         * and report an error.
         */
        protected long timeout;
        /** Time that the task started, used to evaluate the timeout. */
        protected long start;

        /**
         * Create a new retrieval task.
         *
         * @param url        URL of document to retrieve.
         * @param context    Context of the link to the document. May be null.
         * @param featureRef Reference to a feature in the remote document, with an action to perform on the feature
         *                   (for example, "myFeature;flyto"). The action will be carried out when the document becomes
         *                   available.
         * @param timeout    Timeout for this task in milliseconds. The task will fail if the document has not been
         *                   downloaded in this many milliseconds.
         */
        public DocumentRetrievalTask(String url, KMLRoot context, String featureRef, long timeout)
        {
            this.docUrl = url;
            this.context = context;
            this.featureRef = featureRef;
            this.timeout = timeout;
        }

        /**
         * Request the document from the {@link gov.nasa.worldwind.cache.FileStore}. If the document is available, parse
         * it and schedule a callback on the EDT to {@link BalloonController#onDocumentLoaded(String,
         * gov.nasa.worldwind.ogc.kml.KMLRoot, String)}. If an exception occurs, or the timeout is exceeded, schedule a
         * callback on the EDT to {@link BalloonController#onDocumentFailed(String, Exception)}
         */
        public void run()
        {
            KMLRoot root = null;

            try
            {
                // If this is the first execution, capture the start time so that we can evaluate the timeout later.
                if (this.start == 0)
                    this.start = System.currentTimeMillis();

                // Check for timeout before doing any work
                if (System.currentTimeMillis() > this.start + this.timeout)
                    throw new WWTimeoutException(Logging.getMessage("generic.CannotOpenFile", this.docUrl));

                // If we have a context document, let that doc resolve the reference. Otherwise, request it from the
                // file store.
                Object docSource;
                if (this.context != null)
                    docSource = this.context.resolveReference(this.docUrl);
                else
                    docSource = WorldWind.getDataFileStore().requestFile(this.docUrl);

                if (docSource instanceof KMLRoot)
                {
                    root = (KMLRoot) docSource;
                    // Roots returned by resolveReference are already parsed, no need to parse here
                }
                else if (docSource != null)
                {
                    root = KMLRoot.create(docSource);
                    root.parse();
                }

                // If root is non-null we have succeeded in loading the document.
                if (root != null)
                {
                    // Schedule a callback on the EDT to let the BalloonController finish loading the document.
                    final KMLRoot pinnedRoot = root; // Final ref that can be accessed by anonymous class
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            BalloonController.this.onDocumentLoaded(docUrl, pinnedRoot, featureRef);
                        }
                    });

                    this.cancel();
                }
            }
            catch (final Exception e)
            {
                // Schedule a callback on the EDT to report the error to the BalloonController
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        BalloonController.this.onDocumentFailed(docUrl, e);
                    }
                });
                this.cancel();
            }
        }
    }
}

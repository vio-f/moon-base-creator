/* Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.util.webview;

import gov.nasa.worldwind.avlist.AVList;

/**
 * WebNavigationPolicy provides an interface for defining a web browser's navigation policy. The browser calls {@link
 * #decidePolicyForNavigation(gov.nasa.worldwind.avlist.AVList)} when a navigation action occurs. The implementation
 * returns whether to allow the navigation event or to ignore it.
 *
 * @author dcollins
 * @version $Id: WebNavigationPolicy.java 14261 2010-12-17 00:50:44Z dcollins $
 */
public interface WebNavigationPolicy
{
    /**
     * Defines the policy for how to process a browser navigation events. Returns {@link
     * gov.nasa.worldwind.avlist.AVKey#ALLOW} to let the browser process the navigation in the default manner, or {@link
     * gov.nasa.worldwind.avlist.AVKey#IGNORE} to suppress the browser's default behavior and ignore the navigation. The
     * parameter list describes the navigation event, and contains at least the following keys and their potential
     * values:
     * <p/>
     * <table> <tr><th>Key</th><th>Value</th></tr> <tr><td>{@link gov.nasa.worldwind.avlist.AVKey#MIME_TYPE}</td><td>A
     * string mime type describing the content type of the URL.</td></tr> <tr><td>{@link
     * gov.nasa.worldwind.avlist.AVKey#NAVIGATION_TYPE}</td><td>What initiated the navigation, either {@link
     * gov.nasa.worldwind.avlist.AVKey#NAVIGATION_TYPE_BROWSER_INITIATED} or {@link
     * gov.nasa.worldwind.avlist.AVKey#NAVIGATION_TYPE_LINK_ACTIVATED}.</td></tr> <tr><td>{@link
     * gov.nasa.worldwind.avlist.AVKey#TARGET}</td><td>The target browser, either {@link
     * gov.nasa.worldwind.avlist.AVKey#NAVIGATION_TARGET_CURRENT_BROWSER} or {@link
     * gov.nasa.worldwind.avlist.AVKey#NAVIGATION_TARGET_NEW_BROWSER}.</td></tr> <tr><td>{@link
     * gov.nasa.worldwind.avlist.AVKey#URL}</td><td>A URL formatted string the browser wants to navigate to.</td></tr>
     * </table>
     *
     * @param params parameter list describing the navigation event.
     *
     * @return {@link gov.nasa.worldwind.avlist.AVKey#ALLOW} if the browser should process the navigation in the default
     *         manner, or {@link gov.nasa.worldwind.avlist.AVKey#IGNORE} if the browser should suppress its default
     *         behavior and ignore the navigation attempt.
     */
    String decidePolicyForNavigation(AVList params);
}
/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.webview;

import com.sun.opengl.util.texture.Texture;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.logging.Level;

/**
 * {@link WebView} implementation for Windows. This implementation uses the Window's native web browser control and the
 * MSHTML library to render a web page and create an OpenGL texture from the web browser window.
 * <p/>
 * <a name="limits"><h2>Limits on the number of WebViews that can be created</h2></a> WindowsWebView creates a hidden
 * native window. Creating the native window can fail if the process runs out of Windows user object handles. Other GUI
 * elements in an application also consume these handles, so it is difficult to put a firm limit on how many WebViews
 * can be created. An application that creates only WebViews and no other windows can create about 1500 WebViews before
 * running out of handles. See <a href="http://msdn.microsoft.com/en-us/library/ms725486%28v=vs.85%29.aspx">MSDN</a> for
 * more information on User Objects and operating system limits.
 *
 * @author pabercrombie
 * @version $Id: WindowsWebView.java 14746 2011-02-17 06:48:06Z pabercrombie $
 */
public class WindowsWebView extends AbstractWebView
{
    /**
     * Default interval at which to check for changes in the rendered WebView content. This setting can be overridden in
     * a configuration file by setting a value for {@link AVKey#WEB_VIEW_REFRESH_INTERVAL}.
     */
    protected static final long DEFAULT_REFRESH_INTERVAL = 50;

    /** Lock to protect creation of the web view message loop thread. */
    protected static final Object webViewUILock = new Object();
    /** Thread to run web view message loop. All web view instances share one message loop. */
    protected static Thread webViewUI;
    /** Identifier for the message loop in native code. */
    protected static long webViewMessageLoop;

    /** The address of the native WindowsWebView object. Initialized during construction. */
    protected long webViewWindowPtr;
    /** The address of the native NotificationAdapter object. Initialized during construction. */
    protected long observerPtr;

    /**
     * Create a new WebView.
     *
     * @param frameSize The size of the WebView rectangle.
     *
     * @throws UnsupportedOperationException if this class is instantiated on a non-Windows operating system.
     * @throws WWRuntimeException            if creating the native web browser window fails for any reason. For
     *                                       example, because the process has run out of User Object handles (see
     *                                       documentation <a href="#limits">above</a>).
     */
    public WindowsWebView(Dimension frameSize)
    {
        if (frameSize == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!Configuration.isWindowsOS())
        {
            String message = Logging.getMessage("NativeLib.UnsupportedOperatingSystem", "Windows WebView",
                System.getProperty("os.name"));
            Logging.logger().severe(message);
            throw new UnsupportedOperationException(message);
        }

        this.frameSize = frameSize;

        // Make sure that the message loop thread is running
        this.ensureMessageLoopRunning();

        // Create the web view
        this.webViewWindowPtr = WindowsWebViewJNI.newWebViewWindow(webViewMessageLoop);
        if (this.webViewWindowPtr == 0)
        {
            String message = Logging.getMessage("WebView.ExceptionCreatingWebView",
                Logging.getMessage("NativeLib.ErrorInNativeLib"));
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        long refreshInterval = Configuration.getLongValue(AVKey.WEB_VIEW_REFRESH_INTERVAL, DEFAULT_REFRESH_INTERVAL);
        this.setRefreshInterval(refreshInterval);

        WindowsWebViewJNI.setFrameSize(this.webViewWindowPtr, this.frameSize.width, this.frameSize.height);

        this.observerPtr = WindowsWebViewJNI.newNotificationAdapter(this);

        WindowsWebViewJNI.addWindowUpdateObserver(this.webViewWindowPtr, observerPtr);
        WindowsWebViewJNI.setWebNavigationPolicy(this.webViewWindowPtr, WindowsWebView.this);
    }

    /** {@inheritDoc} */
    public void dispose()
    {
        try
        {
            // Remove the notification adapter
            if (webViewWindowPtr != 0 && observerPtr != 0)
                WindowsWebViewJNI.removeWindowUpdateObserver(webViewWindowPtr, observerPtr);
            // Free the native WebView object associated with this Java WebView object.
            if (webViewWindowPtr != 0)
                WindowsWebViewJNI.releaseWebView(webViewWindowPtr);
            if (observerPtr != 0)
                WindowsWebViewJNI.releaseComObject(observerPtr);

            this.webViewWindowPtr = 0;
            this.observerPtr = 0;
        }
        catch (Exception e)
        {
            Logging.logger().log(Level.SEVERE, Logging.getMessage("generic.ExceptionAttemptingToDisposeRenderable"), e);
        }
    }

    /**
     * Ensure that the message loop thread is running. This method simply returns if the thread is already running. It
     * creates a new thread if the message thread is not running. This method does not return until the message loop is
     * initialized and ready for use.
     */
    protected void ensureMessageLoopRunning()
    {
        synchronized (webViewUILock)
        {
            if (webViewUI == null || !webViewUI.isAlive())
            {
                webViewMessageLoop = 0;

                // Create a new thread to run the web view message loop.
                webViewUI = new Thread("WebView UI")
                {
                    public void run()
                    {
                        try
                        {
                            // Create a message loop in native code. This call must return
                            // before any messages are sent to the WebView.
                            webViewMessageLoop = WindowsWebViewJNI.newMessageLoop();
                        }
                        catch (Throwable t)
                        {
                            webViewMessageLoop = -1;
                        }
                        finally
                        {
                            // Notify the outer thread that the message loop is ready or failed to start.
                            synchronized (webViewUILock)
                            {
                                webViewUILock.notify();
                            }
                        }

                        // Process messages in native code until the message loop
                        // is terminated.
                        WindowsWebViewJNI.runMessageLoop();
                    }
                };
                webViewUI.start();

                // Wait for the newly started thread to create the message loop. We cannot
                // safely use the WebView until the message loop has been initialized.
                while (webViewMessageLoop == 0)
                {
                    try
                    {
                        webViewUILock.wait(1000);
                    }
                    catch (InterruptedException ignored)
                    {
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void setHTMLString(final String string, URL baseUrl)
    {
        final String htmlString = string != null ? string : "";
        final String baseUrlString = baseUrl != null ? baseUrl.toString() : this.getDefaultBaseURL();

        if (this.webViewWindowPtr != 0)
            WindowsWebViewJNI.setHTMLString(this.webViewWindowPtr, htmlString, baseUrlString);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overridden to apply the frame size to the native WebView.
     */
    @Override
    public void setFrameSize(Dimension size)
    {
        super.setFrameSize(size);

        if (this.webViewWindowPtr != 0)
            WindowsWebViewJNI.setFrameSize(this.webViewWindowPtr, this.frameSize.width, this.frameSize.height);
    }

    /** {@inheritDoc} */
    public void sendEvent(InputEvent event)
    {
        if (event != null)
        {
            // Convert OpenGL coordinates to Windows.
            if (event instanceof MouseEvent)
                event = convertToWindows((MouseEvent) event);

            // Send the AWT InputEvent to the native WebView object
            if (this.webViewWindowPtr != 0)
                WindowsWebViewJNI.sendEvent(this.webViewWindowPtr, event);
        }

        this.lastEvent = event;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overridden to apply the active state to the native WebView.
     */
    @Override
    public void setActive(boolean active)
    {
        super.setActive(active);

        if (this.webViewWindowPtr != 0)
            WindowsWebViewJNI.setActive(this.webViewWindowPtr, active);
    }

    /**
     * Get the interval at which the WebView updates its GL texture. The rendered WebView content can change due to
     * animation, user input, or scripts in the content. The WebView will periodically check for changes in the rendered
     * page, and update the GL texture if necessary. The refresh interval determines how often the WebView checks for
     * changes.
     *
     * @return interval in milliseconds at which the WebView will periodically check for changes in its rendered
     *         content, and update the GL texture if necessary.
     */
    public long getRefreshInterval()
    {
        return WindowsWebViewJNI.getRefreshInterval(this.webViewWindowPtr);
    }

    /**
     * Set the interval at which the WebView updates the GL texture representation of its contents. The rendered WebView
     * content can change due to animation or user input. The WebView will periodically check for changes in the
     * rendered page, and update the GL texture if necessary. The refresh interval determines how often the WebView
     * checks for changes.
     * <p/>
     * For example, if the refresh interval is 50 milliseconds, the WebView checks for changes in its contents every 50
     * milliseconds. If the WebView is displaying a video or animation, the video will render to the WebView's GL
     * texture at 20 frames per second.
     *
     * @param interval interval in milliseconds at which the WebView will periodically check for changes in the rendered
     *                 content, and update the GL texture if necessary.
     */
    public void setRefreshInterval(long interval)
    {
        if (interval < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", interval);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        WindowsWebViewJNI.setRefreshInterval(this.webViewWindowPtr, interval);
    }

    /** {@inheritDoc} */
    public void goBack()
    {
        if (this.webViewWindowPtr != 0)
            WindowsWebViewJNI.goBack(this.webViewWindowPtr);
    }

    /** {@inheritDoc} */
    public void goForward()
    {
        if (this.webViewWindowPtr != 0)
            WindowsWebViewJNI.goForward(this.webViewWindowPtr);
    }

    /** {@inheritDoc} */
    public Iterable<AVList> getLinks()
    {
        return null; // TODO: implement
    }

    /**
     * Get the default base URL. This URL is used to resolve relative links if no base URL is provided. The default
     * behavior is to resolve links relative to the current working directory.
     *
     * @return The default base URL.
     */
    protected String getDefaultBaseURL()
    {
        // The Windows web browser resolves links against the current working directory when the base is "about:blank"
        return "about:blank";
    }

    /**
     * Converts the specified mouse event's screen point from WebView coordinates to Windows coordinates, and returns a
     * new event who's screen point is in Windows coordinates, with the origin at the upper left corner of the WebView
     * window.
     *
     * @param e The event to convert.
     *
     * @return A new mouse event in the Windows coordinate system.
     */
    protected MouseEvent convertToWindows(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();

        // Translate OpenGL screen coordinates to Windows by moving the Y origin from the lower left corner to
        // the upper left corner and flipping the direction of the Y axis.
        y = this.frameSize.height - y;

        if (e instanceof MouseWheelEvent)
        {
            return new MouseWheelEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), x, y,
                e.getClickCount(), e.isPopupTrigger(), ((MouseWheelEvent) e).getScrollType(),
                ((MouseWheelEvent) e).getScrollAmount(), ((MouseWheelEvent) e).getWheelRotation());
        }
        else
        {
            return new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), x, y,
                e.getClickCount(), e.isPopupTrigger(), e.getButton());
        }
    }

    //**********************************************************************//
    //********************  Texture Representation  ************************//
    //**********************************************************************//

    /** {@inheritDoc} */
    @Override
    protected WWTexture createTextureRepresentation(DrawContext dc)
    {
        BasicWWTexture texture = new WindowsWebViewTexture(this.getFrameSize(), false);
        texture.setUseAnisotropy(false); // Do not use anisotropic texture filtering.

        return texture;
    }

    protected class WindowsWebViewTexture extends WebViewTexture
    {
        protected long updateTime = -1;

        public WindowsWebViewTexture(Dimension frameSize, boolean useMipMaps)
        {
            super(frameSize, useMipMaps, true);
        }

        /**
         * Update the texture if the native WebView window has changed.
         *
         * @param dc Draw context
         */
        @Override
        protected void updateIfNeeded(DrawContext dc)
        {
            // Return immediately if the native WebViewWindow object isn't initialized, and wait to update until the
            // native object is initialized. This method is called after the texture is bound, so we'll get another
            // chance to update as long as the WebView generates repaint events when it changes.
            long webViewWindowPtr = WindowsWebView.this.webViewWindowPtr;
            if (webViewWindowPtr == 0)
                return;

            // Return immediately if the texture isn't in the texture cache, and wait to update until the texture is
            // initialized and placed in the cache. This method is called after the texture is bound, so we'll get
            // another chance to update as long as the WebView generates repaint events when it changes.
            Texture texture = this.getTextureFromCache(dc);
            if (texture == null)
                return;

            // Load the WebViewWindow's current display pixels into the currently bound OGL texture if our update time
            // is different than the WebViewWindow's update time.
            if (WindowsWebViewJNI.getUpdateTime(webViewWindowPtr) != this.updateTime)
            {
                WindowsWebViewJNI.loadDisplayInGLTexture(webViewWindowPtr, texture.getTarget());
                this.updateTime = WindowsWebViewJNI.getUpdateTime(webViewWindowPtr);
            }
        }
    }
}

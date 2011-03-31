/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.util.webview;

import com.sun.opengl.util.texture.Texture;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

/**
 * @author dcollins
 * @version $Id: MacWebView.java 14561 2011-01-26 01:56:37Z dcollins $
 */
public class MacWebView extends AbstractWebView
{
    /** The address of the native WebViewWindow object. Initialized during construction. */
    protected AtomicLong webViewWindowPtr = new AtomicLong(0);
    /** The address of the native NotificationAdapter object. Initialized during construction. */
    protected AtomicLong observerPtr = new AtomicLong(0);

    // TODO: throw an exception if wrong os
    // TODO: catch and handle native exceptions

    public MacWebView(Dimension frameSize)
    {
        if (frameSize == null)
        {
            String message = Logging.getMessage("nullValue.SizeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!Configuration.isMacOS())
        {
            String message = Logging.getMessage("NativeLib.UnsupportedOperatingSystem", "Mac WebView",
                System.getProperty("os.name"));
            Logging.logger().severe(message);
            throw new UnsupportedOperationException(message);
        }

        this.frameSize = frameSize;

        // Copy the width and height to ensure the values don't change when accessed from the AppKit thread.
        final int frameWidth = this.frameSize.width;
        final int frameHeight = this.frameSize.height;

        MacWebViewJNI.invokeInAppKitThread(new Runnable()
        {
            public void run()
            {
                webViewWindowPtr.set(MacWebViewJNI.newWebViewWindow(frameWidth, frameHeight));
                observerPtr.set(MacWebViewJNI.newNotificationAdapter(MacWebView.this));
                MacWebViewJNI.addWindowUpdateObserver(webViewWindowPtr.get(), observerPtr.get());
                MacWebViewJNI.setWebNavigationPolicy(webViewWindowPtr.get(), MacWebView.this);
            }
        });
    }

    public void dispose()
    {
        // Free the native WebView object associated with this Java WebView object.
        MacWebViewJNI.invokeInAppKitThread(new Runnable()
        {
            public void run()
            {
                try
                {
                    if (webViewWindowPtr.get() != 0 && observerPtr.get() != 0)
                        MacWebViewJNI.removeWindowUpdateObserver(webViewWindowPtr.get(), observerPtr.get());
                    if (webViewWindowPtr.get() != 0)
                        MacWebViewJNI.releaseNSObject(webViewWindowPtr.get());
                    if (observerPtr.get() != 0)
                        MacWebViewJNI.releaseNSObject(observerPtr.get());

                    webViewWindowPtr.set(0);
                    observerPtr.set(0);
                }
                catch (Exception e)
                {
                    Logging.logger().log(Level.SEVERE,
                        Logging.getMessage("generic.ExceptionAttemptingToDisposeRenderable"), e);
                }
            }
        });
    }

    /** {@inheritDoc} */
    public void setHTMLString(final String string, URL baseUrl)
    {
        final String htmlString = string != null ? string : "";
        final String baseUrlString = baseUrl != null ? baseUrl.toString() : this.getDefaultBaseURL().toString();

        MacWebViewJNI.invokeInAppKitThread(new Runnable()
        {
            public void run()
            {
                if (webViewWindowPtr.get() != 0)
                    MacWebViewJNI.setHTMLString(webViewWindowPtr.get(), htmlString, baseUrlString);
            }
        });
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

        // Copy the width and height to ensure the values don't change when accessed from the AppKit thread.
        final int width = size.width;
        final int height = size.height;

        MacWebViewJNI.invokeInAppKitThread(new Runnable()
        {
            public void run()
            {
                if (webViewWindowPtr.get() != 0)
                    MacWebViewJNI.setFrameSize(webViewWindowPtr.get(), width, height);
            }
        });
    }

    /** {@inheritDoc} */
    public Iterable<AVList> getLinks()
    {
        return Collections.emptyList(); // TODO: implement
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
        // TODO
        return 0L;
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
        // TODO
    }

    /** {@inheritDoc} */
    public void sendEvent(final InputEvent event)
    {
        if (event != null)
        {
            // Send the AWT InputEvent to the native WebView object on the AppKit thread.
            MacWebViewJNI.invokeInAppKitThread(new Runnable()
            {
                public void run()
                {
                    if (webViewWindowPtr.get() != 0)
                        MacWebViewJNI.sendEvent(webViewWindowPtr.get(), event);
                }
            });
        }

        this.lastEvent = event;
    }

    /** {@inheritDoc} */
    public void goBack()
    {
        MacWebViewJNI.invokeInAppKitThread(new Runnable()
        {
            public void run()
            {
                if (webViewWindowPtr.get() != 0)
                    MacWebViewJNI.goBack(webViewWindowPtr.get());
            }
        });
    }

    /** {@inheritDoc} */
    public void goForward()
    {
        MacWebViewJNI.invokeInAppKitThread(new Runnable()
        {
            public void run()
            {
                if (webViewWindowPtr.get() != 0)
                    MacWebViewJNI.goForward(webViewWindowPtr.get());
            }
        });
    }

    protected URL getDefaultBaseURL()
    {
        File file = new File(Configuration.getCurrentWorkingDirectory());

        try
        {
            return file.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            return null; // TODO: determine if WebKit requires a base url.
        }
    }

    //**********************************************************************//
    //********************  Texture Representation  ************************//
    //**********************************************************************//

    @Override
    protected WWTexture createTextureRepresentation(DrawContext dc)
    {
        BasicWWTexture texture = new MacWebViewTexture(this.getFrameSize(), false);
        texture.setUseAnisotropy(false); // Do not use anisotropic texture filtering.

        return texture;
    }

    protected class MacWebViewTexture extends WebViewTexture
    {
        protected long updateTime = -1;

        public MacWebViewTexture(Dimension frameSize, boolean useMipMaps)
        {
            super(frameSize, useMipMaps, true);
        }

        @Override
        protected void updateIfNeeded(DrawContext dc)
        {
            // Return immediately if the native WebViewWindow object isn't initialized, and wait to update until the
            // native object is initialized. This method is called after the texture is bound, so we'll get another
            // chance to update as long as the WebView generates repaint events when it changes.
            long webViewWindowPtr = MacWebView.this.webViewWindowPtr.get();
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
            if (MacWebViewJNI.getUpdateTime(webViewWindowPtr) != this.updateTime)
            {
                MacWebViewJNI.loadDisplayInGLTexture(webViewWindowPtr, texture.getTarget());
                this.updateTime = MacWebViewJNI.getUpdateTime(webViewWindowPtr);
            }
        }
    }
}

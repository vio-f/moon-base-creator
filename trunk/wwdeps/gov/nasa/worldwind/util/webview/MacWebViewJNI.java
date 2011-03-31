/*
Copyright (C) 2001, 2010 United States Government as represented by 
the Administrator of the National Aeronautics and Space Administration. 
All Rights Reserved. 
*/
package gov.nasa.worldwind.util.webview;

import java.awt.event.*;
import java.beans.PropertyChangeListener;

/**
 * @author dcollins
 * @version $Id: MacWebViewJNI.java 14268 2010-12-17 20:50:12Z dcollins $
 */
public class MacWebViewJNI
{
    static
    {
        System.loadLibrary("webview");
    }

    public static native long newWebViewWindow(int frameWidth, int frameHeight);

    public static native long newNotificationAdapter(PropertyChangeListener listener);

    public static native void releaseNSObject(long nsObjectPtr);

    public static native void setHTMLString(long webViewWindowPtr, String htmlString, String baseUrlString);

    public static native void setFrameSize(long webViewWindowPtr, int width, int height);

    public static native void setWebNavigationPolicy(long webViewWindowPtr, WebNavigationPolicy policy);

    public static native void loadDisplayInGLTexture(long webViewWindowPtr, int target);

    public static native long getUpdateTime(long webViewWindowPtr);

    public static native void sendEvent(long webViewWindowPtr, InputEvent event);

    public static native void addWindowUpdateObserver(long webViewWindowPtr, long observerPtr);

    public static native void removeWindowUpdateObserver(long webViewWindowPtr, long observerPtr);

    public static native void goBack(long webViewWindowPtr);

    public static native void goForward(long webViewWindowPtr);

    public static native void invokeInAppKitThread(Runnable runnable);
}

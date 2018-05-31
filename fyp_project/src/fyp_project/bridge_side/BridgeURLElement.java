package fyp_project.bridge_side;

import soot.SootMethod;

import java.net.MalformedURLException;
import java.net.URL;

public class BridgeURLElement extends BridgeWebView {
    public static int url_count = 0;
    public final BridgeElement element;
    public final String url;
    public final int url_id;

    /**
     * Create a new bridgeURLElement associated a specific element with a URL
     *
     * @param element
     * @param url
     */
    public BridgeURLElement(BridgeElement element, String url) {
        this.element = element;
        URL availableURL;
        try {
            availableURL = new URL(url);
        } catch (MalformedURLException e) {
            System.out.println("-> Malformed url: " + url);
            availableURL = null;
        }
        if (availableURL != null) {
            this.url = availableURL.toString();
        } else {
            this.url = "-> Unknown:" + url;
        }
        this.url_id = url_count + 1;
    }

    /**
     * Function print bridgeURLElement to string
     *
     * @return
     */
    public String toString() {
        return String.format("-> BridgeManager URL Element : [bridgePath](METHOD)%s --> (URL) %s",
                this.element.getInvokedMethod(), this.url);
    }

    /**
     * Function to get bridgeURLElement's invokedMethod
     *
     * @return SootMethod
     */
    public SootMethod getInvokedMethod() {
        return this.element.getInvokedMethod();
    }

    /**
     * Function to retrieve the bridgeURLElement's url
     *
     * @return
     */
    public String getUrl() {
        return this.url;
    }
}

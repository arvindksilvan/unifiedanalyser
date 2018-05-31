package fyp_project.bridge_side;

import fyp_project.Main;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.HashSet;

public class BridgeWebView {
    public static ArrayList<BridgeElement> urlContexts = new ArrayList<>();
    private static BridgeWebView bridgeWebView;
    private static HashSet<BridgeURLElement> bridges = new HashSet<BridgeURLElement>();


    /**
     * Function to create a new bridgeWebView
     *
     * @return bridgeWebView;
     */
    public static BridgeWebView v() {
        if (bridgeWebView == null) {
            bridgeWebView = new BridgeWebView();
        }
        return bridgeWebView;
    }

    /**
     * Function to add a new bridge
     *
     * @param bridge
     */
    public static void addBridge(BridgeURLElement bridge) {
        bridges.add(bridge);
        SootMethod b_method = bridge.getInvokedMethod();
        String url = bridge.getUrl();
        add_JavaSinkAndSources(b_method);
        Main.possibleURLs.add(url);
    }

    /**
     * Function to add the bridge URL element into Java sinks and sources
     */
    public static void add_JavaSinkAndSources(SootMethod method) {
        String line = String.format("%s -> _SINK_", method.getSignature());
        Main.sources_sinks.add(line);
    }

    /**
     * Function to add a single fyp_project.bridge_side.BridgeManager Element to the HashSet
     *
     * @param bridgeElement
     */
    public void loadUrlElement(BridgeElement bridgeElement) {

        urlContexts.add(bridgeElement);
    }
}

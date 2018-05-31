package fyp_project.bridge_side;

import fyp_project.Main;
import fyp_project.bridge_side.BridgeElement;
import fyp_project.bridge_side.BridgeURLElement;
import fyp_project.bridge_side.BridgeWebView;
import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class Bridge_Manager {
    /**
     * Generate the bridge for the APK
     */
    public static void createAPKBridge() {
        //Iterate through the webview classes
        for (SootClass cls : Main.webviewClasses) {
            //Get iterate through each method
            for (SootMethod method : cls.getMethods()) {
                //Get all concrete classes (Concrete classes are classes that has implementation for all methods)
                if (!method.isConcrete()) {
                    continue;
                }
                Body b;
                //Get the method's active body
                if (method.hasActiveBody()) {
                    b = method.getActiveBody();
                } else {
                    b = method.retrieveActiveBody();
                }
                int UnitId = 0;
                //Iterate through each Unit from the method
                for (Unit u : b.getUnits()) {
                    Stmt statement = (Stmt) u;
                    //Check if the statement contains any invoked expression
                    if (statement.containsInvokeExpr()) {
                        //Create a new BridgeElement
                        try {
                            BridgeElement be = new BridgeElement(method, u, UnitId);
                            //Get invoked expression
                            InvokeExpr expr = statement.getInvokeExpr();
                            SootMethod target_method = expr.getMethod();
                            //Check if the target method is similiar to the loadUrlMethod
                            if (Main.isSimilarMethod(target_method, Main.loadUrlMethod) || Main.isSimilarMethod(target_method, Main.cordova_loadUrlMethod) || Main.isSimilarMethod(target_method, Main.cordova_ActivityloadUrlMethod)) {
                                //Add the method to the BridgeWebView
                                BridgeWebView.v().loadUrlElement(be);
                                //Get the URL value [usually might be unrelevant if non-cordova application]
                                //Check if it is a CordovaActivity // Need change this for dynamic scaling
                                String url_String = null;
                                if (target_method.getDeclaringClass().toString().equals(Main.cordova_activityClass)) {
                                    url_String = "file:///android_asset/www/index.html";
                                    url_String = Main.trimQuotation(url_String);
                                }
                                //Add it to the BridgeWebView if and only if URL is not null
                                if (url_String != null) {
                                    BridgeURLElement item = new BridgeURLElement(be, url_String);
                                    System.out.println(item.toString());
                                    BridgeWebView.addBridge(item);
                                }
                            }
                        } catch (Exception e) {
                            //
                        }
                        UnitId++;
                    }
                }
            }
        }
        System.out.println("-> Bridge_Manager Completed");
        System.out.println("");
    }
}


package fyp_project;

import com.ibm.wala.ssa.IR;
import fyp_project.bridge_side.Bridge_Manager;
import fyp_project.bridge_side.BridgePluginObject;
import fyp_project.htmljs_side.HTMLJSManager;
import fyp_project.htmljs_side.HTMLJSObject;
import fyp_project.java_side.Instrument_Manager;
import fyp_project.java_side.Taint_Manager;
import org.apache.commons.io.FileUtils;
import org.xmlpull.v1.XmlPullParserException;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    //get the app
    public static String PRE_APP = "hybridcalculator";
    public static String APP = "tests/" + PRE_APP;
    public static String FULLAPP = APP + ".apk";
    public static String SPLIT_APP = null;
    //get the directory strings
    public static String working_dir = "./output";
    public static String java_dir = working_dir + "/java";
    public static String java_dir_taint_results = java_dir + "/Flowdroid_Results.txt";
    public static String java_dir_graph_dot_file = java_dir + "/graph.txt";
    public static String java_dir_graph_dot_image_file = java_dir;
    public static String html_dir = working_dir + "/htmljs";
    public static String html_assets_dir = html_dir + "/assets";
    public static String possible_plugins_file = html_assets_dir + "/www/PossiblePlugins.txt";
    public static String index_html_file = html_assets_dir + "/www/modified_index/index.html";
    public static String js_file_path = html_assets_dir + "/www/";
    public static String plugin_js_file_path = js_file_path + "/";


    //Original application classes
    public static ArrayList<SootClass> originalApplicationclasses = new ArrayList<SootClass>();

    //Webview classes & methods
    public static Set<SootClass> webviewClasses = new HashSet<SootClass>();
    public static HashSet<SootClass> notWebviewClasses = new HashSet<SootClass>();
    public static SootMethod loadUrlMethod;
    public static SootMethod cordova_loadUrlMethod;
    public static SootMethod cordova_loadUrlMethodDefault;
    public static SootMethod cordova_ActivityloadUrlMethod;
    public static String cordova_activityClass;

    //Contain sources and sinks / urls
    public static HashSet<String> sources_sinks = new HashSet<String>();
    public static HashSet<String> possibleURLs = new HashSet<String>();

    //Contain plugins used
    public static ArrayList<BridgePluginObject> plugins_list = new ArrayList<BridgePluginObject>();

    //Contain which js files are referenced in the HTML index file
    public static ArrayList<String> jsfiles_list = new ArrayList<String>();

    //Contain each js file and and an arraylist of its ir functions
    public static HashMap<String, ArrayList<IR>> js_ir_maplist = new HashMap<String, ArrayList<IR>>();

    //Contain each js file and an HTMLJSObject of it
    public static HashMap<String, HTMLJSObject> js_methods_hashmap = new HashMap<String, HTMLJSObject>();

    //Contain the merged unfiltered results
    public static ArrayList<String> merged_uf_results = new ArrayList<String>();

    //Contain the merged filtered results
    public static ArrayList<String> merged_f_results = new ArrayList<String>();

    //Contain the split filtered results
    public static ArrayList<ArrayList<String>> split_f_results = new ArrayList<ArrayList<String>>();
    //Start and end timings
    static long start;
    static long end;

    //Memory calculation
    static long beforeUsedMem;
    static long afterUsedMem;
    static long actualMemUsed;

    public static void main(String[] args) throws IOException, XmlPullParserException {
        //parse in the APK file
        //break the APK file into its appropriate sources
        start = System.currentTimeMillis();
        beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        debugAPKPath();
        createFileDirectories();
        getHTMLAssets("./app_stuff/" + FULLAPP, html_dir);
        checkWebViewClassesAPK("./app_stuff/" + FULLAPP, java_dir);
        Bridge_Manager.createAPKBridge();
        getJavaAssets();
        HTMLJSManager.checkCordovaPlugins();
        HTMLJSManager.computeCordovaPluginsEvent();
        HTMLJSManager.analyzeURLs();
        HTMLJSManager.analyzeJSfiles();         //analyseJSFiles->createJSCallgraph(index.js is done here)
        HTMLJSManager.computeJSMethods();
        HTMLJSManager.computeIndexFile();
        HTMLJSManager.computeJS_ControlFlow();
        HTMLJSManager.mapJS_ControlFlow();
        Instrument_Manager.generateFrameworkSummary(); //Added
        Instrument_Manager.run_Instrumentation();
        Taint_Manager.runJavaTaint();
        try {
            Taint_Manager.computeResults(); //Added
            Taint_Manager.processResults();
        }catch (Exception e){
        }
        debugStats();

    }

    private static void debugStats() {
        //Name of app
        System.out.println("App name:" + Main.PRE_APP);
        //Get the number of Java Classes
        System.out.println("Java classes:" + Main.originalApplicationclasses.size());
        //Get the number of Java Methods
        int method_count = 0;
        for (SootClass c : originalApplicationclasses) {
            method_count = method_count + c.getMethods().size();
        }
        System.out.println("Java methods:" + method_count);
        //Get the number of Plugins
        System.out.println("Number of plugins:" + Main.plugins_list.size());
        end = System.currentTimeMillis();
        long total_time = end - start;
        System.out.println("Time:" + total_time + "ms");
        afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        actualMemUsed = afterUsedMem - beforeUsedMem;
        System.out.println("Memory Used:" + actualMemUsed);
    }

    /**
     * Split the APK file if found in tests folder
     */
    private static void debugAPKPath() {
        String path = APP;
        String paths[] = path.split("/");
        String new_path;
        try {
            new_path = paths[1];
        } catch (Exception e) {
            new_path = APP;
        }
        SPLIT_APP = new_path + ".apk";
    }

    /**
     * Create the file directories for java and htmljs side
     */
    private static void createFileDirectories() {
        File working_directory = new File(working_dir);
        if (working_directory.exists()) {
            try {
                FileUtils.cleanDirectory(working_directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //else make the directory (working_dir)
        working_directory.mkdir();

        File java_directory = new File(java_dir);
        if (java_directory.exists()) {
            try {
                FileUtils.cleanDirectory(java_directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // make the directory (java_dir)
        java_directory.mkdir();

        File html_directory = new File(html_dir);
        // make the directory (html_dir)
        if (html_directory.exists()) {
            try {
                FileUtils.cleanDirectory(html_directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        html_directory.mkdir();
        System.out.println("-> Folders ./java & ./htmljs successfully created");
    }

    /**
     * Retrieve the HTML assets of the APK file
     *
     * @param apkPath  the source path of the APK
     * @param destPath the destination path of the HTML files
     */
    private static void getHTMLAssets(String apkPath, String destPath) throws IOException {
        ZipFile zipApk = new ZipFile(apkPath);
        Enumeration<? extends ZipEntry> entries = zipApk.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.startsWith("assets") && (entryName.endsWith(".html") || entryName.endsWith(".js"))) {
                String tgtPath = String.format("%s/%s", destPath, entryName);
                InputStream in = zipApk.getInputStream(entry);
                File tgtFile = new File(tgtPath);
                tgtFile.getParentFile().mkdirs();
                Files.copy(in, tgtFile.toPath());
            }
        }
    }


    /**
     * Retrieve an instrumented version of the APK file
     *
     * @param apkPath  the source path of the APK
     * @param destPath the destination path of the instrumented APK
     */
    private static void checkWebViewClassesAPK(String apkPath, String destPath) {
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(false);
        Options.v().set_whole_program(true);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_output_dir(destPath);
        Options.v().set_debug(true);
        Options.v().set_validate(true);
        Options.v().set_android_api_version(23);
        Options.v().set_output_format(Options.output_format_none);
        //Use Soot to break down into Java

        List<String> process_dirs = new ArrayList<>();
        process_dirs.add(apkPath);
        Options.v().set_process_dir(process_dirs);
        Options.v().set_android_jars("C:\\Tools\\AndroidSDK\\platforms");

        try {
            Scene.v().loadNecessaryClasses();
        } catch (Exception e) {
            //
        }
        checkAPKWebViewClasses();
    }

    /**
     * Iterate through the classes of the APK file and check whether they contain anything related to WebView / CordodvaWebViewImpl
     * Checks whether the APK is a Hybrid application [terminate if not Hybrid Application]
     */
    private static void checkAPKWebViewClasses() {
        //Iterate through Soot find all the classes
        ArrayList<SootMethod> applicationEntryPoints = new ArrayList<SootMethod>();
        for (SootClass classes : Scene.v().getApplicationClasses()) {
            originalApplicationclasses.add(classes);
            //Find classes that have implementation (Abstract classes are classes that do not have implementation)
            if (classes.isAbstract()) {
                continue;
            }
            //Iterate through each normal class's methods
            for (SootMethod method : classes.getMethods()) {
                applicationEntryPoints.add(method);
            }
        }

        String webViewClass = "android.webkit.WebView";
        String webChromeClientClass = "android.webkit.WebChromeClient";
        String webViewClientClass = "android.webkit.WebViewClient";
        String cordova_webViewImplClass = "org.apache.cordova.CordovaWebViewImpl";
        String cordova_webViewDefaultClass = "org.apache.cordova.CordovaWebView";
        cordova_activityClass = "org.apache.cordova.CordovaActivity";
        String cordova_configClass = "org.apache.cordova.Config";
        String loadUrlSig =
                "<android.webkit.WebView: void loadUrl(java.lang.String)>";
        String addJavascriptInterfaceSig =
                "<android.webkit.WebView: void addJavascriptInterface(java.lang.Object,java.lang.String)>";
        String setWebViewClientSig =
                "<android.webkit.WebView: void setWebViewClient(android.webkit.WebViewClient)>";
        String setWebChromeClientSig =
                "<android.webkit.WebView: void setWebChromeClient(android.webkit.WebChromeClient)>";
        String cordova_loadUrlSig = "<org.apache.cordova.CordovaWebViewImpl: void loadUrl(java.lang.String)>";
        String cordova_loadUrlDefaultSig = "<org.apache.cordova.CordovaWebView: void loadUrl(java.lang.String)>";
        String cordova_ActivityloadUrlSig = "<org.apache.cordova.CordovaActivity: void loadUrl(java.lang.String)>";
        SootClass WebViewClass = null;
        SootClass WebChromeClientClass = null;
        SootClass WebViewClientClass = null;
        SootClass cordova_WebViewImplClass = null;
        SootClass cordova_ActivityClass = null;
        SootClass cordova_WebViewDefault = null;
        SootClass cordova_ConfigClass = null;
        SootMethod addJavascriptInterfaceMethod = null;
        SootMethod WebViewClientMethod = null;
        SootMethod setWebChromeClientMethod = null;
        SootMethod cordova_Config_getUrl = null;
        // Now we do a try and catch to find out which Classes are available
        try {
            WebViewClass = Scene.v().getSootClass(webViewClass);
        } catch (Exception e) {
            System.out.println("Can not find WebView class");
        }
        try {
            WebChromeClientClass = Scene.v().getSootClass(webChromeClientClass);
        } catch (Exception e) {
            System.out.println("Can not find WebChromeClient class");
        }
        try {
            WebViewClientClass = Scene.v().getSootClass(webViewClientClass);
        } catch (Exception e) {
            System.out.println("Can not find WebViewClient class");
        }
        try {
            cordova_WebViewDefault = Scene.v().getSootClass(cordova_webViewDefaultClass);
        } catch (Exception e) {
            System.out.println("Can not find CordovaWebView class");
        }
        try {
            cordova_WebViewImplClass = Scene.v().getSootClass(cordova_webViewImplClass);
        } catch (Exception e) {
            System.out.println("Can not find CordovaWebViewImpl class");
        }
        try {
            cordova_ActivityClass = Scene.v().getSootClass(cordova_activityClass);
        } catch (Exception e) {
            System.out.println("Can not find CordovaActivity class");
        }
        try {
            cordova_ConfigClass = Scene.v().getSootClass(cordova_configClass);
        } catch (Exception e) {
            System.out.println("Can not find CordovaConfig class");
        }
        //We will now proceed with finding which Methods are available
        try {
            loadUrlMethod = Scene.v().getMethod(loadUrlSig);
        } catch (Exception e) {
            System.out.println("Can not find loadUrl method");
        }
        try {
            addJavascriptInterfaceMethod = Scene.v().getMethod(addJavascriptInterfaceSig);
        } catch (Exception e) {
            System.out.println("Can not find addJavascriptInterface method");
        }
        try {
            WebViewClientMethod = Scene.v().getMethod(setWebViewClientSig);
        } catch (Exception e) {
            System.out.println("Can not find setWebViewClient method");
        }
        try {
            setWebChromeClientMethod = Scene.v().getMethod(setWebChromeClientSig);
        } catch (Exception e) {
            System.out.println("Can not find setWebChromeClient method");
        }
        try {
            cordova_loadUrlMethod = Scene.v().getMethod(cordova_loadUrlSig);
        } catch (Exception e) {
            System.out.println("Can not find Cordova_loadUrl method");
        }
        try {
            cordova_loadUrlMethodDefault = Scene.v().getMethod(cordova_loadUrlDefaultSig);
        } catch (Exception e) {
            System.out.println("Can not find Cordova_loadUrl method");
        }
        try {
            cordova_ActivityloadUrlMethod = Scene.v().getMethod(cordova_ActivityloadUrlSig);
        } catch (Exception e) {
            System.out.println("Can not find Cordova_ActivityloadUrl method");
        }
        //Filter all WebView related classes

        boolean flagged = false;
        //Iterate through all the classes
        for (SootClass cls : Scene.v().getApplicationClasses()) {
            if (isSimilarClass(cls, WebViewClientClass)) {
                webviewClasses.add(cls);
                continue;
            }
            if (isSimilarClass(cls, WebChromeClientClass)) {
                webviewClasses.add(cls);
                continue;
            }
            if (isSimilarClass(cls, cordova_WebViewImplClass)) {
                webviewClasses.add(cls);
                continue;
            }
            if (isSimilarClass(cls, cordova_WebViewDefault)) {
                webviewClasses.add(cls);
                continue;
            }
            //Iterate through methods in each class
            Iterator mi = cls.getMethods().iterator();
            while (mi.hasNext()) {
                SootMethod sm = (SootMethod) mi.next();
                if (sm.isConcrete()) {
                    //Get the body of each method
                    Body body;
                    try {
                        body = sm.retrieveActiveBody();
                    } catch (Exception ex) {
                        continue;
                    }
                    //Iterate through each body class
                    for (Unit unit : body.getUnits()) {
                        //Make each unit into a statement
                        Stmt statement = (Stmt) unit;
                        //Get all invoked expressions
                        if (statement.containsInvokeExpr()) {
                            InvokeExpr expr = statement.getInvokeExpr();
                            //Get the target method
                            try {
                                SootMethod method = expr.getMethod();
                                if (method.getDeclaringClass().equals(WebViewClass) || method.getDeclaringClass().equals(cordova_WebViewImplClass) || method.getDeclaringClass().equals(cordova_ActivityClass)) {
                                    //Set boolean flag if method is part of a class
                                    flagged = true;

                                }
                            }
                            catch (Exception e){
                                //Do nothing
                            }
                        }
                        //The moment it is true - > break the loop
                        if (flagged == true) {
                            break;
                        }


                    }
                    //If flag is true, add class to Set and break it
                    if (flagged == true) {
                        webviewClasses.add(cls);
                        break;
                    }
                }

            }
        }
        //All WebView based classes added from the APK
        System.out.println("-> Filtered out WebView Classes from APK");
        //Filter all non-WebView based classes
        for (SootClass cls : Scene.v().getApplicationClasses()) {
            if (webviewClasses.contains(cls)) {
                continue;
            }
            notWebviewClasses.add(cls);
        }
        //All non-WebView based classes added from the APK
        System.out.println("-> Filtered out non-WebView Classes from APK");
        //Get APK entry points and set them as the entry points
        Scene.v().setEntryPoints(applicationEntryPoints);
        System.out.println("-> Entry points set");
        //Check if APK is a Hybrid Application and terminate if not Hybrid Application
        boolean isHybridApp = false;
        if ((webviewClasses != null) && (webviewClasses.size() != 0)) {
            isHybridApp = true;
        }
        if (isHybridApp == true) {
            Main.webviewClasses = webviewClasses;
            System.out.println("-> APK is a Hybrid Application");
        } else {
            System.out.println("-> APK is not a Hybrid Application //terminating");
            System.exit(0);
        }
    }

    /**
     * Function to generate instrumented APK and Java side peripherals
     */
    public static void getJavaAssets() {
        PackManager.v().writeOutput();
        System.out.println("");
        readSourcesandSinksFile("./app_stuff/SourcesAndSinks.txt");
        writeSourcesandSinksFile();
        System.out.println("-> Generated Java assets successfully");
    }


    /**
     * Function to read in the existing sources and sinks
     *
     * @param path
     */
    private static void readSourcesandSinksFile(String path) {
        File sourceAndSinkFile = new File(path);
        try {
            List<String> lines = FileUtils.readLines(sourceAndSinkFile);
            for (String line : lines) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("#") || line.startsWith("%")) {
                    continue;
                }
                // Add the line to the hashset
                sources_sinks.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Function to write the newly updated sources and sinks
     */
    private static void writeSourcesandSinksFile() {
        File javaSourceAndSink = new File(java_dir + "/SourcesAndSinks.txt");
        try {
            FileUtils.writeLines(javaSourceAndSink, sources_sinks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether two Soot Classes are similiar
     *
     * @param c1
     * @param c2
     * @return boolean
     */
    public static boolean isSimilarClass(SootClass c1, SootClass c2) {
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1 == c2) {
            return true;
        }
        while (c1.hasSuperclass()) {
            c1 = c1.getSuperclass();
            if (c1 == c2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether two Soot Methods are similiar
     *
     * @param m1
     * @param m2
     * @return boolean
     */
    public static boolean isSimilarMethod(SootMethod m1, SootMethod m2) {
        if (m1 == null || m2 == null) {
            return false;
        }
        if (m1 == m2) {
            return true;
        }
        if ((m1.getSubSignature().equals(m2.getSubSignature())) && isSimilarClass(m1.getDeclaringClass(), m2.getDeclaringClass())) {
            return true;
        }
        return false;
    }

    /**
     * Function to trim the " " quotes from the url
     *
     * @param value
     * @return String
     */
    public static String trimQuotation(String value) {
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();

        while ((st < len) && (val[st] <= ' ' || val[st] == '"')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ' || val[len - 1] == '"')) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }
}



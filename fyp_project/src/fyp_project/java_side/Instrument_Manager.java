package fyp_project.java_side;

import fyp_project.Main;
import fyp_project.bridge_side.BridgePluginObject;
import fyp_project.htmljs_side.HTMLJSManager;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.Chain;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Instrument_Manager {
    //static String APK = Main.java_dir + "/hybridcalculator.apk";
   // static String BAPK = "./app_stuff/hybridcalculator.apk";
    static String LIB = "C:\\Tools\\AndroidSDK\\platforms";
    static String MAPK = fyp_project.Main.java_dir + "/" + fyp_project.Main.FULLAPP;
    static String MBAPK = "./app_stuff/" + fyp_project.Main.FULLAPP;

    /**
     * Function to modify the default apk into an instrumented APK
     */

    public static void run_Instrumentation() {
        System.out.println("-> Instrumenting input APK file");
        File APK_file = new File(MAPK);
        try {
            boolean result = Files.deleteIfExists(APK_file.toPath());
        } catch (Exception e) {
            //Do nothing
        }
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);
        Options.v().set_android_api_version(23);
        Options.v().set_output_dir(fyp_project.Main.java_dir);
        //Options.v().set_process_dir(Collections.singletonList(APK));
        Options.v().set_keep_line_number(true);
        Options.v().set_android_jars(LIB);
        Options.v().set_process_multiple_dex(true);
        List<String> process_dirs = new ArrayList<>();
        process_dirs.add(MBAPK);
        Options.v().set_process_dir(process_dirs);
        Scene.v().loadNecessaryClasses();
        //DO MODIFICATIONS HERE
        modify_device(); //org.apache.cordova.device.Device
        //createEnvironmentClass();
        //modify_mainClass(); //insert simulate method
        createintoOnCreateClass();
        PackManager.v().runPacks();
        PackManager.v().writeOutput();
    }

    /**
     * Function to modify the device class
     **/
    private static void modify_device() {
        String device_class_name = "org.apache.cordova.device.Device";
        String device_execute = "boolean execute(java.lang.String,org.json.JSONArray,org.apache.cordova.CallbackContext)";
        SootClass sClass;
        SootMethod method;
        //try and catch

        try {
            //Get the class
            sClass = Scene.v().getSootClass(device_class_name);

            //Get the method
            method = sClass.getMethod(device_execute);

            //Set the return type to JSONObject
            method.setReturnType(RefType.v("org.json.JSONObject"));

            //Get the body
            Body body = method.retrieveActiveBody();

            method.setActiveBody(body);
            Chain units = body.getUnits();
            Chain locals = body.getLocals();
            Iterator<Unit> u_itr = units.iterator();
            Iterator<Local> l_itr = locals.iterator();
            Unit remove_item0 = null;
            Unit remove_item1 = null;
            Unit remove_item2 = null;
            Unit test = null;
            while (u_itr.hasNext()) {
                Unit item = u_itr.next();
                if (item.toString().contains("virtualinvoke $r5.<java.lang.String: boolean equals(java.lang.Object)>($r1)")) {
                    remove_item0 = item;
                }
                if (item.toString().contains("goto return null")) {
                    remove_item1 = item;
                }
                if (item.toString().contains("return null")) {
                    remove_item2 = item;
                }
            }
            //Get the JSON Local
            Value json_Object = null;
            while (l_itr.hasNext()) {
                Local item = l_itr.next();
                if (item.getName().equals("$r4")) {
                    json_Object = item;
                }
            }
            //Remove the IF statement
            units.remove(remove_item0);
            units.remove(remove_item1);
            units.swapWith(remove_item2, Jimple.v().newReturnStmt(json_Object));
            sClass.setApplicationClass();
        } catch (Exception e) {
            //Do nothing here
        }
    }
    public static void createintoOnCreateClass() {
        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                final PatchingChain<Unit> units = b.getUnits();

                //important to use snapshotIterator here
                for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
                    final Unit u = iter.next();
                    u.apply(new AbstractStmtSwitch() {

                        public void caseInvokeStmt(InvokeStmt stmt) {
                            InvokeExpr invokeExpr = stmt.getInvokeExpr();
                            //Need make dynamic(automate)
                            List<Unit> chain = new ArrayList<>();
                            if (invokeExpr.toString().contains("loadUrl(java.lang.String)>") && !invokeExpr.toString().contains("org.apache.cordova") && !invokeExpr.toString().contains("android.webkit")) {
                                Body body = invokeExpr.getMethod().getActiveBody();
                                SootClass sClass = invokeExpr.getMethod().getDeclaringClass();


                                //STANDARD_LOCALS(JSON ARRAY)
                                Local jArray_standard = addJSONArray(body);
                                Unit t1 = Jimple.v().newAssignStmt(jArray_standard, Jimple.v().newNewExpr(RefType.v("org.json.JSONObject")));
                                Unit t2 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(jArray_standard, Scene.v().getMethod("<org.json.JSONObject: void <init>()>").makeRef()));
                                chain.add(t1);
                                chain.add(t2);

                                ArrayList<String[]> lines = HTMLJSManager.lines;
                                for (int i = 0; i < lines.size(); i++) {
                                    ArrayList<String> actions = null;
                                    String exec_command = null;
                                    String[] line = lines.get(i);
                                    String name = line[1];
                                    String source_o = line[2];
                                    //System.out.println(source_o);
                                    String type = null;
                                    if (line[3].equals("o")) {
                                        if (line[0].equals("Object")) {
                                            type = "java.lang.Object";
                                        } else {
                                            type = line[0];
                                            //Get the actions associated with the method
                                            for (int a = 0; a < fyp_project.Main.plugins_list.size(); a++) {
                                                String file_name = fyp_project.Main.plugins_list.get(a).getfile();
                                                String[] split = file_name.split("/");
                                                String actual_name = split[split.length - 1].replace(".js", "").trim();
                                                if (name.equals(actual_name)) {
                                                    //means they are the same thing
                                                    actions = fyp_project.Main.plugins_list.get(a).getActions();
                                                }
                                            }
                                        }
                                    } else {
                                        type = "Method";
                                    }
                                    //Check that type is not method
                                    if (!type.equals("Method")) {
                                        Local item = addItem(body, name, type);
                                        String method_sig = "<" + type + ": void <init>()>";
                                        Unit t4 = Jimple.v().newAssignStmt(item, Jimple.v().newNewExpr(RefType.v(type)));
                                        Unit t5 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(item, Scene.v().getMethod(method_sig).makeRef()));
                                        chain.add(t4);
                                        chain.add(t5);
                                        //Check for DEVICE CLASS
                                        if (name.equals("device")) {

                                            //CREATE A NEW JSON OBJECT
                                            Local jObject = addJSONObj(body);
                                            Unit t6 = Jimple.v().newAssignStmt(jObject, Jimple.v().newNewExpr(RefType.v("org.json.JSONObject")));
                                            chain.add(t6);

                                            //CREATE NEW VIRTUALINVOKE
                                            Value actual_action = (StringConstant.v("getDeviceInfo"));
                                            Value constant = (StringConstant.v(""));
                                            exec_command = "<" + "org.apache.cordova.device.Device" + ":" + " " + "org.json.JSONObject execute(java.lang.String,org.json.JSONArray,org.apache.cordova.CallbackContext)>";
                                            SootMethod toCall = Scene.v().getMethod(exec_command);
                                            VirtualInvokeExpr vexpr = Jimple.v().newVirtualInvokeExpr(item, toCall.makeRef(), actual_action, jArray_standard, constant);
                                            Unit t7 = Jimple.v().newAssignStmt(jObject, vexpr);
                                            chain.add(t7);
                                            //Add field
                                            SootField a = new SootField(item + "_jsonObject", RefType.v("org.json.JSONObject"), Modifier.PUBLIC | Modifier.STATIC);
                                            sClass.addField(a);
                                            Unit t8 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(a.makeRef()), jObject);
                                            chain.add(t8);
                                        }
                                    }

                                    //NEED IMPLEMENT FOR FUTURE NUMBERS ETC AS INPUT CAUSE 22:9344322 (THIS CASE DOES NOT STORE THE NUMBER)
                                    if (type.equals("Method")) {
                                        Local jArray_standard1 = null;
                                        ArrayList<String> args_class = new ArrayList<String>();
                                        String method_name = line[1];
                                        String before_args = line[2];
                                        before_args = before_args.replace("[", "");
                                        before_args = before_args.replace("]", "");
                                        String[] args = before_args.trim().split(",");
                                        //Iterate through each "element" in the array
                                        for (int x = 0; x < args.length; x++) {
                                            //Iterate through each "object" in lines
                                            String element_arg = args[x].trim();
                                            //split arg and value if : exists
                                            String input_arg = null;
                                            String input_value = null;
                                            if (element_arg.contains(":")) {
                                                String[] split = element_arg.split(":");
                                                input_arg = split[0].trim();
                                                input_value = split[1].trim();
                                            } else {
                                                input_arg = element_arg;
                                            }
                                            //Iterate through each object element
                                            ArrayList<String[]> compare_lines = HTMLJSManager.lines;
                                            for (int size = 0; size < compare_lines.size(); size++) {
                                                String[] item = compare_lines.get(size);
                                                String c_source = item[2].trim();
                                                //only for objects
                                                if (item[3].equals("o")) {
                                                    //Compare it
                                                    if (c_source.equals(input_arg)) {
                                                        args_class.add(item[1].trim());
                                                    }

                                                }
                                            }
                                        }
                                        //Iterate through all input arguments clases and use JArray to store the object(device->device_jsonObject)
                                        for (int l = 0; l < args_class.size(); l++) {
                                            String field_name = args_class.get(l).concat("_jsonObject").trim();
                                            String array_name = args_class.get(l).concat("_jArray").trim();
                                            //Get the specific field
                                            try {
                                                SootField field = sClass.getFieldByName(field_name);
                                                //CREATE A NEW JSON OBJECT
                                                Local jObject = addItem(body, field_name, "org.json.JSONObject");
                                                //Unit t6 = Jimple.v().newAssignStmt(jObject, Jimple.v().newNewExpr(RefType.v("org.json.JSONObject")));
                                                //units.add(t6);
                                                //ASSIGN THIS TO THE FIELD
                                                Unit t7 = Jimple.v().newAssignStmt(jObject, Jimple.v().newStaticFieldRef(field.makeRef()));
                                                chain.add(t7);
                                                //CREATE A NEW SPECIFIC ARRAY TO STORE THE FIELD
                                                jArray_standard1 = addItem(body, array_name, "org.json.JSONArray");
                                                Unit t8 = Jimple.v().newAssignStmt(jArray_standard1, Jimple.v().newNewExpr(RefType.v("org.json.JSONArray")));
                                                Unit t9 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(jArray_standard1, Scene.v().getMethod("<org.json.JSONArray: void <init>()>").makeRef()));
                                                chain.add(t8);
                                                chain.add(t9);
                                                //STORE THE FIELD INSIDE NEWJARRAY
                                                String invoke_method = "<org.json.JSONArray: org.json.JSONArray put(java.lang.Object)>";
                                                Unit t10 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(jArray_standard1, Scene.v().getMethod(invoke_method).makeRef(), jObject));
                                                chain.add(t10);
                                            } catch (Exception e) {

                                            }
                                        }
                                        //Iterate through the JS methods and find which are the same
                                        for (int k = 0; k < fyp_project.Main.plugins_list.size(); k++) {
                                            try {
                                                String path = fyp_project.Main.plugins_list.get(k).getfile();
                                                String[] split = path.split("/");
                                                String actual_path = split[split.length - 1].trim().replace(".js", "");
                                                //IF SAME
                                                if (method_name.equals(actual_path)) {
                                                    //GET THE ACTIONS
                                                    ArrayList<String> action_list = fyp_project.Main.plugins_list.get(k).getActions();
                                                    for (int size = 0; size < action_list.size(); size++) {
                                                        String individual_action = action_list.get(size);
                                                        Value actual_action = (StringConstant.v(individual_action));
                                                        Value constant = (StringConstant.v(""));
                                                        String cp = fyp_project.Main.plugins_list.get(k).getCpath();
                                                        exec_command = "<" + cp + ":" + " " + "boolean execute(java.lang.String,org.json.JSONArray,org.apache.cordova.CallbackContext)>";
                                                        SootMethod toCall = Scene.v().getMethod(exec_command);
                                                        Local getBaseLocal = retreiveLocal(body, method_name);
                                                        VirtualInvokeExpr vexpr = Jimple.v().newVirtualInvokeExpr(getBaseLocal, toCall.makeRef(), actual_action, jArray_standard1, constant);
                                                        Unit t11 = Jimple.v().newInvokeStmt(vexpr);
                                                        chain.add(t11);


                                                    }
                                                }
                                            } catch (Exception e) {
                                                //
                                            }
                                        }


                                    }
                                }
                                b.validate();
                                units.insertAfter(chain, u);
                            }
                        }
                    });
                }

            }


        }));
    }

    public static void createEnvironmentClass() {
        SootClass sClass;
        SootMethod method;
        // Declare 'public class Environment'
        sClass = new SootClass("scan.Environment", Modifier.PUBLIC);
        Scene.v().addBasicClass("java.lang.Object", SootClass.BODIES);
        Scene.v().addClass(sClass);

        // Create the method, public static void main(String[])
        method = new SootMethod("simulate", null,
                VoidType.v(), Modifier.PUBLIC);

        sClass.addMethod(method);

        JimpleBody body = Jimple.v().newBody(method);

        method.setActiveBody(body);
        Chain units = body.getUnits();

        //STANDARD_LOCALS(JSON ARRAY)
        Local jArray_standard = addJSONArray(body);
        Unit t1 = Jimple.v().newAssignStmt(jArray_standard, Jimple.v().newNewExpr(RefType.v("org.json.JSONObject")));
        Unit t2 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(jArray_standard, Scene.v().getMethod("<org.json.JSONObject: void <init>()>").makeRef()));
        units.add(t1);
        units.add(t2);

        ArrayList<String[]> lines = HTMLJSManager.lines;
        for (int i = 0; i < lines.size(); i++) {
            ArrayList<String> actions = null;
            String exec_command = null;
            String[] line = lines.get(i);
            String name = line[1];
            String source_o = line[2];
            //System.out.println(source_o);
            String type = null;
            if (line[3].equals("o")) {
                if (line[0].equals("Object")) {
                    type = "java.lang.Object";
                } else {
                    type = line[0];
                    //Get the actions associated with the method
                    for (int a = 0; a < fyp_project.Main.plugins_list.size(); a++) {
                        String file_name = fyp_project.Main.plugins_list.get(a).getfile();
                        String[] split = file_name.split("/");
                        String actual_name = split[split.length - 1].replace(".js", "").trim();
                        if (name.equals(actual_name)) {
                            //means they are the same thing
                            actions = fyp_project.Main.plugins_list.get(a).getActions();
                        }
                    }
                }
            } else {
                type = "Method";
            }
            //Check that type is not method
            if (!type.equals("Method")) {
                Local item = addItem(body, name, type);
                String method_sig = "<" + type + ": void <init>()>";
                Unit t4 = Jimple.v().newAssignStmt(item, Jimple.v().newNewExpr(RefType.v(type)));
                Unit t5 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(item, Scene.v().getMethod(method_sig).makeRef()));
                units.add(t4);
                units.add(t5);
                //Check for DEVICE CLASS
                if (name.equals("device")) {

                    //CREATE A NEW JSON OBJECT
                    Local jObject = addJSONObj(body);
                    Unit t6 = Jimple.v().newAssignStmt(jObject, Jimple.v().newNewExpr(RefType.v("org.json.JSONObject")));
                    units.add(t6);

                    //CREATE NEW VIRTUALINVOKE
                    Value actual_action = (StringConstant.v("getDeviceInfo"));
                    Value constant = (StringConstant.v(""));
                    exec_command = "<" + "org.apache.cordova.device.Device" + ":" + " " + "org.json.JSONObject execute(java.lang.String,org.json.JSONArray,org.apache.cordova.CallbackContext)>";
                    SootMethod toCall = Scene.v().getMethod(exec_command);
                    VirtualInvokeExpr vexpr = Jimple.v().newVirtualInvokeExpr(item, toCall.makeRef(), actual_action, jArray_standard, constant);
                    Unit t7 = Jimple.v().newAssignStmt(jObject, vexpr);
                    units.add(t7);
                    //Add field
                    SootField a = new SootField(item + "_jsonObject", RefType.v("org.json.JSONObject"), Modifier.PUBLIC | Modifier.STATIC);
                    sClass.addField(a);
                    Unit t8 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(a.makeRef()), jObject);
                    units.add(t8);
                }
            }

            //NEED IMPLEMENT FOR FUTURE NUMBERS ETC AS INPUT CAUSE 22:9344322 (THIS CASE DOES NOT STORE THE NUMBER)
            if (type.equals("Method")) {
                Local jArray_standard1 = null;
                ArrayList<String> args_class = new ArrayList<String>();
                String method_name = line[1];
                String before_args = line[2];
                before_args = before_args.replace("[", "");
                before_args = before_args.replace("]", "");
                String[] args = before_args.trim().split(",");
                //Iterate through each "element" in the array
                for (int x = 0; x < args.length; x++) {
                    //Iterate through each "object" in lines
                    String element_arg = args[x].trim();
                    //split arg and value if : exists
                    String input_arg = null;
                    String input_value = null;
                    if (element_arg.contains(":")) {
                        String[] split = element_arg.split(":");
                        input_arg = split[0].trim();
                        input_value = split[1].trim();
                    } else {
                        input_arg = element_arg;
                    }
                    //Iterate through each object element
                    ArrayList<String[]> compare_lines = HTMLJSManager.lines;
                    for (int size = 0; size < compare_lines.size(); size++) {
                        String[] item = compare_lines.get(size);
                        String c_source = item[2].trim();
                        //only for objects
                        if (item[3].equals("o")) {
                            //Compare it
                            if (c_source.equals(input_arg)) {
                                args_class.add(item[1].trim());
                            }

                        }
                    }
                }
                //Iterate through all input arguments clases and use JArray to store the object(device->device_jsonObject)
                for (int l = 0; l < args_class.size(); l++) {
                    String field_name = args_class.get(l).concat("_jsonObject").trim();
                    String array_name = args_class.get(l).concat("_jArray").trim();
                    //Get the specific field
                    SootField field = sClass.getFieldByName(field_name);
                    //CREATE A NEW JSON OBJECT
                    Local jObject = addItem(body, field_name, "org.json.JSONObject");
                    //Unit t6 = Jimple.v().newAssignStmt(jObject, Jimple.v().newNewExpr(RefType.v("org.json.JSONObject")));
                    //units.add(t6);
                    //ASSIGN THIS TO THE FIELD
                    Unit t7 = Jimple.v().newAssignStmt(jObject, Jimple.v().newStaticFieldRef(field.makeRef()));
                    units.add(t7);
                    //CREATE A NEW SPECIFIC ARRAY TO STORE THE FIELD
                    jArray_standard1 = addItem(body, array_name, "org.json.JSONArray");
                    Unit t8 = Jimple.v().newAssignStmt(jArray_standard1, Jimple.v().newNewExpr(RefType.v("org.json.JSONArray")));
                    Unit t9 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(jArray_standard1, Scene.v().getMethod("<org.json.JSONArray: void <init>()>").makeRef()));
                    units.add(t8);
                    units.add(t9);
                    //STORE THE FIELD INSIDE NEWJARRAY
                    String invoke_method = "<org.json.JSONArray: org.json.JSONArray put(java.lang.Object)>";
                    Unit t10 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(jArray_standard1, Scene.v().getMethod(invoke_method).makeRef(), jObject));
                    units.add(t10);
                }
                //Iterate through the JS methods and find which are the same
                for (int k = 0; k < fyp_project.Main.plugins_list.size(); k++) {
                    String path = fyp_project.Main.plugins_list.get(k).getfile();
                    String[] split = path.split("/");
                    String actual_path = split[split.length - 1].trim().replace(".js", "");
                    //IF SAME
                    if (method_name.equals(actual_path)) {
                        //GET THE ACTIONS
                        ArrayList<String> action_list = fyp_project.Main.plugins_list.get(k).getActions();
                        for (int size = 0; size < action_list.size(); size++) {
                            String individual_action = action_list.get(size);
                            Value actual_action = (StringConstant.v(individual_action));
                            Value constant = (StringConstant.v(""));
                            String cp = fyp_project.Main.plugins_list.get(k).getCpath();
                            exec_command = "<" + cp + ":" + " " + "boolean execute(java.lang.String,org.json.JSONArray,org.apache.cordova.CallbackContext)>";
                            SootMethod toCall = Scene.v().getMethod(exec_command);
                            Local getBaseLocal = retreiveLocal(body, method_name);
                            VirtualInvokeExpr vexpr = Jimple.v().newVirtualInvokeExpr(getBaseLocal, toCall.makeRef(), actual_action, jArray_standard1, constant);
                            Unit t11 = Jimple.v().newInvokeStmt(vexpr);
                            units.add(t11);


                        }
                    }
                }


            }
        }
        Unit t3 = Jimple.v().newReturnVoidStmt();
        units.add(t3);
        sClass.setApplicationClass();
    }

    /**
     * Get the main class
     */
    private static void modify_mainClass() {
        //Add the classes needed
        Scene.v().addBasicClass("scan.Environment", SootClass.SIGNATURES);

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                final PatchingChain<Unit> units = b.getUnits();

                //important to use snapshotIterator here
                for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
                    final Unit u = iter.next();
                    u.apply(new AbstractStmtSwitch() {

                        public void caseInvokeStmt(InvokeStmt stmt) {
                            InvokeExpr invokeExpr = stmt.getInvokeExpr();
                            //Need make dynamic(automate)
                            if (invokeExpr.toString().contains("loadUrl(java.lang.String)>") && !invokeExpr.toString().contains("org.apache.cordova") && !invokeExpr.toString().contains("android.webkit")) {
                                List<Unit> chain = new ArrayList<>();
                                Local item = addItem(b, "env", "scan.Environment");
                                String exec_command = "<scan.Environment: void simulate()>";
                                Unit t2 = Jimple.v().newAssignStmt(item, Jimple.v().newNewExpr(RefType.v("scan.Environment")));
                                Unit t3 = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(item, Scene.v().getMethod(exec_command).makeRef()));
                                chain.add(t2);
                                chain.add(t3);
                                units.insertAfter(chain, u);
                                b.validate();
                            }
                        }
                    });
                }

            }


        }));
    }

    /**
     * Generate all the possible methods from the all the possible classes
     */
    public static void generateFrameworkSummary() {
        ArrayList<String> contain_lines = readEasyTaintWrapperSource();
        String outputfile = fyp_project.Main.java_dir + "/EasyTaintWrapperSource.txt";
        BufferedWriter bw = null;
        FileWriter fw = null;
        for (int i = 0; i < fyp_project.Main.originalApplicationclasses.size(); i++) {
            List<SootMethod> methods = Main.originalApplicationclasses.get(i).getMethods();
            //Iterate through each method
            for (int x = 0; x < methods.size(); x++) {
                SootMethod method = methods.get(x);
                //Check if the method takes in any parameter
                if (!method.toString().contains("$")) {
                    if (method.getParameterCount() > 0) {
                        List<Type> parameter = method.getParameterTypes();
                        String paramater_type = parameter.toString();
                        paramater_type = paramater_type.replace("[", "(");
                        paramater_type = paramater_type.replace("]", ")");
                        String method_original = method.toString();
                        String combine = method_original + paramater_type;
                        contain_lines.add(combine);
                        //Write to the file
                    }
                }
            }
        }
        try {
            fw = new FileWriter(outputfile);
            bw = new BufferedWriter(fw);
            //Iterate through each line
            for (int i = 0; i < contain_lines.size(); i++) {
                String line = contain_lines.get(i).toString()+"\n";
                bw.write(line);
            }
        } catch (Exception e) {

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }

    private static Local addItem(Body body, String name, String type) {
        Local obj = Jimple.v().newLocal(name, RefType.v(type));
        body.getLocals().add(obj);
        return obj;
    }

    private static Local retreiveLocal(Body body, String name) {
        Iterator<Local> iterator = body.getLocals().iterator();
        Local obj = null;
        while (iterator.hasNext()) {
            Local item = iterator.next();
            if (item.getName().equals(name)) {
                obj = item;
            }
        }
        return obj;
    }

    private static Local addJSONArray(Body body) {
        Local jArray = Jimple.v().newLocal("jArray", RefType.v("org.json.JSONArray"));
        body.getLocals().add(jArray);
        return jArray;
    }

    private static Local addJSONObj(Body body) {
        Local jObject = Jimple.v().newLocal("jObject", RefType.v("org.json.JSONObject"));
        body.getLocals().add(jObject);
        return jObject;
    }

    private static Local addPluginBody(Body body, BridgePluginObject po) {
        String ref_type = po.getCpath().trim().toString();
        String name = po.getclobbers().replace(".", "");
        Local item = Jimple.v().newLocal(name, RefType.v(ref_type));
        body.getLocals().add(item);
        return item;
    }

    private static ArrayList<String> readEasyTaintWrapperSource() {
        ArrayList<String> contain_lines = new ArrayList<String>();
        String file = "./app_stuff/EasyTaintWrapperSource.txt";


        BufferedReader br = null;
        FileReader fr = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                contain_lines.add(sCurrentLine);
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return contain_lines;
    }
}


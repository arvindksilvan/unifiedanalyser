package fyp_project.htmljs_side;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil;
import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
import com.ibm.wala.cast.types.AstMethodReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.impl.Everywhere;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.*;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import fyp_project.Main;
import fyp_project.bridge_side.BridgePluginObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import soot.Scene;
import soot.SootClass;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLJSManager {
    public static ArrayList<String[]> lines = new ArrayList<>();

    /**
     * Function to check presence of whether cordova plugins are used by finding for
     * cordova_plugins.js file
     */
    public static void checkCordovaPlugins() {
        ArrayList<String> lines = new ArrayList<String>();
        String cordovajsfile = "/assets/www/cordova_plugins.js";
        File f = new File(Main.html_dir + cordovajsfile);
        if (f.exists() && !f.isDirectory()) {
            Scanner input = null;
            try {
                input = new Scanner(f);
            } catch (FileNotFoundException e) {
                System.out.println("-> <Cordova> cordova_plugins.js file not found");
            }
            //Read through all the lines
            while (input.hasNextLine()) {
                // (next line is equal to = module.exports
                String start_line = input.nextLine();
                if (start_line.equals("module.exports = [")) {
                    // Now we have found the first line, we continue to iterate through
                    lines.add("[");
                    while (input.hasNext()) {
                        //Now we find the end line
                        String end_line = input.nextLine();
                        // if (final line) is equal to = ]; -> break loop
                        if (end_line.equals("];")) {
                            lines.add("]");
                            break;
                        }
                        lines.add(end_line);
                    }
                }
            }
        }
        if (lines.size() != 0) {
            //Write a new file for future references
            File possible_plugins = new File(Main.possible_plugins_file);
            try {
                FileUtils.writeLines(possible_plugins, lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("-> Possible <Cordova> plugins found and written to text file");
            //Read the text file as a JSON object
            //Create a new JSON parser
            JSONParser json_parser = new JSONParser();
            //Parse in the file
            try {
                JSONArray json_array = (JSONArray) json_parser.parse(new FileReader(Main.possible_plugins_file));
                //Iterate through
                for (int i = 0; i < json_array.size(); i++) {
                    Object o = json_array.get(i);
                    JSONObject plugin = (JSONObject) o;
                    String id = plugin.get("id").toString();
                    String file = plugin.get("file").toString();
                    String pluginId = "";
                    try {
                        pluginId = plugin.get("pluginId").toString();
                    }
                    catch (Exception e){
                        //
                    }
                    JSONArray clobbers_array = (JSONArray) plugin.get("clobbers");
                    String clobbers = null;
                    try {
                        for (Object x : clobbers_array) {
                            clobbers = x.toString();
                        }
                    } catch (Exception e) {
                        //Do nothing
                    }
                    //Get the name of from the id (com.plugin.imei.IMEIPlugin -> IMEIPlugin)
                    String[] break_id = id.split("\\.");
                    String name = break_id[break_id.length - 1];
                    //Get the appropriate classes

                    ArrayList<SootClass> classes = new ArrayList<SootClass>();
                    for (SootClass cls : Scene.v().getApplicationClasses()) {
                        if (cls.toString().contains(name)) {
                            classes.add(cls);
                        }
                    }
                    ArrayList<String> action_list = new ArrayList<String>();
                    ArrayList<String> ml = new ArrayList<String>();
                    ArrayList<String> js_methods = new ArrayList<String>();
                    BridgePluginObject b_plugin = new BridgePluginObject(name, id, file, pluginId, clobbers, null, classes, action_list, ml, js_methods);
                    Main.plugins_list.add(b_plugin);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("-> No plugins found");
        }
        for (int i = 0; i < Main.plugins_list.size(); i++) {
            String before_name = Main.plugins_list.get(i).getid();
            String m_before_name = before_name.replace("-", ".");
            String[] split_name = m_before_name.split("\\.");
            String actual_path = null;
            String last_name = split_name[split_name.length - 1];
            //System.out.println(last_name + " FULL NAME:" + m_before_name);
            //Get class actual path / load Scene (first batch)
            if (Main.plugins_list.get(i).getCpath() == null) {
                for (SootClass classes : Scene.v().getApplicationClasses()) {
                    String class_name = classes.toString();
                    String final_path;
                    if (!class_name.contains("$")) {
                        String[] split_classname = class_name.split("\\.");
                        if ((split_classname[split_classname.length - 1].equals(last_name))) {
                            final_path = class_name;
                            Main.plugins_list.get(i).addCpath(final_path);
                        }
                    }
                }
            }
            //Add second batch
            if (Main.plugins_list.get(i).getCpath() == null) {
                for (SootClass classes : Scene.v().getApplicationClasses()) {
                    String class_name = classes.toString();
                    String final_path;
                    if (!class_name.contains("$")) {
                        String[] split_classname = class_name.split("\\.");
                        if ((split_classname[split_classname.length - 1].equalsIgnoreCase(last_name))) {
                            final_path = class_name;
                            Main.plugins_list.get(i).addCpath(final_path);
                        }
                    }
                }
            }
        }
        soot.G.reset();
    }

    /**
     * Function to gather all URLs and search for them as a file
     */
    public static void analyzeURLs() {
        Iterator<String> iterator = Main.possibleURLs.iterator();
        String final_url = null;
        while (iterator.hasNext()) {
            String url = iterator.next();
            String replace_url = url.substring(url.indexOf("/www") + 1);
            final_url = "/" + replace_url;
            System.out.println("-> Locating file: " + final_url);
        }
        // Now we have the URL, we have to search for the possible index file in the directory
        File f = new File(Main.html_assets_dir + final_url);
        if (f.exists() && !f.isDirectory()) {
            //Now we must remove all jquery script lines to a new index.html (to prevent infinite looping during callgraph creation)
            removeMisclleanous(f.getAbsolutePath());
            try {
                //Create HTML callgraph on the new html file
                createHTMLCallGraph(Main.index_html_file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CancelException e) {
                e.printStackTrace();
            } catch (WalaException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("-> File: " + final_url + " not found");
            System.out.println("-> Anaylsis failed //terminating");
            System.exit(0);
        }
    }

    /**
     * Function to remove <jquery> scripts, and <doctype> from html file if they exists
     *
     * @param path
     */
    private static void removeMisclleanous(String path) {
        ArrayList<String> html_lines = new ArrayList<String>();
        File f = new File(path);
        Scanner input = null;
        try {
            input = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("-> File not found");
        }
        while (input.hasNext()) {
            String line = input.nextLine();
            //If it doesn't contain anything related to JQuery
            if (!line.contains("http://code.jquery.com/")) {
                html_lines.add(line);
            }

        }
        //Once done, write to a new html file
        if (html_lines.size() != 0) {
            //Write a new file for future references
            File modified_htmlfile = new File(Main.index_html_file);
            try {
                FileUtils.writeLines(modified_htmlfile, html_lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("-> Updated file (removal of <jquery> scripts): " + Main.index_html_file);
        }
    }

    /**
     * Function to createaHTMLCallGraph to find out all js files used in the html file
     *
     * @param filepath
     * @throws MalformedURLException
     * @throws IOException
     * @throws CancelException
     * @throws WalaException
     */
    private static void createHTMLCallGraph(String filepath) throws IOException, CancelException, WalaException {
        File file = new File(filepath);
        URL url = file.toURI().toURL();
        //Create call graph of html file
        JSCallGraphBuilderUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());
        CallGraph callGraph = JSCallGraphBuilderUtil.makeHTMLCG(url);
        //Iterate through the callgraph
        Iterator<CGNode> cgIterator = callGraph.iterator();
        CGNode cgNode;
        //get the IR of each CGNode
        TypeName typeName = TypeName.findOrCreate("Lindex.html");
        while (cgIterator.hasNext()) {
            cgNode = cgIterator.next();
            TypeName cgTypeName = cgNode.getMethod().getDeclaringClass().getName();
            //Sift out those with index.html
            if (cgTypeName.toString().startsWith(typeName.toString())) {
                //Get the IR
                IR irNode = cgNode.getIR();
                //Make a controlflowgraph from the node
                SSACFG cfg = irNode.getControlFlowGraph();
                //Iterate through the basic blocks of the cfg
                Iterator<ISSABasicBlock> basic_block_iterator = cfg.iterator();
                while (basic_block_iterator.hasNext()) {
                    //Iterate through the instructions of each basic block
                    Iterator<SSAInstruction> instructions_iterator = basic_block_iterator.next().iterator();
                    while (instructions_iterator.hasNext()) {
                        //Get the individual instruction
                        SSAInstruction ssa_instruction = instructions_iterator.next();
                        String get_jstype = "text/javascript";
                        String ssa_instruction_field = ssa_instruction.toString(irNode.getSymbolTable());
                        //Check if the field contains the js type
                        if (ssa_instruction_field.contains(get_jstype)) {
                            //Get the next basic block
                            while (basic_block_iterator.hasNext()) {
                                //Create an iterator for the next basic block
                                Iterator<SSAInstruction> next_instruction_iterator = basic_block_iterator.next().iterator();
                                //Iterate through all the instructions in this basic block
                                while (next_instruction_iterator.hasNext()) {
                                    //Get the individual instruction
                                    SSAInstruction field_instruction_js = next_instruction_iterator.next();
                                    String fieldref_js = field_instruction_js.toString(irNode.getSymbolTable());
                                    //Check if the field reference contains .js
                                    String container_js = ".js";
                                    if (fieldref_js.contains(container_js)) {
                                        //Get out the object js (fieldref v2.v22:#src = v21:#js/index.js = v21:#js/index.js)
                                        String[] array = fieldref_js.split("=");
                                        //Currently (v21:#js/index.js)
                                        String non_removed_field = array[array.length - 1];
                                        //Remove everythng after #
                                        String fieldArray[] = non_removed_field.split("#");
                                        String final_jsfile = fieldArray[1];
                                        Main.jsfiles_list.add(final_jsfile);
                                        System.out.println("-> <Javascript> file found: " + final_jsfile);
                                        break;
                                    }


                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Function to iterate through all Javascript files and get all functions involved
     */
    public static void analyzeJSfiles() {
        //Iterate through each possible js file
        for (int i = 0; i < Main.jsfiles_list.size(); i++) {
            String file_path = Main.jsfiles_list.get(i);
            //Find the file (omit out cordova.js for now)
            if (!file_path.equals("cordova.js")) {          //Technically (index.js)
                String path = Main.js_file_path + file_path;
                createJSCallgraph(path);
            }

        }
    }

    /**
     * Function to create a callgraph for the Javascript file [unfinished]
     *
     * @param original_filepath
     */
    private static void createJSCallgraph(String original_filepath) {
        //Get the directory of the file path
        File path = new File(original_filepath);
        //Get the parent directory
        String parent = path.getParent();
        //Get the file
        String file_name = path.getName();
        String file_typename = "L" + parent + "\\" + file_name;
        //Replace and convert from "\" to "/"
        file_typename = file_typename.replace("\\", "/");
        CAstRhinoTranslatorFactory translatorFactory = new CAstRhinoTranslatorFactory();
        JSCallGraphUtil.setTranslatorFactory(translatorFactory);
        try {
            CallGraph cg = JSCallGraphBuilderUtil.makeScriptCG(parent, file_name);
            //Iterate through the callgraph
            IClassHierarchy cha = cg.getClassHierarchy();
            // for constructing IRs
            IRFactory<IMethod> factory = AstIRFactory.makeDefaultFactory();
            for (IClass klass : cha) {
                // ignore models of built-in JavaScript methods
                if (!klass.getName().toString().startsWith("Lprologue.js")) {
                    if (!klass.getName().toString().startsWith("Lpreamble.js")) {
                        // get the IMethod representing the code (the ‘do’ method)

                        IMethod m = klass.getMethod(AstMethodReference.fnSelector);
                        if (m != null) {
                            IR ir = factory.makeIR(m, Everywhere.EVERYWHERE,
                                    new SSAOptions());
                            //Store the IR in a hashmap <index.js, ArrayList<ir>>
                            if (!Main.js_ir_maplist.containsKey(file_name)) {
                                Main.js_ir_maplist.put(file_name, new ArrayList<IR>());
                            }
                            Main.js_ir_maplist.get(file_name).add(ir);
                            //System.out.println(ir);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CancelException e) {
            e.printStackTrace();
        } catch (WalaException e) {
            e.printStackTrace();
        }
    }

    public static void computeCordovaPluginsEvent() {
        for (int i = 0; i < Main.plugins_list.size(); i++) {
            ArrayList<String> js_function = new ArrayList<String>();
            String fp = Main.plugins_list.get(i).getfile();
            String combined_fp = Main.plugin_js_file_path + fp;
            BufferedReader br = null;
            FileReader fr = null;

            try {

                fr = new FileReader(combined_fp);
                br = new BufferedReader(fr);

                String sCurrentLine;
                boolean exeception_caught = false;
                boolean tag = false;
                int counter = 0;
                while ((sCurrentLine = br.readLine()) != null) {
                    if ((sCurrentLine.contains("function") && sCurrentLine.contains("=")) || (sCurrentLine.contains("function") && sCurrentLine.contains(":"))) {
                        js_function.add(sCurrentLine);
                    }
                    if ((exeception_caught == false) && (sCurrentLine.contains("exec("))) {
                        try {
                            String[] split = sCurrentLine.split(",");
                            String f_replace = split[3].replace("\'", "");
                            String s_replace = f_replace.replace("\"", "");
                            String action = s_replace.trim();
                            //Add to the pluginlist
                            Main.plugins_list.get(i).getActions().add(action);
                            String js_func = js_function.get(js_function.size() - 1);
                            js_func = modifyJSFunction(js_func);
                            Main.plugins_list.get(i).setMap_jsmethods_js_actions(action, js_func);
                            //System.out.println(js_func + " " + action);
                        } catch (Exception e) {
                            exeception_caught = true;
                        }
                    }
                    //check if the current line has the "exec" word
                    if ((exeception_caught == true)) {
                        //find for the 'failure' line
                        if (sCurrentLine.contains("exec(")) {
                            tag = true;
                        }
                        //hit exec already
                        if (tag == true) {
                            counter++;
                        }
                        if (counter == 5) {
                            String[] split = sCurrentLine.split(",");
                            String f_replace = split[0].replace("\'", "");
                            String s_replace = f_replace.replace("\"", "");
                            String action = s_replace.trim();
                            //Add to the pluginlist
                            Main.plugins_list.get(i).getActions().add(action);
                            try {
                                String js_func = js_function.get(js_function.size() - 1);
                                //Modify the func name
                                js_func = modifyJSFunction(js_func);
                                Main.plugins_list.get(i).setMap_jsmethods_js_actions(action, js_func);
                            } catch (Exception e) {
                                //
                            }
                            //System.out.println(js_func + " " + action);
                            tag = false;
                            counter = 0;
                        }
                    }
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
        }
    }

    /**
     * Create a text file through WALA based on the plugins to search for fields and consecutively method names
     */
    public static void computeJSMethods() {
        //iterate through every plugin text file
        System.out.println("-> Locating JavaScript methods.");
        for (int i = 0; i < Main.plugins_list.size(); i++) {
            String js_file_path = Main.plugins_list.get(i).getfile();
            String actual_path = Main.js_file_path + js_file_path;
            String[] actual_path_splitted = actual_path.split("/");
            String tag_name = actual_path_splitted[actual_path_splitted.length - 1];
            String modified_tag_name = "WALA_" + tag_name;
            //writePlugintoTextFiles
            writePluginToTextFiles(actual_path, modified_tag_name);
            //locate fields based on actions
            ArrayList<String> actions = Main.plugins_list.get(i).getActions();
            for (int x = 0; x < actions.size(); x++) {
                String todo_action = actions.get(x);
                String m_action = "#" + todo_action;
                //System.out.println(m_action);
                searchField(actual_path, modified_tag_name, m_action, Main.plugins_list.get(i));
            }

            //For each methodline
            ArrayList<String> ml = Main.plugins_list.get(i).getMethodLineNumbers();
            for (int y = 0; y < ml.size(); y++) {
                String line = ml.get(y);
                locateActualMethods(actual_path, line, Main.plugins_list.get(i));
            }

        }
    }

    private static void writePluginToTextFiles(String original_filepath, String tag_name) {
        String file_path = Main.js_file_path + tag_name;
        Path outputPath = Paths.get(file_path);
        File out = outputPath.toFile();
        try {
            if (out.exists()) {
                out.delete();
            }
        } catch (Exception e) {

        }
        BufferedWriter bw = null;
        FileWriter fw = null;
        //Get the directory of the file path
        File path = new File(original_filepath);
        //Get the parent directory
        String parent = path.getParent();
        //Get the file
        String file_name = path.getName();
        String file_typename = "L" + parent + "\\" + file_name;
        //Replace and convert from "\" to "/"
        file_typename = file_typename.replace("\\", "/");
        CAstRhinoTranslatorFactory translatorFactory = new CAstRhinoTranslatorFactory();
        JSCallGraphUtil.setTranslatorFactory(translatorFactory);
        try {
            fw = new FileWriter(file_path);
            bw = new BufferedWriter(fw);
            CallGraph cg = JSCallGraphBuilderUtil.makeScriptCG(parent, file_name);
            //Iterate through the callgraph
            IClassHierarchy cha = cg.getClassHierarchy();
            // for constructing IRs
            IRFactory<IMethod> factory = AstIRFactory.makeDefaultFactory();
            for (IClass klass : cha) {
                // ignore models of built-in JavaScript methods
                if (!klass.getName().toString().startsWith("Lprologue.js")) {
                    if (!klass.getName().toString().startsWith("Lpreamble.js")) {
                        // get the IMethod representing the code (the ‘do’ method)

                        IMethod m = klass.getMethod(AstMethodReference.fnSelector);
                        if (m != null) {
                            IR ir = factory.makeIR(m, Everywhere.EVERYWHERE,
                                    new SSAOptions());
                            //Write to text file
                            bw.write(ir.toString());
                        }
                    }
                }
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

    /**
     * Function to search for the specific field containing the action (example: 'send' or 'get')
     *
     * @param original_filepath
     */
    private static void searchField(String original_filepath, String file, String action, BridgePluginObject po) {
        //Get the directory of the file path
        File path = new File(original_filepath);
        //Get the parent directory
        String parent = path.getParent();
        //Get the file
        String file_name = path.getName();
        String file_typename = "L" + parent + "\\" + file_name;
        //Replace and convert from "\" to "/"
        file_typename = file_typename.replace("\\", "/");
        CAstRhinoTranslatorFactory translatorFactory = new CAstRhinoTranslatorFactory();
        JSCallGraphUtil.setTranslatorFactory(translatorFactory);
        try {
            CallGraph cg = JSCallGraphBuilderUtil.makeScriptCG(parent, file_name);
            //Iterate through the callgraph
            IClassHierarchy cha = cg.getClassHierarchy();
            // for constructing IRs
            IRFactory<IMethod> factory = AstIRFactory.makeDefaultFactory();
            for (IClass klass : cha) {
                // ignore models of built-in JavaScript methods
                if (!klass.getName().toString().startsWith("Lprologue.js")) {
                    if (!klass.getName().toString().startsWith("Lpreamble.js")) {
                        // get the IMethod representing the code (the ‘do’ method)

                        IMethod m = klass.getMethod(AstMethodReference.fnSelector);
                        if (m != null) {
                            IR ir = factory.makeIR(m, Everywhere.EVERYWHERE,
                                    new SSAOptions());
                            //Get control-flowgraph
                            SSACFG cfg = ir.getControlFlowGraph();
                            Iterator<ISSABasicBlock> basic_block_iterator = cfg.iterator();
                            while (basic_block_iterator.hasNext()) {
                                Iterator<SSAInstruction> instructions_iterator = basic_block_iterator.next().iterator();
                                while (instructions_iterator.hasNext()) {
                                    //Get the individual instruction
                                    SSAInstruction ssa_instruction = instructions_iterator.next();
                                    String ssa_instruction_field = ssa_instruction.toString(ir.getSymbolTable());
                                    if ((ssa_instruction_field.contains(action)) && (!ssa_instruction_field.contains("fieldref"))) {
                                        //Found[@TODO]
                                        String found_field = ssa_instruction_field;
                                        //System.out.println(found_field);
                                        locateMethodLine(found_field, Main.js_file_path + file, po);
                                    }
                                }
                            }
                            //UNFINISHED @TODO IN FUTURE
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CancelException e) {
            e.printStackTrace();
        } catch (WalaException e) {
            e.printStackTrace();
        }
    }

    private static void locateMethodLine(String field, String path, BridgePluginObject po) {
        boolean tag = false;
        int counter = 0;
        try {
            ReversedLinesFileReader filereader = new ReversedLinesFileReader(new File(path));
            String sCurrentLine;
            do {
                sCurrentLine = filereader.readLine();
                if (sCurrentLine.contains(field)) {
                    tag = true;
                }
                //hit the specific line of code already
                if (tag == true) {
                    counter++;
                }
                //if the counter more than 0 and contains that specific code - print out and break
                if (counter > 0 && sCurrentLine.contains("<JavaScriptLoader,LArray>")) {
                    String found_line = sCurrentLine;
                    //get the first instance
                    Pattern regex = Pattern.compile("\\[(.*?)->(.*?)\\]");
                    Matcher regexMatcher = regex.matcher(found_line);
                    String paranthesis = null;
                    while (regexMatcher.find()) {//Finds Matching Pattern in String
                        paranthesis = regexMatcher.group();//Fetching Group from String example [423->1350]
                    }
                    String[] number = paranthesis.split("->");
                    String post_number = number[1].replace("]", "");
                    //Read the first occurance of the pre_number
                    String result = readPreNumber(path, post_number);
                    if (result != null) {
                        addLineNumber(result, po);
                    }
                    if (result == null) {
                        addLineNumber(found_line, po);
                    }
                    break;
                }
            } while (sCurrentLine != null);
            filereader.close();
        } catch (Exception e) {
        }
    }

    private static void locateActualMethods(String filepath, String line_number, BridgePluginObject po) {
        //read the Javascript file
        BufferedReader br = null;
        FileReader fr = null;
        int linecounter = 0;
        try {
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                linecounter++;
                if (linecounter == Integer.parseInt(line_number)) {
                    parseMethod(sCurrentLine, po);
                }
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
    }

    private static void parseMethod(String line, BridgePluginObject po) {
        //sms.send = function(x,x,x,x)
        //check if declared through equals
        String method = null;
        if (line.contains("=")) {
            String[] splitLine = line.split("=");
            //iterate
            for (int i = 0; i < splitLine.length; i++) {
                if (!splitLine[i].contains("function")) {
                    method = splitLine[i].trim();
                }
            }
        } else if (line.contains(":")) {
            String[] splitLine = line.split(":");
            for (int i = 0; i < splitLine.length; i++) {
                if (!splitLine[i].contains("function")) {
                    method = splitLine[i].trim();
                }
            }
        }
        //Add to the po
        po.addJS_Methods(method);
    }

    private static String readPreNumber(String filepath, String pre_number) {
        BufferedReader br = null;
        FileReader fr = null;
        String rLine = null;
        try {
            String sCurrentLine;
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);


            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.contains(pre_number)) {
                    //Gotten first occurance
                    //Check if this first occurance is a global Function
                    if (sCurrentLine.contains("global:global Function")) {
                        rLine = sCurrentLine;
                        break;
                    }
                }
            }

        } catch (IOException e) {

            e.printStackTrace();

        }
        try {

            if (br != null)
                br.close();

            if (fr != null)
                fr.close();

        } catch (IOException ex) {

            ex.printStackTrace();

        }
        return rLine;
    }

    private static void addLineNumber(String obj, BridgePluginObject po) {
        Pattern a_regex = Pattern.compile("\\((.*?)\\)");
        Matcher a_regexMatcher = a_regex.matcher(obj);
        String a_paranthesis = null;
        String line_number = null;
        while (a_regexMatcher.find()) {
            a_paranthesis = a_regexMatcher.group();
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(a_paranthesis);
            while (m.find()) {
                line_number = m.group();
                //Add the line numbers
                po.addMethodLineNumbers(line_number);
            }
        }
    }

    public static void computeIndexFile() {
        //Get the directory of the file path
        for(String key: Main.js_ir_maplist.keySet()) {
            String original_filepath = Main.js_file_path + "/js/"+key;
            File path = new File(original_filepath);
            //Get the parent directory
            String parent = path.getParent();
            //Get the file
            String file_name = path.getName();
            String file_typename = "L" + parent + "\\" + file_name;
            //Replace and convert from "\" to "/"
            file_typename = file_typename.replace("\\", "/");
            CAstRhinoTranslatorFactory translatorFactory = new CAstRhinoTranslatorFactory();
            JSCallGraphUtil.setTranslatorFactory(translatorFactory);
            try {
                CallGraph cg = JSCallGraphBuilderUtil.makeScriptCG(parent, file_name);
                //Iterate through the callgraph
                IClassHierarchy cha = cg.getClassHierarchy();
                // for constructing IRs
                IRFactory<IMethod> factory = AstIRFactory.makeDefaultFactory();
                for (IClass klass : cha) {
                    // ignore models of built-in JavaScript methods
                    if (!klass.getName().toString().startsWith("Lprologue.js")) {
                        if (!klass.getName().toString().startsWith("Lpreamble.js")) {
                            // get the IMethod representing the code (the ‘do’ method)

                            IMethod m = klass.getMethod(AstMethodReference.fnSelector);
                            if (m != null) {
                                IR ir = factory.makeIR(m, Everywhere.EVERYWHERE,
                                        new SSAOptions());
                                //Store the IR in a hashmap <index.js, ArrayList<ir>
                                //Get out all methods name
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CancelException e) {
                e.printStackTrace();
            } catch (WalaException e) {
                e.printStackTrace();
            }
        }
    }

    public static void computeJS_ControlFlow() {
        //Go through each function of the index.js file and store them
        //-> index.js -> [Add] -> (body)[1,2,3]
        //            -> [onDeviceReady] -> (body),1,2,3]
        //@ is changed to AT
        //: is change to COLUMN
        HashMap<String, HTMLJSObject> store_map = Main.js_methods_hashmap;
        //Initialise the hashmap
        //Get the values from the index.js hashmap
        //ArrayList<IR> arrayList = Main.js_ir_maplist.get("index.js");
        //For each item in the array get the method name
        for (String key : Main.js_ir_maplist.keySet()) {
            ArrayList<IR> arrayList = Main.js_ir_maplist.get(key);
            try {
                for (int i = 0; i < arrayList.size(); i++) {
                    String method_name = arrayList.get(i).getMethod().getDeclaringClass().toString();
                    //<Code body of function L./output/htmljs/assets/www/js/index.js>
                    //Start filtering out name
                    method_name = method_name.replace("function L./output/htmljs/assets/www/js/"+key+"/", "").trim();
                    method_name = method_name.replace("./output/htmljs/assets/www/js/", "").trim();
                    //Check that it doesnt contain the word function(first layer of removal)
                    if (!method_name.contains("function")) {
                        //Check that it doesn't have any subsets(IS A PARENT)
                        if (!method_name.contains("/")) {
                            //Check that it doesnt contain't any symbols or colons
                            if (!method_name.contains("@") || !method_name.contains(":")) {
                                String final_method_name = method_name.trim();
                                ArrayList<HTMLJSObject> aL = new ArrayList<HTMLJSObject>();
                                ArrayList<String> body = getBody(arrayList, i);
                                HTMLJSObject htmljsObject = new HTMLJSObject(final_method_name, body, aL);
                                //Check if contains
                                if (!store_map.containsKey(final_method_name)) {
                                    store_map.put(final_method_name, htmljsObject);
                                } else {
                                    store_map.get(final_method_name).setMethod_body(body);
                                }
                            }
                            //Contains symbols and stuff
                            else {
                                method_name = method_name.replace(".", "_");
                                method_name = method_name.replace("@", "_");
                                method_name = method_name.replace(":", "_");
                                String final_method_name = method_name.trim();
                                ArrayList<HTMLJSObject> aL = new ArrayList<HTMLJSObject>();
                                ArrayList<String> body = getBody(arrayList, i);
                                HTMLJSObject htmljsObject = new HTMLJSObject(final_method_name, body, aL);
                                //Add the object to Hashmap[index_js_466_sendSms]
                                if (!store_map.containsKey(final_method_name)) {
                                    store_map.put(final_method_name, htmljsObject);
                                } else {
                                    store_map.get(final_method_name).setMethod_body(body);
                                }
                            }
                        }
                        //IS A CHILD
                        if (method_name.contains("/")) {
                            String full_name = method_name;
                            String[] split_method_name = method_name.split("/");
                            String parent = split_method_name[0];
                            //Do transformation
                            parent = parent.replace(".", "_");
                            parent = parent.replace("@", "_");
                            parent = parent.replace(":", "_");
                            String final_parent;
                            //Transformation on parent name done, now do child
                            String child = split_method_name[1];
                            child = child.replace(".", "_");
                            child = child.replace("@", "_");
                            child = child.replace(":", "_");
                            String final_child;
                            final_parent = parent;
                            final_child = child;
                            //Check if hashmap has the specific parent
                            if (!store_map.containsKey(final_parent)) {
                                //insert
                                ArrayList<HTMLJSObject> aL = new ArrayList<HTMLJSObject>();
                                ArrayList<String> body = getBody(arrayList, i);
                                HTMLJSObject parent_htmljsObject = new HTMLJSObject(final_parent, body, aL);
                                store_map.put(final_parent, parent_htmljsObject);
                            }
                            //Hashmap has the parent
                            ArrayList<HTMLJSObject> aL = new ArrayList<HTMLJSObject>();
                            ArrayList<String> body = getBody(arrayList, i);
                            HTMLJSObject child_htmljsObject = new HTMLJSObject(final_child, body, aL);
                            store_map.get(final_parent).addMethod_children(child_htmljsObject);
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * Maps from the JavaScript format to Java format
     */
    public static void mapJS_ControlFlow() {
        System.out.println("-> JavaScript methods found: " + Main.js_methods_hashmap.keySet());
        for (String key : Main.js_methods_hashmap.keySet()) {
            ArrayList<String> body = Main.js_methods_hashmap.get(key.trim()).getMethod_body();
            ArrayList<MAP_JSOBJECT> js_lines = new ArrayList<MAP_JSOBJECT>();
            //Iterate through each body
            for (int i = 0; i < body.size(); i++) {
                String line = body.get(i);
                //FIELD TYPE
                if (line.contains("TAG: FIELD") || line.contains("TAG: GLOBALFIELD")) {
                    String split[] = line.split("->");
                    String name = split[0].trim();
                    //Split the fieldtype
                    String split1[] = split[1].split("/");
                    String source = split1[1].trim();
                    String split2[] = split1[0].trim().split(":");
                    String type = split2[1].trim();
                    //create new JSOBJECT
                    MAP_JSOBJECT jsobject = new MAP_JSOBJECT(name, type, source);
                    js_lines.add(jsobject);
                }
                //PROTOTYPE SD_OBJECT_6 -> TAG: PROTOTYPE ARGS:19 / 6
                if (line.contains("TAG: PROTOTYPE")) {
                    String split[] = line.split("->");
                    String name = split[0].trim();
                    //Split the fieldtype
                    String split1[] = split[1].split("/");
                    String source = split1[1].trim();
                    String split2[] = split1[0].split(":");
                    String type = "PROTOTYPE";
                    String input = split2[2].trim();
                    MAP_JSOBJECT jsobject = new MAP_JSOBJECT(name, type, source);
                    jsobject.setInput(input);
                    //Iterate through all js_lines and check which field has number 19 as a source(input -> source)
                    for (int x = 0; x < js_lines.size(); x++) {
                        MAP_JSOBJECT field = js_lines.get(x);
                        if (jsobject.getInput().equals(field.getSource())) {
                            //Change the field's source
                            field.setSource(jsobject.getSource());
                        }
                    }
                }
                if (line.contains("TAG: GETFIELD")) {
                    String split[] = line.split("->");
                    String name = split[0].trim();
                    //Split the fieldtype
                    String split1[] = split[1].split("/");
                    String source = split1[1].trim();
                    String split2[] = split1[0].split(":");
                    String type = "GETFIELD";
                    String input = split2[2].trim();
                    MAP_JSOBJECT jsobject = new MAP_JSOBJECT(name, type, source);
                    jsobject.setInput(input);
                    //Iterate through all js_lines and check which field has number 19 as a source(input -> source)
                    for (int x = 0; x < js_lines.size(); x++) {
                        MAP_JSOBJECT field = js_lines.get(x);
                        if (jsobject.getInput().equals(field.getSource())) {
                            //Change the field's source
                            field.setSource(jsobject.getSource());
                        }
                    }
                }
                //SEND_61 -> TAG: DISPATCH -> INPUT: 59 ARGS:22:#93241495,17,36,48,52 / 61
                if (line.contains("TAG: DISPATCH")) {
                    String split[] = line.split("->");
                    String name = split[0].trim();
                    //Split the fieldtype
                    String split1[] = split[2].split("/");
                    String source = split1[1].trim();
                    String type = "DISPATCH";
                    String split3[] = split[2].trim().split(" ");
                    String input = split3[1].trim();
                    String before_args = split3[2].trim().replace("ARGS:", "");
                    MAP_JSOBJECT jsobject = new MAP_JSOBJECT(name, type, source);
                    jsobject.setInput(input);
                    //Split the args
                    String split_args[] = before_args.split(",");
                    for (int length = 0; length < split_args.length; length++) {
                        jsobject.addArguments(split_args[length]);
                    }
                    for (int x = 0; x < js_lines.size(); x++) {
                        MAP_JSOBJECT field = js_lines.get(x);
                        if (jsobject.getInput().equals(field.getSource())) {
                            //Change the field's source
                            jsobject.setSuper_object(field);
                        }
                    }
                    js_lines.add(jsobject);
                }
            }
            try {
                convertJS_toJava(js_lines);
            } catch (Exception e) {
                //
            }
        }
    }

    private static void convertJS_toJava(ArrayList<MAP_JSOBJECT> js_lines) {
        //Get all the lines and make them to Java format before instrumentation
        System.out.println("-> Convert JavaScript to Java format");
        ArrayList<String> names = new ArrayList<String>();
        for (int x = 0; x < Main.plugins_list.size(); x++) {
            String main_file = Main.plugins_list.get(x).getfile();
            String[] split = main_file.split("/");
            String file = split[split.length - 1].replace(".js", "");
            names.add(file);
        }
        for (int i = 0; i < js_lines.size(); i++) {
            String name = js_lines.get(i).getName();
            //Split the name
            String[] split_name = name.split("_");
            String final_name = split_name[0];
            //Check if the final_name is one of the many js files
            for (int a = 0; a < names.size(); a++) {
                if (final_name.equals(names.get(a))) {
                    js_lines.get(i).setName(final_name);
                    js_lines.get(i).setCpath(Main.plugins_list.get(a).getCpath());
                }
            }
            //NOW CREATE BASIC OBJECTS
            if (js_lines.get(i).getType().equals("FIELD") || js_lines.get(i).getType().equals("GLOBALFIELD")) {
                //
                String jsname = js_lines.get(i).getName();
                String cpath = js_lines.get(i).getCpath();
                String type = null;
                String source = js_lines.get(i).getSource();
                String format = "o";
                if (cpath == null) {
                    type = "Object";
                } else {
                    type = cpath;
                }
                String[] object = {type, jsname, source, format};
                System.out.println(Arrays.toString(object));
                lines.add(object);
            }
            if (js_lines.get(i).getType().equals("DISPATCH")) {
                //Will have a superobject
                String type = "Method";
                String jsname = js_lines.get(i).getSuper_object().getName();
                String format = "m";
                //Pack in the arguments
                ArrayList<String> args = js_lines.get(i).getArguments();
                String[] object = {type, jsname, String.valueOf(args), format};
                System.out.println(Arrays.toString(object));
                lines.add(object);
            }
        }
    }


    private static ArrayList<String> getBody(ArrayList<IR> arrayList, int i) {
        ArrayList<String> body = new ArrayList<String>();
        SSAInstruction[] instructions = arrayList.get(i).getInstructions();
        for (int x = 0; x < instructions.length; x++) {
            SSAInstruction ins = instructions[x];
            try {
                String line = ins.toString(arrayList.get(i).getSymbolTable());
                //Get out all variables [global:global xxx]
                String final_var = null;
                if (line.contains("global:global")) {
                    //split the line
                    String[] variables = line.split("=");
                    //split the line [using whitespace]
                    String[] variables_split = variables[1].split(" ");
                    //If the variable is undefined, define it
                    if (variables_split[2].trim().equals("$$undefined")) {
                        String var = variables_split[2].trim().concat("_" + ins.getDef());
                        String var_modified = var.replace("$", "");
                        final_var = var_modified.concat(" -> " + "TAG: FIELD" + " / " + ins.getDef());
                    } else {
                        String var = variables_split[2].trim().concat("_" + ins.getDef());
                        final_var = var.trim().concat(" -> " + "TAG: GLOBALFIELD" + " / " + ins.getDef());
                    }
                    body.add(final_var);
                    //System.out.println(final_var);
                }

                //v6 = prototype_values(v19) 6
                if (line.contains("prototype_values")) {
                    String[] items = line.split("=");
                    String self_defined = "SD_OBJECT";
                    Matcher m = Pattern.compile("\\((.*?)\\)").matcher(items[1]);
                    String input_arg = null;
                    while (m.find()) {
                        String before = m.group(1);
                        String after = before.replace("v", "");
                        input_arg = after;
                    }
                    self_defined = self_defined + "_" + ins.getDef();
                    self_defined = self_defined.concat(" -> " + "TAG: PROTOTYPE");
                    self_defined = self_defined.concat(" INPUT:" + input_arg);
                    self_defined = self_defined.concat(" / " + ins.getDef());
                    body.add(self_defined);
                    //System.out.println(self_defined);
                }
                //v17 = getfield < JavaScriptLoader, LRoot, serial, <JavaScriptLoader,LRoot> > v6index.js [494->507] (line 21)
                if (line.contains("getfield <")) {
                    String[] items = line.split("=");
                    int idx = items[1].lastIndexOf(">");
                    String args = items[1].substring(idx + 1);
                    args = args.replace("v", "").trim();
                    String self_defined = "SD_OBJECT";
                    self_defined = self_defined + "_" + ins.getDef();
                    self_defined = self_defined.concat(" -> " + "TAG: GETFIELD");
                    self_defined = self_defined.concat(" INPUT:" + args);
                    self_defined = self_defined.concat(" / " + ins.getDef());
                    body.add(self_defined);
                    //System.out.println(self_defined);
                }
                //v61 = dispatch v60:#send@53 v59,v22:#93241495,v17,v36,v48,v52 exception:v62
                if (line.contains("dispatch")) {
                    String[] items = line.split("=");
                    Matcher m = Pattern.compile("\\#(.*?)\\@").matcher(items[1]);
                    String name = null;
                    while (m.find()) {
                        String before = m.group(1);
                        name = before;
                    }
                    name = name.toLowerCase();
                    String self_defined = name;
                    //Name is done
                    self_defined = self_defined + "_" + ins.getDef();
                    self_defined = self_defined.concat(" -> " + "TAG: DISPATCH");
                    String[] items2 = line.split(name.toLowerCase());
                    Matcher m1 = Pattern.compile("\\@(.*?)\\,").matcher(items2[1]);
                    String before = null;
                    while (m1.find()) {
                        before = m1.group(1);
                    }
                    String split[] = before.split("v");
                    String after_arg = split[1];
                    self_defined = self_defined.concat(" -> " + "INPUT: " + after_arg);
                    //get the arguments
                    Matcher m2 = Pattern.compile("\\,(.*?)\\ ").matcher(items2[1]);
                    String before_args = null;
                    while (m2.find()) {
                        before_args = m2.group(1);
                    }
                    before_args = before_args.trim();
                    //Split the args
                    String[] args_split = before_args.split(",");
                    String ARGS = "";
                    for (int size = 0; size < args_split.length; size++) {
                        String single_arg = args_split[size].replace("v", "");
                        if (size == (args_split.length - 1)) {
                            ARGS += single_arg;
                        } else {
                            ARGS += single_arg + ",";
                        }
                    }
                    self_defined = self_defined.concat(" ARGS:" + ARGS);
                    self_defined = self_defined.concat(" / " + ins.getDef());
                    body.add(self_defined);
                    //System.out.println(self_defined);
                }

            } catch (Exception e) {
                //
            }
        }
        return body;
    }

    private static String modifyJSFunction(String line) {
        String method = null;
        if (line.contains("=")) {
            String[] splitLine = line.split("=");
            //iterate
            for (int i = 0; i < splitLine.length; i++) {
                if (!splitLine[i].contains("function")) {
                    method = splitLine[i].trim();
                }
            }
        } else if (line.contains(":")) {
            String[] splitLine = line.split(":");
            for (int i = 0; i < splitLine.length; i++) {
                if (!splitLine[i].contains("function")) {
                    method = splitLine[i].trim();
                }
            }
        }
        return method;
    }
}

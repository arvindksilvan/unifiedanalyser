package fyp_project.java_side;


import fj.data.Array;
import fyp_project.Main;
import fyp_project.bridge_side.BridgePluginObject;
import heros.solver.Pair;
import org.xmlpull.v1.XmlPullParserException;
import soot.ValueBox;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.options.Options;
import soot.util.MultiMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory.PathBuilder;

public class Taint_Manager {
    //Main.java_dir
    static String APK = Main.java_dir + "/" + Main.SPLIT_APP;
    //static String APK = "./app_stuff/hybridcalculator_JIMPLE.apk";
    //static String APK = Main.java_dir+"/";
    static String LIB = "C:\\Tools\\AndroidSDK\\platforms";
    static String SS = "./app_stuff/SourcesAndSinks.txt";
    static String CB = "./app_stuff/AndroidCallbacks.txt";
    static String TW = Main.java_dir + "/EasyTaintWrapperSource.txt";
    static MultiMap<ResultSinkInfo, ResultSourceInfo> results = null;

    /**
     * Function to run the Flowdroid taint analysis
     *
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static void runJavaTaint() throws IOException, XmlPullParserException {
        System.out.println("-> Running Taint Analysis on APK");
        EasyTaintWrapper easyTaintWrapper = new EasyTaintWrapper(new
                File(TW));
        SetupApplication app1 = new SetupApplication
                (LIB,
                        APK);
        app1.setCallbackFile(CB);
        app1.setTaintWrapper(easyTaintWrapper);
        IInfoflowConfig config = new IInfoflowConfig() {
            @Override
            public void setSootOptions(Options options) {
                Options.v().set_src_prec(Options.src_prec_apk_class_jimple);
                Options.v().set_process_dir(Collections.singletonList(APK));
                Options.v().set_android_jars(LIB);
                Options.v().set_android_api_version(23);
                Options.v().set_process_multiple_dex(true);
                Options.v().set_whole_program(true);
                Options.v().set_allow_phantom_refs(true);
                Options.v().set_output_format(Options.output_format_class);
                Options.v().setPhaseOption("cg.spark", "on");
            }
        };
        //Set Soot Config
        app1.setSootConfig(config);
        //Set Android Config
        InfoflowAndroidConfiguration a_config = new InfoflowAndroidConfiguration();
        a_config.setAccessPathLength(10);
        a_config.setTaintAnalysisEnabled(true);
        a_config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA);
        a_config.setPathBuilder(PathBuilder.ContextSensitive);
        a_config.setSequentialPathProcessing(true);
        a_config.setComputeResultPaths(true); //SHOW FULL PATHS OR NOT
        app1.setConfig(a_config);
        InfoflowResults taint_results = app1.runInfoflow(SS);
        Path outputPath = Paths.get(Main.java_dir_taint_results);
        //Get the thread to sleep awhile to write after all results are gotten
        try {
            Thread.sleep(05);
        } catch (Exception e) {
            //Do nothing
        }
        File out = outputPath.toFile();
        try {
            if (out.exists()) {
                out.delete();
            }
            FileWriter fw = new FileWriter(out);
            taint_results.printResults(fw);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-> Java results dumped to file: " + outputPath);
        results = taint_results.getResults();
        //Iterate through the hashmap

    }

    public static void computeResults() {
        int k = 0;
        Iterator<Pair<ResultSinkInfo, ResultSourceInfo>> results_itr = results.iterator();
        while (results_itr.hasNext()) {

            Pair<ResultSinkInfo, ResultSourceInfo> source = results_itr.next();
            //System.out.println(source.getO1().getSink().getInvokeExpr().getMethod());
            //System.out.println(source.getO2().getSource().getInvokeExpr().getMethod());
            ResultSinkInfo sink_node = source.getO1();
            for (ResultSourceInfo source_node : results.get(sink_node)) {
                Stmt[] array;
                if (source_node.getPath() != null) {
                    if (k > 0) {
                        String f_line = "---";
                        Main.merged_uf_results.add(f_line);
                    }
                    array = source_node.getPath();
                    for (int i = 0; i < array.length; i++) {
                        //Split up the line to find out the method involved
                        Stmt line = array[i];
                        if (line.containsInvokeExpr()) {
                            //System.out.println(line);
                            //System.out.println(line.getUseBoxes());
                            String get_class = line.getInvokeExpr().getMethod().getDeclaringClass().toString();
                            //System.out.println(get_class);
                            for (BridgePluginObject po : Main.plugins_list) {
                                if (get_class.equals(po.getCpath().toString())) {
                                    String file_name = po.getfile().toString();
                                    String get_method = line.getInvokeExpr().getMethod().getName().toString();
                                    if (!get_method.contains("$")) {
                                        List<ValueBox> useBoxes = line.getUseBoxes();
                                        String object_val = null;
                                        for (int x = 0; x < useBoxes.size(); x++) {
                                            if (useBoxes.get(x).toString().contains("JimpleLocalBox")) {
                                                object_val = useBoxes.get(x).getValue().toString();
                                            }
                                        }
                                        //Only for device
                                        if (get_class.contains("device.Device")) {
                                            //Find for device the function name
                                            if (po.getname().equals("device")) {
                                                String js_method = po.getJS_Methods().get(0);
                                                String nline = file_name + "(" + js_method + ")" + "  ->  " + get_class + "(" + get_method + ")" + " : " + String.valueOf(object_val);
                                                //System.out.println(nline);
                                                Main.merged_uf_results.add(nline);
                                            }
                                        } else {
                                            //For other classes
                                            Object[] get_arguments = line.getInvokeExpr().getArgs().toArray();
                                            for (int x = 0; x < po.getActions().size(); x++) {
                                                for (int a = 0; a < get_arguments.length; a++) {
                                                    String arg = get_arguments[a].toString().replace("\"", "");
                                                    if (arg.equals(po.getActions().get(x))) {
                                                        String js_method = po.getMap_jsmethods_js_actions(arg);
                                                        String nline = file_name + "-" + "(" + js_method + ")" + "  ->  " + get_class + "(" + get_method + ")" + "-" + Arrays.toString(get_arguments);
                                                        //System.out.println(nline);
                                                        Main.merged_uf_results.add(nline);
                                                    }
                                                }

                                            }
                                            String nline = file_name + "-" + "(" + "" + ")" + "  ->  " + get_class + "(" + get_method + ")" + "-" + Arrays.toString(get_arguments);
                                            //System.out.println(nline);
                                            Main.merged_uf_results.add(nline);
                                        }
                                    }
                                }


                            }
                            String get_method = line.getInvokeExpr().getMethod().getName().toString();
                            Object[] get_arguments = line.getInvokeExpr().getArgs().toArray();
                            //Iterate through to get JimpleLocalBox
                            List<ValueBox> useBoxes = line.getUseBoxes();
                            String object_val = null;
                            for (int x = 0; x < useBoxes.size(); x++) {
                                if (useBoxes.get(x).toString().contains("JimpleLocalBox")) {
                                    object_val = useBoxes.get(x).getValue().toString();
                                }
                            }
                            if (!get_class.contains("$") || !get_method.contains("$")) {
                                String nline = "->  " + get_class + "(" + get_method + ")" + "-" + Arrays.toString(get_arguments) + " : " + String.valueOf(object_val);
                                //System.out.println(nline);
                                Main.merged_uf_results.add(nline);

                            }
                        }

                    }
                    k++;
                }

            }
            String nline = "-------";
            //System.out.println(nline);
            Main.merged_uf_results.add(nline);
        }
    }

    public static void processResults() throws IOException {
        String item_check = "-------";
        //Get index of last ="-------"
        int counter = 0;
        ArrayList<Integer> counter_store = new ArrayList<Integer>();
        for (int i = 0; i < Main.merged_uf_results.size(); i++) {
            if (Main.merged_uf_results.get(i).equalsIgnoreCase(item_check)) {
                counter = i;
                counter_store.add(counter);
            }
        }
        //Get the size of the counter_store
        int size = counter_store.size();
        //Get the from second last element
        int token_number = counter_store.get(size - 2);
        for (int a = token_number; a < Main.merged_uf_results.size(); a++) {
            String line = Main.merged_uf_results.get(a);
            if (!line.equalsIgnoreCase(item_check)) {
                Main.merged_f_results.add(line);
            }
        }
        //Iterate through each element from newly filtered arraylist (CHANGE TO f_results.size lateR) @TODO //42
        String previous_sink = null;
        ArrayList<String> store_temp = new ArrayList<String>();
        for (int k = 0; k < Main.merged_f_results.size(); k++) {
            String[] split_items = null;
            String line = Main.merged_f_results.get(k);
            if (!line.equals("---")) {
                split_items = line.split("->");
                if (!split_items[0].isEmpty()) { //isEmpty
                    previous_sink = split_items[1];
                    store_temp.add(split_items[0].trim());
                    store_temp.add(split_items[1].trim());
                }
                if (split_items[0].isEmpty()) {
                    //System.out.println(previous_sink + " -> " + split_items[1]);
                    try {
                        store_temp.add(previous_sink.trim());
                        store_temp.add(split_items[1].trim());
                        previous_sink = split_items[1];
                    }
                    catch (Exception e){

                    }
                }
            }
            if (line.equals("---")) {
                store_temp.add("---\n");

            }
        }
        //@TODO
        boolean start = false;
        ArrayList<String> store_write = new ArrayList<String>();
        boolean activateColor = false;
        store_write.add("digraph G {");
        String colorlabel = "";
        String addLabel = "";
        for (int i = 0; i < store_temp.size(); i++) {
            String label = null;
            if(store_temp.get(i).toString().trim().equals("---")){
                if(activateColor == false){
                    //activate it
                    activateColor = true;
                    colorlabel = getColor();
                }
                if(activateColor == true){
                    //get new color
                    colorlabel = getColor();
                }
            }
            addLabel = "[color=\""+colorlabel+"\"]";
            try {
                if (!store_temp.get(i).toString().isEmpty() || !store_temp.get(i).toString().equals("---\n")) {
                    String first = store_temp.get(i).replace("\"", "\'");
                    String second = store_temp.get(i + 1).replace("\"", "\'");
                    label = "\"" + first + "\"" + " -> " + "\"" + second + "\"";
                    String[] label_split = label.split("->");
                    if (!label_split[0].trim().contains("---")) {
                        if (!label_split[1].trim().contains("---")) {
                            if (!label_split[0].trim().equals(label_split[1].trim())) {
                                String new_label = label_split[0].trim() + " -> " + label_split[1].trim();
                                //add in the labels for the graph
                                new_label = new_label + " " + addLabel;
                                store_write.add(new_label);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //Do nothing
            }
        }
        store_write.add("}");
        //Write
        String app_name = "cfg";
        //Make new folder
        String fname = app_name;
        String filename = fname + ".txt";
        File folder = new File("./output/java/" + fname);
        folder.mkdir();
        // Initalize Writer
        String file_name = "./output/java/" + fname + "/" + filename;
        File file = new File(file_name);
        try {
            writeDotFile(store_write, file_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        convertToGraph(fname, filename);
    }

    private static void writeDotFile(ArrayList<String> dot_graph, String filepath) throws IOException {
        String filename = filepath;
        BufferedWriter bw = null;
        FileWriter fw = null;
        fw = new FileWriter(filename);
        bw = new BufferedWriter(fw);
        String first[] = dot_graph.get(1).toString().split("->");
        String source[] = first[0].split("\\s");
        String final_source = source[0].trim();
        String last[] = dot_graph.get(dot_graph.size()-2).split("->");
        System.out.println(Arrays.toString(last));
        String sink[] = last[1].split("\\[color");
        String final_sink = sink[0].trim();
        for (int i = 0; i < dot_graph.size(); i++) {
            String line = dot_graph.get(i);
            if(i == 1){
                bw.write("{" + "\n");
                bw.write("node [style=filled]");
                bw.write(final_source +" [fillcolor=yellow]" + "\n");
                bw.write(final_sink +"[fillcolor=lightblue]" + "\n");
                bw.write("}" + "\n");
            }
            bw.write(line + "\n");
        }
        bw.close();
        fw.close();
    }

    private static void convertToGraph(String picname, String filename) {
        // Execute command
        try {
            String pic = picname + ".png";
            String graphviz = "dot -Tpng " + filename + " -o " + pic;
            Process process2 = Runtime.getRuntime().exec(graphviz,
                    null, new File("./output/java/" + picname));
        } catch (IOException e) {

        }
    }
    private static String getColor(){
        Random random = new Random();

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(256*256*256);

        // format it as hexadecimal string (with hashtag and leading zeros)
        String colorCode = String.format("#%06x", nextInt);

        // print it
        return colorCode;
    }
}

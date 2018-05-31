package fyp_project.bridge_side;

import soot.SootClass;

import java.util.ArrayList;
import java.util.HashMap;

public class BridgePluginObject {
    String name = null;
    String id = null;
    String file = null;
    String plugin_id = null;
    String clobbers = null;
    String cpath = null;
    ArrayList<SootClass> classList = null;
    ArrayList<String> actions = null;
    ArrayList<String> methodline_numbers = null;
    ArrayList<String> js_methods = null;
    HashMap<String, String> map_jsmethods_js_actions = new HashMap<String, String>();


    public BridgePluginObject(String name, String id, String file, String plugin_id, String clobbers, String cpath, ArrayList<SootClass> classList, ArrayList<String> actions, ArrayList<String> methodline_numbers, ArrayList<String> js_methods) {
        this.name = name;
        this.id = id;
        this.file = file;
        this.plugin_id = plugin_id;
        this.clobbers = clobbers;
        this.classList = classList;
        this.cpath = cpath;
        this.actions = actions;
        this.methodline_numbers = methodline_numbers;
        this.js_methods = js_methods;
    }

    public String getid() {
        return id;
    }

    public String getfile() {
        return file;
    }

    public String getplugin_id() {
        return plugin_id;
    }

    public String getclobbers() {
        return clobbers;
    }

    public ArrayList<SootClass> getclassList() {
        return classList;
    }

    public String getname() {
        return name;
    }

    public String getCpath() {
        return cpath;
    }

    public void addCpath(String cpath) {
        this.cpath = cpath;
    }

    public void addActions(ArrayList<String> actions) {
        this.actions = actions;
    }

    public ArrayList<String> getActions() {
        return this.actions;
    }

    public ArrayList<String> getMethodLineNumbers() {
        return this.methodline_numbers;
    }

    public void addMethodLineNumbers(String ml) {
        this.methodline_numbers.add(ml);
    }

    public ArrayList<String> getJS_Methods() {
        return this.js_methods;
    }

    public void addJS_Methods(String m) {
        this.js_methods.add(m);
    }

    public String getMap_jsmethods_js_actions(String key) {
        return map_jsmethods_js_actions.get(key);
    }

    public void setMap_jsmethods_js_actions(String key, String value) {
        this.map_jsmethods_js_actions.put(key, value);
    }
}

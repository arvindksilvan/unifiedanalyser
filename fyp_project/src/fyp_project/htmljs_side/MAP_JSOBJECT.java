package fyp_project.htmljs_side;

import java.util.ArrayList;

public class MAP_JSOBJECT {
    private String name = null;
    private String type = null;
    private String input = null;
    private String source = null;

    private MAP_JSOBJECT super_object = null;
    private ArrayList<String> arguments = new ArrayList<>();

    private String cpath = null;

    public MAP_JSOBJECT(String name, String type, String source) {
        this.name = name;
        this.type = type;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public void addArguments(String argument) {
        this.arguments.add(argument);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    public MAP_JSOBJECT getSuper_object() {
        return super_object;
    }

    public void setSuper_object(MAP_JSOBJECT super_object) {
        this.super_object = super_object;
    }
    public String getCpath() {
        return cpath;
    }
    public void setCpath(String cpath) {
        this.cpath = cpath;
    }
}

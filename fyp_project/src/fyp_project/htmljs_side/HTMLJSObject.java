package fyp_project.htmljs_side;

import java.util.ArrayList;

public class HTMLJSObject {
    private String method_name = null;
    private ArrayList<String> method_body = null;
    private ArrayList<HTMLJSObject> method_children = null;

    public HTMLJSObject(String method_name, ArrayList<String> method_body, ArrayList<HTMLJSObject> method_parent) {
        this.method_name = method_name;
        this.method_body = method_body;
        this.method_children = method_parent;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public ArrayList<String> getMethod_body() {
        return method_body;
    }

    public void addMethod_body(String line_code) {
        this.method_body.add(line_code);
    }
    public void setMethod_body(ArrayList<String> body){
        this.method_body = body;
    }

    public ArrayList<HTMLJSObject> getMethod_children() {
        return method_children;
    }

    public void addMethod_children(HTMLJSObject method_children) {
        this.method_children.add(method_children);
    }
}
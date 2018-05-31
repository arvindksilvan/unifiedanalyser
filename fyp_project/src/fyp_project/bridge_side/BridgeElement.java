package fyp_project.bridge_side;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

/**
 * Class for a single BridgeElement
 * Contains information for the SootClass, Unit Id, Unit itself
 */
public class BridgeElement {
    private static SootMethod SMethod;
    private static Unit SUnit;
    private static int SUnit_Id;

    /**
     * Create an object for the BridgeElement
     *
     * @param SMethod
     * @param SUnit
     * @param SUnit_Id
     */
    public BridgeElement(SootMethod SMethod, Unit SUnit, int SUnit_Id) {
        this.SMethod = SMethod;
        this.SUnit = SUnit;
        this.SUnit_Id = SUnit_Id;
    }

    /**
     * get bridgeElement according to method and its unit id
     *
     * @return String.format bridgeElement
     */
    public static String getBridgeElement() {
        return String.format("{method}%s, {unit%d}%s", SMethod, SUnit_Id, SUnit);
    }

    /**
     * get the method that has invoked that specific Unit
     *
     * @return SUnit or null
     */
    public SootMethod getInvokedMethod() {
        if (SUnit instanceof Stmt) {
            if (((Stmt) SUnit).containsInvokeExpr()) {
                return ((Stmt) SUnit).getInvokeExpr().getMethod();
            }
        }
        return null;
    }
}

package com.ibm.wala.cast.python.jython3.test;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ssa.IRView;
import com.ibm.wala.util.collections.Iterator2Iterable;

import static com.ibm.wala.cast.ipa.callgraph.CAstCallGraphUtil.getShortName;

public class TestUtil {
    public static void dumpCG(SSAContextInterpreter interp, PointerAnalysis<? extends InstanceKey> PA, CallGraph CG) {
        for (CGNode N : CG) {
            System.err.print("callees of node " + getShortName(N) + " : [");
            boolean fst = true;
            for (CGNode n : Iterator2Iterable.make(CG.getSuccNodes(N))) {
                if (fst) fst = false;
                else System.err.print(", ");
                System.err.print(getShortName(n));
            }
            System.err.println("]");
            System.err.println("\nIR of node " + N.getGraphNodeId() + ", context " + N.getContext());
            IRView ir = interp.getIRView(N);
            if (ir != null) {
                System.err.println(ir);
            } else {
                System.err.println("no IR!");
            }
        }
    }

    public static boolean hasEdge(CallGraph CG, String from, String to) {
        for (CGNode N : CG) {
            if (getShortName(N).startsWith(from)) {
                boolean fst = true;
                for (CGNode n : Iterator2Iterable.make(CG.getSuccNodes(N))) {
                    if (fst) fst = false;
                    if (getShortName(n).startsWith(to)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

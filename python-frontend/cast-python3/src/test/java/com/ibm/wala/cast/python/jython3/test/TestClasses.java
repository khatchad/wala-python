package com.ibm.wala.cast.python.jython3.test;

import com.ibm.wala.cast.ipa.callgraph.CAstCallGraphUtil;
import com.ibm.wala.cast.python.client.PythonAnalysisEngine;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PropagationCallGraphBuilder;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.WalaException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestClasses extends TestPythonCallGraphShape {

	 protected static final Object[][] assertionsClasses1 = new Object[][] {
		    new Object[] { ROOT, new String[] { "script classes1.py" } },
		    new Object[] {
		        "script classes1.py",
		        new String[] { "script classes1.py/Outer", 
		        		"$script classes1.py/Outer/foo:trampoline2",
		        		"script classes1.py/Outer/Inner", 
		        		"$script classes1.py/Outer/Inner/foo:trampoline2" } },
		    new Object[] {
		    	"$script classes1.py/Outer/foo:trampoline2",
		    	new String[] { "script classes1.py/Outer/foo" } },
		    new Object[] {
			    "$script classes1.py/Outer/Inner/foo:trampoline2",
			    new String[] { "script classes1.py/Outer/Inner/foo" } }
	 };
	 
	@Test
	public void testClasses1() throws WalaException, IllegalArgumentException, CancelException, IOException {
		PythonAnalysisEngine<?> engine = makeEngine("clazz/classes1.py");
		PropagationCallGraphBuilder builder = (PropagationCallGraphBuilder) engine.defaultCallGraphBuilder();
		CallGraph cg = builder.makeCallGraph(engine.getOptions(), new NullProgressMonitor());
		TestUtil.dumpCG(builder, cg);
		Assert.assertTrue(hasEdge(cg,"class1.py","Outer"));
		// FIXME
		Assert.assertTrue(hasEdge(cg,"class1.py","self_trampoline_foo(2)"));
		Assert.assertTrue(hasEdge(cg,"class1.py","Inner"));
		Assert.assertTrue(hasEdge(cg,"class1.py","Inner"));

	}

	 protected static final Object[][] assertionsClasses2 = new Object[][] {
		    new Object[] { ROOT, new String[] { "script classes2.py" } },
		    new Object[] {
		        "script classes2.py",
		        new String[] { "script classes2.py/fc",
		        		"script classes2.py/Ctor", 
		        		"$script classes2.py/Ctor/get:trampoline2" } },
		    new Object[] {
			        "script classes2.py/Ctor",
			        new String[] { "script classes2.py/Ctor/__init__" } },
		    new Object[] {
			        "$script classes2.py/Ctor/get:trampoline2",
			        new String[] { "script classes2.py/Ctor/get" } },
		    new Object[] {
			        "script classes2.py/Ctor/get",
			        new String[] { "script classes2.py/fa",
			        		 "script classes2.py/fb",
			        		 "script classes2.py/fc"} }
	 };
		    
	@Test
	public void testClasses2() throws ClassHierarchyException, IllegalArgumentException, CancelException, IOException {
		CallGraph CG = process("clazz/classes2.py");
		verifyGraphAssertions(CG, assertionsClasses2);
	}

	 protected static final Object[][] assertionsClasses3 = new Object[][] {
		    new Object[] { ROOT, new String[] { "script classes3.py" } },
		    new Object[] {
		        "script classes3.py",
		        new String[] { "script classes3.py/Ctor",
		        		"$script classes3.py/Ctor/get:trampoline2",
		        		"script classes3.py/SubCtor",
		        		"script classes3.py/OtherSubCtor"}
		    },
		    new Object[] {
			        "script classes3.py",
			        new String[] { "script classes3.py/Ctor",
			        		"$script classes3.py/Ctor/get:trampoline2"}
			},
		    new Object[] {
			        "script classes3.py/Ctor",
			        new String[] { "script classes3.py/Ctor/__init__" }
		    },
		    new Object[] {
			        "script classes3.py/SubCtor",
			        new String[] { "script classes3.py/SubCtor/__init__" }
		    },
		    new Object[] {
			        "script classes3.py/OtherSubCtor",
			        new String[] { "script classes3.py/OtherSubCtor/__init__" }
		    },
		    new Object[] {
			        "script classes3.py/SubCtor/__init__",
			        new String[] { "$script classes3.py/Ctor/__init__:trampoline4" }
		    },
		    new Object[] {
			        "$script classes3.py/Ctor/__init__:trampoline4",
			        new String[] { "script classes3.py/Ctor/__init__" }
		    },
		    new Object[] {
			        "script classes3.py/OtherSubCtor/__init__",
			        new String[] { "$script classes3.py/Ctor/__init__:trampoline4" }
		    }
	 };
	 
	@Test
	public void testClasses3() throws ClassHierarchyException, IllegalArgumentException, CancelException, IOException {
		PythonAnalysisEngine<?> engine = makeEngine("clazz/classes3.py");
		SSAPropagationCallGraphBuilder builder = (SSAPropagationCallGraphBuilder) engine.defaultCallGraphBuilder();
		CallGraph CG = builder.makeCallGraph(builder.getOptions());
		System.err.println(CG);
		CAstCallGraphUtil.AVOID_DUMP = false;
		CAstCallGraphUtil.dumpCG((SSAContextInterpreter)builder.getContextInterpreter(), builder.getPointerAnalysis(), CG);
		verifyGraphAssertions(CG, assertionsClasses3);
	}

}

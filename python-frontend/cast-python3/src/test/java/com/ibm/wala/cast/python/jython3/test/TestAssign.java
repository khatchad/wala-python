package com.ibm.wala.cast.python.jython3.test;

import com.ibm.wala.cast.python.client.PythonAnalysisEngine;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.propagation.PropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.WalaException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestAssign extends TestPythonCallGraphShape {

	@Test
	public void testAssign1() throws WalaException, IllegalArgumentException, CancelException, IOException {
		PythonAnalysisEngine<?> engine = makeEngine("assign/assign1.py");
		PropagationCallGraphBuilder builder = (PropagationCallGraphBuilder) engine.defaultCallGraphBuilder();
		CallGraph cg = builder.makeCallGraph(engine.getOptions(), new NullProgressMonitor());
		Assert.assertTrue(TestUtil.hasEdge(cg,"assign1.py","f"));
		Assert.assertTrue(TestUtil.hasEdge(cg,"assign1.py","g"));
		Assert.assertTrue(TestUtil.hasEdge(cg,"f","a"));
		Assert.assertTrue(TestUtil.hasEdge(cg,"g","a"));
	}

}

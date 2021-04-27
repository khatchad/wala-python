# WALA-Python
This is a Python frontend in WALA framework. This repository helps you parse Python source code into SSAs. Further analysis can be done based on this, e.g, pointer analysis, building callgraph or CFG, ...

# Build
The original is hard to build. I reconstructed it in `python-fronted` directory. Now we can build it easily with maven:
```bash
cd python-frontend
mvn clean package
```

*PS1: This project needs <https://github.com/juliandolby/jython3> to parse source code into AST, we already built it and put it at [here](https://github.com/Anemone95/wala-python/blob/master/python-frontend/cast-python3/libs/jython-dev.jar), but if you want to build it by your self, see [Build Jython3](#Build Jython3).*

*PS2: I only reconstructed a part of the original one, the other code is stored in `origin_deprecated` dir. And the original project needs a dependency called [IDE](https://github.com/wala/IDE). If you want to build this, I really recommend you to use [this version](https://github.com/Anemone95/IDE.git)*

## Build Jython3

```
git clone https://github.com/juliandolby/jython3
cd jython3
ant
```
Please copy `jython-dev.jar` to `cast-python3/libs/jython-dev.jar` after that.

# Usage

Build callgraph: 
```java
Class<?> j3 = Class.forName("com.ibm.wala.cast.python3.loader.Python3LoaderFactory");
PythonAnalysisEngine.setLoaderFactory((Class<? extends PythonLoaderFactory>) j3);
Class<?> i3 = Class.forName("com.ibm.wala.cast.python3.util.Python3Interpreter");
PythonInterpreter.setInterpreter((PythonInterpreter) i3.newInstance());

String filename = "demo.py";
Collection<Module> src = 
		Collections.singleton(new SourceURLModule(
        							new File(filename).toURL()));
PythonAnalysisEngine<Void> analysisEngine = new PythonAnalysisEngine<Void>() {
    @Override
    public Void performAnalysis(PropagationCallGraphBuilder builder) throws CancelException {
        assert false;
        return null;
    }
};
analysisEngine.setModuleFiles(src);
SSAPropagationCallGraphBuilder builder = (SSAPropagationCallGraphBuilder) analysisEngine.defaultCallGraphBuilder();
CallGraph callGraph = builder.makeCallGraph(builder.getOptions());

CAstCallGraphUtil.AVOID_DUMP = false;
CAstCallGraphUtil.dumpCG((SSAContextInterpreter) builder.getContextInterpreter(), builder.getPointerAnalysis(), callGraph);
DotUtil.dotify(callGraph, null, PDFTypeHierarchy.DOT_FILE, "callgraph.pdf", "dot");
```

More demos are written in [test cases](https://github.com/Anemone95/wala-python/tree/master/python-frontend/cast-python3/src/test/java/com/ibm/wala/cast/python/jython3/test).

# New Features

* Support `@classmethod`, `@staticmethod`
* Support modules (`import pkg.mod`, `from pkg.mod import *`)
* Support `*args` and `**kwargs`
* Fix some bugs, e.g., function summary of `str()` , NPE



# Limitation

* Decorator is not fully supported.
* System library is not fully supported, (1)`PyLibURLModule` waits to be realized, (2) Lack many XML summaries 


# Original Readme

## [Ariadne](https://wala.github.io/ariadne/)

This is the top level repository for Ariadne code.  More information on using the Ariadne tools can be found [here](https://wala.github.io/ariadne/).  This repository is code to analyze machine learning code with WALA.  Currently, the code consists of the analysis of Python (com.ibm.wala.cast.python), analysis focused on machine learning in Python (com.ibm.wala.cast.python.ml), support for using the analysis via J2EE Websockets (com.ibm.wala.cast.python.ml.j2ee) and their associated test projects.

Since it is built using WALA, you need to have WALA on your system to use it:

* make sure Apache Maven and the Android SDK tools are installed, including build tools 26.0.2
* set ANDROID_HOME to the location of the Android SDK
* make sure JAVA_HOME points to a Java 8 JDK
* clone WALA with `git clone https://github.com/wala/WALA`
* in the cloned directory, `mvn clean install -DskipTests`

Currently, the Python analysis code is being developed in Eclipse:

* import the WALA projects into Eclipse
* clone this repository, and import its projects into Eclipse
* run `TestCalls` in the `com.ibm.wala.cast.python.test` projects to test


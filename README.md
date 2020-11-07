# [Ariadne](https://wala.github.io/ariadne/)

This is the top level repository for Ariadne code.  More information on using the Ariadne tools can be found [here](https://wala.github.io/ariadne/).  This repository is code to analyze machine learning code with WALA.  Currently, the code consists of the analysis of Python (com.ibm.wala.cast.python), analysis focused on machine learning in Python (com.ibm.wala.cast.python.ml), support for using the analysis via J2EE Websockets (com.ibm.wala.cast.python.ml.j2ee) and their associated test projects.

# Build

1. Build WALA 1.5.6-SNAPSHOT

   需要禁用GoogleJavaFormat

   ```
   git clone https://github.com/wala/WALA.git
   gradle publishToMavenLocal # mvn is deprecated
   ```

2. Build IDE

   ```
   git clone https://github.com/Anemone95/IDE.git
   gradle publishToMavenLocal # mvn is deprecated
   ```

3. Build Ariadne

   ```
   gradle publishToMavenLocal # mvn is deprecated
   ```

   Run `TestCalls` in the `com.ibm.wala.cast.python.test` projects to test
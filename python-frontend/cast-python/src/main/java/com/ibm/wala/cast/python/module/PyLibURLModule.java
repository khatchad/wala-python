package com.ibm.wala.cast.python.module;

import com.ibm.wala.cast.python.global.SystemPath;
import com.ibm.wala.classLoader.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.io.File;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class PyLibURLModule extends SourceURLModule{
    public PyLibURLModule(File file) throws MalformedURLException {
        super(file.getAbsoluteFile().toURI().toURL());
        if (file.exists()) {
            SystemPath.getInstance().addLibPath(new File(file.getAbsoluteFile().getParent()).toPath());
        } else {
            System.err.println("[ERROR] " + file + " not exist");
        }
    }

    public String getName() {
        return getURL().toString();
    }
}

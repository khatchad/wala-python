package com.ibm.wala.cast.python.types;

import com.ibm.wala.cast.tree.CAstType;

import java.util.Collection;
import java.util.HashSet;

public class PythonCAstTypes {

    public static final CAstType dynamicAnnotation = new CAstType() {
        @Override
        public String getName() {
            return "DYNAMIC_ANNOTATION";
        }

        @Override
        public Collection<CAstType> getSupertypes() {
            return new HashSet<>();
        }
    };
}

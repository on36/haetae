package com.on36.haetae.hotswap.scan;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ClassPathAnnotationScanner {
	
    // Annotation name to search for
    String annotation;

    // scanner to search path
    static Scanner scanner = new ClassPathScanner();

    /**
     * Run the scan - search path for files containing annotation.
     *
     * @param classLoader classloader to resolve path
     * @param path        path to scan {@link org.hotswap.agent.util.scanner.Scanner#scan(ClassLoader, String, ScannerVisitor)}
     * @return list of class names containing the annotation
     * @throws IOException scan exception.
     */
    public static List<String> scan(ClassLoader classLoader, String path) throws IOException {
        final List<String> files = new LinkedList<String>();
        scanner.scan(classLoader, path, new ScannerVisitor() {
            @Override
            public void visit(String filePath) {
                System.out.println(filePath);
            }
        });
        return files;
    }

}
package com.wanna.springioc;

import com.wanna.springioc.config.AnnotationConfigSpringContext;

public class MainApplication {
    public static void main(String[] args) {
        String pack = "com.wanna.springioc.entity";
        AnnotationConfigSpringContext context = new AnnotationConfigSpringContext(pack);
        System.out.println(context.getBean("userbean"));
    }
}

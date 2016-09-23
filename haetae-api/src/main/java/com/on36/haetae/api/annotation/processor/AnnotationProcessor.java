package com.on36.haetae.api.annotation.processor;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.on36.haetae.api.Context;
import com.on36.haetae.api.annotation.ApiDoc;

/**
 * @author zhanghr
 * @date 2016年3月16日
 */
@SupportedAnnotationTypes({ "com.on36.haetae.api.annotation.Api" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

	// 元素操作的辅助类
	Elements elementUtils;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		elementUtils = processingEnv.getElementUtils();
	}

	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment env) {
		Messager messager = processingEnv.getMessager();
		for (TypeElement te : annotations) {
			for (Element e : env.getElementsAnnotatedWith(te)) {
				if (!e.getModifiers().contains(Modifier.PUBLIC)) {
					messager.printMessage(Diagnostic.Kind.ERROR,
							"The method that be defined by the annotation @Api must be public",
							e);
				}
				ExecutableElement method = (ExecutableElement) e;
				List<? extends VariableElement> children = method
						.getParameters();
				if (children == null || children.size() != 1 || !children.get(0)
						.asType().toString().equals(Context.class.getName()))
					messager.printMessage(Diagnostic.Kind.ERROR,
							"The method that be defined by the annotation @Api only has one parameter "
									+ Context.class.getName(),
							e);
				ApiDoc apiDoc = e.getAnnotation(ApiDoc.class);
				if (apiDoc == null)
					messager.printMessage(Diagnostic.Kind.WARNING,
							"The annotation @ApiDoc must be defined at the method that be defined by the annotation @Api",
							e);
			}
		}
		return false;
	}
}

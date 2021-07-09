package net.fabricmc.tinyremapper.extension.mixin.factory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.api.Classpath;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationVisitorCommonDataHolder;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor.Interface;
import net.fabricmc.tinyremapper.extension.mixin.annotation.MixinAnnotationVisitor;

public class ClassAnnotationVisitorFactory {
	private final AnnotationVisitorCommonDataHolder data;

	public ClassAnnotationVisitorFactory(Remapper remapper, Classpath classpath,
			AnnotationVisitor delegate, IMappingHolder mapping, String className) {
		this.data = new AnnotationVisitorCommonDataHolder(
				remapper, classpath, delegate, mapping, AnnotationType.CLASS,
				className, null, null);
	}

	public AnnotationVisitor mixin(AtomicBoolean remapOut, List<String> targetsOut) {
		return new MixinAnnotationVisitor(data, remapOut, targetsOut);
	}

	public AnnotationVisitor _implements(List<Interface> interfacesOut) {
		return new ImplementsAnnotationVisitor(data, interfacesOut);
	}
}

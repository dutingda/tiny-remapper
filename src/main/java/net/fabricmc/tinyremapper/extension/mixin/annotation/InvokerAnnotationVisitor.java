package net.fabricmc.tinyremapper.extension.mixin.annotation;

import java.util.List;
import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;

import net.fabricmc.tinyremapper.extension.mixin.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.data.Annotation;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationElement;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.BoolRemapAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationVisitorCommonDataHolder;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.AnnotationVisitorCommonUtil;
import net.fabricmc.tinyremapper.extension.mixin.common.Logger;

/**
 * Depends on {@code value = ""} or not, remap the soft-target of {@code @Invoker}
 * or emits the mapping for hard-target. It contains 2 passes. The first pass read
 * {@code remap} and in the second pass if {@code remap = true} and {@code value = ""},
 * then emits the mapping; or if {@code remap = true} and {@code value != ""}, then remap
 * {@code value}; otherwise do nothing.
 */
public class InvokerAnnotationVisitor extends BoolRemapAnnotationVisitor {
	private final AnnotationVisitorCommonDataHolder data;
	private final List<String> targets;
	private String value;

	public InvokerAnnotationVisitor(AnnotationVisitorCommonDataHolder data, boolean remap, List<String> targets) {
		super(Annotation.INVOKER.get(), remap, data.delegate);
		this.data = Objects.requireNonNull(data);
		this.targets = Objects.requireNonNull(targets);
		this.value = "";
	}

	@Override
	public AnnotationVisitor next(AnnotationVisitor delegate, boolean remap) {
		return new AnnotationVisitor(Constant.ASM_VERSION, data.delegate) {
			@Override
			public void visit(String name, Object value) {
				if (name.equals(AnnotationElement.VALUE.get())) {
					InvokerAnnotationVisitor.this.value = Objects.requireNonNull((String) value);

					String srcName = InvokerAnnotationVisitor.this.value;
					String srcDesc = data.memberDescriptor;

					String dstName = AnnotationVisitorCommonUtil.remapMember(
							data.remapper, AnnotationType.METHOD,
							InvokerAnnotationVisitor.this.targets, srcName, srcDesc);

					if (srcName.equals(dstName)) {
						Logger.warn("@Invoker", InvokerAnnotationVisitor.this.targets, data.className, data.memberName);
					}

					value = dstName;
				}

				super.visit(name, value);
			}

			@Override
			public void visitEnd() {
				if (remap && InvokerAnnotationVisitor.this.value.isEmpty()) {
					// only remap hard-target if no value is specify
					String prefix;

					if (data.memberName.startsWith("call")) {
						prefix = "call";
					} else if (data.memberName.startsWith("invoke")) {
						prefix = "invoke";
					} else {
						throw new RuntimeException(data.memberName + " does not start with call or invoke.");
					}

					String srcName = AnnotationVisitorCommonUtil.removeCamelPrefix(prefix, data.memberName);
					String srcDesc = data.memberDescriptor;

					String dstName = AnnotationVisitorCommonUtil.remapMember(
							data.remapper, AnnotationType.METHOD,
							InvokerAnnotationVisitor.this.targets, srcName, srcDesc);

					if (srcName.equals(dstName)) {
						Logger.warn("@Invoker", InvokerAnnotationVisitor.this.targets, data.className, data.memberName);
					} else {
						srcName = data.memberName;
						dstName = AnnotationVisitorCommonUtil.addCamelPrefix(prefix, dstName);

						AnnotationVisitorCommonUtil.emitMapping(
								data.remapper, AnnotationType.METHOD, data.mapping,
								data.className, srcName, srcDesc, dstName);
					}
				}

				super.visitEnd();
			}
		};
	}
}

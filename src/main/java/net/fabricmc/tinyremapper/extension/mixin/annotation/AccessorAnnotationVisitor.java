package net.fabricmc.tinyremapper.extension.mixin.annotation;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.AnnotationVisitor;

import net.fabricmc.tinyremapper.extension.mixin.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.data.Annotation;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationElement;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.BoolRemapAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationVisitorCommonDataHolder;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.AnnotationVisitorCommonUtil;
import net.fabricmc.tinyremapper.extension.mixin.util.Logger;

/**
 * Depends on {@code value = ""} or not, remap the soft-target of {@code @Accessor}
 * or emits the mapping for hard-target. It contains 2 passes. The first pass read
 * {@code remap} and in the second pass if {@code remap = true} and {@code value = ""},
 * then emits the mapping; or if {@code remap = true} and {@code value != ""}, then remap
 * {@code value}; otherwise do nothing.
 */
public class AccessorAnnotationVisitor extends BoolRemapAnnotationVisitor {
	private final AnnotationVisitorCommonDataHolder data;
	private final List<String> targets;
	private final String fieldDesc;
	private String value;

	private static final Pattern GETTER_PATTERN = Pattern.compile("(?<=\\(\\)).*");
	private static final Pattern SETTER_PATTERN = Pattern.compile("(?<=\\().*(?=\\)V)");

	public AccessorAnnotationVisitor(AnnotationVisitorCommonDataHolder data, boolean remap, List<String> targets) {
		super(Annotation.ACCESSOR.get(), remap, data.delegate);
		this.data = Objects.requireNonNull(data);
		this.targets = Objects.requireNonNull(targets);
		this.value = "";

		Matcher getterMatcher = GETTER_PATTERN.matcher(data.memberDescriptor);
		Matcher setterMatcher = SETTER_PATTERN.matcher(data.memberDescriptor);

		if (getterMatcher.find()) {
			this.fieldDesc = getterMatcher.group();
		} else if (setterMatcher.find()) {
			this.fieldDesc = setterMatcher.group();
		} else {
			throw new RuntimeException(data.memberDescriptor + " is not getter or setter");
		}
	}

	@Override
	public AnnotationVisitor next(AnnotationVisitor delegate, boolean remap) {
		return new AnnotationVisitor(Constant.ASM_VERSION, data.delegate) {
			@Override
			public void visit(String name, Object value) {
				if (name.equals(AnnotationElement.VALUE.get())) {
					AccessorAnnotationVisitor.this.value = Objects.requireNonNull((String) value);

					String srcName = AccessorAnnotationVisitor.this.value;
					String srcDesc = AccessorAnnotationVisitor.this.fieldDesc;

					String dstName = AnnotationVisitorCommonUtil.remapMember(
							data.remapper, AnnotationType.FIELD,
							AccessorAnnotationVisitor.this.targets, srcName, srcDesc);

					if (srcName.equals(dstName)) {
						Logger.warn("@Accessor", AccessorAnnotationVisitor.this.targets, data.className, data.memberName);
					}

					value = dstName;
				}

				super.visit(name, value);
			}

			@Override
			public void visitEnd() {
				if (remap && AccessorAnnotationVisitor.this.value.isEmpty()) {
					// only remap hard-target if no value is specify
					String prefix;

					if (data.memberName.startsWith("get")) {
						prefix = "get";
					} else if (data.memberName.startsWith("set")) {
						prefix = "set";
					} else if (data.memberName.startsWith("is")) {
						prefix = "is";
					} else {
						throw new RuntimeException(data.memberName + " does not start with get, set or is.");
					}

					String srcName = AnnotationVisitorCommonUtil.removeCamelPrefix(prefix, data.memberName);
					String srcDesc = AccessorAnnotationVisitor.this.fieldDesc;

					String dstName = AnnotationVisitorCommonUtil.remapMember(
							data.remapper, AnnotationType.FIELD,
							AccessorAnnotationVisitor.this.targets, srcName, srcDesc);

					if (srcName.equals(dstName)) {
						Logger.warn("@Accessor", AccessorAnnotationVisitor.this.targets, data.className, data.memberName);
					} else {
						srcName = data.memberName;
						srcDesc = data.memberDescriptor;
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

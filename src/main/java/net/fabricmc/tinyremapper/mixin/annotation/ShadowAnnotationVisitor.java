package net.fabricmc.tinyremapper.mixin.annotation;

import java.util.List;
import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;

import net.fabricmc.tinyremapper.mixin.Constant;
import net.fabricmc.tinyremapper.mixin.Constant.Annotation;
import net.fabricmc.tinyremapper.mixin.Constant.AnnotationElement;
import net.fabricmc.tinyremapper.mixin.annotation.common.BoolRemapAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.annotation.factory.DataHolder;
import net.fabricmc.tinyremapper.mixin.util.AnnotationVisitorUtil;
import net.fabricmc.tinyremapper.mixin.util.Logger;

/**
 * Emits the mapping of hard-target of {@code @Shadow}, it reads {@code remap} and
 * {@code prefix}, and emit the mapping if {@code remap = true} at the end. The
 * emitted mapping is used by another tiny-remapper to remap the hard-target.
 */
public class ShadowAnnotationVisitor extends BoolRemapAnnotationVisitor {
	private final DataHolder data;
	private final List<String> targets;
	private String prefix;

	public ShadowAnnotationVisitor(DataHolder data, boolean remap, List<String> targets) {
		super(Annotation.SHADOW.get(), remap, data.delegate);
		this.data = Objects.requireNonNull(data);
		this.targets = Objects.requireNonNull(targets);
		this.prefix = "shadow$";
	}

	/**
	 * This is valid because {@code visit} happens before {@code visitEnd},
	 * which is the function invokes {@code next}.
	 */
	@Override
	public void visit(String name, Object value) {
		if (name.equals(AnnotationElement.PREFIX.get())) {
			prefix = Objects.requireNonNull((String) value);
		}

		super.visit(name, value);
	}

	@Override
	public AnnotationVisitor next(AnnotationVisitor delegate, boolean remap) {
		return new AnnotationVisitor(Constant.ASM_VERSION, delegate) {
			@Override
			public void visitEnd() {
				if (remap) {
					// Generate mappings
					String srcName = data.memberName.startsWith(prefix)
							? data.memberName.substring(prefix.length()) : data.memberName;
					String srcDesc = data.memberDescriptor;

					String dstName = AnnotationVisitorUtil.remapMember(
							data.remapper, data.type, ShadowAnnotationVisitor.this.targets,
							srcName, srcDesc);

					if (srcName.equals(dstName)) {
						Logger.warn("@Shadow", ShadowAnnotationVisitor.this.targets, data.className, data.memberName);
					} else {
						srcName = data.memberName;
						dstName = prefix + dstName;
						AnnotationVisitorUtil.emitMapping(
								data.remapper, data.type, data.mapping,
								data.className, srcName, srcDesc, dstName);
					}
				}

				super.visitEnd();
			}
		};
	}
}

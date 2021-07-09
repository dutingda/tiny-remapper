package net.fabricmc.tinyremapper.extension.mixin.annotation;

import java.util.List;
import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;

import net.fabricmc.tinyremapper.extension.mixin.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.data.Annotation;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.BoolRemapAnnotationVisitor;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationVisitorCommonDataHolder;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.AnnotationVisitorCommonUtil;
import net.fabricmc.tinyremapper.extension.mixin.common.Logger;

/**
 * Emits the mapping of hard-target of {@code @Overwrite}, it reads {@code remap},
 * and emit the mapping if {@code remap = true} at the end. The
 * emitted mapping is used by another tiny-remapper to remap the hard-target.
 */
public class OverwriteAnnotationVisitor extends BoolRemapAnnotationVisitor {
	private final AnnotationVisitorCommonDataHolder data;
	private final List<String> targets;

	public OverwriteAnnotationVisitor(AnnotationVisitorCommonDataHolder data, boolean remap, List<String> targets) {
		super(Annotation.OVERWRITE.get(), remap, data.delegate);
		this.data = Objects.requireNonNull(data);
		this.targets = Objects.requireNonNull(targets);
	}

	@Override
	public AnnotationVisitor next(AnnotationVisitor delegate, boolean remap) {
		return new AnnotationVisitor(Constant.ASM_VERSION, delegate) {
			@Override
			public void visitEnd() {
				if (remap) {
					// Generate mappings
					String srcName = data.memberName;
					String srcDesc = data.memberDescriptor;

					String dstName = AnnotationVisitorCommonUtil.remapMember(
							data.remapper, AnnotationType.METHOD,
							OverwriteAnnotationVisitor.this.targets, srcName, srcDesc);

					if (srcName.equals(dstName)) {
						Logger.warn("@Overwrite", OverwriteAnnotationVisitor.this.targets, data.className, data.memberName);
					} else {
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

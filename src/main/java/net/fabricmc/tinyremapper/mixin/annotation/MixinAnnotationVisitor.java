package net.fabricmc.tinyremapper.mixin.annotation;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import net.fabricmc.tinyremapper.mixin.Constant;
import net.fabricmc.tinyremapper.mixin.Constant.Annotation;
import net.fabricmc.tinyremapper.mixin.Constant.AnnotationElement;
import net.fabricmc.tinyremapper.mixin.annotation.common.BoolRemapAnnotationVisitor;
import net.fabricmc.tinyremapper.mixin.annotation.factory.DataHolder;
import net.fabricmc.tinyremapper.mixin.util.Logger;

/**
 * Remaps the soft-target of {@code @Mixin}, it contains 2 passes. The first pass read
 * {@code remap} and the second pass remaps {@code targets} if @{@code remap = true}.
 * Notice that {@code value} is not remapped as it will be handled by tiny-remapper.
 */
public class MixinAnnotationVisitor extends BoolRemapAnnotationVisitor {
	private final DataHolder data;
	private final AtomicBoolean remap;
	private final List<String> targets;

	public MixinAnnotationVisitor(DataHolder data, AtomicBoolean remapOut, List<String> targetsOut) {
		super(Annotation.MIXIN.get(), true, data.delegate);
		this.data = Objects.requireNonNull(data);
		this.remap = Objects.requireNonNull(remapOut);
		this.targets = Objects.requireNonNull(targetsOut);
	}

	@Override
	public AnnotationVisitor next(AnnotationVisitor delegate, boolean remap) {
		this.remap.set(remap);		// output remap variable

		return new AnnotationVisitor(Constant.ASM_VERSION, delegate) {
			@Override
			public AnnotationVisitor visitArray(String name) {
				AnnotationVisitor visitor = super.visitArray(name);

				if (name.equals(AnnotationElement.TARGETS.get())) {
					return new AnnotationVisitor(Constant.ASM_VERSION, visitor) {
						@Override
						public void visit(String name, Object value) {
							String srcName = Objects.requireNonNull((String) value).replace(".", "/");
							String dstName = srcName;

							MixinAnnotationVisitor.this.targets.add(srcName);

							if (remap) {
								dstName = data.remapper.map(srcName);

								if (srcName.equals(dstName)) {
									Logger.warn("@Mixin", srcName, data.className);
								}
							}

							value = dstName;
							super.visit(name, value);
						}
					};
				} else if (name.equals(AnnotationElement.VALUE.get())) {
					return new AnnotationVisitor(Constant.ASM_VERSION, visitor) {
						@Override
						public void visit(String name, Object value) {
							Type srcType = Objects.requireNonNull((Type) value);
							MixinAnnotationVisitor.this.targets.add(srcType.getInternalName());

							super.visit(name, value);
						}
					};
				} else {
					return visitor;
				}
			}
		};
	}
}

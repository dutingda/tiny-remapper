package net.fabricmc.tinyremapper.extension.mixin.annotation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;

import net.fabricmc.tinyremapper.extension.mixin.data.Constant;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationElement;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationType;
import net.fabricmc.tinyremapper.extension.mixin.data.AnnotationVisitorCommonDataHolder;
import net.fabricmc.tinyremapper.extension.mixin.annotation.common.AnnotationVisitorCommonUtil;
import net.fabricmc.tinyremapper.extension.mixin.data.IMappingHolder;
import net.fabricmc.tinyremapper.extension.mixin.util.Logger;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor.Interface;
import net.fabricmc.tinyremapper.extension.mixin.annotation.ImplementsAnnotationVisitor.Interface.Remap;

public class ImplementsAnnotationVisitor extends AnnotationVisitor {
	public static class Interface {
		public enum Remap {
			ALL, FORCE, NONE, ONLY_PREFIX;
		}

		private String target;
		private String prefix;
		private Remap remap;

		private String target() {
			return target;
		}

		private String prefix() {
			return prefix;
		}

		private Remap remap() {
			return remap;
		}

		void setTarget(String target) {
			this.target = target;
		}

		void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		void setRemap(Remap remap) {
			this.remap = remap;
		}

		@Override
		public String toString() {
			return "Interface{"
					+ "target='" + target + '\''
					+ ", prefix='" + prefix + '\''
					+ ", remap=" + remap
					+ '}';
		}
	}

	private final List<Interface> interfaces;

	public ImplementsAnnotationVisitor(AnnotationVisitorCommonDataHolder data, List<Interface> interfacesOut) {
		super(Constant.ASM_VERSION, data.delegate);
		this.interfaces = Objects.requireNonNull(interfacesOut);
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		AnnotationVisitor visitor = super.visitArray(name);

		if (name.equals(AnnotationElement.VALUE.get())) {
			return new AnnotationVisitor(Constant.ASM_VERSION, visitor) {
				@Override
				public AnnotationVisitor visitAnnotation(String name, String descriptor) {
					Interface _interface = new Interface();
					interfaces.add(_interface);
					return new InterfaceAnnotationVisitor(super.visitAnnotation(name, descriptor), _interface);
				}
			};
		} else {
			return visitor;
		}
	}

	public static void MethodVisitorUtil(Remapper remapper, List<Interface> interfaces, IMappingHolder mapping,
										String owner, String methodName, String methodDesc) {
		for (Interface _interface : interfaces) {
			Remap remap = _interface.remap();
			String target = _interface.target();
			String prefix = _interface.prefix();

			if (methodName.startsWith(prefix)) {
				// remap prefix member

				if (remap.equals(Remap.ONLY_PREFIX) || remap.equals(Remap.ALL) || remap.equals(Remap.FORCE)) {
					String srcName = methodName.substring(prefix.length());
					String srcDesc = methodDesc;

					String dstName = AnnotationVisitorCommonUtil.remapMember(
							remapper, AnnotationType.METHOD, Collections.singletonList(target),
							srcName, srcDesc);

					if (srcName.equals(dstName)) {
						Logger.warn("@Implements", Collections.singletonList(target), owner, methodName);
					} else {
						srcName = methodName;
						dstName = prefix + dstName;

						AnnotationVisitorCommonUtil.emitMapping(
								remapper, AnnotationType.METHOD, mapping,
								owner, srcName, srcDesc, dstName);
					}
				}
			} else {
				// remap non-prefix member

				if (remap.equals(Remap.ALL) || remap.equals(Remap.FORCE)) {
					// TODO: actually verify the interface has the method

					String srcName = methodName;
					String srcDesc = methodDesc;

					String dstName = AnnotationVisitorCommonUtil.remapMember(
							remapper, AnnotationType.METHOD, Collections.singletonList(target),
							srcName, srcDesc);

					if (srcName.equals(dstName)) {
						if (remap.equals(Remap.FORCE)) {
							Logger.warn("@Implements", Collections.singletonList(target), owner, methodName);
						}
					} else {
						AnnotationVisitorCommonUtil.emitMapping(
								remapper, AnnotationType.METHOD, mapping,
								owner, srcName, srcDesc, dstName);
					}
				}
			}
		}
	}
}

class InterfaceAnnotationVisitor extends AnnotationVisitor {
	private final Interface _interface;

	InterfaceAnnotationVisitor(AnnotationVisitor delegate, Interface _interfaceOut) {
		super(Constant.ASM_VERSION, delegate);
		this._interface = Objects.requireNonNull(_interfaceOut);

		this._interface.setRemap(Remap.ALL);	// default value
	}

	@Override
	public void visit(String name, Object value) {
		if (name.equals(AnnotationElement.IFACE.get())) {
			Type target = Objects.requireNonNull((Type) value);
			this._interface.setTarget(target.getInternalName());
		} else if (name.equals(AnnotationElement.PREFIX.get())) {
			String prefix = Objects.requireNonNull((String) value);
			this._interface.setPrefix(prefix);
		}

		super.visit(name, value);
	}

	@Override
	public void visitEnum(String name, String descriptor, String value) {
		if (name.equals(AnnotationElement.REMAP.get())) {
			if (!descriptor.equals("Lorg/spongepowered/asm/mixin/Interface$Remap;")) {
				throw new RuntimeException("Incorrect enum type of Interface.Remap " + descriptor);
			}

			for (Remap candidate : Remap.values()) {
				if (candidate.name().equals(value)) {
					this._interface.setRemap(candidate);
					break;
				}
			}
		}

		super.visitEnum(name, descriptor, value);
	}
}

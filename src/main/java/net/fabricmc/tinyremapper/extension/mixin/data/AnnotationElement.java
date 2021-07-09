package net.fabricmc.tinyremapper.extension.mixin.data;

public enum AnnotationElement {
	VALUE("value"),
	REMAP("remap"),
	TARGETS("targets"),
	PREFIX("prefix"),
	IFACE("iface");

	private final String literal;

	AnnotationElement(String literal) {
		this.literal = literal;
	}

	public String get() {
		return literal;
	}
}

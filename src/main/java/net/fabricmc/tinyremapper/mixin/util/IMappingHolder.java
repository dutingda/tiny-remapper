package net.fabricmc.tinyremapper.mixin.util;

import net.fabricmc.tinyremapper.IMappingProvider;

public interface IMappingHolder extends IMappingProvider {
	void putMethod(String owner, String srcName, String desc, String dstName);
	void putField(String owner, String srcName, String desc, String dstName);
}

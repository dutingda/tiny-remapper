package net.fabricmc.tinyremapper.extension.mixin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TestUtil;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

public class MixinAnnotationProcessorTest {
	private static final String MIXIN_INPUT_PATH = "/integration/mixin/input.jar";
	private static final String MIXIN_MAPPING_PATH = "mapping/yarn-1.17+build.13-tiny.gz";
	private static final String MIXIN_CLASS_PATH = "/integration/mixin/minecraft.jar";
	@TempDir
	static Path folder;

	@BeforeAll
	public static void setup() throws IOException {
		TestUtil.folder = folder;

		TestUtil.copyFile(MixinAnnotationProcessorTest.class, MIXIN_INPUT_PATH);
		TestUtil.copyFile(MixinAnnotationProcessorTest.class, MIXIN_CLASS_PATH);
	}

	@Test
	public void mixinRemap1() throws IOException {
		TinyRemapper remapper;
		MixinAnnotationProcessor ap = new MixinAnnotationProcessor();
		TinyRemapper remapperOut;
		Path input = TestUtil.input(MIXIN_INPUT_PATH);
		Path inter = folder.resolve(MIXIN_INPUT_PATH.replace("input", "inter").substring(1));
		Path output = TestUtil.output(MIXIN_INPUT_PATH);
		Path minecraftPath = TestUtil.input(MIXIN_CLASS_PATH);
		Path[] classPath = new Path[]{minecraftPath};

		try (BufferedReader reader = getMappingReader()) {
			remapper = TinyRemapper.newRemapper()
					.withMappings(TinyUtils.createTinyMappingProvider(reader, "named", "intermediary"))
					.extraPreVisitor(ap::getPreVisitor)
					.build();

			//remapper.readClassPathAsync(Paths.get("/home/m/.gradle/caches/fabric-loom/minecraft-1.17-intermediary-net.fabricmc.yarn-1.17+build.6-v2.jar"));
			try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(inter).build()) {
				remapper.readInputs(input);
				remapper.readClassPath(classPath);
				remapper.apply(outputConsumer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				remapper.finish();
			}
		}

		remapperOut = TinyRemapper.newRemapper()
				.withMappings(ap.getMapping())
				.build();

		try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(output).build()) {
			remapperOut.readInputs(inter);
			remapperOut.apply(outputConsumer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			remapperOut.finish();
		}
	}

	private static BufferedReader getMappingReader() throws IOException {
		InputStream is = getInputStream(MIXIN_MAPPING_PATH);

		return new BufferedReader(new InputStreamReader(new GZIPInputStream(is), StandardCharsets.UTF_8));
	}

	private static InputStream getInputStream(String file) {
		return MixinAnnotationProcessorTest.class.getClassLoader().getResourceAsStream(file);
	}
}

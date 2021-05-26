package net.fabricmc.tinyremapper;

import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;


import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.InputTag;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.objectweb.asm.commons.Remapper;


class JarRemapper {
    private final List<IMappingProvider> mappingProviders = new ArrayList<>();
    private final Set<Path> classPath = new HashSet<>();
    private final List<RemapData> remapData = new ArrayList<>();

    public void addMappings(IMappingProvider mappingProvider) {
        mappingProviders.add(mappingProvider);
    }

    public void addToClasspath(Path... paths) {
        classPath.addAll(Arrays.asList(paths));
    }

    public RemapData scheduleRemap(Path input, Path output) {
        RemapData data = new RemapData(input, output);
        remapData.add(data);
        return data;
    }

    public void remap() throws IOException {
        TinyRemapper.Builder remapperBuilder = TinyRemapper.newRemapper();
        mappingProviders.forEach(remapperBuilder::withMappings);


        TinyRemapper remapper = remapperBuilder.build();

        Path[] remapClasspath = classPath.stream()
                .filter(path ->
                        remapData.stream().noneMatch(remapData -> remapData.input.equals(path))
                )
                .toArray(Path[]::new);

        remapper.readClassPathAsync(remapClasspath);

        for (RemapData data : remapData) {
            InputTag tag = remapper.createInputTag();
            data.tag = tag;
            remapper.readInputsAsync(tag, data.input);
        }

        List<OutputConsumerPath> outputConsumers = new ArrayList<>();

        for (RemapData data : remapData) {
            OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(data.output).build();
            outputConsumers.add(outputConsumer);

            outputConsumer.addNonClassFiles(data.input);

            remapper.apply(outputConsumer, data.tag);
        }

        remapper.finish();

        for (OutputConsumerPath outputConsumer : outputConsumers) {
            outputConsumer.close();
        }

    }

    public static class RemapData {
        public final Path input;
        public final Path output;

        private InputTag tag;

        public RemapData(Path input, Path output) {
            this.input = input;
            this.output = output;
        }
    }
}


public class RemappingTest {

    @Test
    public void processRemap () {

    }

}

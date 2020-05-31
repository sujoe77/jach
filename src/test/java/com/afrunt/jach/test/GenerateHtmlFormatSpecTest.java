package com.afrunt.jach.test;

import com.afrunt.jach.ACH;
import com.afrunt.jach.metadata.ACHBeanMetadata;
import com.afrunt.jach.metadata.ACHMetadata;
import java8.util.function.Consumer;
import java8.util.function.Function;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrii Frunt
 */
public class GenerateHtmlFormatSpecTest {

    @Test
    public void testGenerateSpec() {
        NACHASpecRenderer renderer = new NACHASpecRenderer();
        StreamSupport.stream(renderer.renderSingleSpecs().entrySet())
                .forEach(new Consumer<Map.Entry<String, String>>() {
                    @Override
                    public void accept(Map.Entry<String, String> entry) {
                        storeToFile(entry.getKey(), entry.getValue());
                    }
                });
        storeToFile("nacha-spec.htm", renderer.renderFullSpec());
    }

    @Test
    public void testIdenticalPatterns() {
        ACHMetadata metadata = new ACH().getMetadata();
        Set<Map.Entry<String, List<ACHBeanMetadata>>> entrySet = StreamSupport.stream(metadata.getACHBeansMetadata())
                .collect(Collectors.groupingBy(new Function<ACHBeanMetadata, String>() {
                    @Override
                    public String apply(ACHBeanMetadata achBeanMetadata) {
                        return achBeanMetadata.getPattern();
                    }
                })).entrySet();

        StreamSupport.stream(entrySet).filter(new Predicate<Map.Entry<String, List<ACHBeanMetadata>>>() {
            @Override
            public boolean test(Map.Entry<String, List<ACHBeanMetadata>> entry) {
                return entry.getValue().size() > 1;
            }
        }).forEach(new Consumer<Map.Entry<String, List<ACHBeanMetadata>>>() {
            @Override
            public void accept(Map.Entry<String, List<ACHBeanMetadata>> e) {
                String message = StreamSupport.stream(e.getValue())
                        .map(new Function<ACHBeanMetadata, String>() {
                            @Override
                            public String apply(ACHBeanMetadata achBeanMetadata) {
                                return achBeanMetadata.getSimpleTypeName();
                            }
                        }).collect(Collectors.joining(", "));
                System.out.println(String.format("%s: %s", e.getKey(), message));
            }
        });
    }

    private void storeToFile(String fileName, String contents) {
        try {
            Path achDirPath = Paths.get(System.getProperty("user.dir")).resolve("target/ach");
            Files.createDirectories(achDirPath);
            Path filePath = achDirPath.resolve(fileName);
            PrintWriter writer = new PrintWriter(new FileOutputStream(filePath.toFile()));
            writer.write(contents);
            writer.flush();
            writer.close();
            System.out.println("File " + filePath.toAbsolutePath() + " stored successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

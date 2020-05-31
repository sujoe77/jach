package com.afrunt.jach.test;

import com.afrunt.jach.ACH;
import com.afrunt.jach.metadata.ACHBeanMetadata;
import com.afrunt.jach.metadata.ACHFieldMetadata;
import java8.util.Comparators;
import java8.util.function.BiFunction;
import java8.util.function.BinaryOperator;
import java8.util.function.Consumer;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.IntStreams;
import java8.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.afrunt.jach.logic.StringUtil.letter;

/**
 * @author Andrii Frunt
 */
public class NACHASpecRenderer {
    private ACH ach = new ACH();
    private List<String> notes = new ArrayList<>();

    public String renderFullSpec() {
        return StreamSupport.stream(ach.getMetadata().getACHBeansMetadata())
                .sorted(Comparators.comparing(new Function<ACHBeanMetadata, String>() {
                    @Override
                    public String apply(ACHBeanMetadata achBeanMetadata) {
                        return achBeanMetadata.getRecordTypeCode();
                    }
                }))
                .peek(new Consumer<ACHBeanMetadata>() {
                    @Override
                    public void accept(ACHBeanMetadata achBeanMetadata) {
                        notes = new ArrayList<>();
                    }
                })
                .map(new Function<ACHBeanMetadata, String>() {
                    @Override
                    public String apply(ACHBeanMetadata achBeanMetadata) {
                        return generateSpecBody(achBeanMetadata);
                    }
                }).reduce(new BinaryOperator<String>() {
                    @Override
                    public String apply(String s, String s2) {
                        return s + s2;
                    }
                }).map(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return generateHtmlBeanSpec(s);
                    }
                }).orElse("");
    }

    public Map<String, String> renderSingleSpecs() {
        notes = new ArrayList<>();
        return StreamSupport.stream(ach.getMetadata().getACHBeansMetadata())
                .sorted(Comparators.comparing(new Function<ACHBeanMetadata, String>() {
                    @Override
                    public String apply(ACHBeanMetadata achBeanMetadata) {
                        return achBeanMetadata.getRecordTypeCode();
                    }
                }))
                .peek(new Consumer<ACHBeanMetadata>() {
                    @Override
                    public void accept(ACHBeanMetadata achBeanMetadata) {
                        notes = new ArrayList<>();
                    }
                })
                .collect(Collectors.toMap(new Function<ACHBeanMetadata, String>() {
                    @Override
                    public String apply(ACHBeanMetadata achBeanMetadata) {
                        return achBeanMetadata.getRecordTypeCode() + "-" + achBeanMetadata.getSimpleTypeName() + ".htm";
                    }
                }, new Function<ACHBeanMetadata, String>() {
                    @Override
                    public String apply(ACHBeanMetadata achBeanMetadata) {
                        return generateHtmlBeanSpec(achBeanMetadata);
                    }
                }));
    }

    private String generateHtmlBeanSpec(ACHBeanMetadata bm) {
        return generateHtmlBeanSpec(generateSpecBody(bm));
    }

    private String generateHtmlBeanSpec(String body) {
        return formatHtmlContainer("NACHA Records Specification", body);
    }

    private String generateSpecBody(ACHBeanMetadata bm) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>(")
                .append(bm.getRecordTypeCode())
                .append(") ")
                .append(bm.getACHRecordName())
                .append("</h1>")
                .append("<table><tbody>");

        sb
                .append(row("FIELD", bm, new BiFunction<ACHFieldMetadata, Integer, String>() {
                    @Override
                    public String apply(ACHFieldMetadata achFieldMetadata, Integer integer) {
                        return String.valueOf(integer + 1);
                    }
                }))
                .append(row("Data Element Name", bm, new BiFunction<ACHFieldMetadata, Integer, String>() {
                    @Override
                    public String apply(ACHFieldMetadata fm, Integer integer) {
                        return fm.getAchFieldName();
                    }
                }))
                .append(row("Field Inclusion Requirement", bm, new BiFunction<ACHFieldMetadata, Integer, String>() {
                    @Override
                    public String apply(ACHFieldMetadata fm, Integer integer) {
                        return fm.getInclusionRequirement().name();
                    }
                }))
                .append(renderContentsRow(bm))
                .append(row("Length", bm, new BiFunction<ACHFieldMetadata, Integer, String>() {
                    @Override
                    public String apply(ACHFieldMetadata fm, Integer integer) {
                        return String.valueOf(fm.getLength());
                    }
                }))
                .append(row("Position", bm, new BiFunction<ACHFieldMetadata, Integer, String>() {
                    @Override
                    public String apply(ACHFieldMetadata fm, Integer integer) {
                        return (fm.getStart() + 1) + "-" + fm.getEnd();
                    }
                }))
                .append(row("Pattern key", bm, new BiFunction<ACHFieldMetadata, Integer, String>() {
                    @Override
                    public String apply(ACHFieldMetadata achFieldMetadata, Integer i) {
                        return letter(i);
                    }
                }));

        sb.append("</tbody></table>");

        return sb
                .append(renderPattern(bm))
                .append(renderNotes(bm))
                .append("<hr/>")
                .toString();
    }

    private String renderNotes(ACHBeanMetadata bm) {
        if (!notes.isEmpty()) {
            return "<h2>NOTES</h2><ul>"
                    + IntStreams
                    .range(0, notes.size())
                    .boxed()
                    .map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer integer) {
                            return renderNoteLineItem(integer);
                        }
                    })
                    .collect(Collectors.joining())
                    + "</ul>";
        } else {
            return "";
        }
    }

    private String renderNoteLineItem(int i) {
        return "<li><sup>" + (i + 1) + "</sup> " + notes.get(i) + "</li>" + ACH.LINE_SEPARATOR;
    }

    private String renderPattern(ACHBeanMetadata bm) {
        return "<h2>Pattern</h2><pre>"
                + bm.getPattern()
                + "</pre>";
    }

    private String row(String firstColumnValue, ACHBeanMetadata bm, final BiFunction<ACHFieldMetadata, Integer, String> fmFn) {
        final List<ACHFieldMetadata> achFieldsMetadata = bm.getACHFieldsMetadata();

        final StringBuilder sb = new StringBuilder()
                .append("<tr><td>")
                .append(firstColumnValue)
                .append("</td>");

        IntStreams.range(0, achFieldsMetadata.size())
                .boxed()
                .forEach(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer i) {
                        sb.append("<td>").append(fmFn.apply(achFieldsMetadata.get(i), i)).append("</td>");
                    }
                });
        return sb.append(ACH.LINE_SEPARATOR).toString();
    }

    private String renderContentsRow(ACHBeanMetadata bm) {
        return row("Contents", bm, new BiFunction<ACHFieldMetadata, Integer, String>() {
            @Override
            public String apply(ACHFieldMetadata fm, Integer integer) {
                return renderContentCell(fm);
            }
        });
    }

    private String renderContentCell(ACHFieldMetadata fm) {
        if (fm.hasConstantValues() && requiresNote(fm)) {
            notes.add("May contain " + StreamSupport.stream(fm.getValues()).map(new Function<String, String>() {
                @Override
                public String apply(String s) {
                    return "'" + s + "'";
                }
            }).collect(Collectors.joining(", ")));
            return fm.getSimpleTypeName() + "<sup>" + notes.size() + "</sup> ";
        } else if (fm.hasConstantValues() && !requiresNote(fm)) {
            return "'" + fm.getValues().iterator().next() + "'";
        } else {
            return fm.getSimpleTypeName() + (!"".equals(fm.getDateFormat().trim()) ? ". " + fm.getDateFormat() : "");
        }
    }

    private boolean requiresNote(ACHFieldMetadata fm) {
        return fm.hasConstantValues() && fm.getValues().size() > 1;
    }


    private String formatHtmlContainer(String title, String content) {
        return String.format(readFile("template/container.html"), title, content);
    }

    private String readFile(String classPath) {
        try {
            Path path = Paths.get(ClassLoader.getSystemResource(classPath).toURI());
            List<String> lines = FileUtils.readLines(path.toFile(), StandardCharsets.UTF_8);
            return StreamSupport.stream(lines).collect(Collectors.joining(ACH.LINE_SEPARATOR));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

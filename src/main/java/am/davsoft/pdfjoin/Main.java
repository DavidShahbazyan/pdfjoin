package am.davsoft.pdfjoin;

import am.davsoft.pdfjoin.util.PDFJoiner;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {
            File outputFile = null;
            List<File> inputFiles = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-o") || args[i].equalsIgnoreCase("--output-file")) {
                    String outputFilePath = args[i + 1];
                    if (outputFilePath != null && !outputFilePath.isBlank()) {
                        outputFile = new File(outputFilePath);
                    } else {
                        System.err.println("Output file path should be specified if -o or --output-file is specified.");
                        System.exit(1);
                    }
                    i++;
                    continue;
                }
                if (Files.exists(Path.of(args[i]))) {
                    inputFiles.add(new File(args[i]));
                } else {
                    System.err.println("Input file '" + args[i] + "' does not exist.");
                    System.exit(1);
                }
            }

            if (inputFiles.isEmpty()) {
                System.err.println("No input files specified.");
                System.exit(1);
            }

            if (outputFile == null) {
                outputFile = new File("output.pdf");
            }

            if (outputFile.exists()) {
                System.err.println("Output file already exists. Overwriting existing output file.");
            }

            PDFJoiner pdfJoiner = new PDFJoiner();
            pdfJoiner.join(
                    inputFiles,
                    Files.newOutputStream(outputFile.toPath(), StandardOpenOption.CREATE_NEW),
                    (message, total, current) -> System.out.printf("\r    Processing pages: [%50s] (%s/%s)", "#".repeat((total / 100 * current) / 2), current, total)
            );
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}


package am.davsoft.pdfjoin.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PDFJoiner {
    public void join(final List<File> fileList, final OutputStream outputStream, final ProgressViewer progressViewer) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte pageContentByte = writer.getDirectContent();
        PdfImportedPage pdfImportedPage;
        int currentPdfReaderPage = 1;
        int totalPagesCount = 0;
        int processedPagesCount = 0;

        List<PdfReader> readersList = new ArrayList<>(fileList.size());

        for (File item : fileList) {
            PdfReader pdfReader = new PdfReader(item.getPath());
            totalPagesCount += pdfReader.getNumberOfPages();
            readersList.add(pdfReader);
        }

        for (PdfReader pdfReader : readersList) {
            while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                document.newPage();
                pdfImportedPage = writer.getImportedPage(pdfReader, currentPdfReaderPage);
                pageContentByte.addTemplate(pdfImportedPage, 0, 0);
                currentPdfReaderPage++;
            }
            currentPdfReaderPage = 1;

            processedPagesCount++;
            progressViewer.updateProgress(processedPagesCount, totalPagesCount);
        }

        outputStream.flush();
        document.close();
        outputStream.close();
    }
}

package am.davsoft.pdfjoin.controller;

import am.davsoft.pdfjoin.util.Dialogs;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final FileChooser.ExtensionFilter EXTENSION_FILTER = new FileChooser.ExtensionFilter("PDF File", "*.pdf", "*.PDF");
    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private BooleanProperty processRunning = new SimpleBooleanProperty();
    private BooleanProperty processTerminated = new SimpleBooleanProperty();
    private Task joinTask;

    @FXML
    private VBox processIndicatorLayer;
    @FXML
    private JFXListView<File> fileListView;
    @FXML
    private JFXSpinner processIndicatorSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FILE_CHOOSER.setInitialDirectory(new File(System.getProperty("user.home")));
        processIndicatorLayer.visibleProperty().bind(processRunning);
        processIndicatorLayer.managedProperty().bind(processRunning);
    }

    @FXML
    private void addNewFileAction(ActionEvent event) {
        List<File> files = FILE_CHOOSER.showOpenMultipleDialog(((Node) event.getSource()).getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            for (File file : files) {
                if (!fileAlreadyExistsInList(file)) {
                    fileListView.getItems().add(file);
                } else {
                    Dialogs.showWarningPopup(((StackPane) ((Node) event.getSource()).getScene().getRoot()), "File already exists!", String.format("The file \"%s\" you're trying to add already exists in the files list.", file.getName()));
                }
            }
        }
    }

    @FXML
    private void removeSelectedFileAction(ActionEvent event) {
        if (fileListView.getSelectionModel().getSelectedItems() != null && !fileListView.getSelectionModel().getSelectedItems().isEmpty()) {
            fileListView.getItems().removeAll(fileListView.getSelectionModel().getSelectedItems());
        } else {
            Dialogs.showWarningPopup(((StackPane) ((Node) event.getSource()).getScene().getRoot()), "Nothing to remove!", "You have not chosen anything.\nPlease, choose a file(s) to remove from the list and try again.");
        }
    }

    @FXML
    private void startJoinProcessAction(ActionEvent event) throws IOException, DocumentException {
        if (fileListView.getItems().size() > 2) {
            File file = FILE_CHOOSER.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
                joinTask = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Document document = new Document();
                        OutputStream outputStream = Files.newOutputStream(file.toPath());
                        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                        document.open();
                        PdfContentByte pageContentByte = writer.getDirectContent();
                        PdfImportedPage pdfImportedPage;
                        int currentPdfReaderPage = 1;
                        int totalFilesProcessed = 0;

                        for (File item : fileListView.getItems()) {
                            FileInputStream fis = new FileInputStream(item);
                            PdfReader pdfReader = new PdfReader(fis);
                            while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                                document.newPage();
                                pdfImportedPage = writer.getImportedPage(pdfReader, currentPdfReaderPage);
                                pageContentByte.addTemplate(pdfImportedPage, 0, 0);
                                currentPdfReaderPage++;
                            }
                            currentPdfReaderPage = 1;
                            fis.close();

                            totalFilesProcessed++;
                            updateProgress(totalFilesProcessed, fileListView.getItems().size());
                        }
                        outputStream.flush();
                        document.close();
                        outputStream.close();
                        return null;
                    }

                    @Override
                    protected void running() {
                        super.running();
                        processRunning.setValue(true);
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        processRunning.setValue(false);
                        Dialogs.showInfoPopup(((StackPane) ((Node) event.getSource()).getScene().getRoot()), "Done!", "The join process completed successfully.");
                    }

                    @Override
                    protected void cancelled() {
                        super.cancelled();
                        try {
                            Files.deleteIfExists(file.toPath());
                            processRunning.setValue(false);
                            Dialogs.showWarningPopup(((StackPane) ((Node) event.getSource()).getScene().getRoot()), "Terminated!", "The join process has been terminated by user.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        try {
                            Files.deleteIfExists(file.toPath());
                            processRunning.setValue(false);
                            Dialogs.showErrorDialog(((StackPane) ((Node) event.getSource()).getScene().getRoot()), "Failed!", "The join process failed!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                processIndicatorSpinner.progressProperty().bind(joinTask.progressProperty());

                Thread t = new Thread(joinTask);
                t.setDaemon(true);
                t.start();
            }
        } else {
            Dialogs.showWarningPopup(((StackPane) ((Node) event.getSource()).getScene().getRoot()), "Invalid amount of files!", "Please, add at least two files to start joining.");
        }
    }

    @FXML
    private void cancelJoinProcessAction(ActionEvent event) {
        if (joinTask != null) {
            joinTask.cancel();
        }
    }

    private boolean fileAlreadyExistsInList(File file) {
        return fileListView.getItems() != null && fileListView.getItems().contains(file);
    }
}

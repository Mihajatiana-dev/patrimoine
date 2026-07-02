package school.hei.patrimoine.visualisation.swing.ihm.google.component;

import static java.awt.FileDialog.SAVE;

import java.awt.*;
import java.io.File;
import java.util.Optional;
import javax.swing.*;

public class FileSelector {
  private static final String CSV_EXTENSION = ".csv";
  private static final String TXT_EXTENSION = ".txt";
  private static final String DOWNLOADS_PATH = "Downloads";
  private static final String TELECHARGEMENTS_PATH = "Téléchargements";

  public static Optional<File> selectOutputFile(String title, String baseName) {
    var fileDialog = new FileDialog((Frame) null, title, SAVE);
    fileDialog.setDirectory(getDefaultDir().getAbsolutePath());
    fileDialog.setFile(baseName + TXT_EXTENSION);
    fileDialog.setVisible(true);

    var fileName = fileDialog.getFile();
    var fileDirectory = fileDialog.getDirectory();
    fileDialog.dispose();
    if (fileName == null) {
      return Optional.empty();
    }

    var outputFile = new File(fileDirectory, fileName);
    if (!fileName.toLowerCase().endsWith(TXT_EXTENSION)
        && !fileName.toLowerCase().endsWith(CSV_EXTENSION)) {
      outputFile = new File(outputFile.getParentFile(), fileName + TXT_EXTENSION);
    }

    return Optional.of(outputFile);
  }

  private static File getDefaultDir() {
    var home = new File(System.getProperty("user.home"));
    var downloadsPath = new File(home, DOWNLOADS_PATH);

    if (!downloadsPath.exists()) {
      downloadsPath = new File(home, TELECHARGEMENTS_PATH);
    }

    return downloadsPath.exists() ? downloadsPath : home;
  }
}

package school.hei.patrimoine.visualisation.swing.ihm.google.component.appbar.builtin;

import static school.hei.patrimoine.visualisation.swing.ihm.google.component.FileSelector.selectOutputFile;
import static school.hei.patrimoine.visualisation.swing.ihm.google.component.files.FileSideBar.getSelectedFile;
import static school.hei.patrimoine.visualisation.swing.ihm.google.modele.MessageDialog.showError;
import static school.hei.patrimoine.visualisation.swing.ihm.google.modele.MessageDialog.showInfo;
import static school.hei.patrimoine.visualisation.swing.ihm.google.providers.FilesProvider.getDoneFile;

import java.io.IOException;
import school.hei.patrimoine.modele.comptable.fec.FEC;
import school.hei.patrimoine.modele.comptable.fec.factory.FECFactory;
import school.hei.patrimoine.visualisation.swing.ihm.google.component.button.Button;
import school.hei.patrimoine.visualisation.swing.ihm.google.modele.State;
import school.hei.patrimoine.visualisation.swing.ihm.google.providers.OperationProvider;
import school.hei.patrimoine.visualisation.swing.ihm.google.providers.PJProvider;

public class AddExportButton extends Button {
  public AddExportButton(State state) {
    super(
        "Exporter",
        e -> {
          try {
            var fec = getFec(state);
            var optionalFile = selectOutputFile("Exportation FEC", "FEC");
            if (optionalFile.isPresent()) {
              fec.export(optionalFile.get().toPath());
              showInfo("Exportation FEC", "Le fichier a été enregistré.");
            }
          } catch (IllegalStateException ex) {
            showError(ex.getMessage());
          } catch (IOException ex) {
            throw new RuntimeException("Erreur lors de l'écriture du fichier", ex);
          }
        });

    this.setToolTipText("Exporter FEC");
  }

  private static FEC getFec(State state) {
    var optionalSelectedFile = getSelectedFile(state);
    if (optionalSelectedFile.isEmpty()) {
      throw new IllegalStateException("Veuillez sélectionner un fichier avant d'exporter.");
    }

    var selectedFile = optionalSelectedFile.get();
    var doneFile = getDoneFile(selectedFile);
    var operations = new OperationProvider().apply(doneFile);
    var pjs = new PJProvider().apply(doneFile);
    return FECFactory.make(operations, pjs);
  }
}

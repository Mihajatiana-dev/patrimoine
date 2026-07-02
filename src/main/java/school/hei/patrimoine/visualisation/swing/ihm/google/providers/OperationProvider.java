package school.hei.patrimoine.visualisation.swing.ihm.google.providers;

import static school.hei.patrimoine.visualisation.swing.ihm.google.modele.files.PatriLangFilesWatcher.getCas;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import school.hei.patrimoine.modele.comptable.OperationComptable;
import school.hei.patrimoine.modele.decomposeur.PossessionDecomposeurFacade;
import school.hei.patrimoine.modele.possession.Possession;
import school.hei.patrimoine.visualisation.swing.ihm.google.modele.files.PatriLangFileContext;

public class OperationProvider
    implements Function<PatriLangFileContext, Collection<OperationComptable>> {
  @Override
  public Collection<OperationComptable> apply(PatriLangFileContext casFile) {
    var cas = getCas(casFile);
    if (cas == null) return List.of();
    var possessions = cas.possessions();
    var decomposedPossessions = decompose(possessions);

    return decomposedPossessions.stream().flatMap(p -> OperationComptable.of(p).stream()).toList();
  }

  private static List<Possession> decompose(Set<Possession> possessions) {
    return possessions.stream()
        .map(p -> PossessionDecomposeurFacade.decompose(p, LocalDate.MIN, LocalDate.MAX))
        .flatMap(List::stream)
        .toList();
  }
}

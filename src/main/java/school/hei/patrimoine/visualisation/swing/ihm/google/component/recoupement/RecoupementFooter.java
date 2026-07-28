package school.hei.patrimoine.visualisation.swing.ihm.google.component.recoupement;

import static java.awt.Color.WHITE;
import static java.awt.Cursor.HAND_CURSOR;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import javax.swing.*;
import school.hei.patrimoine.visualisation.swing.ihm.google.component.button.Button;
import school.hei.patrimoine.visualisation.swing.ihm.google.modele.State;
import school.hei.patrimoine.visualisation.swing.ihm.google.pages.RecoupementPage;
import school.hei.patrimoine.visualisation.swing.ihm.google.providers.model.Pagination;

public class RecoupementFooter extends JPanel {
  private final State state;
  private final Button nextPageButton;
  private final JCheckBox pagedCheckBox;
  private final Button previousPageButton;
  private final JComboBox<Integer> pageSelector;

  public RecoupementFooter(State state) {
    this.state = state;

    setLayout(new FlowLayout(FlowLayout.RIGHT));
    setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

    var isPaged = state.get("isPaged");
    pagedCheckBox = new JCheckBox("Navigation par pages", Boolean.TRUE.equals(isPaged));
    pagedCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
    pagedCheckBox.setCursor(new Cursor(HAND_CURSOR));
    pagedCheckBox.addActionListener(
        e ->
            state.update(
                Map.of(
                    "isPaged",
                    pagedCheckBox.isSelected(),
                    "pagination",
                    new Pagination(1, RecoupementPage.RECOUPEMENT_ITEM_PER_PAGE))));

    previousPageButton = new Button("Précédente", e -> goToPreviousPage());
    previousPageButton.setFont(new Font("Arial", Font.PLAIN, 14));
    previousPageButton.setBackground(WHITE);
    previousPageButton.setCursor(new Cursor(HAND_CURSOR));
    previousPageButton.setPreferredSize(new Dimension(110, 40));

    pageSelector = new JComboBox<>();
    pageSelector.setFont(new Font("Arial", Font.PLAIN, 14));
    pageSelector.setPreferredSize(new Dimension(70, 40));
    pageSelector.setCursor(new Cursor(HAND_CURSOR));
    pageSelector.setRenderer(
        new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(
              JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label =
                (JLabel)
                    super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
            label.setOpaque(true);
            label.setHorizontalAlignment(CENTER);

            label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            label.setBackground(isSelected ? new Color(100, 150, 255) : WHITE);
            label.setForeground(isSelected ? WHITE : Color.BLACK);

            return label;
          }
        });
    nextPageButton = new Button("Suivante", e -> goToNextPage());
    nextPageButton.setFont(new Font("Arial", Font.PLAIN, 14));
    nextPageButton.setBackground(WHITE);
    nextPageButton.setCursor(new Cursor(HAND_CURSOR));
    nextPageButton.setPreferredSize(new Dimension(110, 40));

    state.subscribe(Set.of("totalPages", "pagination", "isPaged"), this::updateFooter);

    add(pagedCheckBox);
    add(Box.createHorizontalStrut(15));
    add(previousPageButton);
    add(pageSelector);
    add(nextPageButton);

    pageSelector.addActionListener(
        e -> {
          if (pageSelector.getSelectedItem() != null) {
            int selectedPage = (Integer) pageSelector.getSelectedItem();

            var currentPage = (Pagination) state.get("pagination");
            state.update("pagination", new Pagination(selectedPage, currentPage.size()));
          }
        });
  }

  private void goToPreviousPage() {
    Pagination current = state.get("pagination");

    if (current.page() > 1)
      state.update("pagination", current.toBuilder().page(current.page() - 1).build());
  }

  private void goToNextPage() {
    Pagination current = state.get("pagination");

    int total = state.get("totalPages");
    if (current.page() < total)
      state.update("pagination", current.toBuilder().page(current.page() + 1).build());
  }

  public void updateFooter() {
    int totalPages = state.get("totalPages") != null ? state.get("totalPages") : 1;
    Pagination pagination = state.get("pagination");
    var currentPage = pagination != null ? pagination.page() : 1;

    var isPaged = state.get("isPaged");
    var paged = Boolean.TRUE.equals(isPaged);

    pagedCheckBox.setSelected(paged);

    var listeners = pageSelector.getActionListeners();
    for (var listener : listeners) {
      pageSelector.removeActionListener(listener);
    }

    pageSelector.removeAllItems();
    IntStream.rangeClosed(1, totalPages).forEach(pageSelector::addItem);

    if (currentPage <= totalPages && currentPage > 0) {
      pageSelector.setSelectedItem(currentPage);
    } else {
      pageSelector.setSelectedItem(1);
    }

    for (var listener : listeners) {
      pageSelector.addActionListener(listener);
    }

    previousPageButton.setEnabled(paged && currentPage > 1);
    nextPageButton.setEnabled(paged && currentPage < totalPages);
    pageSelector.setEnabled(paged);
  }
}

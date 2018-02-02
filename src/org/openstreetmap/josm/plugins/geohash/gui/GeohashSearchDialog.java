package org.openstreetmap.josm.plugins.geohash.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Optional;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.util.Convert;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.Shortcut;


/**
 * Class containing the Geohash plug-in's dialog logic: display and search.
 *
 * @author laurad
 */
public class GeohashSearchDialog extends ToggleDialog {

    private static final long serialVersionUID = -8605852967336765994L;

    /** the preferred dimension of the panel components */
    private static final Dimension DIM = new Dimension(50, 100);
    private final Dimension INPUT_DIM = new Dimension(Integer.MAX_VALUE, 20);
    private static final int DLG_HEIGHT = 50;
    private static final Configurer config = Configurer.getINSTANCE();
    private final JPanel searchContainer;
    private final JTextField searchInput;
    private final JButton searchButton;
    private final JTextArea searchOutput;
    private final GeohashLayer layer;


    public GeohashSearchDialog() {
        super(config.getPluginName(), config.getDialogShortcutIcon(), config.getPluginName(),
                Shortcut.registerShortcut(config.getDialogShortcutName(), config.getDialogShortcutName(), KeyEvent.VK_0,
                        Shortcut.ALT_SHIFT),
                DLG_HEIGHT, true, null);
        layer = GeohashLayer.getInstance();
        searchContainer = new JPanel();
        searchContainer.setLayout(new BoxLayout(searchContainer, BoxLayout.PAGE_AXIS));
        searchContainer.setPreferredSize(DIM);
        searchContainer.add(new JLabel(" "));

        searchInput = new JTextField(20);
        searchInput.setMaximumSize(INPUT_DIM);
        searchContainer.add(searchInput);

        searchButton = new JButton(Configurer.getINSTANCE().getDialogButtonName());
        searchButton.addActionListener(new SearchAction());
        searchContainer.add(new JLabel(" "));
        searchContainer.add(searchButton);

        searchOutput = new JTextArea();
        searchOutput.setEditable(false);
        searchOutput.setLineWrap(true);
        searchOutput.setWrapStyleWord(true);
        searchOutput.setBackground(this.getBackground());
        searchContainer.add(searchOutput);

        add(createLayout(searchContainer, false, null));
    }

    /**
     * ActionListener implementing search button logic.
     *
     * @author laurad
     */
    public class SearchAction implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            final String geohashCode = searchInput.getText();

            final Optional<Geohash> searchedGeohash =
                    layer.getGeohashes().stream().filter(g -> g.code().equals(geohashCode)).findFirst();
            if (searchedGeohash.isPresent()) {
                outputSearchFound(searchedGeohash.get());
            } else {
                try {
                    final Geohash findGeohash = new Geohash(geohashCode);
                    layer.addGeohash(findGeohash);
                    outputSearchFound(findGeohash);
                } catch (final IllegalArgumentException ex) {
                    searchOutput.setText(Configurer.getINSTANCE().getDialogLabelNotFound());
                }
            }
        }

        /**
         * Method for displaying the searched geohash on map. Also clears the output message.
         *
         * @param searchedGeohash
         */
        private void outputSearchFound(final Geohash searchedGeohash) {
            MainApplication.getMap().mapView.zoomTo(Convert.convertBoundingBoxToBounds(searchedGeohash.bounds()));
            searchOutput.setText("");
        }


    }
}

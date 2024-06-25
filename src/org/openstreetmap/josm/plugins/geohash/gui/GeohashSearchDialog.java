/*
 * Copyright 2019 Grabtaxi Holdings PTE LTE (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file.
 *
 */
package org.openstreetmap.josm.plugins.geohash.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
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
import com.grab.josm.common.gui.builder.ButtonBuilder;
import com.grab.josm.common.gui.builder.TextComponentBuilder;


/**
 * Class containing the Geohash plug-in's dialog logic: display and search.
 *
 * @author laurad
 */
public class GeohashSearchDialog extends ToggleDialog {

    private static final int CODE_MAX_SIZE_10 = 10;

    private static final long serialVersionUID = -8605852967336765994L;

    /**
     * the preferred dimension of the panel components
     */
    private static final Dimension DIM = new Dimension(50, 70);
    private static final Dimension INPUT_DIM = new Dimension(Integer.MAX_VALUE, 20);
    private static final Color WHITE = new Color(255, 255, 255);
    private static final int DLG_HEIGHT = 50;
    private static final Configurer CONFIG = Configurer.getINSTANCE();
    private final JTextField searchInput;
    private final JTextArea searchOutput;

    public GeohashSearchDialog() {
        super(CONFIG.getPluginName(), CONFIG.getDialogShortcutIcon(), CONFIG.getPluginName(),
                Shortcut.registerShortcut(CONFIG.getDialogShortcutName(), CONFIG.getDialogShortcutName(), KeyEvent.VK_0,
                        Shortcut.ALT_SHIFT),
                DLG_HEIGHT, true, null);
        final JPanel searchContainer = new JPanel();
        searchContainer.setLayout(new BoxLayout(searchContainer, BoxLayout.PAGE_AXIS));
        searchContainer.setPreferredSize(DIM);
        searchContainer.add(new JLabel(" "));

        searchInput = TextComponentBuilder.buildTextField("", Font.PLAIN, WHITE, 20);
        searchInput.setMaximumSize(INPUT_DIM);
        searchInput.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyTyped(final java.awt.event.KeyEvent evt) {
                if (searchInput.getText().length() >= CODE_MAX_SIZE_10
                        && !(evt.getKeyChar() == KeyEvent.VK_DELETE || evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
                    getToolkit().beep();
                    evt.consume();
                }
            }
        });
        searchContainer.add(searchInput);
        final JButton searchButton = ButtonBuilder.build(new SearchAction(), Configurer.getINSTANCE().getDialogButtonName());
        searchContainer.add(new JLabel(" "));
        searchContainer.add(searchButton);

        searchOutput = TextComponentBuilder.buildTextArea("", this.getBackground(), false);
        searchOutput.setLineWrap(true);
        searchOutput.setWrapStyleWord(true);
        searchContainer.add(searchOutput);

        add(createLayout(searchContainer, false, null));
    }

    /**
     * ActionListener implementing search button logic.
     *
     * @author laurad
     */
    public class SearchAction extends AbstractAction {

        private static final long serialVersionUID = 1366345844455493440L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            final String geohashCode = searchInput.getText();
            try {
                final Geohash findGeohash = new Geohash(geohashCode);
                outputSearchFound(findGeohash);
            } catch (final IllegalArgumentException ex) {
                searchOutput.setText(Configurer.getINSTANCE().getDialogLabelNotFound());
            }
        }

        /**
         * Method for displaying the searched geohash on map. Also clears the output message.
         *
         * @param searchedGeohash - the geohash to be displayed on map
         */
        private void outputSearchFound(final Geohash searchedGeohash) {
            MainApplication.getMap().mapView.zoomTo(Convert.convertBoundingBoxToBounds(searchedGeohash.bounds()));
            searchOutput.setText("");
        }
    }
}
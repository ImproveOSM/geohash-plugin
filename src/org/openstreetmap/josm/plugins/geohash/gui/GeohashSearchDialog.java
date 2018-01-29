package org.openstreetmap.josm.plugins.geohash.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.Shortcut;


/**
 *
 *
 * @author laurad
 */
public class GeohashSearchDialog extends ToggleDialog {

    private static final long serialVersionUID = -8605852967336765994L;

    /** the preferred dimension of the panel components */
    private static final Dimension DIM = new Dimension(30, 100);
    private static final int DLG_HEIGHT = 30;
    private static final Configurer config = Configurer.getINSTANCE();
    private final JPanel searchContainer;
    private final JTextField searchInput;
    private final JButton searchButton;


    public GeohashSearchDialog() {
        super(config.getPluginName(), config.getDialogShortcutIcon(), config.getPluginName(),
                Shortcut.registerShortcut(config.getDialogShortcutName(), config.getDialogShortcutName(), KeyEvent.VK_0,
                        Shortcut.ALT_SHIFT),
                DLG_HEIGHT, true, null);
        searchContainer = new JPanel();
        searchContainer.setLayout(new FlowLayout());
        searchContainer.setPreferredSize(DIM);

        searchInput = new JTextField(20);
        searchContainer.add(searchInput);

        searchButton = new JButton(Configurer.getINSTANCE().getDialogButtonName());
        searchButton.addActionListener(new SearchAction());
        searchContainer.add(searchButton);

        add(createLayout(searchContainer, false, null));
    }

    public class SearchAction implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            System.out.println(searchInput.getText());

        }

    }

}

package org.openstreetmap.josm.plugins.geohash;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.geohash.gui.GeohashLayer;
import org.openstreetmap.josm.plugins.geohash.gui.GeohashSearchDialog;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.ImageProvider;


/**
 *
 *
 * @author laurad
 */
public class GeohashPlugin extends Plugin {

    private GeohashSearchDialog searchDialog;
    private GeohashLayer layer;
    private JMenuItem layerMenu;

    /**
     * @param info
     */
    public GeohashPlugin(final PluginInformation info) {
        super(info);

    }

    @Override
    public void mapFrameInitialized(final MapFrame oldMapFrame, final MapFrame newMapFrame) {
        if (MainApplication.getMap() != null && !GraphicsEnvironment.isHeadless()) {
            initializeDialog(newMapFrame);
            layer = new GeohashLayer();
            MainApplication.getLayerManager().addLayer(layer);
        }
        if (layerMenu == null) {
            layerMenu = MainMenu.add(MainApplication.getMenu().imageryMenu, new LayerActivator(), false);
            layerMenu.setEnabled(true);
        }

    }

    private void initializeDialog(final MapFrame newMapFrame) {
        // create details dialog
        searchDialog = new GeohashSearchDialog();
        newMapFrame.addToggleDialog(searchDialog);

        // enable dialog
        final boolean panelOpened = Main.pref.get("improveosm.panelOpened").isEmpty() ? false
                : Boolean.valueOf(Main.pref.get("improveosm.panelOpened"));
        if (panelOpened) {
            searchDialog.showDialog();
        } else {
            searchDialog.hideDialog();
        }
    }

    private final class LayerActivator extends JosmAction {

        private static final long serialVersionUID = -1361735274900300621L;

        private LayerActivator() {
            super(Configurer.getINSTANCE().getPluginName(), new ImageProvider(Configurer.getINSTANCE().getLayerIcon()),
                    null, null, false, null, false);
            setEnabled(false);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            // TODO Auto-generated method stub

        }


    }

}

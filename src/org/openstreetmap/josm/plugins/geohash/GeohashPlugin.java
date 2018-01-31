package org.openstreetmap.josm.plugins.geohash;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Optional;
import java.util.Set;
import javax.swing.JMenuItem;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.NavigatableComponent;
import org.openstreetmap.josm.gui.NavigatableComponent.ZoomChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.TMSLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.core.GeohashIdentifier;
import org.openstreetmap.josm.plugins.geohash.gui.GeohashLayer;
import org.openstreetmap.josm.plugins.geohash.gui.GeohashSearchDialog;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.ImageProvider;


/**
 *
 *
 * @author laurad
 */
public class GeohashPlugin extends Plugin implements ZoomChangeListener, LayerChangeListener, KeyListener {

    private GeohashSearchDialog searchDialog;
    private GeohashLayer layer;
    private JMenuItem layerMenu;
    private boolean zoomKeyPressed = false;

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
            layer = GeohashLayer.getInstance();
            MainApplication.getLayerManager().addLayer(layer);
            registerListeners();
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
            if (layer == null && MainApplication.getMap() != null && !GraphicsEnvironment.isHeadless()) {
                layer = GeohashLayer.getInstance();
                MainApplication.getLayerManager().addLayer(layer);
                registerListeners();
            }
        }


    }

    @Override
    public void zoomChanged() {
        if (zoomKeyPressed) {
            final Point mousePoint = MainApplication.getMainPanel().getMousePosition();
            final LatLon mouseCoordinates =
                    MainApplication.getMap().mapView.getLatLon(mousePoint.getX(), mousePoint.getY());
            final Optional<Geohash> zoomedHash = layer.getGeohashes().stream().filter(geohash -> {
                return geohash.containsPoint(mouseCoordinates);
            }).max((g1, g2) -> Integer.compare(g1.code().length(), g2.code().length()));
            if (zoomedHash.isPresent()) {
                final Set<Geohash> newGeohashes = (Set<Geohash>) GeohashIdentifier.get(zoomedHash.get().bounds());
                layer.addGeohashes(newGeohashes);
                MainApplication.getMap().repaint();
            }

        }

    }

    @Override
    public void layerAdded(final LayerAddEvent e) {
        if (e.getAddedLayer() instanceof TMSLayer) {
            layerMenu.setEnabled(true);
        }
    }

    @Override
    public void layerRemoving(final LayerRemoveEvent e) {
        if (e.getRemovedLayer() instanceof GeohashLayer) {
            layer.destroyInstance();
            unregisterListeners();
            layer = null;
        }
        if (MainApplication.getLayerManager().getLayersOfType(TMSLayer.class).size() == 0) {
            layerMenu.setEnabled(false);
        }
    }

    public void registerListeners() {
        NavigatableComponent.addZoomChangeListener(this);
        MainApplication.getMap().mapView.addKeyListener(this);
        MainApplication.getLayerManager().addLayerChangeListener(this);
    }

    public void unregisterListeners() {
        NavigatableComponent.removeZoomChangeListener(this);
        MainApplication.getMap().mapView.removeKeyListener(this);
        MainApplication.getLayerManager().removeLayerChangeListener(this);
    }

    @Override
    public void layerOrderChanged(final LayerOrderChangeEvent e) {
        // no logic here
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        // no logic here

    }

    @Override
    public void keyPressed(final KeyEvent e) {

        if (e.isShiftDown()) {
            zoomKeyPressed = true;

        }

    }

    @Override
    public void keyReleased(final KeyEvent e) {

        if (!e.isShiftDown()) {
            zoomKeyPressed = false;
        }

    }

}

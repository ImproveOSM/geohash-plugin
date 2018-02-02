package org.openstreetmap.josm.plugins.geohash;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JMenuItem;
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
 * Entry Point for Geohash plug-in.
 *
 * @author laurad
 */
public class GeohashPlugin extends Plugin
implements ZoomChangeListener, LayerChangeListener, KeyListener, MouseListener {

    private GeohashSearchDialog searchDialog;
    private GeohashLayer layer;
    private JMenuItem layerMenu;
    /** Shift key is considered zoom key */
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

    /**
     * Initializes the the plug-in dialog containing the search geohash option.
     *
     * @param newMapFrame
     */
    private void initializeDialog(final MapFrame newMapFrame) {
        searchDialog = new GeohashSearchDialog();
        newMapFrame.addToggleDialog(searchDialog);
    }


    /**
     * The zoom even is captured by this method. If during the zoom event, the Shift key is pressed, the area containing
     * the current mouse position has it's inner geohashes calculated.
     */
    @Override
    public void zoomChanged() {
        if (zoomKeyPressed) {
            final LatLon mouseCoordinates = getMouseCoordinates();
            final Optional<Geohash> zoomedHash = layer.getGeohashes().stream().filter(geohash -> {
                return geohash.containsPoint(mouseCoordinates);
            }).max((g1, g2) -> Integer.compare(g1.code().length(), g2.code().length()));
            if (zoomedHash.isPresent()) {
                final Set<Geohash> newGeohashes = (Set<Geohash>) GeohashIdentifier.get(zoomedHash.get().bounds());
                layer.addGeohashes(newGeohashes);
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
        MainApplication.getMap().mapView.addMouseListener(this);
    }

    public void unregisterListeners() {
        NavigatableComponent.removeZoomChangeListener(this);
        MainApplication.getMap().mapView.removeKeyListener(this);
        MainApplication.getLayerManager().removeLayerChangeListener(this);
        MainApplication.getMap().mapView.removeMouseListener(this);
    }

    /**
     * Used to return the coordinates of the mouse position on screen in JOSM LatLon format.
     *
     * @return LatLon
     */
    private LatLon getMouseCoordinates() {
        final Point mousePoint = MainApplication.getMainPanel().getMousePosition();
        final LatLon mouseCoordinates =
                MainApplication.getMap().mapView.getLatLon(mousePoint.getX(), mousePoint.getY());
        return mouseCoordinates;
    }

    @Override
    public void layerOrderChanged(final LayerOrderChangeEvent e) {
        // not required
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        // not required
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

    /**
     * When double clicking a geohash, delete all equally sized geohashes from that parent. Does not delete the world
     * geohash grid.
     */
    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getClickCount() == 2) {
            final LatLon mouseCoordinates = getMouseCoordinates();
            final Optional<Geohash> zoomedHash = layer.getGeohashes().stream().filter(geohash -> {
                return geohash.containsPoint(mouseCoordinates);
            }).max((g1, g2) -> Integer.compare(g1.code().length(), g2.code().length()));
            if (zoomedHash.isPresent() && zoomedHash.get().code().length() > 1) {
                final String zoomedGeohash = zoomedHash.get().code();
                final Set<Geohash> toBeDeleted = layer.getGeohashes().stream().filter(geohash -> {
                    return (geohash.code().length() == zoomedGeohash.length())
                            && geohash.code().startsWith(zoomedGeohash.substring(0, zoomedGeohash.length() - 1));
                }).collect(Collectors.toSet());
                layer.removeGeohashes(toBeDeleted);
            }
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        // not required
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        // not required
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        // not required
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        // not required
    }

    /**
     * This class is called when the Geohash plug-in is selected from the JOSM Imagery menu, resulting in creating a new
     * Geohash layer.
     *
     * @author laurad
     */
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

}

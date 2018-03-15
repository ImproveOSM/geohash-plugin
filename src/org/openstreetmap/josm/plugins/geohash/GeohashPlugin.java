package org.openstreetmap.josm.plugins.geohash;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JMenuItem;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.TMSLayer;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.gui.GeohashLayer;
import org.openstreetmap.josm.plugins.geohash.gui.GeohashSearchDialog;
import org.openstreetmap.josm.plugins.geohash.util.PreferenceManager;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.ImageProvider;


/**
 * Entry Point for Geohash plug-in.
 *
 * @author laurad
 */
public class GeohashPlugin extends Plugin
implements LayerChangeListener, MouseListener, MouseMotionListener {

    private static final int MIN_CODE_LENGTH = 1;
    private static final int MAX_ATTEMPTS = 3;
    private static final int DOUBLE_CLICK = 2;
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
            if (PreferenceManager.getInstance().loadLayerOpenedFlag()) {
                initializeLayer();
                registerListeners();
            }
        }
        if (layerMenu == null) {
            layerMenu = MainMenu.add(MainApplication.getMenu().imageryMenu, new LayerActivator(), false);
            layerMenu.setEnabled(true);
        }
    }

    /**
     * Initializes the geohash plug-in layer
     */
    private void initializeLayer() {
        layer = GeohashLayer.getInstance();
        MainApplication.getLayerManager().addLayer(layer);
    }

    /**
     * Initializes the the plug-in dialog containing the search geohash option.
     *
     * @param newMapFrame
     */
    private void initializeDialog(final MapFrame newMapFrame) {
        final GeohashSearchDialog searchDialog = new GeohashSearchDialog();
        newMapFrame.addToggleDialog(searchDialog);
    }

    @Override
    public void layerAdded(final LayerAddEvent e) {
        if (e.getAddedLayer() instanceof TMSLayer) {
            layerMenu.setEnabled(true);
            layer.setColors();
        }
    }

    @Override
    public void layerRemoving(final LayerRemoveEvent e) {
        if (e.getRemovedLayer() instanceof GeohashLayer) {
            GeohashLayer.destroyInstance();
            unregisterListeners();
            layer = null;
        } else {
            layer.setColors();
        }
        if (MainApplication.getLayerManager().getLayersOfType(TMSLayer.class).isEmpty()) {
            layerMenu.setEnabled(false);
        }
    }

    public void registerListeners() {
        MainApplication.getLayerManager().addLayerChangeListener(this);
        MainApplication.getMap().mapView.addMouseListener(this);
        MainApplication.getMap().mapView.addMouseMotionListener(this);
    }

    public void unregisterListeners() {
        MainApplication.getLayerManager().removeLayerChangeListener(this);
        MainApplication.getMap().mapView.removeMouseListener(this);
        MainApplication.getMap().mapView.removeMouseMotionListener(this);
    }

    @Override
    public void layerOrderChanged(final LayerOrderChangeEvent e) {
        // not required
    }

    /**
     * Used to return the coordinates of the mouse position on screen in JOSM LatLon format.
     *
     * @return LatLon
     */
    private LatLon getMouseCoordinates() {
        final Point mousePoint = MainApplication.getMap().mapView.getMousePosition();
        return mousePoint != null ? MainApplication.getMap().mapView.getLatLon(mousePoint.getX(), mousePoint.getY())
                : null;
    }

    /**
     * When double clicking a geohash, delete all equally sized geohashes from that parent. Does not delete the world
     * geohash grid.
     */
    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getClickCount() == DOUBLE_CLICK) {
            int attempts = 0;
            LatLon mouseCoordinates = getMouseCoordinates();
            while (mouseCoordinates == null && attempts < MAX_ATTEMPTS) {
                mouseCoordinates = getMouseCoordinates();
                attempts++;
            }
            if (mouseCoordinates != null) {
                final LatLon mousePosition = mouseCoordinates;
                final Optional<Geohash> zoomedHash = getSelectedGeohash(mousePosition);
                if (zoomedHash.isPresent() && zoomedHash.get().code().length() > MIN_CODE_LENGTH) {
                    final String zoomedGeohash = zoomedHash.get().code();
                    final Set<Geohash> toBeDeleted = layer.getGeohashes().stream().filter(geohash -> {
                        return (geohash.code().length() >= zoomedGeohash.length())
                                && geohash.code().startsWith(
                                        zoomedGeohash.substring(0, zoomedGeohash.length() - MIN_CODE_LENGTH));
                    }).collect(Collectors.toSet());
                    layer.removeGeohashes(toBeDeleted);
                }
                if (zoomedHash.isPresent() && zoomedHash.get().code().length() == MIN_CODE_LENGTH) {
                    final String zoomedGeohash = zoomedHash.get().code();
                    final Set<Geohash> toBeDeleted = layer.getGeohashes().stream().filter(geohash -> {
                        return geohash.code()
                                .startsWith(zoomedGeohash.substring(0, zoomedGeohash.length() - MIN_CODE_LENGTH));
                    }).collect(Collectors.toSet());
                    layer.removeGeohashes(toBeDeleted);
                }
                layer.clearSelectedGeohash();
            }
        }
    }

    /**
     * Get the geohash over which the mouse is currently positioned
     *
     * @param mousePosition
     * @return
     */
    private Optional<Geohash> getSelectedGeohash(final LatLon mousePosition) {
        if (mousePosition != null) {
            return layer.getGeohashes().stream().filter(geohash -> geohash.containsPoint(mousePosition))
                    .max((g1, g2) -> Integer.compare(g1.code().length(), g2.code().length()));
        }
        return Optional.empty();
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

    @Override
    public void mouseDragged(final MouseEvent e) {
        // not required
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        final LatLon mouseCoordinates = getMouseCoordinates();
        final Optional<Geohash> selectedGeohash = getSelectedGeohash(mouseCoordinates);
        if (selectedGeohash.isPresent()) {
            if (layer.getSelectedGeohash() != null) {
                if (!selectedGeohash.get().code().equals(layer.getSelectedGeohash().code())) {
                    layer.clearSelectedGeohash();
                    layer.setSelectedGeohash(selectedGeohash.get());
                }
            } else {
                layer.setSelectedGeohash(selectedGeohash.get());
            }
        } else {
            layer.clearSelectedGeohash();
        }
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
                PreferenceManager.getInstance().setLayerOpenedFlag(true);
            }
        }
    }
}
/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash;

import org.openstreetmap.josm.actions.JosmAction;
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
import org.openstreetmap.josm.plugins.geohash.gui.GeohashLayer;
import org.openstreetmap.josm.plugins.geohash.gui.GeohashSearchDialog;
import org.openstreetmap.josm.plugins.geohash.util.PreferenceManager;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Entry Point for Geohash plug-in.
 *
 * @author laurad
 */
public class GeohashPlugin extends Plugin implements LayerChangeListener {

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
    }

    public void unregisterListeners() {
        MainApplication.getLayerManager().removeLayerChangeListener(this);
    }

    @Override
    public void layerOrderChanged(final LayerOrderChangeEvent e) {
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
                PreferenceManager.getInstance().setLayerOpenedFlag(true);
            }
        }
    }
}
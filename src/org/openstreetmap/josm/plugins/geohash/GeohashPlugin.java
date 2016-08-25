/*
 *  Copyright 2016 Telenav, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openstreetmap.josm.plugins.geohash;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.NavigatableComponent;
import org.openstreetmap.josm.gui.NavigatableComponent.ZoomChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.core.GeohashIdentifier;
import org.openstreetmap.josm.plugins.geohash.gui.layer.GeohashLayer;
import org.openstreetmap.josm.plugins.geohash.util.Util;
import net.exfidefortis.map.BoundingBox;


/**
 *
 * @author Beata
 * @version $Revision$
 */
public class GeohashPlugin extends Plugin implements ZoomChangeListener, LayerChangeListener {

    private static final int SEARCH_DELAY = 600;
    private Timer zoomTimer;

    private GeohashLayer layer;


    public GeohashPlugin(final PluginInformation pluginInfo) {
        super(pluginInfo);
    }

    @Override
    public void mapFrameInitialized(final MapFrame oldMapFrame, final MapFrame newMapFrame) {
        if (Main.map != null && !GraphicsEnvironment.isHeadless()) {
            NavigatableComponent.addZoomChangeListener(this);
            layer = new GeohashLayer();
            newMapFrame.mapView.getLayerManager().addLayer(layer);
            NavigatableComponent.addZoomChangeListener(this);
            Main.getLayerManager().addLayerChangeListener(this);
        }
    }


    @Override
    public void layerAdded(final LayerAddEvent event) {
        if (event.getAddedLayer() instanceof GeohashLayer) {
            zoomChanged();
        }
    }

    @Override
    public void layerOrderChanged(final LayerOrderChangeEvent event) {
        // nothing to add here
    }

    @Override
    public void layerRemoving(final LayerRemoveEvent event) {
        if (event.getRemovedLayer() instanceof GeohashLayer) {
            NavigatableComponent.removeZoomChangeListener(this);
            Main.getLayerManager().removeLayerChangeListener(this);
            layer = null;
        }
    }

    @Override
    public void zoomChanged() {
        if (zoomTimer != null && zoomTimer.isRunning()) {
            // if timer is running restart it
            zoomTimer.restart();
        } else {
            zoomTimer = new Timer(SEARCH_DELAY, new ActionListener() {


                @Override
                public void actionPerformed(final ActionEvent event) {
                    Main.worker.execute(new UpdateThread());
                }
            });
            zoomTimer.setRepeats(false);
            zoomTimer.start();
        }
    }

    private final class UpdateThread implements Runnable {

        @Override
        public void run() {
            if (Main.map != null && Main.map.mapView != null && Util.zoom(Main.map.mapView.getRealBounds()) > 0) {
                final BoundingBox boundingBox = Util.buildBoundingBox(Main.map.mapView);
                final Collection<Geohash> geohashes = new GeohashIdentifier().get(boundingBox);
                System.out.println(geohashes);
                SwingUtilities.invokeLater(new Runnable() {


                    @Override
                    public void run() {
                        layer.setGeohashes(geohashes);
                        Main.map.repaint();
                    }
                });
            }
        }
    }
}
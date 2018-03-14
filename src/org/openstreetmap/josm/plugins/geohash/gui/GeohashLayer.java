/*
 *  Copyright 2018 Telenav, Inc.
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
package org.openstreetmap.josm.plugins.geohash.gui;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.core.GeohashIdentifier;
import org.openstreetmap.josm.plugins.geohash.util.Convert;
import org.openstreetmap.josm.plugins.geohash.util.PaintHandler;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.ImageProvider;
import net.exfidefortis.map.BoundingBox;


/**
 * Contains Geohash plug-in's layer logic.
 *
 * @author laurad
 */
public final class GeohashLayer extends Layer {


    private static GeohashLayer INSTANCE;
    private Set<Geohash> geohashes;
    private Geohash selectedGeohash = null;

    /** Map containing zoom level and code lengths to be shown in order to avoid overlapping */
    private final Map<Integer, Integer> visibleZoomLevels = Configurer.getINSTANCE().getCodeVizibilityLevels();
    private final PaintHandler paintHandler;

    private GeohashLayer() {
        super(Configurer.getINSTANCE().getPluginName());
        paintHandler = new PaintHandler();
        geohashes = new HashSet<>();
    }

    public static GeohashLayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeohashLayer();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void paint(final Graphics2D graphics, final MapView mapView, final Bounds bounds) {
        mapView.setDoubleBuffered(true);
        final BoundingBox mapViewBounds = Convert.convertBoundsToBoundingBox(MainApplication.getMap().mapView
                .getProjection().getLatLonBoundsBox(MainApplication.getMap().mapView.getProjectionBounds()));
        geohashes.addAll(GeohashIdentifier.get(mapViewBounds));
        setColors();
        for (final Geohash geohash : geohashes) {
            paintHandler.drawGeohash(graphics, mapView, geohash, false, visibleZoomLevels);
        }
        if (selectedGeohash != null) {
            paintHandler.drawGeohash(graphics, mapView, selectedGeohash, true, visibleZoomLevels);
        }
    }

    @Override
    public Icon getIcon() {
        return ImageProvider.get(Configurer.getINSTANCE().getLayerIcon());
    }

    @Override
    public Object getInfoComponent() {
        return Configurer.getINSTANCE().getLayerInfoComponent();
    }

    @Override
    public Action[] getMenuEntries() {
        final LayerListDialog layerListDialog = LayerListDialog.getInstance();
        return new Action[] { layerListDialog.createActivateLayerAction(this),
                layerListDialog.createShowHideLayerAction(), new GeohashLayerDeleteAction(layerListDialog.getModel()),
                new ClearAction(),
                SeparatorLayerAction.INSTANCE, new LayerListPopup.InfoAction(this) };
    }

    public Set<Geohash> getGeohashes() {
        return geohashes;
    }

    public void addGeohash(final Geohash geohash) {
        geohashes.add(geohash);
        MainApplication.getMap().repaint();
    }

    public void addGeohashes(final Collection<Geohash> newGeohashes) {
        geohashes.addAll(newGeohashes);
        MainApplication.getMap().repaint();
    }

    public void removeGeohashes(final Collection<Geohash> removeGeohashes) {
        geohashes.removeAll(removeGeohashes);
        MainApplication.getMap().repaint();
    }

    public void setSelectedGeohash(final Geohash geohash) {
        selectedGeohash = geohash;
        if (this.isVisible()) {
            paintHandler.drawGeohash((Graphics2D) MainApplication.getMap().mapView.getGraphics(),
                    MainApplication.getMap().mapView,
                    selectedGeohash, true, visibleZoomLevels);
        }
    }

    public Geohash getSelectedGeohash() {
        return selectedGeohash;
    }

    public void clearSelectedGeohash() {
        if (selectedGeohash != null && this.isVisible()) {
            paintHandler.drawGeohash((Graphics2D) MainApplication.getMap().mapView.getGraphics(),
                    MainApplication.getMap().mapView,
                    selectedGeohash, false, visibleZoomLevels);
            selectedGeohash = null;
        }
    }

    @Override
    public String getToolTipText() {
        return Configurer.getINSTANCE().getLayerTooltipText();
    }

    @Override
    public boolean isMergable(final Layer arg0) {
        return false;
    }

    @Override
    public void mergeFrom(final Layer arg0) {
        // not required
    }

    @Override
    public void visitBoundingBox(final BoundingXYVisitor arg0) {
        // not required
    }

    /**
     * Method called on layer change to set the line color according to the visible layer background.
     */
    public void setColors() {
        paintHandler.setColors();
    }

    /**
     * Layer menu has a clear geohashes option which is implemented by this class. This option removed all existing
     * children leaving behind just the world geohash.
     *
     * @author laurad
     * @version $Revision$
     */
    private class ClearAction extends AbstractAction{

        private static final long serialVersionUID = -7430280025253271160L;

        private ClearAction() {
            putValue(NAME, tr("Clear geohashes"));
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            geohashes.clear();
            final Bounds worldBounds = Main.getProjection().getWorldBoundsLatLon();
            geohashes = (Set<Geohash>) GeohashIdentifier.get(Convert.convertBoundsToBoundingBox(worldBounds));
            MainApplication.getMap().repaint();
        }
    }
}

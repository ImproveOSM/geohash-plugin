/*
 * The code is licensed under the GPL Version 3 license https://www.gnu.org/licenses/quick-guide-gplv3.html.
 *
 * Copyright (c)2017, Telenav, Inc. All Rights Reserved
 */
package org.openstreetmap.josm.plugins.geohash.gui;

import net.exfidefortis.map.BoundingBox;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.ProjectionBounds;
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
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.ImageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;


/**
 * Contains Geohash plug-in's layer logic.
 *
 * @author laurad
 */
public final class GeohashLayer extends Layer {

    private class IncreaseCoverageAction extends AbstractAction{

        private IncreaseCoverageAction() {
            putValue(NAME, I18n.tr("Increase coverage"));
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            geohashIdentifier.increaseCoveragePercent(mapViewBounds());
            GeohashLayer.getInstance().invalidate();
            MainApplication.getMap().repaint();
        }
    }

    private class DecreaseCoverageAction extends AbstractAction{

        private DecreaseCoverageAction() {
            putValue(NAME, I18n.tr("Decrease coverage"));
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            geohashIdentifier.decreaseCoveragePercent(mapViewBounds());
            GeohashLayer.getInstance().invalidate();
            MainApplication.getMap().repaint();
        }
    }

    private static GeohashLayer instance;


    private final PaintHandler paintHandler;
    private final GeohashIdentifier geohashIdentifier;

    private GeohashLayer() {
        super(Configurer.getINSTANCE().getPluginName());
        paintHandler = new PaintHandler();
        geohashIdentifier = new GeohashIdentifier(0.3, 0.1);
    }

    public static GeohashLayer getInstance() {
        if (instance == null) {
            instance = new GeohashLayer();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    @Override
    public void paint(final Graphics2D graphics, final MapView mapView, final Bounds bounds) {
        mapView.setDoubleBuffered(true);
        final Collection<Geohash> geohashes = geohashIdentifier.get(mapViewBounds());
        paintHandler.setCodeVisibility(geohashes, graphics, mapView);
        setColors();
        for (final Geohash geohash : geohashes) {
            paintHandler.drawGeohash(graphics, mapView, geohash, false);
        }
    }

    private BoundingBox mapViewBounds() {
        final ProjectionBounds projectionBounds = MainApplication.getMap().mapView.getProjectionBounds();
        final Bounds bounds = MainApplication.getMap().mapView.getProjection().getLatLonBoundsBox(projectionBounds);
        return Convert.convertBoundsToBoundingBox(bounds);
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
                new IncreaseCoverageAction(), new DecreaseCoverageAction(),
                SeparatorLayerAction.INSTANCE, new LayerListPopup.InfoAction(this) };
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
}
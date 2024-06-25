/*
 * Copyright 2019 Grabtaxi Holdings PTE LTE (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file.
 *
 */
package org.openstreetmap.josm.plugins.geohash.gui;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

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
import net.exfidefortis.map.BoundingBox;


/**
 * Contains Geohash plug-in's layer logic.
 *
 * @author laurad
 */
public final class GeohashLayer extends Layer {

    private static GeohashLayer instance;

    private Configurer configurer = Configurer.getINSTANCE();

    private final AbstractAction toggleZoomFreezeAction = new AbstractAction(
            I18n.tr(configurer.getEnableZoomFreezeText())) {
        @Override
        public void actionPerformed(final ActionEvent e) {
            //Set zoom freeze
            if (!geohashIdentifier.getZoomFreeze()) {
                putValue(Action.NAME, configurer.getDisableZoomFreezeText());
                geohashIdentifier.setZoomFreeze(true, mapViewBounds());
            } else {
                //Unfreeze zoom
                putValue(Action.NAME, configurer.getEnableZoomFreezeText());
                geohashIdentifier.setZoomFreeze(false, mapViewBounds());
                MainApplication.getMap().repaint();
            }
        }
    };

    private final AbstractAction increaseCoverageAction = new AbstractAction(
            I18n.tr(configurer.getDisplayLargerGeohashesText())) {

        private static final long serialVersionUID = -6243874188335817320L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            geohashIdentifier.increaseSideRatio();
            GeohashLayer.getInstance().invalidate();
            MainApplication.getMap().repaint();
        }
    };

    private final AbstractAction decreaseCoverageAction = new AbstractAction(
            I18n.tr(configurer.getDisplaySmallerGeohashesText())) {

        private static final long serialVersionUID = -8097975275523408384L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            geohashIdentifier.decreaseSideRatio();
            GeohashLayer.getInstance().invalidate();
            MainApplication.getMap().repaint();
        }
    };

    private final PaintHandler paintHandler;
    private final GeohashIdentifier geohashIdentifier;

    private GeohashLayer() {
        super(Configurer.getINSTANCE().getPluginName());
        paintHandler = new PaintHandler();
        geohashIdentifier = new GeohashIdentifier();
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
        final Collection<Geohash> geohashes;
        if (!geohashIdentifier.getZoomFreeze()) {
            geohashes = geohashIdentifier.get(mapViewBounds());
        } else {
            geohashes = geohashIdentifier.getGeohashesBeforeFreeze();
        }

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
        toggleZoomFreezeAction.setEnabled(true);
        if (geohashIdentifier.getZoomFreeze()) {
            increaseCoverageAction.setEnabled(false);
            decreaseCoverageAction.setEnabled(false);
        } else {
            increaseCoverageAction.setEnabled(geohashIdentifier.canIncreaseSideRatio()
                    && geohashIdentifier.wouldNoticeSideRatioIncrease(mapViewBounds()));
            decreaseCoverageAction.setEnabled(geohashIdentifier.canDecreaseSideRatio()
                    && geohashIdentifier.wouldNoticeSideRatioDecrease(mapViewBounds()));
        }
        final LayerListDialog layerListDialog = LayerListDialog.getInstance();
        return new Action[]{layerListDialog.createActivateLayerAction(this),
                layerListDialog.createShowHideLayerAction(), new GeohashLayerDeleteAction(layerListDialog.getModel()),
                increaseCoverageAction, decreaseCoverageAction, toggleZoomFreezeAction,
                SeparatorLayerAction.INSTANCE, new LayerListPopup.InfoAction(this)};
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
    }

    @Override
    public void visitBoundingBox(final BoundingXYVisitor arg0) {
    }

    /**
     * Method called on layer change to set the line color according to the visible layer background.
     */
    public void setColors() {
        paintHandler.setColors();
    }
}
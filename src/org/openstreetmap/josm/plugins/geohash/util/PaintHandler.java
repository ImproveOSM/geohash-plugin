/*
 * Copyright 2019 Grabtaxi Holdings PTE LTE (GRAB), All rights reserved.
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file.
 *
 */
package org.openstreetmap.josm.plugins.geohash.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.List;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import com.telenav.josm.common.gui.PaintManager;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 * Class containing logic used by GeohashLayer to paint the geohash data over the map
 *
 * @author laurad
 * @version $Revision$
 */
public class PaintHandler {

    private static final int TRANSLATE_X = 5;
    private static final int TRANSLATE_Y = 10;
    private static final int STROKE_WIDTH_2 = 2;
    private static final int FONT_SIZE = 13;
    private static final String FONT_NAME = "Verdana";

    private static final String IMAGERY = "imagery";
    private static final String MAPBOX_SATELLITE = "Mapbox Satellite";

    private static final Color LINE_COLOR_LIGHT_BACKGROUND = new Color(0, 0, 255);
    private static final Color LINE_COLOR_DARK_BACKGROUND = new Color(51, 255, 255);
    private static final Color SELECTED_LINE_COLOR = new Color(255, 0, 0);
    private Color lineColor;


    public PaintHandler() {
        lineColor = LINE_COLOR_DARK_BACKGROUND;
    }

    /**
     * Method for drawing a geohash on map. This includes the rectangle (geohash area) and the text (geohash code).
     *
     * @param graphics - the JOSM graphics
     * @param mapView - the current mapView
     * @param geohash - the geohash to be drawn
     */
    public void drawGeohash(final Graphics2D graphics, final MapView mapView, final Geohash geohash,
            final boolean isSelected) {
        final GeneralPath path = getGeohashPath(geohash, mapView);
        if (isSelected) {
            graphics.setColor(SELECTED_LINE_COLOR);
        } else {
            graphics.setColor(lineColor);
        }
        graphics.setStroke(new BasicStroke(STROKE_WIDTH_2));
        graphics.draw(path);
        final Point textPoint = getTextPoint(geohash, mapView, geohash.code(), graphics);
        PaintManager.drawText(graphics, geohash.code(), textPoint, new Font(FONT_NAME, Font.BOLD, FONT_SIZE),
                lineColor);

    }

    /**
     * Method for calculating geohash rectangle path adapted to map view coordinates.
     *
     * @param geohash - the geohash for which the calculation is made
     * @param mapView - the current JOSM mapView
     * @return geohashPath - the path to be drawn
     */
    private GeneralPath getGeohashPath(final Geohash geohash, final MapView mapView) {
        final Latitude north = geohash.bounds().north();
        final Longitude west = geohash.bounds().west();
        final Latitude south = geohash.bounds().south();
        final Longitude east = geohash.bounds().east();

        final LatLon northWest = new LatLon(Convert.fitLatitudeInBounds(north.asDegrees()), west.asDegrees());
        final LatLon northEast = new LatLon(Convert.fitLatitudeInBounds(north.asDegrees()), east.asDegrees());
        final LatLon southWest = new LatLon(Convert.fitLatitudeInBounds(south.asDegrees()), west.asDegrees());
        final LatLon southEast = new LatLon(Convert.fitLatitudeInBounds(south.asDegrees()), east.asDegrees());

        final GeneralPath path = new GeneralPath();
        path.moveTo(mapView.getPoint(northWest).getX(), mapView.getPoint(northWest).getY());
        path.lineTo(mapView.getPoint(southWest).getX(), mapView.getPoint(southWest).getY());
        path.lineTo(mapView.getPoint(southEast).getX(), mapView.getPoint(southEast).getY());
        path.lineTo(mapView.getPoint(northEast).getX(), mapView.getPoint(northEast).getY());
        path.lineTo(mapView.getPoint(northWest).getX(), mapView.getPoint(northWest).getY());

        return path;
    }

    private Point getTextPoint(final Geohash geohash, final MapView mapView, final String text,
            final Graphics2D graphics) {
        final double latitude = Convert.fitLatitudeInBounds(geohash.bounds().north().asDegrees());
        final double longitude = geohash.bounds().west().asDegrees();
        final LatLon northWest = new LatLon(latitude, longitude);
        final Point textPoint = mapView.getPoint(northWest);
        textPoint.translate(graphics.getFontMetrics().stringWidth(text) / 2 + TRANSLATE_X, TRANSLATE_Y);
        return new Point((int) textPoint.getX(), (int) textPoint.getY());

    }

    /**
     * Method called on layer change to set the line color according to the visible layer background.
     */
    public void setColors() {
        final List<Layer> layers = MainApplication.getLayerManager().getVisibleLayersInZOrder();
        String layerName = "";
        for (int i = layers.size() - 1; i >= 0; i--) {
            if (layers.get(i) instanceof ImageryLayer) {
                layerName = ((ImageryLayer) layers.get(i)).getInfo().getName();
                break;
            }
        }
        if (layerName.toLowerCase().contains(IMAGERY) || layerName.equals(MAPBOX_SATELLITE)) {
            lineColor = LINE_COLOR_DARK_BACKGROUND;
        } else {
            lineColor = LINE_COLOR_LIGHT_BACKGROUND;
        }
    }
}
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
package org.openstreetmap.josm.plugins.geohash.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Map;
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

    private static final int STROKE_WIDTH_2 = 2;
    private static final int TRANSLATION_20 = 40;
    private static final int FONT_SIZE = 13;
    private static final String FONT_NAME = "Verdana";

    private static final String BING_AERIAL_IMAGERY = "Bing aerial imagery";
    private static final String MAPBOX_SATELLITE = "Mapbox Satellite";
    private static final String DIGITAL_GLOBE_PREMIUM = "DigitalGlobe Premium Imagery";
    private static final String DIGITAL_GLOBE_STANDARD = "DigitalGlobe Standard Imagery";


    private final Color LINE_COLOR_LIGHT_BACKGROUND = new Color(0, 0, 255);
    private final Color LINE_COLOR_DARK_BACKGROUND = new Color(51, 255, 255);
    private final Color SELECTED_LINE_COLOR = new Color(255, 0, 0);
    private Color lineColor;


    public PaintHandler() {
        lineColor = LINE_COLOR_DARK_BACKGROUND;
    }

    /**
     * Method for drawing a geohash on map. This includes the rectangle (geohash area) and the text (geohash code).
     *
     * @param graphics
     * @param mapView
     * @param geohash
     */
    public void drawGeohash(final Graphics2D graphics, final MapView mapView, final Geohash geohash,
            final boolean isSelected, final Map<Integer, Integer> visibleZoomLevels) {
        final GeneralPath path = getGeohashPath(geohash, mapView);
        if (isSelected) {
            graphics.setColor(SELECTED_LINE_COLOR);
        } else {
            graphics.setColor(lineColor);
        }
        graphics.setStroke(new BasicStroke(STROKE_WIDTH_2));
        graphics.draw(path);
        final int zoomLevel = Convert.boundsToZoomLevel(mapView.getRealBounds());
        if (geohash.code().length() <= visibleZoomLevels.get(zoomLevel)) {
            final Point textPoint = getTextPoint(geohash, mapView);
            PaintManager.drawText(graphics, geohash.code(), textPoint, new Font(FONT_NAME, Font.BOLD, FONT_SIZE),
                    lineColor);
        }
    }

    /**
     * Method for calculating geohash rectangle path adapted to map view coordinates.
     *
     * @param geohash
     * @param mapView
     * @return geohashPath
     */
    public GeneralPath getGeohashPath(final Geohash geohash, final MapView mapView) {
        final Latitude N = geohash.bounds().north();
        final Longitude W = geohash.bounds().west();
        final Latitude S = geohash.bounds().south();
        final Longitude E = geohash.bounds().east();

        final LatLon NW = new LatLon(Convert.fitLatitudeInBounds(N.asDegrees()), W.asDegrees());
        final LatLon NE = new LatLon(Convert.fitLatitudeInBounds(N.asDegrees()), E.asDegrees());
        final LatLon SW = new LatLon(Convert.fitLatitudeInBounds(S.asDegrees()), W.asDegrees());
        final LatLon SE = new LatLon(Convert.fitLatitudeInBounds(S.asDegrees()), E.asDegrees());

        final GeneralPath path = new GeneralPath();
        path.moveTo(mapView.getPoint(NW).getX(), mapView.getPoint(NW).getY());
        path.lineTo(mapView.getPoint(SW).getX(), mapView.getPoint(SW).getY());
        path.lineTo(mapView.getPoint(SE).getX(), mapView.getPoint(SE).getY());
        path.lineTo(mapView.getPoint(NE).getX(), mapView.getPoint(NE).getY());
        path.lineTo(mapView.getPoint(NW).getX(), mapView.getPoint(NW).getY());

        return path;
    }

    public Point getTextPoint(final Geohash geohash, final MapView mapView) {
        final double latitude = Convert.fitLatitudeInBounds(geohash.bounds().north().asDegrees());
        final double longitude = geohash.bounds().west().asDegrees();
        final LatLon NW = new LatLon(latitude, longitude);
        final Point textPoint = mapView.getPoint(NW);
        textPoint.translate(TRANSLATION_20, TRANSLATION_20);
        return textPoint;
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
        if (layerName.equals(BING_AERIAL_IMAGERY) || layerName.equals(MAPBOX_SATELLITE)
                || layerName.equals(DIGITAL_GLOBE_PREMIUM) || layerName.equals(DIGITAL_GLOBE_STANDARD)) {
            lineColor = LINE_COLOR_DARK_BACKGROUND;
        } else {
            lineColor = LINE_COLOR_LIGHT_BACKGROUND;
        }
    }
}
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
package org.openstreetmap.josm.plugins.geohash.gui.layer;


import static org.openstreetmap.josm.plugins.geohash.gui.layer.Constants.LINE_STROKE;
import static org.openstreetmap.josm.plugins.geohash.gui.layer.Constants.NORMAL_COMPOSITE;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import net.exfidefortis.map.BoundingBox;


/**
 *
 * @author Beata
 * @version $Revision$
 */
class PaintHandler {

    void drawGeohash(final Graphics2D graphics, final MapView mapView, final Geohash geohash) {
        // draw border
        graphics.setComposite(NORMAL_COMPOSITE);
        graphics.setStroke(LINE_STROKE);
        graphics.setColor(Color.yellow.brighter());
        final GeneralPath path = buildPath(mapView, geohash.bounds());
        graphics.draw(path);

        // fill the bounding box
        // graphics.setComposite(BBOX_COMPOSITE);
        // graphics.fill(path);

        // draw Gaohash name
        final LatLon center = new LatLon(geohash.bounds().center().latitude().asDegrees(),
                geohash.bounds().center().latitude().asDegrees());
        drawText(graphics, mapView, geohash.code(), center);
    }

    private static GeneralPath buildPath(final MapView mv, final BoundingBox bounds) {
        final Point northEast = mv.getPoint(new LatLon(bounds.north().asDegrees(), bounds.east().asDegrees()));
        final Point northWest = mv.getPoint(new LatLon(bounds.south().asDegrees(), bounds.west().asDegrees()));
        final GeneralPath path = new GeneralPath();
        path.moveTo(northEast.getX(), northEast.getY());
        path.lineTo(northWest.getX(), northEast.getY());
        path.lineTo(northWest.getX(), northWest.getY());
        path.lineTo(northEast.getX(), northWest.getY());
        path.lineTo(northEast.getX(), northEast.getY());
        return path;
    }

    private static void drawText(final Graphics2D graphics, final MapView mapView, final String txt,
            final LatLon center) {
        final Point labelPoint = mapView.getPoint(center);
        graphics.setComposite(NORMAL_COMPOSITE);
        graphics.setFont(mapView.getFont().deriveFont(Font.BOLD, 30));
        graphics.setColor(Color.black);
        graphics.drawString(" " + txt + " ", labelPoint.x, labelPoint.y);

    }
}
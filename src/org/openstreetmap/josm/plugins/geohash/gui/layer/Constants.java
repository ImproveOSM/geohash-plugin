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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Beata
 * @version $Revision$
 */
final class Constants {

    static final Map<RenderingHints.Key, Object> RENDERING_MAP = createRenderingMap();

    private static Map<RenderingHints.Key, Object> createRenderingMap() {
        final Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        map.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return map;
    }

    static final Composite NORMAL_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F);
    static final Composite BBOX_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50F);
    static final Stroke LINE_STROKE = new BasicStroke(4F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    static final Composite LABEL_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.90F);
    static final Stroke LABEL_STROKE = new BasicStroke(5F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    static final Color LABEL_BACKGROUND_COLOR = Color.white;
    static final int LABEL_DIST = 15;
}
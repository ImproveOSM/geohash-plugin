package org.openstreetmap.josm.plugins.geohash.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.Icon;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.core.GeohashIdentifier;
import org.openstreetmap.josm.plugins.geohash.util.Converters;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.ImageProvider;
import com.telenav.josm.common.gui.PaintManager;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 *
 *
 * @author laurad
 */
public class GeohashLayer extends Layer {

    private static GeohashLayer INSTANCE;
    private static final int TRANSLATION_20 = 20;
    private static final int FONT_SIZE = 25;
    private static final String FONT_NAME = "Verdana";
    private final Collection<Geohash> geohashes;
    private final Color LINE_COLOR = new Color(0, 0, 200);
    private final Color TEXT_COLOR = new Color(255, 0, 0);


    public GeohashLayer() {
        super(Configurer.getINSTANCE().getPluginName());
        final Bounds worldBounds = Main.getProjection().getWorldBoundsLatLon();
        geohashes = new GeohashIdentifier().get(Converters.convertBoundsToBoundingBox(worldBounds));
    }

    public static GeohashLayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeohashLayer();
        }
        return INSTANCE;
    }

    @Override
    public void paint(final Graphics2D graphics, final MapView mapView, final Bounds bounds) {
        mapView.setDoubleBuffered(true);
        for (final Geohash geohash : geohashes) {
            drawGeohash(graphics, mapView, geohash);
        }

    }

    /**
     * @param graphics
     * @param mapView
     * @param geohash
     */
    private void drawGeohash(final Graphics2D graphics, final MapView mapView, final Geohash geohash) {
        final GeneralPath path = getGeohashPath(geohash, mapView);
        graphics.setColor(LINE_COLOR);
        graphics.draw(path);

        final Point textPoint = getTextPoint(geohash, mapView);
        PaintManager.drawText(graphics, geohash.code(), textPoint, new Font(FONT_NAME, Font.BOLD, FONT_SIZE),
                TEXT_COLOR);
    }

    public GeneralPath getGeohashPath(final Geohash geohash, final MapView mapView) {
        final Latitude N = geohash.bounds().north();
        final Longitude W = geohash.bounds().west();
        final Latitude S = geohash.bounds().south();
        final Longitude E = geohash.bounds().east();

        final LatLon NW = new LatLon(N.asDegrees(), W.asDegrees());
        final LatLon NE = new LatLon(N.asDegrees(), E.asDegrees());
        final LatLon SW = new LatLon(S.asDegrees(), W.asDegrees());
        final LatLon SE = new LatLon(S.asDegrees(), E.asDegrees());

        final GeneralPath path = new GeneralPath();
        path.moveTo(mapView.getPoint(NW).getX(), mapView.getPoint(NW).getY());
        path.lineTo(mapView.getPoint(SW).getX(), mapView.getPoint(SW).getY());
        path.lineTo(mapView.getPoint(SE).getX(), mapView.getPoint(SE).getY());
        path.lineTo(mapView.getPoint(NE).getX(), mapView.getPoint(NE).getY());
        path.lineTo(mapView.getPoint(NW).getX(), mapView.getPoint(NW).getY());

        return path;
    }

    public Point getTextPoint(final Geohash geohash, final MapView mapView) {
        final LatLon NW = new LatLon(geohash.bounds().north().asDegrees(), geohash.bounds().west().asDegrees());
        final Point textPoint = mapView.getPoint(NW);
        textPoint.translate(TRANSLATION_20, TRANSLATION_20);
        return textPoint;
    }

    @Override
    public Icon getIcon() {
        return ImageProvider.get(Configurer.getINSTANCE().getLayerIcon());
    }

    @Override
    public Object getInfoComponent() {
        return "This layer contains the logic to display the world's geohash grid over the provided map.";
    }

    @Override
    public Action[] getMenuEntries() {
        final LayerListDialog layerListDialog = LayerListDialog.getInstance();
        return new Action[] { layerListDialog.createActivateLayerAction(this),
                layerListDialog.createShowHideLayerAction(), layerListDialog.createDeleteLayerAction(),
                SeparatorLayerAction.INSTANCE, SeparatorLayerAction.INSTANCE, new LayerListPopup.InfoAction(this) };

    }

    public Collection<Geohash> getGeohashes() {
        return geohashes;
    }

    @Override
    public String getToolTipText() {
        return "Layer displaying geohash grid.";
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

}

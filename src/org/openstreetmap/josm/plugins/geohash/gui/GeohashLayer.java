package org.openstreetmap.josm.plugins.geohash.gui;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.GeneralPath;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.ImageryLayer;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.core.GeohashIdentifier;
import org.openstreetmap.josm.plugins.geohash.util.Convert;
import org.openstreetmap.josm.plugins.geohash.util.config.Configurer;
import org.openstreetmap.josm.tools.ImageProvider;
import com.telenav.josm.common.gui.PaintManager;
import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 * Contains Geohash plug-in's layer logic.
 *
 * @author laurad
 */
public class GeohashLayer extends Layer {


    private static GeohashLayer INSTANCE;
    private Set<Geohash> geohashes;
    /** Map containing zoom level and code lengths to be shown in order to avoid overlapping */
    private final Map<Integer, Integer> visibleZoomLevels = Configurer.getINSTANCE().getCodeVizibilityLevels();
    private Color lineColor;

    private static final String IMAGERY = "imagery";
    private static final String MAPBOX_SATELLITE = "Mapbox Satellite";

    private static final int STROKE_WIDTH_2 = 2;
    private static final int TRANSLATION_20 = 40;
    private static final int FONT_SIZE = 13;
    private static final String FONT_NAME = "Verdana";
    private final Color LINE_COLOR_LIGHT_BACKGROUND = new Color(0, 0, 255);
    private final Color LINE_COLOR_DARK_BACKGROUND = new Color(51, 255, 255);

    private GeohashLayer() {
        super(Configurer.getINSTANCE().getPluginName());
        final Bounds worldBounds = Main.getProjection().getWorldBoundsLatLon();
        try {
            geohashes = (Set<Geohash>) GeohashIdentifier.get(Convert.convertBoundsToBoundingBox(worldBounds));
            final BoundingBox mapViewBounds =
                    Convert.convertBoundsToBoundingBox(MainApplication.getMap().mapView.getRealBounds());
            final Optional<Geohash> geoParent =
                    geohashes.stream().filter(geohash -> geohash.bounds().contains(mapViewBounds)).findFirst();
            if (geoParent.isPresent()) {
                geohashes.addAll(GeohashIdentifier.getAllInView(geoParent.get().bounds(), mapViewBounds));
            }
        } catch (final IllegalArgumentException e) {
            geohashes = (Set<Geohash>) GeohashIdentifier.get(Configurer.getWorldBorder());
        }
    }

    public static GeohashLayer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeohashLayer();
        }
        return INSTANCE;
    }

    public void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void paint(final Graphics2D graphics, final MapView mapView, final Bounds bounds) {
        mapView.setDoubleBuffered(true);
        setColors();
        for (final Geohash geohash : geohashes) {
            drawGeohash(graphics, mapView, geohash);
        }
    }

    /**
     * Method for drawing a geohash on map. This includes the rectangle (geohash area) and the text (geohash code).
     *
     * @param graphics
     * @param mapView
     * @param geohash
     */
    private void drawGeohash(final Graphics2D graphics, final MapView mapView, final Geohash geohash) {
        final GeneralPath path = getGeohashPath(geohash, mapView);
        graphics.setColor(lineColor);
        graphics.setStroke(new BasicStroke(STROKE_WIDTH_2));
        graphics.draw(path);
        final int zoomLevel = Convert.boundsToZoomLevel(mapView.getRealBounds());
        if (geohash.code().length() <= visibleZoomLevels.get(zoomLevel)) {
            final Point textPoint = getTextPoint(geohash, mapView);
            PaintManager.drawText(graphics, geohash.code(), textPoint,
                    new Font(FONT_NAME, Font.BOLD, FONT_SIZE),
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
        final List<Layer> layers = MainApplication.getLayerManager().getVisibleLayersInZOrder();
        String layerName = "";
        for(int i= layers.size()-1; i>=0; i--) {
            if (layers.get(i) instanceof ImageryLayer) {
                layerName = ((ImageryLayer) layers.get(i)).getInfo().getName();
                break;
            }
        }
        if (layerName.toLowerCase().contains(IMAGERY) || layerName.equals(MAPBOX_SATELLITE)) {
            lineColor = LINE_COLOR_DARK_BACKGROUND;
        }else {
            lineColor = LINE_COLOR_LIGHT_BACKGROUND;
        }
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

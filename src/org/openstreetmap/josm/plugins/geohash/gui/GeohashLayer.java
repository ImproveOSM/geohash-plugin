package org.openstreetmap.josm.plugins.geohash.gui;

import java.awt.Color;
import java.awt.Graphics2D;
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
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;


/**
 *
 *
 * @author laurad
 */
public class GeohashLayer extends Layer {

    private final Collection<Geohash> geohashes;


    public GeohashLayer() {
        super(Configurer.getINSTANCE().getPluginName());

        final Bounds worldBounds = Main.getProjection().getWorldBoundsLatLon();
        geohashes = new GeohashIdentifier().get(Converters.convertBoundsToBoundingBox(worldBounds));
        System.out.println("Geohashes:" + geohashes);
    }

    @Override
    public void paint(final Graphics2D graphics, final MapView mapView, final Bounds bounds) {
        mapView.setDoubleBuffered(true);

        for (final Geohash geohash : geohashes) {

            final Latitude N = geohash.bounds().north();
            final Longitude W = geohash.bounds().west();
            final Latitude S = geohash.bounds().south();
            final Longitude E = geohash.bounds().east();

            final LatLon NW = new LatLon(N.asDegrees(), W.asDegrees());
            final LatLon NE = new LatLon(N.asDegrees(), E.asDegrees());
            final LatLon SW = new LatLon(S.asDegrees(), W.asDegrees());
            final LatLon SE = new LatLon(S.asDegrees(), E.asDegrees());



            /*
             * PaintManager.drawText(graphics, N.asDegrees() + "" + W.asDegrees(), mapView.getPoint(SE), new
             * Font("Verdana", Font.BOLD, 12), new Color(255, 0, 0));
             */


            final GeneralPath path = new GeneralPath();
            path.moveTo(mapView.getPoint(NW).getX(), mapView.getPoint(NW).getY());
            path.lineTo(mapView.getPoint(SW).getX(), mapView.getPoint(SW).getY());
            path.lineTo(mapView.getPoint(SE).getX(), mapView.getPoint(SE).getY());
            path.lineTo(mapView.getPoint(NE).getX(), mapView.getPoint(NE).getY());
            path.lineTo(mapView.getPoint(NW).getX(), mapView.getPoint(NW).getY());
            graphics.setColor(new Color(110, 100, 100));
            graphics.draw(path);


        }

    }


    @Override
    public Icon getIcon() {
        return ImageProvider.get(Configurer.getINSTANCE().getLayerIcon());
    }

    @Override
    public Object getInfoComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Action[] getMenuEntries() {
        final LayerListDialog layerListDialog = LayerListDialog.getInstance();
        return new Action[] { layerListDialog.createActivateLayerAction(this),
                layerListDialog.createShowHideLayerAction(), layerListDialog.createDeleteLayerAction(),
                SeparatorLayerAction.INSTANCE, SeparatorLayerAction.INSTANCE, new LayerListPopup.InfoAction(this) };

    }

    @Override
    public String getToolTipText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isMergable(final Layer arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void mergeFrom(final Layer arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visitBoundingBox(final BoundingXYVisitor arg0) {
        // TODO Auto-generated method stub

    }

}

package org.openstreetmap.josm.plugins.geohash.gui.layer;

import static org.openstreetmap.josm.plugins.geohash.gui.layer.Constants.RENDERING_MAP;
import java.awt.Graphics2D;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.Icon;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.geohash.core.Geohash;
import org.openstreetmap.josm.plugins.geohash.util.cnf.GuiConfig;
import org.openstreetmap.josm.plugins.geohash.util.cnf.IconConfig;


/**
 *
 * @author Beata
 * @version $Revision$
 */
public class GeohashLayer extends Layer {


    private Collection<Geohash> geohashes;
    private final PaintHandler paintHandler;

    public GeohashLayer() {
        super(GuiConfig.getInstance().getPluginShortName());
        this.paintHandler = new PaintHandler();
    }


    @Override
    public void paint(final Graphics2D graphics, final MapView mapView, final Bounds bounds) {
        mapView.setDoubleBuffered(true);
        graphics.setRenderingHints(RENDERING_MAP);
        if (geohashes != null) {
            // TODO: eliminate duplicate things
            for (final Geohash geohash : geohashes) {
                paintHandler.drawGeohash(graphics, mapView, geohash);
            }
        }
    }

    public Collection<Geohash> getGeohashes() {
        return geohashes;
    }


    public void setGeohashes(final Collection<Geohash> geohashes) {
        this.geohashes = geohashes;
    }


    @Override
    public Icon getIcon() {
        return IconConfig.getInstance().getLayerIcon();
    }

    @Override
    public Object getInfoComponent() {
        return GuiConfig.getInstance().getPluginTlt();
    }

    @Override
    public Action[] getMenuEntries() {
        final LayerListDialog layerListDialog = LayerListDialog.getInstance();
        return new Action[] { layerListDialog.createActivateLayerAction(this),
                layerListDialog.createShowHideLayerAction(), layerListDialog.createDeleteLayerAction(),
                SeparatorLayerAction.INSTANCE, new LayerListPopup.InfoAction(this) };
    }

    @Override
    public String getToolTipText() {
        // TODO Auto-generated method stub
        return GuiConfig.getInstance().getPluginLongName();
    }

    @Override
    public boolean isMergable(final Layer layer) {
        return false;
    }

    @Override
    public void mergeFrom(final Layer layer) {
        // this operation is not supported
    }

    @Override
    public void visitBoundingBox(final BoundingXYVisitor visitor) {
        // this operation is not supported
    }
}
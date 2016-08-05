package org.openstreetmap.josm.plugins.geohash.gui.layer;

import java.awt.Graphics2D;
import javax.swing.Action;
import javax.swing.Icon;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.plugins.geohash.util.cnf.GuiConfig;
import org.openstreetmap.josm.plugins.geohash.util.cnf.IconConfig;


/**
 *
 * @author Beata
 * @version $Revision$
 */
public class GeohashLayer extends Layer {

    public GeohashLayer() {
        super(GuiConfig.getInstance().getPluginShortName());
    }

    @Override
    public void paint(final Graphics2D arg0, final MapView arg1, final Bounds arg2) {
        // TODO add drawing logic

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
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
package org.openstreetmap.josm.plugins.geohash.gui;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.event.ActionEvent;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog.LayerListModel;
import org.openstreetmap.josm.gui.dialogs.layer.DeleteLayerAction;
import org.openstreetmap.josm.gui.help.HelpUtil;
import org.openstreetmap.josm.plugins.geohash.util.PreferenceManager;
import org.openstreetmap.josm.tools.ImageProvider;

/**
 *
 * @author laurad
 * @version $Revision$
 */
public class GeohashLayerDeleteAction extends JosmAction {

    private static final long serialVersionUID = 2317473279328069599L;
    private final DeleteLayerAction deleteAction;

    public GeohashLayerDeleteAction(final LayerListModel model) {
        new ImageProvider("dialogs", "delete").getResource().attachImageIcon(this, true);
        putValue(SHORT_DESCRIPTION, tr("Delete the selected layers."));
        putValue(NAME, tr("Delete"));
        putValue("help", HelpUtil.ht("/Dialog/LayerList#DeleteLayer"));
        deleteAction = new DeleteLayerAction(model);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        PreferenceManager.getInstance().setLayerOpenedFlag(false);
        deleteAction.actionPerformed(e);
    }

}

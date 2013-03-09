/*
 Authors : Umberto Griffo <umberto.griffo@gmail.com>
 Linkedin : it.linkedin.com/pub/umberto-griffo/31/768/99
 Twitter : @UmbertoGriffo
 
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. 
 You can obtain a copy of the License at http://www.gnu.org/licenses/gpl-3.0.txt.

 */
package umberto.G_RandomWaypointMobilityModel;

import javax.swing.JPanel;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Umberto Griffo
 */
@ServiceProvider(service = RWMobilityModelUI.class)
public class RWMobilityModelUIImpl implements RWMobilityModelUI {

    private RWMobilityModelPanel panel;
    private RWMobilityModel RWGraph;

    public RWMobilityModelUIImpl() {
    }

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new RWMobilityModelPanel();
        }
        return RWMobilityModelPanel.createValidationPanel(panel);
    }

    @Override
    public void setup(Generator generator) {
        this.RWGraph = (RWMobilityModel) generator;

        //Set UI
        if (panel == null) {
            panel = new RWMobilityModelPanel();
        }
        panel.nodeField.setText(String.valueOf(RWGraph.getNumberOfNodes()));
        panel.maxXField.setText(String.valueOf(RWGraph.getMaxX()));
        panel.maxYField.setText(String.valueOf(RWGraph.getMaxY()));
        panel.maxVField.setText(String.valueOf(RWGraph.getMaxV()));
        panel.minVField.setText(String.valueOf(RWGraph.getMinV()));
        panel.maxWTField.setText(String.valueOf(RWGraph.getMaxWT()));
        panel.iterationField.setText(String.valueOf(RWGraph.getIteration()));
    }

    @Override
    public void unsetup() {
        //Set params
        RWGraph.setNumberOfNodes(Integer.parseInt(panel.nodeField.getText()));
        RWGraph.setMaxX(Double.parseDouble(panel.maxXField.getText()));
        RWGraph.setMaxY(Double.parseDouble(panel.maxYField.getText()));
        RWGraph.setMaxV(Float.parseFloat(panel.maxVField.getText()));
        RWGraph.setMinV(Float.parseFloat(panel.minVField.getText()));
        RWGraph.setMaxWT(Float.parseFloat(panel.maxWTField.getText()));
        RWGraph.setIteration(Integer.parseInt(panel.iterationField.getText()));
        panel = null;
    }
}

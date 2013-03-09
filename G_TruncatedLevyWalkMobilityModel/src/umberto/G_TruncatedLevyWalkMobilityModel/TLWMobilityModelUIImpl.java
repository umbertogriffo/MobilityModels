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
package umberto.G_TruncatedLevyWalkMobilityModel;
import javax.swing.JPanel;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Umberto Griffo
 */
@ServiceProvider(service = TLWMobilityModelUI.class)
public class TLWMobilityModelUIImpl implements TLWMobilityModelUI{
     private TLWMobilityModelPanel panel;
    private TLWMobilityModel TLWGraph;

    public TLWMobilityModelUIImpl() {
    }

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new TLWMobilityModelPanel();
        }
        return TLWMobilityModelPanel.createValidationPanel(panel);
    }

    @Override
    public void setup(Generator generator) {
        this.TLWGraph = (TLWMobilityModel) generator;

        //Set UI
        if (panel == null) {
            panel = new TLWMobilityModelPanel();
        }
        panel.nodeField.setText(String.valueOf(TLWGraph.getNumberOfNodes()));
        panel.maxXField.setText(String.valueOf(TLWGraph.getMaxX()));
        panel.maxYField.setText(String.valueOf(TLWGraph.getMaxY()));
        panel.alphaField.setText(String.valueOf(TLWGraph.getAlpha()));
        panel.betaField.setText(String.valueOf(TLWGraph.getBeta()));
        panel.maxWTField.setText(String.valueOf(TLWGraph.getMaxWT()));
        panel.iterationField.setText(String.valueOf(TLWGraph.getIteration()));
    }

    @Override
    public void unsetup() {
        //Set params
        TLWGraph.setNumberOfNodes(Integer.parseInt(panel.nodeField.getText()));
        TLWGraph.setMaxX(Double.parseDouble(panel.maxXField.getText()));
        TLWGraph.setMaxY(Double.parseDouble(panel.maxYField.getText()));
        TLWGraph.setAlpha(Float.parseFloat(panel.alphaField.getText()));
        TLWGraph.setBeta(Float.parseFloat(panel.betaField.getText()));
        TLWGraph.setMaxWT(Float.parseFloat(panel.maxWTField.getText()));
        TLWGraph.setIteration(Integer.parseInt(panel.iterationField.getText()));
        panel = null;
    }
    
}

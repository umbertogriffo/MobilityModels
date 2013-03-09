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
package umberto.MobilitySimulatorInterface;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;

/**
 * Simulation of a mobility enviroment.
 *
 * @author Umberto Griffo
 */
public interface IMobilityEnviromentSimulator {
    //SET

    public void setNodeX(int nodeID, double x);

    public void setNodeY(int nodeID, double y);

    public void setNodeXWaypoint(int nodeID, double x);

    public void setNodeYWaypoint(int nodeID, double y);

    public void setNodeSpeed(int nodeID, double v);

    public void setNodePause(int nodeID, double p);

    public void setNodeRadius(int nodeID, double r);

    public void setNodeDistance(int nodeID, double r);

    public void setNodeTheta(int nodeID, double t);
    //GET

    public double getNodeSpeed(int nodeID);

    public double getNodePause(int nodeID);

    public double getNodeRadius(int nodeID);

    public double getNodeX(int nodeID);

    public double getNodeY(int nodeID);

    public double getNodeXWaypoint(int nodeID);

    public double getNodeYWaypoint(int nodeID);

    public double getNodeDistance(int nodeID);

    public double getNodeTheta(int nodeID);

    /**
     * Return the subset of node's id, where d is equal or smaller than speed
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    public IntLinkedOpenHashSet arrivedNodesId(int numberOfNodes);

    /**
     * Return the subset of node's id, where speed == 0
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    public IntLinkedOpenHashSet pausedNodesId(int numberOfNodes);

    /**
     * Return the subset of node's id, where speed == 0 && waitTime is smaller
     * than 0
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    public IntLinkedOpenHashSet movingNodesId(int numberOfNodes);

    /**
     * Detect collision.
     *
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     * @param r1
     * @param r2
     * @return True|False
     */
    public boolean hasCollision(double x1, double x2, double y1, double y2, double r1, double r2);
}

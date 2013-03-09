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

import it.unimi.dsi.fastutil.ints.Int2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import java.util.Random;
import java.util.logging.Logger;
import umberto.MobilitySimulatorInterface.IMobilityEnviromentSimulator;

/**
 * Simulation of a mobility enviroment.
 *
 * @author Umberto Griffo
 */
public class RWMobilityEnviromentSimulator implements IMobilityEnviromentSimulator {

    private final static Logger LOGGER = Logger.getLogger("org.umberto.random_waypoint_mobility_model");
    //Area
    private Int2DoubleLinkedOpenHashMap x; //map key:nodeID, value: x coordinate
    private Int2DoubleLinkedOpenHashMap y; //map key:nodeID, value: y coordinate
    private Int2DoubleLinkedOpenHashMap speeds;//map key:nodeID, value: v velocity
    private Int2DoubleLinkedOpenHashMap waitTime;//map key:nodeID, value: wt wait time
    private Int2DoubleLinkedOpenHashMap radius;//map key:nodeID, value: r radius of disk
    private Int2DoubleLinkedOpenHashMap x_waypoint;//map key:nodeID, value: waypoint x coordinate
    private Int2DoubleLinkedOpenHashMap y_waypoint;//map key:nodeID, value: waypoint y coordinate
    private Int2DoubleLinkedOpenHashMap distanceToWaypoint;//map key:nodeID, value: distance d to current waypoint

    public RWMobilityEnviromentSimulator() {
        x = new Int2DoubleLinkedOpenHashMap();
        y = new Int2DoubleLinkedOpenHashMap();
        x_waypoint = new Int2DoubleLinkedOpenHashMap();
        y_waypoint = new Int2DoubleLinkedOpenHashMap();
        speeds = new Int2DoubleLinkedOpenHashMap();
        waitTime = new Int2DoubleLinkedOpenHashMap();
        radius = new Int2DoubleLinkedOpenHashMap();
        distanceToWaypoint = new Int2DoubleLinkedOpenHashMap();
    }
    //SET

    @Override
    public void setNodeX(int nodeID, double x) {
        this.x.put(nodeID, x);
    }

    @Override
    public void setNodeY(int nodeID, double y) {
        this.y.put(nodeID, y);
    }

    @Override
    public void setNodeXWaypoint(int nodeID, double x) {
        this.x_waypoint.put(nodeID, x);
    }

    @Override
    public void setNodeYWaypoint(int nodeID, double y) {
        this.y_waypoint.put(nodeID, y);
    }

    @Override
    public void setNodeSpeed(int nodeID, double v) {
        speeds.put(nodeID, v);
    }

    @Override
    public void setNodePause(int nodeID, double p) {
        waitTime.put(nodeID, p);
    }

    @Override
    public void setNodeRadius(int nodeID, double r) {
        radius.put(nodeID, r);
    }

    @Override
    public void setNodeDistance(int nodeID, double r) {
        distanceToWaypoint.put(nodeID, r);
    }

    @Override
    public void setNodeTheta(int nodeID, double t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    //GET

    @Override
    public double getNodeSpeed(int nodeID) {
        return speeds.get(nodeID);
    }

    @Override
    public double getNodePause(int nodeID) {
        return waitTime.get(nodeID);
    }

    @Override
    public double getNodeRadius(int nodeID) {
        return radius.get(nodeID);
    }

    @Override
    public double getNodeX(int nodeID) {
        return x.get(nodeID);
    }

    @Override
    public double getNodeY(int nodeID) {
        return y.get(nodeID);
    }

    @Override
    public double getNodeXWaypoint(int nodeID) {
        return x_waypoint.get(nodeID);
    }

    @Override
    public double getNodeYWaypoint(int nodeID) {
        return y_waypoint.get(nodeID);
    }

    @Override
    public double getNodeDistance(int nodeID) {
        return distanceToWaypoint.get(nodeID);
    }

    @Override
    public double getNodeTheta(int nodeID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IntLinkedOpenHashSet arrivedNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (distanceToWaypoint.get(nodeID) <= speeds.get(nodeID)) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    @Override
    public IntLinkedOpenHashSet pausedNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (speeds.get(nodeID) == 0) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    @Override
    public IntLinkedOpenHashSet movingNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (speeds.get(nodeID) == 0 && waitTime.get(nodeID) < 0) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    @Override
    public boolean hasCollision(double x1, double x2, double y1, double y2, double r1, double r2) {
        double xDif = Math.abs(x1 - x2);
        double yDif = Math.abs(y1 - y2);
        double distanceSquared = Math.pow(xDif, 2) + Math.pow(yDif, 2);
        return distanceSquared < (r1 + r2) * (r1 + r2);
    }

    /**
     * Return a random number from a Uniform Distribution.
     *
     * @param max max value.
     * @return rNumber number take of a Uniform Distribution.
     */
    public double getFromuniformDistribution(double rangeMin, double rangeMax) {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }
}

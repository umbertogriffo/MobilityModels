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
public class TLWMobilityEnviromentSimulator implements IMobilityEnviromentSimulator {

    private final static Logger LOGGER = Logger.getLogger("org.umberto.random_waypoint_mobility_model");
    //Area
    private double max_x;
    private double max_y;
    private Int2DoubleLinkedOpenHashMap x; //map key:nodeID, value: x coordinate
    private Int2DoubleLinkedOpenHashMap y; //map key:nodeID, value: y coordinate
    private Int2DoubleLinkedOpenHashMap speeds;//map key:nodeID, value: v velocity
    private Int2DoubleLinkedOpenHashMap waitTime;//map key:nodeID, value: wt wait time
    private Int2DoubleLinkedOpenHashMap radius;//map key:nodeID, value: r radius of disk
    private Int2DoubleLinkedOpenHashMap flight_distance;//map key:nodeID, value: flight_distance
    private Int2DoubleLinkedOpenHashMap theta;//map key:nodeID, value: theta

    public TLWMobilityEnviromentSimulator() {
        x = new Int2DoubleLinkedOpenHashMap();
        y = new Int2DoubleLinkedOpenHashMap();
        flight_distance = new Int2DoubleLinkedOpenHashMap();
        theta = new Int2DoubleLinkedOpenHashMap();
        speeds = new Int2DoubleLinkedOpenHashMap();
        waitTime = new Int2DoubleLinkedOpenHashMap();
        radius = new Int2DoubleLinkedOpenHashMap();

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNodeYWaypoint(int nodeID, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        flight_distance.put(nodeID, r);
    }

    @Override
    public void setNodeTheta(int nodeID, double t) {
        theta.put(nodeID, t);
    }

    public void setMaxX(double max) {
        max_x = max;
    }

    public void setMaxY(double max) {
        max_y = max;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getNodeYWaypoint(int nodeID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getNodeDistance(int nodeID) {
        return flight_distance.get(nodeID);
    }

    @Override
    public double getNodeTheta(int nodeID) {
        return theta.get(nodeID);
    }

    /**
     * Return the subset of node's id, where x is smaller than 0
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    public IntLinkedOpenHashSet BouncesMinXNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (x.get(nodeID) < 0) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    /**
     * Return the subset of node's id, where x is larger than Max X.
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    public IntLinkedOpenHashSet BouncesMaxXNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (x.get(nodeID) > max_x) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    /**
     * Return the subset of node's id, where y is smaller than 0
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    public IntLinkedOpenHashSet BouncesMinYNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (y.get(nodeID) < 0) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    /**
     * Return the subset of node's id, where y is larger than Max Y.
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    public IntLinkedOpenHashSet BouncesMaxYNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (y.get(nodeID) > max_y) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    /**
     * Return the subset of node's id, where v is larger than 0 and
     * flight_distance is equal or smaller than v
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
    @Override
    public IntLinkedOpenHashSet arrivedNodesId(int numberOfNodes) {
        IntLinkedOpenHashSet nodeIDArray = new IntLinkedOpenHashSet();
        for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
            if (speeds.get(nodeID) > 0 && flight_distance.get(nodeID) <= 0) {
                nodeIDArray.add(nodeID);
            }
        }
        return nodeIDArray;
    }

    /**
     * Return the subset of node's id, where speed == 0
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
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

    /**
     * Return the subset of node's id, where speed == 0 && waitTime is smaller
     * than 0
     *
     * @param numberOfNodes mumber of nodes.
     * @return nodeIDArray array with a subset of node's id that satisfy the
     * where clausole.
     */
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
     * Define a Random Uniform Distribution.
     *
     * @param max max value.
     * @return rNumber number take of a Uniform Distribution.
     */
    public double uniformDistribution(double rangeMin, double rangeMax) {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    /**
     * Return a random number from the following Power law Distribution: x =
     * [(x1^(n+1) - x0^(n+1))*y + x0^(n+1)]^(1/(n+1)) where y is a uniform
     * variate, n is the distribution power, x0 and x1 define the range of the
     * distribution, and x is your power-law distributed variate.
     *
     * @param n
     * @param x0
     * @param x1
     * @param y
     * @return rNumber number take of a Uniform Distribution.
     */
    public double getFromPowerLawDistribution(double n, double x0, double x1) {
        double rNumber;
        rNumber = Math.abs(Math.pow(Math.random() * (Math.pow(x1, n + 1) - Math.pow(x0, n + 1)) + Math.pow(x0, n + 1), 1 / (n + 1)));
//        LOGGER.log(Level.INFO, "number: {0}", rNumber);
        return rNumber;
    }
}

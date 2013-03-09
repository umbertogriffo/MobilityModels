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

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.type.LongList;
import org.gephi.dynamic.api.DynamicModel.TimeFormat;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Implementation of the Random Waypoint model.
 *
 * @author Umberto Griffo
 */
@ServiceProvider(service = Generator.class)
public class RWMobilityModel implements Generator {

    public static final String TIMESTAMPS = "dnf_timestamps";
    public static final String WEIGTH = "peso";
    //Mobility variable
    protected int numberOfNodes = 116;
    protected double max_x = 150; //Area
    protected double max_y = 50;
    protected double max_v = 1.0; //max speed
    protected double min_v = 0.1; //min speed
    protected double max_wt = 3600; //max wait time
    protected int nr_steps = 100000;
    protected NodeDraft[] nodeArray;
    //Dynamic
    private long start_time = 0;
    private long end_time = 0;
    private long total_time = 0;
    //HashMap EdgeDraft->Timestamps ordered by edgedraft    
    Object2ObjectLinkedOpenHashMap<EdgeDraft, LongLinkedOpenHashSet> map_edgedraft_time;
    Object2ObjectLinkedOpenHashMap<NodeDraft, LongLinkedOpenHashSet> map_nodedraft_time;
    //
    private final static Logger LOGGER = Logger.getLogger("org.umberto.random_waypoint_mobility_model");
    protected ProgressTicket progress;
    protected boolean cancel = false;
    private ContainerLoader container;
    //Cols
    private AttributeColumn col_timestamps;
    private AttributeColumn col_double;

    @Override
    public void generate(ContainerLoader container) {
        RWMobilityEnviromentSimulator Mobile = new RWMobilityEnviromentSimulator();
        /**
         * INIT CONTAINER && NODES MOBILE PROPERTY
         */
        init(container, Mobile);
        /**
         * START SIMULATION
         */
        Progress.start(progress, nr_steps);
        int progressUnit = 0;
        int numberOfCollision = 0;
        IntListIterator it;
        IntLinkedOpenHashSet set;
        for (long i = 1; i <= (nr_steps + 1000) && !cancel; i += 1) {

            for (int nodeID = 0; nodeID < numberOfNodes; nodeID += 1) {
                /**
                 * CHECK UNIT DISK GRAPH
                 */
                if (i >= 1001) {//per colmare il difetto del RWP
                    for (int nodeID2 = 0; nodeID2 < numberOfNodes; nodeID2 += 1) {
                        if (nodeID == nodeID2) {
                            continue;
                        }
                        if (Mobile.hasCollision(Mobile.getNodeX(nodeID), Mobile.getNodeX(nodeID2), Mobile.getNodeY(nodeID), Mobile.getNodeY(nodeID2), Mobile.getNodeRadius(nodeID), Mobile.getNodeRadius(nodeID2))) {
//                        LOGGER.log(Level.INFO, "Collisione tra node:{0} e node: {1} at time {2}", new Object[]{nodeID, nodeID2, i});
                            numberOfCollision += 1;
                            EdgeDraft edgeDraft;
                            NodeDraft sourceNode;
                            if (!container.nodeExists(String.valueOf(nodeID))) {
                                sourceNode = container.factory().newNodeDraft();
                                sourceNode.setId(String.valueOf(nodeID));
                                container.addNode(sourceNode);
                            } else {
                                sourceNode = container.getNode(String.valueOf(nodeID));
                            }
                            NodeDraft targetNode;
                            if (!container.nodeExists(String.valueOf(nodeID2))) {
                                targetNode = container.factory().newNodeDraft();
                                targetNode.setId(String.valueOf(nodeID2));
                                container.addNode(targetNode);
                            } else {
                                targetNode = container.getNode(String.valueOf(nodeID2));
                            }
                            edgeDraft = container.getEdge(sourceNode, targetNode);
                            if (edgeDraft == null) {

                                edgeDraft = container.factory().newEdgeDraft();
                                edgeDraft.setSource(sourceNode);
                                edgeDraft.setTarget(targetNode);
                                edgeDraft.setWeight(1);
                                container.addEdge(edgeDraft);
                                //add first time
                                LongLinkedOpenHashSet times = new LongLinkedOpenHashSet();
                                times.add(i);
                                map_edgedraft_time.put(edgeDraft, times);
                                //Se non faccio questo controllo i nodi isolati
                                // non hanno timestamp associati
                                if (map_nodedraft_time.containsKey(targetNode)) {
                                    LongLinkedOpenHashSet time = map_edgedraft_time.get(edgeDraft);
                                    time.add(i);
                                    map_edgedraft_time.put(edgeDraft, time);
                                } else {
                                    map_nodedraft_time.put(targetNode, times);
                                }
                                if (map_nodedraft_time.containsKey(sourceNode)) {
                                    LongLinkedOpenHashSet time = map_edgedraft_time.get(edgeDraft);
                                    time.add(i);
                                    map_edgedraft_time.put(edgeDraft, time);
                                } else {
                                    map_nodedraft_time.put(sourceNode, times);
                                }
                            } else {
                                //add first times
                                if (map_edgedraft_time.containsKey(edgeDraft)) {
                                    LongLinkedOpenHashSet times = map_edgedraft_time.get(edgeDraft);
                                    times.add(i);
                                    map_edgedraft_time.put(edgeDraft, times);
                                }
                                if (map_nodedraft_time.containsKey(targetNode)) {
                                    LongLinkedOpenHashSet times = map_nodedraft_time.get(targetNode);
                                    times.add(i);
                                    map_nodedraft_time.put(targetNode, times);
                                }
                                if (map_nodedraft_time.containsKey(sourceNode)) {
                                    LongLinkedOpenHashSet times = map_nodedraft_time.get(sourceNode);
                                    times.add(i);
                                    map_nodedraft_time.put(sourceNode, times);
                                }
                            }
                        }
                    }
                }

                if (Mobile.getNodePause(nodeID) <= 0) {
                    /**
                     * UPDATE NODES POSITION
                     */
                    //angle direction
                    double theta = Math.atan2(Mobile.getNodeYWaypoint(nodeID) - Mobile.getNodeY(nodeID), Mobile.getNodeXWaypoint(nodeID) - Mobile.getNodeX(nodeID));
                    Mobile.setNodeX(nodeID, Mobile.getNodeX(nodeID) + (Mobile.getNodeSpeed(nodeID) * Math.cos(theta)));
                    Mobile.setNodeY(nodeID, Mobile.getNodeY(nodeID) + (Mobile.getNodeSpeed(nodeID) * Math.sin(theta)));
//                    LOGGER.log(Level.INFO, "{0} UPDATE POSITION: ({1}-{2}) Way({3}-{4}) time: {5} Radius: {6} Speed: {7} theta: {8}", new Object[]{nodeID, Mobile.getNodeX(nodeID), Mobile.getNodeY(nodeID), Mobile.getNodeXWaypoint(nodeID), Mobile.getNodeYWaypoint(nodeID), Mobile.getNodePause(nodeID), Mobile.getNodeRadius(nodeID), Mobile.getNodeSpeed(nodeID), theta});

                    /**
                     * CALCULATE DISTANCE TO WAYPOINT
                     */
                    Mobile.setNodeDistance(nodeID, Math.sqrt(Math.pow(Mobile.getNodeYWaypoint(nodeID) - Mobile.getNodeY(nodeID), 2) + Math.pow(Mobile.getNodeXWaypoint(nodeID) - Mobile.getNodeX(nodeID), 2)));
//                    LOGGER.log(Level.INFO, "{0} DISTANCE TO WAYPOINT: ({1}-{2}) Way({3}-{4}) time: {5} Radius: {6} Distance:{7} Speed: {8}", new Object[]{nodeID, Mobile.getNodeX(nodeID), Mobile.getNodeY(nodeID), Mobile.getNodeXWaypoint(nodeID), Mobile.getNodeYWaypoint(nodeID), Mobile.getNodePause(nodeID), Mobile.getNodeRadius(nodeID), Mobile.getNodeDistance(nodeID), Mobile.getNodeSpeed(nodeID)});
                }
            }
            /**
             * UPDATE INFO FOR ARRIVED NODES
             */
            set = Mobile.arrivedNodesId(numberOfNodes);
            it = set.iterator();

            while (it.hasNext()) {
                int id = it.nextInt();
                Mobile.setNodeSpeed(id, 0);
                Mobile.setNodePause(id, Mobile.getFromuniformDistribution(0, max_wt));
//                LOGGER.log(Level.INFO, "{0} UPDATE ARRIVED NODES: ({1}-{2}) Way({3}-{4}) time: {5} Radius: {6} Distance:{7} Speed: {8}", new Object[]{id, Mobile.getNodeX(id), Mobile.getNodeY(id), Mobile.getNodeXWaypoint(id), Mobile.getNodeYWaypoint(id), Mobile.getNodePause(id), Mobile.getNodeRadius(id), Mobile.getNodeDistance(id), Mobile.getNodeSpeed(id)});

            }
            set.clear();
            /**
             * UPDATE INFO FOR PAUSED NODES
             */
            set = Mobile.pausedNodesId(numberOfNodes);
            it = set.iterator();

            while (it.hasNext()) {
                int id = it.nextInt();
                Mobile.setNodePause(id, Mobile.getNodePause(id) - 1);
//                LOGGER.log(Level.INFO, "{0} UPDATE PAUSED NODES: ({1}-{2}) Way({3}-{4}) time: {5} Radius: {6} Distance:{7} Speed: {8}", new Object[]{id, Mobile.getNodeX(id), Mobile.getNodeY(id), Mobile.getNodeXWaypoint(id), Mobile.getNodeYWaypoint(id), Mobile.getNodePause(id), Mobile.getNodeRadius(id), Mobile.getNodeDistance(id), Mobile.getNodeSpeed(id)});
            }
            set.clear();
            /**
             * UPDATE INFO FOR Restarting NODES
             */
            set = Mobile.movingNodesId(numberOfNodes);
            it = set.iterator();

            while (it.hasNext()) {
                int id = it.nextInt();
                Mobile.setNodeXWaypoint(id, Mobile.getFromuniformDistribution(0, max_x));
                Mobile.setNodeYWaypoint(id, Mobile.getFromuniformDistribution(0, max_y));
                Mobile.setNodeSpeed(id, Mobile.getFromuniformDistribution(min_v, max_v));
//                LOGGER.log(Level.INFO, "{0} UPDATE RESTARTING NODES: ({1}-{2}) Way({3}-{4}) time: {5} Radius: {6} Distance:{7} Speed: {8}", new Object[]{id, Mobile.getNodeX(id), Mobile.getNodeY(id), Mobile.getNodeXWaypoint(id), Mobile.getNodeYWaypoint(id), Mobile.getNodePause(id), Mobile.getNodeRadius(id), Mobile.getNodeDistance(id), Mobile.getNodeSpeed(id)});
            }
            set.clear();
            Progress.progress(progress, ++progressUnit);
        }
        /*
         * ADD EDGES TIMES
         */
        LOGGER.log(Level.INFO, "total number of timestamp : {0}", total_time);
        if (!map_edgedraft_time.isEmpty()) {
            ObjectBidirectionalIterator iterator = map_edgedraft_time.keySet().iterator();
            while (iterator.hasNext()) {
                EdgeDraft edge = (EdgeDraft) iterator.next();
                setEdgeTimeIntervalsLite(edge, map_edgedraft_time.get(edge));
                edge.setWeight(calculate_weight(edge, map_edgedraft_time.get(edge), total_time));
            }
//            Set<Map.Entry<EdgeDraft, LongLinkedOpenHashSet>> set_edges = map_edgedraft_time.entrySet();
//            for (Map.Entry<EdgeDraft, LongLinkedOpenHashSet> element : set_edges) {
//                setEdgeTimeIntervalsLite(element.getKey(), element.getValue());
//                element.getKey().setWeight(calculate_weight(element.getKey(), element.getValue(), total_time));
////                LOGGER.log(Level.INFO, "Ho calcolato l''arco : {0}", element.getKey());
//            }
//            set_edges.clear();
            map_edgedraft_time.clear();
        }
        /*
         * ADD NODES TIMES
         */
        if (!map_nodedraft_time.isEmpty()) {
            ObjectBidirectionalIterator iterator = map_nodedraft_time.keySet().iterator();
            while (iterator.hasNext()) {
                NodeDraft node = (NodeDraft) iterator.next();
                setNodeTimeIntervalsLite(node, map_nodedraft_time.get(node));
            }
//            Set<Map.Entry<NodeDraft, LongLinkedOpenHashSet>> set_nodes = map_nodedraft_time.entrySet();
//            for (Map.Entry<NodeDraft, LongLinkedOpenHashSet> element : set_nodes) {
//                setNodeTimeIntervalsLite(element.getKey(), element.getValue());
////                LOGGER.log(Level.INFO, "Ho calcolato il nodo : {0}", element.getKey());
//            }
//            set_nodes.clear();
            map_nodedraft_time.clear();
        }
        LOGGER.log(Level.INFO, "Total collision:{0}", numberOfCollision);
        Progress.finish(progress);
        progress = null;
    }

    @Override
    public String getName() {
        return "Random Waypoint Mobility Model";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(RWMobilityModelUI.class);
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

    /**
     * Init loader and nodes property
     *
     * @param container
     * @param mobile
     */
    private void init(ContainerLoader container, RWMobilityEnviromentSimulator mobile) {
        this.container = container;
        this.container.setEdgeDefault(EdgeDefault.UNDIRECTED);
        start_time = 1;
        end_time = nr_steps;
        total_time = (end_time - start_time) + 1;
        /*
         * Set container TimeFormat
         */
        this.container.setTimeFormat(TimeFormat.DOUBLE);
        this.container.setTimeIntervalMin(String.valueOf(start_time));
        this.container.setTimeIntervalMax(String.valueOf(end_time));
        map_edgedraft_time = new Object2ObjectLinkedOpenHashMap<EdgeDraft, LongLinkedOpenHashSet>();
        map_nodedraft_time = new Object2ObjectLinkedOpenHashMap<NodeDraft, LongLinkedOpenHashSet>();
        //NODES
        nodeArray = new NodeDraft[numberOfNodes];
        LongLinkedOpenHashSet first_times = new LongLinkedOpenHashSet();
        for (int i = 0; i < numberOfNodes && !cancel; i += 1) {
            // create nodes
            NodeDraft n = container.factory().newNodeDraft();
            // set node id
            n.setId(String.valueOf(i));
            //set node mobile property
            mobile.setNodeX(i, mobile.getFromuniformDistribution(0, max_x));
            mobile.setNodeY(i, mobile.getFromuniformDistribution(0, max_y));
            mobile.setNodeXWaypoint(i, mobile.getFromuniformDistribution(0, max_x));
            mobile.setNodeYWaypoint(i, mobile.getFromuniformDistribution(0, max_y));
            mobile.setNodePause(i, 0);
            mobile.setNodeSpeed(i, mobile.getFromuniformDistribution(min_v, max_v));
            mobile.setNodeRadius(i, 1);
            nodeArray[i] = n;
            // fill in the graph
            container.addNode(n);
            //add first time            
            first_times.add(i);
            map_nodedraft_time.put(n, first_times);
            first_times.remove(i);
            LOGGER.log(Level.INFO, "node:{0} ({1} - {2}) Way({3} - {4}) wtime: {5} Speed: {6}", new Object[]{i, mobile.getNodeX(i), mobile.getNodeY(i), mobile.getNodeXWaypoint(i), mobile.getNodeYWaypoint(i), mobile.getNodePause(i), mobile.getNodeSpeed(i)});
        }
    }

    /**
     * Method that set a node's time intervals.(Lite version)
     *
     * @param node node to set attribute.
     * @param timestamps Sorted hash map of node's timestamps.
     */
    private void setNodeTimeIntervalsLite(NodeDraft node, LongLinkedOpenHashSet timestamps) {
        node.addTimeInterval(String.valueOf(start_time), String.valueOf(end_time));
        AttributeTable nodeTable = container.getAttributeModel().getNodeTable();
        col_timestamps = nodeTable.getColumn(TIMESTAMPS);
        if (col_timestamps == null) {
            col_timestamps = nodeTable.addColumn(TIMESTAMPS, "Timestamps", AttributeType.LIST_LONG, AttributeOrigin.DATA, "");
        }
        //Set attribute
        if (col_timestamps != null) {
            final LongList timestamp_list = new LongList(timestamps.toArray(new Long[timestamps.size()]));
            node.addAttributeValue(col_timestamps, timestamp_list);
        } else {
            LOGGER.log(Level.SEVERE, "RRWMobilityModel_error_column_already_exist");

        }
    }

    /**
     * Method that set a edge's time intervals.(Lite Version)
     *
     * @param edge edge to set attribute.
     * @param timestamps Sorted hash map of edge's timestamps.
     */
    private void setEdgeTimeIntervalsLite(EdgeDraft edge, LongLinkedOpenHashSet timestamps) {
        edge.addTimeInterval(String.valueOf(start_time), String.valueOf(end_time));
        AttributeTable edgeTable = container.getAttributeModel().getEdgeTable();
        col_timestamps = edgeTable.getColumn(TIMESTAMPS);
        if (col_timestamps == null) {
            col_timestamps = edgeTable.addColumn(TIMESTAMPS, "Timestamps", AttributeType.LIST_LONG, AttributeOrigin.DATA, "");
        }
        //Set attribute
        if (col_timestamps != null) {
            final LongList timestamp_list = new LongList(timestamps.toArray(new Long[timestamps.size()]));
            edge.addAttributeValue(col_timestamps, timestamp_list);
        } else {
            LOGGER.log(Level.SEVERE, "RRWMobilityModel_error_column_already_exist");
        }
    }

    /**
     * Method that calculate the weight of an edge as: ne/n where ne is the
     * number of timestamp of the edge and n is the max number of timestamp.
     *
     * @param edge edge to set attribute.
     * @param timestamps Sorted hash map of edge's timestamps.
     * @param n # total of timestamps
     */
    private float calculate_weight(EdgeDraft edge, Set<Long> timestamps, long n) {
        //Aggiungo una nuova colonna con i pesi a massima precisione
        AttributeTable edgeTable = container.getAttributeModel().getEdgeTable();
        col_double = edgeTable.getColumn(WEIGTH);
        if (col_double == null) {
            col_double = edgeTable.addColumn(WEIGTH, "WeightPrec", AttributeType.BIGDECIMAL, AttributeOrigin.DATA, new BigDecimal(0));
        }
        //Set attribute
        if (col_double != null) {
            BigDecimal time_presente = new BigDecimal(timestamps.size());
            BigDecimal totali = new BigDecimal(n);
            BigDecimal peso = time_presente.divide(totali, 6, RoundingMode.HALF_UP);
            edge.addAttributeValue(col_double, peso);
        } else {
            LOGGER.log(Level.SEVERE, "RRWMobilityModel_error_column_already_exist");
        }
        //Aggiunta dei pesi a bassa precisione
        float weight;
        weight = (float) timestamps.size() / (float) n;
        return weight;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public double getMaxX() {
        return max_x;
    }

    public double getMaxY() {
        return max_y;
    }

    public double getMaxV() {
        return max_v;
    }

    public double getMinV() {
        return min_v;
    }

    public double getMaxWT() {
        return max_wt;
    }

    public int getIteration() {
        return nr_steps;
    }

    public void setNumberOfNodes(int n) {
        if (numberOfNodes < 0) {
            throw new IllegalArgumentException("# of nodes must be greater than 0");
        }
        numberOfNodes = n;
    }

    public void setMaxX(double n) {
        if (max_x < 0) {
            throw new IllegalArgumentException("Max X value must be greater than 0");
        }
        max_x = n;
    }

    public void setMaxY(double n) {
        if (max_y < 0) {
            throw new IllegalArgumentException("Max Y value must be greater than 0");
        }
        max_y = n;
    }

    public void setMaxV(float n) {
        if (max_v < 0) {
            throw new IllegalArgumentException("Max V value must be greater than 0");
        }
        max_v = n;
    }

    public void setMinV(float n) {
        if (min_v < 0) {
            throw new IllegalArgumentException("Min V value must be greater than 0");
        }
        min_v = n;
    }

    public void setMaxWT(float n) {
        if (max_wt < 0) {
            throw new IllegalArgumentException("Max WT value must be greater than 0");
        }
        max_wt = n;
    }

    public void setIteration(int n) {
        if (nr_steps < 0) {
            throw new IllegalArgumentException("# iteration must be greater than 0");
        }
        nr_steps = n;
    }
}

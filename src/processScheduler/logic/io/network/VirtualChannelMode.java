package processScheduler.logic.io.network;

import processScheduler.model.*;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Milena on 04.12.2015.
 */
public class VirtualChannelMode extends AbstractMode {

    public VirtualChannelMode(Random random, TreeMap<Vertex, RouterModel> nodes, Graph graph) {
        super(random, nodes, graph);
        this.system_time = 0;
    }

    @Override
    public void sendMessage() {
        Message message = generate_message();
        System.out.println("Generated message " + message.getMessage_number() + " , amount_package" + message.getPack_amount());
        System.out.println("Source " + message.getSource().getId() + " Target " + message.getTarget().getId());
        if (message.getSource().getRouteTo() == null && message.getSource().getRouteFrom() == null) {
            SysPackage pkg = new SysPackage(message.getTarget(), message.getSource(), 1, message, SysPackage.Mode.CONFIGURE);
            message.getSource().setRouteTo(nodes.get(message.getSource()).getTable().getRouting().get(pkg.getGlobalTarget()));
            sendPackage(message.getSource(), pkg);
            message.getSource().getMessages().add(message);
        }
        else{
            System.out.println(String.format("Vertex %d rejected sending message", message.getSource().getId()));
        }
    }

    public void sendPackage(Vertex source, Package pkg) {
        Message message = pkg.getMsg();
        Vertex nextnode;
        if (pkg instanceof SysPackage && (((SysPackage) pkg).mode == SysPackage.Mode.DECONFIGURE||((SysPackage) pkg).mode == SysPackage.Mode.ACCEPT))
            nextnode = source.getRouteFrom();
        else
            nextnode = source.getRouteTo();
        pkg.setTarget(nextnode);
        pkg.setSource(source);
        Channel neededChannel = graph.findEdge(source, nextnode);
        neededChannel.addToQueue(pkg);
        System.out.println("In the vertex " + source.getId() + " start to process package " + pkg.getPackage_number() + " of message " + message.getMessage_number() + " and has just send to the vertex " + nextnode.getId());

    }

    public void putPackages(Vertex source) {
        Message message = source.getMessages().poll();
        Set<Package> packageSet = message.getPackages();
        for (Package pack : packageSet) {
            pack.setStart_processing_time(system_time);
            sendPackage(message.getSource(), pack);
        }
    }

    @Override
    public void tick() {
        system_time++;
        graph.getEdges().stream().forEach(e -> {
            if (e.update()) {
                if (e instanceof DuplexChannel) {
                    deliverResult(((DuplexChannel) e).getSourceDelivered());
                    deliverResult(((DuplexChannel) e).getTargetDelivered());
                } else {
                    deliverResult(((HalfDuplexChannel) e).getDelivered());
                }
            }
        });

        for (Map.Entry<Integer, Vertex> i : graph.getVertexes().entrySet()) {
            if (!i.getValue().isQueueEmpty()) {
                processDeliveredPackage(i.getValue());
            }
        }
    }

    public void processDeliveredPackage(Vertex v) {
        Package processing_package = v.getQueue().getFirst();
        Message message = processing_package.getMsg();
        try {
            SysPackage sysPkg = (SysPackage) processing_package;
            if (sysPkg.mode == SysPackage.Mode.CONFIGURE) {
                if (v.getRouteFrom() == null && v.getRouteTo() == null) {
                    v.setRouteFrom(sysPkg.getSource());
                    if (!v.equals(sysPkg.getGlobalTarget())) {
                        v.setRouteTo(nodes.get(v).getTable().getRouting().get(sysPkg.getGlobalTarget()));
                        sendPackage(v, sysPkg);
                    } else {
                        processing_package.setWaiting_time(system_time - processing_package.getStart_processing_time());
                        System.out.println(String.format("Virtual channel for message %d is configured", message.getMessage_number()));
                        sendPackage(v, new SysPackage(message.getSource(), v, 1, message, SysPackage.Mode.ACCEPT));
                    }
                    v.getQueue().remove(processing_package);
                } else {
                    v.getQueue().remove(processing_package);
                    v.getQueue().add(sysPkg);
                }
            } else if (sysPkg.mode == SysPackage.Mode.DECONFIGURE) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                }
                else {
                    processing_package.setWaiting_time(system_time - processing_package.getStart_processing_time());
                }
                v.getQueue().remove(processing_package);
                v.setRouteFrom(null);
                v.setRouteTo(null);
            }
            else if (sysPkg.mode == SysPackage.Mode.ACCEPT){
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                }
                else{
                    putPackages(v);
                    processing_package.setWaiting_time(system_time - processing_package.getStart_processing_time());
                }
                v.getQueue().remove(processing_package);
            }
        } catch (ClassCastException e) {
            if (processing_package.getGlobalTarget().equals(v)) {
                message.confirmDelivery(processing_package);
                System.out.println("Package " + processing_package.getPackage_number() + " of message " + message.getMessage_number() + " received at goal (" + v.getId() + ")");
                if (message.getDeliveryMap().entrySet().stream().allMatch(Map.Entry<Package, Boolean>::getValue)) {
                    System.out.println("Message " + message.getMessage_number() + " was delivered to goal (" + v.getId() + ")");
                    sendPackage(v, new SysPackage(message.getSource(), v, 1, message, SysPackage.Mode.DECONFIGURE));
                }
                processing_package.setWaiting_time(system_time - processing_package.getStart_processing_time());
            } else {
                sendPackage(v, processing_package);
            }
            v.getQueue().remove(processing_package);
        }
    }

    public void deliverResult(Package p) {
        if (p != null) {
            p.getTarget().getQueue().add(p);
            Message message = p.getMsg();
            System.out.println("Package " + p.getPackage_number() + " of message " + message.getMessage_number() + " has just received to the vertex ( " + p.getTarget().getId() + " )");
        }
    }

}

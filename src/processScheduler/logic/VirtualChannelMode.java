package processScheduler.logic;

import processScheduler.model.*;

import java.util.Set;

public class VirtualChannelMode extends AbstractMode {

    public VirtualChannelMode(Graph graph, AbstractBuilder builder) {
        super(graph, builder);
    }

    @Override
    public Message sendMessage() {
        Message message = generate_message();
        System.out.println("Generated message " + message.getMessage_number() + " , amount_package" + message.getPack_amount());
        System.out.println("Source " + message.getSource().getId() + " Target " + message.getTarget().getId());
        message.getSource().getMessages().addLast(message);
        return message;
    }

    public void startTransmission(Message message) {
        SysPackage pkg = new SysPackage(message.getTarget(), message.getSource(), message, SysPackage.Mode.CONFIGURE);
        message.getSource().setRouteTo(nodes.get(message.getSource()).getTable().getRouting().get(pkg.getGlobalTarget()));
        sendPackage(message.getSource(), pkg);
        message.getSource().setStatus(1);
        message.getTarget().setStatus(2);
        message.setTransmitted(true);
    }

    public void sendPackage(Vertex source, Package pkg) {
        Message message = pkg.getMsg();
        Vertex nextnode;
        if (pkg instanceof SysPackage && (((SysPackage) pkg).mode == SysPackage.Mode.NOTIFY_VIRTUAL || ((SysPackage) pkg).mode == SysPackage.Mode.ACCEPT))
            nextnode = source.getRouteFrom();
        else
            nextnode = source.getRouteTo();
        pkg.setTarget(nextnode);
        pkg.setSource(source);
        Channel neededChannel = graph.findChannel(source, nextnode);
        neededChannel.addToQueue(pkg);
        System.out.println("In the vertex " + source.getId() + " start to process package " + pkg.getPackage_number() + " of message " + message.getMessage_number() + " and has just send to the vertex " + nextnode.getId());

    }

    public void putPackages(Vertex source) {
        Message message = source.getMessages().poll();
        Set<Package> packageSet = message.getPackages();
        for (Package pack : packageSet) {
            sendPackage(message.getSource(), pack);
        }
    }

    public void processDeliveredPackage(Vertex v) {
        Package processingPackage = v.getQueue().getFirst();
        Message message = processingPackage.getMsg();
        try {
            SysPackage sysPkg = (SysPackage) processingPackage;
            if (sysPkg.mode == SysPackage.Mode.CONFIGURE) {
                if (v.getRouteFrom() == null && v.getRouteTo() == null) {
                    v.setRouteFrom(sysPkg.getSource());
                    if (!v.equals(sysPkg.getGlobalTarget())) {
                        v.setRouteTo(nodes.get(v).getTable().getRouting().get(sysPkg.getGlobalTarget()));
                        sendPackage(v, sysPkg);
                        v.setStatus(3);
                    } else {
                        deliveryBus.post(processingPackage);
                        System.out.println(String.format("Virtual channel for message %d is configured", message.getMessage_number()));
                        sendPackage(v, new SysPackage(message.getSource(), v, message, SysPackage.Mode.ACCEPT));
                    }
                    v.getQueue().remove(processingPackage);
                } else {
                    System.out.println(String.format("Vertex %d is already configured in channel", v.getId()));
                    v.getQueue().remove(processingPackage);
                    v.getQueue().add(sysPkg);
                }
            } else if (sysPkg.mode == SysPackage.Mode.DECONFIGURE) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                } else {
                    deliveryBus.post(processingPackage);
                    deliveryBus.post(message);
                }
                if (v.getStatus() == 1 || v.getStatus() == 3)
                    v.setStatus(0);
                v.getQueue().remove(processingPackage);
                v.setRouteFrom(null);
                v.setRouteTo(null);
            } else if (sysPkg.mode == SysPackage.Mode.ACCEPT) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                } else {
                    deliveryBus.post(processingPackage);
                    putPackages(v);
                }
                v.getQueue().remove(processingPackage);
            } else if (sysPkg.mode == SysPackage.Mode.NOTIFY_VIRTUAL) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                } else {
                    message.notifyDelivery(processingPackage);
                    deliveryBus.post(processingPackage);
                }
                if (message.isNotified()) {
                    System.out.println("Message " + message.getMessage_number() + " was delivered to goal (" + v.getId() + ")");
                    sendPackage(v, new SysPackage(message.getTarget(), v, message, SysPackage.Mode.DECONFIGURE));
                    v.setStatus(0);
                    v.setRouteFrom(null);
                    v.setRouteTo(null);
                }
                v.getQueue().remove(processingPackage);
            }
        } catch (ClassCastException e) {
            if (processingPackage.getGlobalTarget().equals(v)) {
                deliveryBus.post(processingPackage);
                sendPackage(v, new SysPackage(message.getSource(), v, message, SysPackage.Mode.NOTIFY_VIRTUAL));
                message.confirmDelivery(processingPackage);
                System.out.println("Package " + processingPackage.getPackage_number() + " of message " + message.getMessage_number() + " received at goal (" + v.getId() + ")");
            } else {
                sendPackage(v, processingPackage);
            }
            v.getQueue().remove(processingPackage);
        }
    }

    @Override
    public void tick() {
        super.tick();
        graph.getVertexeses().stream().forEach(v -> {
            if (!v.getMessages().isEmpty()) {
                Message pending = v.getMessages().getFirst();
                if (!pending.isTransmitted()&&pending.getSource().getRouteTo() == null && pending.getSource().getRouteFrom() == null
                        &&pending.getTarget().getRouteTo() == null && pending.getTarget().getRouteFrom() == null) {
                    startTransmission(pending);
                }
            }
        });
    }
}

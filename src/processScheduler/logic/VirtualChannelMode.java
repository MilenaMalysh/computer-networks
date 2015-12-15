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
        sendPackage(message.getSource(), pkg);
        message.getSource().setStatus(1);
        message.getTarget().setStatus(2);
        message.setTransmitted(true);
    }

    private Vertex findNext(Vertex source, Vertex target) {
        return nodes.get(source).getTable().getRouting().get(target);
    }

    public void sendPackage(Vertex source, Package pkg) {
        Message message = pkg.getMsg();
        Vertex nextnode;
        nextnode = findNext(source, pkg.getGlobalTarget());
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
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                    v.configure();
                    v.setStatus(3);
                } else {
                    deliveryBus.post(processingPackage);
                    System.out.println(String.format("Virtual channel for message %d is configured", message.getMessage_number()));
                    sendPackage(v, new SysPackage(message.getSource(), v, message, SysPackage.Mode.ACCEPT));
                }
                v.getQueue().remove(processingPackage);
            } else if (sysPkg.mode == SysPackage.Mode.DECONFIGURE) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                    v.deconfigure();
                } else {
                    deliveryBus.post(processingPackage);
                    deliveryBus.post(message);
                }
                if ((v.getStatus() == 1 || v.getStatus() == 3) && !v.isConfigured())
                    v.setStatus(0);
                v.getQueue().remove(processingPackage);
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
                if (!pending.isTransmitted() && pending.getSource().getStatus() ==0 && pending.getTarget().getStatus() ==0 ) {
                    startTransmission(pending);
                }
            }
        });
    }
}

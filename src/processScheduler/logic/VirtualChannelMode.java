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
        message.getSource().getMessages().addLast(message);
        deliveryBus.post(message);
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
        Vertex nextnode;
        nextnode = findNext(source, pkg.getGlobalTarget());
        pkg.setTarget(nextnode);
        pkg.setSource(source);
        Channel neededChannel = graph.findChannel(source, nextnode);
        neededChannel.addToQueue(pkg);
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
        if (!(processingPackage instanceof SysPackage && ((SysPackage) processingPackage).mode == SysPackage.Mode.NOTIFY)) {
            Channel toNotify = v.findPath(processingPackage.getSource());
            toNotify.pushToQueue(new SysPackage(processingPackage.getSource(), v, processingPackage.getMsg(), SysPackage.Mode.NOTIFY));
        }
        try {
            SysPackage sysPkg = (SysPackage) processingPackage;
            if (sysPkg.mode == SysPackage.Mode.CONFIGURE) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                    v.configure();
                    v.setStatus(3);
                } else {
                    deliveryBus.post(processingPackage);
                    sendPackage(v, new SysPackage(message.getSource(), v, message, SysPackage.Mode.ACCEPT));
                }
            } else if (sysPkg.mode == SysPackage.Mode.DECONFIGURE) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                    v.deconfigure();
                } else {
                    deliveryBus.post(processingPackage);
                    deliveryBus.post(message);
                }
                if ((v.getStatus() == 2 && v.equals(processingPackage.getMsg().getTarget()) || v.getStatus() == 3) && !v.isConfigured())
                    v.setStatus(0);
            } else if (sysPkg.mode == SysPackage.Mode.ACCEPT) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                } else {
                    deliveryBus.post(processingPackage);
                    putPackages(v);
                }
            } else if (sysPkg.mode == SysPackage.Mode.NOTIFY_VIRTUAL) {
                if (!v.equals(sysPkg.getGlobalTarget())) {
                    sendPackage(v, sysPkg);
                } else {
                    message.notifyDelivery(processingPackage);
                    deliveryBus.post(processingPackage);
                }
                if (message.isNotified()) {
                    sendPackage(v, new SysPackage(message.getTarget(), v, message, SysPackage.Mode.DECONFIGURE));
                    if (v.isConfigured())
                        v.setStatus(3);
                    else v.setStatus(0);
                }
            } else if (sysPkg.mode == SysPackage.Mode.NOTIFY) {
                deliveryBus.post(sysPkg);
            }
        } catch (ClassCastException e) {
            if (processingPackage.getGlobalTarget().equals(v)) {
                deliveryBus.post(processingPackage);
                sendPackage(v, new SysPackage(message.getSource(), v, message, SysPackage.Mode.NOTIFY_VIRTUAL));
                message.confirmDelivery(processingPackage);
            } else {
                sendPackage(v, processingPackage);
            }
        }
        v.getQueue().remove(processingPackage);
    }

    @Override
    public void tick() {
        super.tick();
        graph.getVertexeses().stream().forEach(v -> {
            if (!v.getMessages().isEmpty()) {
                Message pending = v.getMessages().getFirst();
                if (!pending.isTransmitted() && pending.getSource().getStatus() == 0 && pending.getTarget().getStatus() == 0) {
                    startTransmission(pending);
                }
            }
        });
    }
}

package processScheduler.logic;

import processScheduler.model.Channel;
import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.Set;

public class DatagramMode extends AbstractMode {
    public DatagramMode(Graph graph, AbstractBuilder builder) {
        super(graph, builder);
    }

    @Override
    public Message sendMessage() {
        Message message = generate_message();
        message.getSource().getMessages().add(message);
        deliveryBus.post(message);
        message.setTransmitted(true);
        putPackages(message.getSource());
        message.getSource().setStatus(1);
        message.getTarget().setStatus(2);
        return message;
    }

    @Override
    public void processDeliveredPackage(Vertex v) {
        Package pkg = v.getQueue().pollFirst();
        if(!(pkg instanceof SysPackage && ((SysPackage) pkg).mode == SysPackage.Mode.NOTIFY)) {
            Channel toNotify = v.findPath(pkg.getSource());
            toNotify.pushToQueue(new SysPackage(pkg.getSource(), v, pkg.getMsg(), SysPackage.Mode.NOTIFY));

        }
        if (pkg.getGlobalTarget().equals(v)) {
            Message message = pkg.getMsg();
            deliveryBus.post(pkg);
            if(!(pkg instanceof SysPackage)){
            message.confirmDelivery(pkg);
            if (message.isDelivered()) {
                deliveryBus.post(message);
                System.out.println("Message " + message.getMessage_number() + " was delivered to goal (" + v.getId() + ")");
                if (message.getSource().getStatus() == 1)
                    message.getSource().setStatus(0);
                if (message.getTarget().getStatus() == 2)
                    message.getTarget().setStatus(0);
            }}
        } else {
            sendPackage(v, pkg);
        }
    }

    public void putPackages(Vertex source) {
        Message message = source.getMessages().poll();
        Set<Package> packageSet = message.getPackages();
        for (Package pack : packageSet) {
            sendPackage(message.getSource(), pack);
        }
    }

    public void sendPackage(Vertex source, Package pkg) {
        update_configuration();
        Vertex dest = nodes.get(source).getTable().getRouting().get(pkg.getGlobalTarget());
        pkg.setSource(source);
        pkg.setTarget(dest);
        graph.findChannel(source, dest).addToQueue(pkg);
    }
}

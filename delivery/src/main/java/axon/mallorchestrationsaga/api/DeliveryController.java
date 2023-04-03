package axon.mallorchestrationsaga.api;

import axon.mallorchestrationsaga.aggregate.*;
import axon.mallorchestrationsaga.command.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public DeliveryController(
        CommandGateway commandGateway,
        QueryGateway queryGateway
    ) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(
        value = "/deliveries/{id}/",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture startDelivery(
        @PathVariable("id") String id,
        @RequestBody StartDeliveryCommand startDeliveryCommand
    ) throws Exception {
        System.out.println("##### /delivery/startDelivery  called #####");

        startDeliveryCommand.setDeliveryId(id);
        // send command
        return commandGateway.send(startDeliveryCommand);
    }

    @Autowired
    EventStore eventStore;

    @GetMapping(value = "/deliveries/{id}/events")
    public ResponseEntity getEvents(@PathVariable("id") String id) {
        ArrayList resources = new ArrayList<DeliveryAggregate>();
        eventStore.readEvents(id).asStream().forEach(resources::add);

        CollectionModel<DeliveryAggregate> model = CollectionModel.of(
            resources
        );

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    EntityModel<DeliveryAggregate> hateoas(DeliveryAggregate resource) {
        EntityModel<DeliveryAggregate> model = EntityModel.of(resource);

        model.add(
            Link.of("/deliveries/" + resource.getDeliveryId()).withSelfRel()
        );

        model.add(
            Link.of("/deliveries/" + resource.getDeliveryId() + "/").withRel("")
        );

        model.add(
            Link
                .of("/deliveries/" + resource.getDeliveryId() + "/events")
                .withRel("events")
        );

        return model;
    }
}

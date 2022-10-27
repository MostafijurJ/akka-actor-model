package com.model.config;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SampleBehaviorActor extends AbstractBehavior<String> {

    private SampleBehaviorActor(ActorContext<String> context) {
        super(context);
    }

    public static Behavior<String> create() {
        return Behaviors.setup(SampleBehaviorActor::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("hello", () -> {
                    log.info("Hello matched");
                    return this;
                })
                .onMessageEquals("world", () -> {
                    log.info("World matched");
                    log.info("path matched "+ getContext().getSelf().path() );
                    return this;
                })
                .onMessageEquals("create a child actor", () -> {
                    ActorRef<String> child_actor = getContext().spawn(SampleBehaviorActor.create(), "Child_actor");
                    log.info("invoking child actor -> "+ child_actor.getClass());
                    child_actor.tell("Sending to child actor");
                    return this;
                })
                .onAnyMessage(message -> {
                    log.info(" Message Received -- " + message);
                    return this;
                })
                .build();
    }
}

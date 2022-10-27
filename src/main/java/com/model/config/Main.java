package com.model.config;


import akka.actor.typed.ActorSystem;

public class Main {
    public static void main(String[] args) {
        ActorSystem<String> actorSystem = ActorSystem.create(SampleBehaviorActor.create(),"SampleBehaviorActor");
        actorSystem.tell("Invoking SampleBehaviorActor");
        actorSystem.tell("hello");
        actorSystem.tell("world");
        actorSystem.tell("create a child actor");
    }
}

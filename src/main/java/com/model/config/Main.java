package com.model.config;


import akka.actor.typed.ActorSystem;

public class Main {
    public static void main(String[] args) {
     /*   ActorSystem<SampleBehaviorActor.Command> actorSystem = ActorSystem.create(SampleBehaviorActor.create(),"BiPrimes");
        actorSystem.tell(new SampleBehaviorActor.InstructionCommand("start"));*/

        ActorSystem<ManageBehaviour.Command> actorSystem = ActorSystem.create(ManageBehaviour.create(), "bigPrimes");
        actorSystem.tell(new ManageBehaviour.InstructionCommand("start"));

    }
}

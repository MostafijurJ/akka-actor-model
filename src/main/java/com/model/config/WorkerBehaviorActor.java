package com.model.config;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

@Slf4j
public class WorkerBehaviorActor  extends AbstractBehavior<WorkerBehaviorActor.Command> {

    public static class Command implements Serializable{
        private static final long serialVersionUID = 1L;
        private String message;
        private ActorRef<ManageBehaviour.Command> sender;

        public Command(String message, ActorRef<ManageBehaviour.Command> sender) {
            this.message = message;
            this.sender = sender;
        }

        public String getMessage() {
            return message;
        }

        public ActorRef<ManageBehaviour.Command> getSender() {
            return sender;
        }
    }
    public WorkerBehaviorActor(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create(){
       return Behaviors.setup(WorkerBehaviorActor::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage( command -> {
                    if(command.getMessage().equals("start")){
                        log.info("********* invoking worker actor ************* path -  {}", command.sender.path());
                        BigInteger bigInteger = new BigInteger(200, new Random());
                        BigInteger prime = bigInteger.nextProbablePrime();
                        log.info("big prime {} ", prime);
                        Random random = new Random();
                        if(random.nextInt(5) < 2){
                            log.info("response send to parent actor");
                            command.getSender().tell(new ManageBehaviour.ResultCommand(prime));
                        }else {
                            log.info(" ~~~~~~~~~~~~~~~~~ Failed to send response to parent actor ~~~~~~~~~");
                        }
                    }
                    return  this;
                })

                .build();
    }
}

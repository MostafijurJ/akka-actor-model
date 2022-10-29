package com.model.config;

import akka.actor.Actor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Duration;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.time.LocalTime.now;

@Slf4j
public class ManageBehaviour extends AbstractBehavior<ManageBehaviour.Command> {

    public ManageBehaviour(ActorContext<ManageBehaviour.Command> context) {
        super(context);
    }
    public interface  Command extends Serializable{}

    public static  class InstructionCommand implements ManageBehaviour.Command {
        public static final long serialVersionUID = 1L;
        private String message;

        public InstructionCommand(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }


    public static class ResultCommand implements ManageBehaviour.Command {
        public static final long serialVersionUID = 1L;
        private BigInteger prime;

        public ResultCommand(BigInteger prime) {
            this.prime = prime;
        }

        public BigInteger getPrime() {
            return prime;
        }
    }

    private static class NoResponseCommand implements ManageBehaviour.Command {
        public static final long serialVersionUID = 1L;
        private ActorRef<WorkerBehaviorActor.Command> worker;

        public NoResponseCommand(ActorRef<WorkerBehaviorActor.Command> worker) {
            this.worker = worker;
        }

        public ActorRef<WorkerBehaviorActor.Command> getWorker() {
            return worker;
        }
    }

    public static Behavior<ManageBehaviour.Command> create(){
        return Behaviors.setup(ManageBehaviour::new);
    }
    private SortedSet<BigInteger> primes = new TreeSet<>();



    @Override
    public Receive<ManageBehaviour.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InstructionCommand.class, command ->{
                    if(command.getMessage().equals("start")){
                        for (int i= 0; i < 10 ; i++){
                            log.info("invoking instruction command ->  " + command.getMessage());
                            log.info("worker thread started at " + now() );
                            //TODO call worker actor from here
                            ActorRef<WorkerBehaviorActor.Command> worker = getContext().spawn(WorkerBehaviorActor.create(),"Worker-"+i);
                           // worker.tell(new WorkerBehaviorActor.Command("start", getContext().getSelf()));
                            askWorkerForPrime(worker);
                        }
                    }
                    return Behaviors.same();
                })
                .onMessage(ResultCommand.class, command -> {
                    primes.add(command.getPrime());
                    log.info("Invoking result command with prime size = {} and  number  =  {} ->  ", primes.size(), command.getPrime());
                    if (primes.size() == 10){
                        primes.forEach(System.out::println);
                        log.info("Processing end of prime size = {} ", primes.size());
                    }
                    return Behaviors.same();
                })
                .onMessage(ManageBehaviour.NoResponseCommand.class, command -> {
                    log.info("retrying with worker " + command.getWorker().path());
                   // askWorkerForPrime(command.getWorker());
                    return Behaviors.same();
                })
                .build();
    }



    private void askWorkerForPrime(ActorRef<WorkerBehaviorActor.Command> worker) {
        getContext().ask(Command.class, worker, Duration.ofSeconds(5),
                (me) -> new WorkerBehaviorActor.Command("start", me),
        (response, throwable) -> {
            if(response != null){
                return response;
            }else{
                log.error("worker failed to response {} ", worker.path());
                return new NoResponseCommand(worker);
            }
        });
    }

}

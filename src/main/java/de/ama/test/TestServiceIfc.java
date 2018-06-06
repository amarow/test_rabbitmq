package de.ama.test;

import de.ama.mq.Implementation;
import de.ama.mq.RemoteObject;

import java.util.List;

@Implementation(name="de.ama.test.TestService")
public interface TestServiceIfc extends RemoteObject {
    String getLand();
    List<String> getLaender();
}

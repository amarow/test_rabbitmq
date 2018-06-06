package de.ama.test;

import de.ama.mq.RemoteObject;

public interface RootServiceIfc extends RemoteObject {

    TestServiceIfc getTestService(String name, int id);
}

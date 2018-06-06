package de.ama.test;



public class RootService implements RootServiceIfc {

    public TestServiceIfc getTestService(String name, int id) {
        return new TestService(name,id);
    }
}


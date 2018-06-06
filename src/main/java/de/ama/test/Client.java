package de.ama.test;

import de.ama.mq.client.ClientContext;

import java.util.List;

public class Client {


    public static void main(String[] args) {
        ClientContext.start("ama","modrow","localhost");
        RootServiceIfc rootService = ClientContext.get().createRemoteObject(RootServiceIfc.class);

        TestServiceIfc service = rootService.getTestService("ama",3);

        for (int i = 0; i < 1000; i++) {
            String land = service.getLand();
            System.out.println("land = " + land);
        }

        List<String> laender = service.getLaender();
        System.out.println("laender = " + laender);


    }

}

package de.ama.test;


import de.ama.mq.server.ServerContext;

import java.math.BigDecimal;
import java.util.Date;

public class RootService implements RootServiceIfc {

    private TestService testService = new TestService("singleton",100);

    public TestServiceIfc getNewTestService(String name, int id) {
        return new TestService(name,id);
    }

    @Override
    public int getContextSize() {
        return ServerContext.get().getSize();
    }

    @Override
    public TestService getTestService() {
        return testService;
    }

    public String getTestServiceLand(TestServiceIfc ts){
          return ts.getLand();
    }


    @Override
    public Date getDate() {
        return new Date();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return new BigDecimal("42");
    }
}


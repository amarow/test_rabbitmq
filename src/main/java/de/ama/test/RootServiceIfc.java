package de.ama.test;

import de.ama.mq.RemoteObject;

import java.math.BigDecimal;
import java.util.Date;

public interface RootServiceIfc extends RemoteObject {

    TestServiceIfc getTestService();

    String getTestServiceLand(TestServiceIfc ts);

    Date getDate();

    BigDecimal getBigDecimal();

    TestServiceIfc getNewTestService(String xxx, int i);

    int getContextSize();
}

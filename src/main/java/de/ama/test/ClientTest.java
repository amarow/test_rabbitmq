package de.ama.test;

import de.ama.mq.client.ClientContext;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.List;

public class ClientTest extends TestCase {

private static RootServiceIfc rootService;


    @Override
    protected void setUp() throws Exception {
        if (rootService==null){
            ClientContext.start("mandant1");
            rootService = ClientContext.get().createRemoteObject(RootServiceIfc.class);
        }
    }

    public void testSimpleGetter() {
        assertEquals(new BigDecimal("42"),rootService.getBigDecimal());
        assertNotNull(rootService.getDate());
    }




    public void testService(){

        for (int i = 0; i < 5; i++) {
            String land = rootService.getTestService().getLand();
            assertEquals("singleton:DE",land);
            ClientContext.get().releaseRemoteObject(rootService.getTestService());
        }

        ClientContext.get().releaseRemoteObject(rootService.getTestService());
        List<String> laender = rootService.getTestService().getLaender();
        assertNotNull(laender);
        assertEquals(3,laender.size());
        assertEquals("DE100",laender.get(0));
        assertEquals("GB100",laender.get(1));
        assertEquals("NL100",laender.get(2));


        ClientContext.get().releaseRemoteObject(rootService.getTestService());

    }

    public void testNewReferenz(){
        for (int i = 0; i < 5; i++) {
            TestServiceIfc testService = rootService.getNewTestService("xxx"+i,i);
            assertEquals("xxx"+i+":DE",rootService.getTestServiceLand(testService));

        }
    }

    public void testReferenzArgument(){
        for (int i = 0; i < 5; i++) {
            assertEquals("singleton:DE",rootService.getTestServiceLand(rootService.getTestService()));
        }
    }




}

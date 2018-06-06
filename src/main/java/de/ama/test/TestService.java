package de.ama.test;

import java.util.ArrayList;
import java.util.List;



public class TestService implements TestServiceIfc {
    private String name;
    private int id;

    public TestService(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getLand(){
        return name+":DE";
    }

    public List<String> getLaender(){
        List<String> laender = new ArrayList();
        laender.add("DE"+id);
        laender.add("GB"+id);
        laender.add("NL"+id);
        return laender;
    }

  }


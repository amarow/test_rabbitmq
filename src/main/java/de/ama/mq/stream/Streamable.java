package de.ama.mq.stream;

import java.io.*;

public class Streamable implements Serializable {

    public byte[] toBytes() {
        byte[]bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            oos.reset();
            bytes = baos.toByteArray();
            oos.close();
            baos.close();
        } catch(IOException e){
            bytes = new byte[] {};
            e.printStackTrace();
        }
        return bytes;
    }


    public static Streamable fromBytes(byte[] bytes) {
        Streamable obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = (Streamable)ois.readObject();
            ois.close();
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}

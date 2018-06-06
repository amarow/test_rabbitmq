package de.ama.mq;

/**
 * Marker-Interface zur Kennzeichnung von Remote-Ojekten.
 *
 * Objecte die nicht per Value zum Client gestreamed werden sollen und stattdessen durch einen
 * {@link de.ama.mq.client.RemoteObjectProxy} im Client repräsentiert werden sollen, müssen dieses Interface erfüllen.
 */
public interface RemoteObject {

}

package de.ama.mq;

import java.lang.annotation.Retention;

/**
 * Diese Annotation dient zur genaueren Festlegung der Implementierungsklasse eines {@link RemoteObject}.
 *
 * Wenn diese Annotation nicht verwendet wird, wird der Klassenname aus dem Interface-Klassennmen durch
 * Abschneiden von "Ifc" gewonnen.
 */
public @interface Implementation {
    /**
     * der Klassenname der Serverseitigen Implementierungsklasse
     * @return
     */
    String name();
}

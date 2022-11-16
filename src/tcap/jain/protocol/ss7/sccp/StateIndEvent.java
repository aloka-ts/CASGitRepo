package jain.protocol.ss7.sccp;

public abstract class StateIndEvent extends java.util.EventObject implements java.io.Serializable, java.lang.Cloneable {

    /**
    * Constructs a new StateIndEvent.
    *
    * @param  source the new object source supplied to the constructor
    */
    public StateIndEvent(Object source) {
        super(source);
    }

    /**
    * Sets the source of this event. This method may be used to override the
    * setting of the event source through the constructor, this provides the
    * ability to clone the Event and change the Event source at any time.
    *
    * @param  source The new Object Source value
    */
    public void setSource(Object source) {
        this.source = source;
    }

}
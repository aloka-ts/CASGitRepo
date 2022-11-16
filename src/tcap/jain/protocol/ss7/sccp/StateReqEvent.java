package jain.protocol.ss7.sccp;

public abstract class StateReqEvent extends java.util.EventObject implements java.io.Serializable, java.lang.Cloneable {

    /**
    * Constructs a new StateReqEvent.
    *
    * @param  source the new object source supplied to the constructor
    */
    public StateReqEvent(Object source) {
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
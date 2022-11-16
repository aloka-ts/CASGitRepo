package jain;

/**
* This JAIN InvalidListenerConfigException is thrown if
* a configuration passed for listener registration violates
* any validation check
*
*/
public class InvalidListenerConfigException extends JainException {

    /**
    * Constructs a new <code>InvalidListenerConfigException</code> with
    * the specified detail message.
    * @param <var>msg</var> the detail message
    */
    public InvalidListenerConfigException(String msg) {
        super(msg);
    }

    /**
    * Constructs a new <code>InvalidListenerConfigException</code>
    */
    public InvalidListenerConfigException() {
        super();
    }
}


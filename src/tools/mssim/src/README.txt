/*
 * MS Simulator
 * Version: 1.0
 * Date: 22 March, 2007
 */
  
=======================================================================

Currently following call-flow is supported by this simualtor:

   SIP INVITE   -------> Rx
   SIP 100      <------- Tx
   
   pause    < g seconds >
   
   HTTP GET     <------- Tx
   HTTP 200     -------> Rx
   HTTP ERR     -------> Rx'
   
   pause    < f seconds >
   
   SIP 2xx      <------- Tx
   SIP Err      <------- Tx'
   SIP ACK(2xx) -------> Rx
   
   pause    < i seconds >
   
   HTTP POST    <------- Tx
   HTTP 200     -------> Rx
   HTTP ERR     -------> Rx'
   
   SIP BYE      -------> Rx
   SIP 200      <------- Tx

=======================================================================

INSTALLATION:

Ungip and untar the package in INSTALLROOT directory.

RUNNING:

Run script "run.sh" in INSTALLROOT directory to run the simulator giving
any options as command line arguments. Following are the configurable
options available:

Options:
    -h <LISTEN-IP-ADDR>               [default = localhost]
    -p <LISTEN-PORT>                  [default = 5060]
    -t <TRANSPORT>                    [default = UDP]
    -i <IVR-INTERACTION-PERIOD>       [default = 20]
    -g <GET-REQ-SEND-DELAY>           [default = 0]
    -f <INVITE-FINAL-RESP-SEND-DELAY> [default = 0]
    -c <INVITE-FINAL-RESP-CODE>       [default = 200]
    -help <This help menu>

=======================================================================

TROUBLESHOOTING:

Script does not start ==> Change 'execute' permissions

Java IO exception ==> Check if some other process has take the given port
                      (or default port 5060, if no port is given) for
					  given protocol (or default UDP, if no protocol is
					  given.)

Java home not found ==> Include java path in PATH

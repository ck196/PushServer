package com.kju.push;

public class GCMSenderException extends Exception {
       
       protected String regID = null;

       public GCMSenderException (String message) {
               super(message);
       }
       
       /**
        * @return        The Registration ID of the device of the message that caused this exception.
        */
       public String getRegID() {
               return this.regID;
       }
       
       /**
        * A Registration ID passed to GCM was an empty String.
        * @author Andrew Shirtz
        *
        */
       public static class MissingRegistrationException extends GCMSenderException {
               public MissingRegistrationException(String regID, String message) { super(message); }
       }
       
       /**
        * A Registration ID passed to GCM was invalid.
        * @author Andrew Shirtz
        *
        */
       public static class InvalidRegistrationException extends GCMSenderException {
               public InvalidRegistrationException(String regID, String message) {
                       super(message);
                       this.regID = regID;
               }
       }
       
       /**
        * The Sender ID that was specified on the client does not match the API Key for the server.
        * @author Andrew Shirtz
        *
        */
       public static class MismatchSenderIdException extends GCMSenderException {
               public MismatchSenderIdException(String regID, String message) {
                       super(message);
                       this.regID = regID;
               }
       }
       
       /**
        * The Registration ID points to a device that has been unregistered from GCM.
        * @author Andrew Shirtz
        *
        */
       public static class NotRegisteredException extends GCMSenderException {
               public NotRegisteredException(String regID, String message) {
                       super(message);
                       this.regID = regID;
               }
       }
       
       /**
        * The message that was passed to GCM for delivery was too large to be sent.
        * @author Andrew Shirtz
        *
        */
       public static class MessageTooBigException extends GCMSenderException {
               public MessageTooBigException(String regID, String message) {
                       super(message);
                       this.regID = regID;
               }
       }
       
       /**
        * One of the keys passed into .sendDataMessage() was a reserved word. <br>
        * These reserved words include: <br>
        * from <br>
        * any value prefixed by 'google' <br>
        * registration_ids <br>
        * collapse_key <br>
        * data <br>
        * delay_while_idle <br>
        * time_to_live <br>
        * restricted_package_name <br>
        * dry_run <br>
        * @author Andrew Shirtz
        *
        */
       public static class InvalidDataKeyException extends GCMSenderException {
               public InvalidDataKeyException(String regID, String message) {
                       super(message);
                       this.regID = regID;
               }
       }
       
       /**
        * Received when GCM is having an error. Equivalent to an HTTP 500 error.
        * @author Andrew Shirtz
        *
        */
       public static class InternalServerErrorException extends GCMSenderException {
               public InternalServerErrorException(String regID, String message) {
                       super(message);
                       this.regID = regID;
               }
       }
       
       /**
        * This exception means that the message has been sent to the right device, but the Registration ID you used to send to the device will be invalid soon. <br>
        * For future messages use the Registration ID returned by GCMSenderException.CanonicalIDException.getCanonID().
        * @author Andrew Shirtz
        *
        */
       public static class CanonicalIDException extends GCMSenderException {
               
               private String canonID = null;
               
               public CanonicalIDException(String regID, String canonID, String message) {
                       super(message);
                       this.regID = regID;
                       this.canonID = canonID;
               }
               
               /**
                * @return        The new Registration ID for the device.
                */
               public String getCanonID() {
                       return this.canonID;
               }
       }
}
package com.kju.push;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCMSender {
		
	
		public static void main(String args[]) throws IOException, GCMSenderException{
			String regId = "APA91bF6nE-3XLLVGAHtJlAAPQkH8w7U-gy4k7TzjhmJ5i4clr5TXtowaj7aOoX9vmfE7eRKRd-zu51wBpbAfZgVc-wm2BcGf8ChPUfMn-p-FMVNrkHUjy36kcX0gkVX_CgpP_P2my3z7EIBnmMwHcQanpHrgpJYFg";
			Map<String,String> data = new HashMap<String,String>(10);
			data.put("msg", "good");
			GCMSender.sendMessage(regId, "1", 100, true, data);
		}

        private static final String API_KEY = "AIzaSyBiYEl6CdiY0Vp26zAQ5yaoRufF-sPcL7s";                //This field is the Project API key from code.google.com that matches at least one Sender ID on the device.
        private static final int NUM_RETRIES = 5;                                                                                                //Number of times the Sender will try to send the message to GCM before failing.
        
        private static ThreadLocal<Message.Builder> messageBuilder = null;
        
        private static Message.Builder getLocalBuilderInstance() {
                if (messageBuilder == null || messageBuilder.get() == null) {
                        messageBuilder = new ThreadLocal<Message.Builder>();
                        messageBuilder.set(new Message.Builder());
                }
                return messageBuilder.get();
        }
        
        private static void sendMessage (List<String> regIds) throws IOException, GCMSenderException {
                Sender sender = new Sender(API_KEY);
                MulticastResult results = sender.send(getLocalBuilderInstance().build(), regIds, NUM_RETRIES);
                analyzeResult(regIds, results);
        }
        
        private static void addData(Map<String, String> data) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                        getLocalBuilderInstance().addData(entry.getKey(), entry.getValue());
                }
        }
        
        /**
         * This method sends a message to the devices listed with {@code List<String> regIds}. All other parameters are optional.
         *
         * @param regIds                                        Necessary parameter. The Registration IDs of the devices to which you are sending the message.
         * @param collapseKey                                Optional parameter. An arbitrary string that is used to collapse a group of like messages so that only the last message gets sent to the client.
         * @param timeToLive                                Optional parameter. How long (in seconds) the message should be kept on GCM storage if the device is offline.
         * @param delayWhileIdle                        Optional parameter. If true, the message will not be sent immediately if the device is idle. Default is false.
         * @param data                                                Optional parameter. The key-value pairs of the message's payload data. Must be less than 4kb.
         *
         * @throws IOException                                The message failed to be delivered to GCM.
         * @throws GCMSenderException                The message made it to GCM but was rejected for delivery. See {@link GCMSenderException}.
         */
        public static void sendMessage(List<String> regIds, String collapseKey, Integer timeToLive, Boolean delayWhileIdle, Map<String, String> data) throws IOException, GCMSenderException {
                if (collapseKey != null && !collapseKey.isEmpty())
                        getLocalBuilderInstance().collapseKey(collapseKey);
                if (timeToLive != null && timeToLive >= 0)
                        getLocalBuilderInstance().timeToLive(timeToLive);
                if (delayWhileIdle != null)
                        getLocalBuilderInstance().delayWhileIdle(delayWhileIdle);
                if (data != null)
                        addData(data);
                sendMessage(regIds);
        }
        
        /**
         * This method seds a message to the device with {@code regId}. All other parameters are optional.
         *
         * @param regId                                                Necessary parameter. The Registration ID of the device to which you are sending the message.
         * @param collapseKey                                Optional parameter. An arbitrary string that is used to collapse a group of like messages so that only the last message gets sent to the client.
         * @param timeToLive                                Optional parameter. How long (in seconds) the message should be kept on GCM storage if the device is offline.
         * @param delayWhileIdle                        Optional parameter. If true, the message will not be sent immediately if the device is idle. Default is false.
         * @param data                                                Optional parameter. The key-value pairs of the message's payload data. Must be less than 4kb.
         *
         * @throws IOException                                The message failed to be delivered to GCM.
         * @throws GCMSenderException                The message made it to GCM but was rejected for delivery. See {@link GCMSenderException}.
         */
        public static void sendMessage(String regId, String collapseKey, int timeToLive, boolean delayWhileIdle, Map<String, String> data) throws IOException, GCMSenderException {
                List<String> regIds = new ArrayList<String>(1);
                regIds.add(regId);
                sendMessage(regIds, collapseKey, timeToLive, delayWhileIdle, data);
        }
        
        private static void analyzeResult(List<String> regIds, MulticastResult multicastResults) throws GCMSenderException {
                List<Result> results = multicastResults.getResults();
                for (Result result : results) {
                        String regId = regIds.get(results.indexOf(result));
                        if (result.getErrorCodeName() != null && !result.getErrorCodeName().equals("")) {
                                handleGCMError(regId, result);
                        } else if (result.getCanonicalRegistrationId() != null && result.getCanonicalRegistrationId().equals("")) {
                                throw new GCMSenderException.CanonicalIDException(regId, result.getCanonicalRegistrationId(), "");
                        }
                }
        }
        
        private static void handleGCMError(String regId, Result result) throws GCMSenderException {
                String error = result.getErrorCodeName();
                if (error.equals("MissingRegistration")) {
                        throw new GCMSenderException.MissingRegistrationException(regId, error);
                } else if (error.equals("InvalidRegistration")) {
                        throw new GCMSenderException.InvalidRegistrationException(regId, error);
                } else if (error.equals("MismatchSenderId")) {
                        throw new GCMSenderException.MismatchSenderIdException(regId, error);
                } else if (error.equals("NotRegistered")) {
                        throw new GCMSenderException.NotRegisteredException(regId, error);
                } else if (error.equals("MessageTooBig")) {
                        throw new GCMSenderException.MessageTooBigException(regId, error);
                } else if (error.equals("InvalidDataKey")) {
                        throw new GCMSenderException.InvalidDataKeyException(regId, error);
                } else if (error.equals("InternalServerError")) {
                        throw new GCMSenderException.InternalServerErrorException(regId, error);                        
                }
        }        
}
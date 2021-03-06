package net.threescale.api.v2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Common utility methods.
 */
public class ApiUtil {

    /**
     * Creates an APIException from an ApiHttpResponse.
     * @param log  Where to log the exception.
     * @param response THe ApiHttpResponse to convert.
     * @return  The response converted to an ApiException.
     */
    public static ApiException createExceptionForUnexpectedResponse(Logger log, ApiHttpResponse response) {
        log.info("Unexpected Response: " + response.getResponseCode() +
                " Text: " + response.getResponseText());
        return new ApiException("Server gave unexpected response",
                "Response Code was \"" + response.getResponseCode() + "\"" +
                        "with text \"" + response.getResponseText() + "\"");
    }


    /**
     * Creates a parameter string from a set of transaction data.
     * @param provider_key Provider Key for the message.
     * @param transactions array of transactions.
     * @return  formatted string.
     */
    public static String formatPostData(String provider_key, ApiTransaction[] transactions) {
        StringBuffer post_data = new StringBuffer();
        post_data.append("provider_key=").append(provider_key);
        for (int index = 0; index < transactions.length; index++) {
            post_data.append("&");
            if (transactions[index].getTransactionType() == ApiTransaction.APP_ID_TRANSACTION) {
                post_data.append(formatTransactionDataForPostWithAppId(index, transactions[index]));
            } else {
                post_data.append(formatTransactionDataForPostWithUserKey(index, transactions[index]));
            }
        }
        return post_data.toString();
    }

    public static String formatTransactionDataForPostWithAppId(int index, ApiTransaction transaction) {
        StringBuffer data = new StringBuffer();
        String prefix = "transactions[" + index + "]";

        data.append(prefix);
        data.append("[app_id]=").append(transaction.getApp_id());
        data.append(formatMetrics(prefix, transaction.getMetrics()));
        if (transaction.getTimestamp() != null) {
            data.append("&").append(prefix);
            data.append("[timestamp]=").append(ApiUtil.urlEncodeField(transaction.getTimestamp()));
        }

        return data.toString();
    }

    public static String formatTransactionDataForPostWithUserKey(int index, ApiTransaction transaction) {
        StringBuffer data = new StringBuffer();
        String prefix = "transactions[" + index + "]";

        data.append(prefix);
        data.append("[user_key]=").append(transaction.getApp_id());
        data.append(formatMetrics(prefix, transaction.getMetrics()));
        if (transaction.getTimestamp() != null) {
            data.append("&").append(prefix);
            data.append("[timestamp]=").append(ApiUtil.urlEncodeField(transaction.getTimestamp()));
        }

        return data.toString();
    }

    public static String urlEncodeField(String field_to_encode) {
        try {
            return URLEncoder.encode(field_to_encode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return field_to_encode;
        }
    }

    private static String formatMetrics(String prefix, Map<String, String> metrics) {
        StringBuffer data = new StringBuffer();

        Set<Map.Entry<String, String>> entries = metrics.entrySet();

        for (Map.Entry<String, String> entry : entries) {
            data.append("&").append(prefix).append("[usage]");
            data.append("[").append(entry.getKey()).append("]=").append(entry.getValue());
        }
        return data.toString();
    }

    public static String formatPostData(String provider_key, List<ApiTransaction> transactionsToSend) {
        StringBuffer post_data = new StringBuffer();
        post_data.append("provider_key=").append(provider_key);
        for (int index = 0; index < transactionsToSend.size(); index++) {
            post_data.append("&");
            post_data.append(formatTransactionDataForPostWithAppId(index, transactionsToSend.get(index)));
        }
        return post_data.toString();
    }
}

package com.qmetric.tonto

import groovy.json.JsonSlurper
import spark.Spark

import java.security.SecureRandom

import static spark.Spark.post


class StubLoanRangerService {

    private static final int ID_LENGTH = 16;

    private static final char[] POSSIBLE_ID_CHARS = "abcdefghijklmnopqrstuvwxyz_0123456789".toCharArray()

    def private static String ERROR_CODE;

    static def generateClientReference()
    {
        final StringBuilder id = new StringBuilder();

        final Random random = new SecureRandom();

        for (int charIndex = 0; charIndex < ID_LENGTH; charIndex++)
        {
            id.append(POSSIBLE_ID_CHARS[random.nextInt(POSSIBLE_ID_CHARS.length)]);
        }

        return id.toString();
    }

    static def registerEndpoints() {
        Spark.get("/ping", { request, response ->
            "pong"
        })

        Spark.get("/healthcheck", { request, response ->
            "{}"
        })

        post("/direct-debits", { request, response ->
            def jsonSlurper = new JsonSlurper()
            def object = jsonSlurper.parseText(request.body())

            def customerName = ""
            def accountNumber = object.bankAccount.accountNumber as String
            customerName += object.customer.name.title as String
            customerName += " "
            customerName += object.customer.name.first as String
            customerName += " "
            customerName += object.customer.name.last as String

            def sortCode = object.bankAccount.sortCode as String

            if(sortCode == "938611" && accountNumber == "02149187" && customerName.equalsIgnoreCase("mr test review"))
            {
                '''{"status":"PENDING","clientReference":"''' + generateClientReference() + '''","message":"Transaction is undergoing a review for CCD checks"}'''
            }
            else if (accountNumber == "02149187" && sortCode == "938611")
            {
                '''{"status": "SUCCESS","clientReference": "''' + generateClientReference() + '''","loanReference": "WEGAVEHIMEMONEY01"}'''
            }
            else
            {
                if(ERROR_CODE == null)
                {
                    ERROR_CODE = "CCD_Declined"
                }
                '''{"status":"REJECTED","clientReference":"''' + generateClientReference() + '''","errorCode":"''' + ERROR_CODE + '''","message":"There is a problem with this transaction. Please transfer to the Customer Service team"}'''
            }
        })

        post("/set-error-code", { request, response ->
            try
            {
                def jsonSlurper = new JsonSlurper()
                def object = jsonSlurper.parseText(request.body())

                ERROR_CODE = object.errorCode;
                if(ERROR_CODE == null)
                {
                    throw new Exception("Failed to set error code.")
                }
            }
            catch (Exception e)
            {
                response.status(500);
                return "NOK"
            }
            "OK"
        })

    }



}

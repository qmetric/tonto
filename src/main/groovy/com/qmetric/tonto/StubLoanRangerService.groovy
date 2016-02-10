package com.qmetric.tonto

import groovy.json.JsonSlurper
import spark.Spark

import static spark.Spark.post


class StubLoanRangerService {

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

            def accountNumber = object.bankAccount.accountNumber as String

            if(accountNumber == "99999999") {
                '''{"status":"REJECTED","clientReference":"ffffffffffffffff","errorCode":"CCD_Declined","message":"There is a problem with this transaction. Please transfer to the Customer Service team"}'''
            } else if(accountNumber == "88888888") {
                '''{"status":"PENDING","clientReference":"ffffffffffffffff","message":"Transaction is undergoing a review for CCD checks"}'''
            } else {
                '''{"status": "SUCCESS","clientReference": "ffffffffffffffff","loanReference": "WEGAVEHIMEMONEY01"}'''
            }
        })
    }
}

package com.qmetric.tonto

import spark.Spark

class StartupHandler {
    int port

    StartupHandler(def port) {
        this.port = port
    }

    public def run() {
        Spark.port(port)

        StubLoanRangerService.registerEndpoints()

        Spark.after({ request, response -> response.type("application/json") })
    }
}

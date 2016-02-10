package com.qmetric.tonto


class Main {
    public static void main(String[] args) {
        def cli = new CliBuilder(usage: "Main -p PORT")

        cli.with {
            p longOpt: 'port', args: 1, argName: 'port', 'Port to listen on', required: true
        }

        def options = cli.parse(args)

        if (!options)
        {
            return
        }

        if (options.h)
        {
            cli.usage()
        }
        def port = options.p as Integer

        println "Starting server on port ${port}"

        new StartupHandler(port).run()
    }
}

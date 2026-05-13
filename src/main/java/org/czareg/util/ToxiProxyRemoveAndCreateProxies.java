package org.czareg.util;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;

import java.io.IOException;

public class ToxiProxyRemoveAndCreateProxies {

    public static void main(String[] args) throws IOException {

        ToxiproxyClient client = new ToxiproxyClient("localhost", 8474);

        // cleanup
        for (Proxy p : client.getProxies()) {
            p.delete();
        }

        Proxy proxy = client.createProxy(
                "activemq",
                "0.0.0.0:8666",
                "activemq:61616"
        );
    }
}
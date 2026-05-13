package org.czareg.util;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ToxiProxyRemoveProxies {

    public static void main(String[] args) throws IOException {

        ToxiproxyClient client = new ToxiproxyClient("localhost", 8474);

        // cleanup
        for (Proxy p : client.getProxies()) {
            p.delete();
        }
    }
}
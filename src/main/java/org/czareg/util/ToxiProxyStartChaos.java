package org.czareg.util;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ToxiProxyStartChaos {

    public static void main(String[] args) throws IOException, InterruptedException {

        ToxiproxyClient client = new ToxiproxyClient("localhost", 8474);

        for (Proxy p : client.getProxies()) {
            p.delete();
        }

        Proxy proxy = client.createProxy(
                "activemq",
                "0.0.0.0:8666",
                "activemq:61616"
        );

        while (true) {

            // 1. FULL OUTAGE
            proxy.disable();
            sleep("🔴 FULL OUTAGE", rand());
            proxy.enable();

            // 2. TIMEOUT toxic
            applyTimeout(proxy);
            sleep("⏱ TIMEOUT ACTIVE", rand());
            clear(proxy);

            // 3. LATENCY + JITTER
            applyLatency(proxy);
            sleep("🟡 LATENCY", rand());
            clear(proxy);

            // 4. SLICER (packet fragmentation)
            applySlicer(proxy);
            sleep("🍰 SLICER ACTIVE", rand());
            clear(proxy);

            // 5. BANDWIDTH LIMIT
            applyBandwidth(proxy);
            sleep("🟠 BANDWIDTH LIMIT", rand());
            clear(proxy);

            // 6. RESET PEER (connection reset chaos)
            applyResetPeer(proxy);
            sleep("💥 RESET PEER ACTIVE", rand());
            clear(proxy);

            // 7. RECOVERY
            System.out.println("🟢 CLEAN STATE RESTORED");
            clear(proxy);
            Thread.sleep(rand());
        }
    }

    // ---------------- TOXICS ----------------

    private static void applyTimeout(Proxy proxy) throws IOException {
        clear(proxy);
        proxy.toxics()
                .timeout("timeout-toxic", ToxicDirection.DOWNSTREAM, 2000);
    }

    private static void applyLatency(Proxy proxy) throws IOException {
        clear(proxy);
        proxy.toxics()
                .latency("latency-toxic", ToxicDirection.DOWNSTREAM, 300)
                .setJitter(100);
    }

    private static void applySlicer(Proxy proxy) throws IOException {
        clear(proxy);
        proxy.toxics()
                .slicer("slicer-toxic", ToxicDirection.DOWNSTREAM, 3, 100);
        // 3-byte chunks, 100ms delay between slices
    }

    private static void applyBandwidth(Proxy proxy) throws IOException {
        clear(proxy);
        proxy.toxics()
                .bandwidth("bandwidth-toxic", ToxicDirection.DOWNSTREAM, 500_000);
    }

    private static void applyResetPeer(Proxy proxy) throws IOException {
        clear(proxy);
        proxy.toxics()
                .resetPeer("reset-peer-toxic", ToxicDirection.DOWNSTREAM, 3000);
    }

    // ---------------- UTIL ----------------

    private static void clear(Proxy proxy) {
        try {
            List<? extends Toxic> toxics = proxy.toxics().getAll();
            for (Toxic t : toxics) {
                t.remove();
            }
        } catch (Exception ignored) {
            System.err.println(ignored);
        }
    }

    private static void sleep(String label, long ms) throws InterruptedException {
        System.out.println(label + " -> " + ms + "ms");
        Thread.sleep(ms);
    }

    private static long rand() {
        return ThreadLocalRandom.current().nextLong(1000, 5000);
    }
}
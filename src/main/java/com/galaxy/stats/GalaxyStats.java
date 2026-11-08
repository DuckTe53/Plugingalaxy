package com.galaxy.stats;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GalaxyStats extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        long periodTicks = getConfig().getLong("interval-minutes", 5) * 60L * 20L;
        // İlk gönderim 10 saniye sonra, sonra periyodik
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::pushAll, 200L, periodTicks);
        getLogger().info("GalaxyStats aktif.");
    }

    private void pushAll() {
        String base = getConfig().getString("firebase-url");
        String secret = getConfig().getString("firebase-secret");
        if (base == null || secret == null || secret.startsWith("BURAYA")) {
            getLogger().warning("firebase-secret ayarlanmamis, gonderim atlandi.");
            return;
        }

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            String name = p.getName();
            if (name == null || !name.matches("[A-Za-z0-9_]{3,16}")) continue;
            try {
                int deaths = p.getStatistic(Statistic.DEATHS);
                int kills = p.getStatistic(Statistic.PLAYER_KILLS);
                long ticks = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
                long minutes = ticks / 1200L;

                String json = String.format(
                    "{\"name\":\"%s\",\"deaths\":%d,\"kills\":%d,\"playtimeMinutes\":%d,\"updated\":%d}",
                    name, deaths, kills, minutes, System.currentTimeMillis());

                String url = base + "/stats/" + name.toLowerCase() + ".json?auth=" + secret;
                put(url, json);
            } catch (Exception ex) {
                getLogger().warning(name + " icin istatistik gonderilemedi: " + ex.getMessage());
            }
        }
    }

    private void put(String url, String body) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        try (OutputStream os = con.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        int code = con.getResponseCode();
        con.disconnect();
        if (code != 200) {
            throw new IllegalStateException("HTTP " + code);
        }
    }
}

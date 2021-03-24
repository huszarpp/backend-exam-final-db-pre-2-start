package com.codecool.database;


import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RadioCharts {

    private String DB_URL;
    private String USERNAME;
    private String PASSWORD;

    public RadioCharts(String DB_URL, String USERNAME, String PASSWORD) {
        this.DB_URL = DB_URL;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException sql) {
            System.out.println("Connection failed!");
        }

        return connection;
    }

    public String getMostPlayedSong() {
        Map<String, Integer> playedSongMap = new LinkedHashMap<>();

        try (Connection connection = getConnection()) {
            String sql = "SELECT song, times_aired FROM music_broadcast;";
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();
            while (rst.next()) {
                String song = rst.getString("song");
                Integer timesAired = rst.getInt("times_aired");
                if (playedSongMap.containsKey(song)) {
                    playedSongMap.put(song, playedSongMap.get(song) + timesAired);
                } else {
                    playedSongMap.put(song, timesAired);
                }
            }

            String mostPlayedSong = "";
            int maxTimesAired = Integer.MIN_VALUE;

            for (Map.Entry<String, Integer> entry : playedSongMap.entrySet()) {
                if (entry.getValue() > maxTimesAired) {
                    mostPlayedSong = entry.getKey();
                    maxTimesAired = entry.getValue();
                }
            }
            return mostPlayedSong;
        } catch (SQLException sql) {
            System.out.println("Exception while getMostPlayedSong()");
        }

        return "";
    }

    public String getMostActiveArtist() {
        return "";
    }
}

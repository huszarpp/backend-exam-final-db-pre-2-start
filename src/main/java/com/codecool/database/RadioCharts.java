package com.codecool.database;


import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

class RadioCharts {

    private String DB_URL;
    private String USERNAME;
    private String PASSWORD;

    RadioCharts(String DB_URL, String USERNAME, String PASSWORD) {
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

    String getMostPlayedSong() {
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

    String getMostActiveArtist() {

        try (Connection connection = getConnection()) {
            String sql = "" +
                    "SELECT MAX(num_of_songs) AS max_num_of_songs " +
                    "FROM (SELECT artist, COUNT(DISTINCT song) AS num_of_songs " +
                    "FROM music_broadcast " +
                    "GROUP BY artist);";
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rst = pst.executeQuery();
            int maxNumOfSongs;
            if (rst.next()) {
                maxNumOfSongs = rst.getInt("max_num_of_songs");
            } else {
                return "";
            }

            sql = "SELECT artist, COUNT(song) AS num_of_songs FROM music_broadcast " +
                    "GROUP BY artist HAVING num_of_songs = ?;";
            pst = connection.prepareStatement(sql);
            pst.setInt(1, maxNumOfSongs);
            rst = pst.executeQuery();

            if (rst.next()) {
                return rst.getString("artist");
            }
            return "";
        } catch (SQLException sql) {
            System.out.println("Exception while getMostActiveArtist()");
        }

        return "";
    }
}

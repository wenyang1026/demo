package com.example.demo.Service;

import com.example.demo.Entity.UrlData;
import com.example.demo.Repository.UrlDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
//import java.util.Base64;
import java.util.Optional;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigtable.admin.v2.BigtableInstanceAdminClient;
import com.google.cloud.bigtable.admin.v2.models.Instance;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import com.google.cloud.bigtable.data.v2.models.Filters;


import java.util.List;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;

import java.io.IOException;

@Service
public class UrlShorteningService {
    @Autowired
    private UrlDataRepository urlDataRepository;

//    @Autowired
//    private BigtableDataClient client;


    private final BigtableDataClient client;
    private final String projectId = "rice-comp-539-spring-2022";
    private final String instanceId = "shared-539";
    private final String tableId = "spring24-team3-quiny";

    public UrlShorteningService() throws IOException {
        this.client = BigtableDataClient.create(projectId, instanceId);
    }


    @Transactional
    public String shortenUrl(String longUrl) {

        String shortUrl = null;
        try {
            System.out.println("------------Inserting values-----------");
            shortUrl = insertUrlData(client, tableId, longUrl);

        } catch (Exception e) {
            // Handle exception here or rethrow it as needed
            System.err.println("Error during URL shortening: " + e.getMessage());
            throw new RuntimeException(e); // Rethrowing the exception as unchecked
        }
        // Not closing the client here as per your request
        return shortUrl; // Ensuring return is outside try-catch block

    }

    public  static String insertUrlData( BigtableDataClient client,String tableId, String longUrl) {
        String shortUrl = null;
        try {

            String id = generateShortUrl(longUrl);
            String createdAt = Instant.now().toString();
            shortUrl = "http://quniy/" + id;

            RowMutation mutation = RowMutation.create(tableId, id)
                    .setCell("url_data", "id", id)
                    .setCell("url_data", "created_at", createdAt)
                    .setCell("url_data", "short_url", shortUrl)
                    .setCell("url_data", "long_url", longUrl);

            client.mutateRow(mutation);

        } catch (Exception e) {
            System.err.println("Exception encountered while inserting URL data: " + e.getMessage());
            e.printStackTrace();
        }
        return shortUrl;

    }

    private static String generateShortUrl(String longUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(longUrl.getBytes(StandardCharsets.UTF_8));
            // Use a portion of the hash to keep the URL short
            return bytesToHex(hash).substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void clearTable(BigtableDataClient client, String tableId) {
        try {
            Query query = Query.create(tableId);
            for (Row row : client.readRows(query)) {
                RowMutation mutation = RowMutation.create(tableId, row.getKey().toStringUtf8())
                        .deleteRow();
                client.mutateRow(mutation);
            }
            System.out.println("All rows deleted from table: " + tableId);
        } catch (Exception e) {
            System.err.println("Exception encountered while clearing the table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private LocalDateTime calculateExpireTime() {

        return LocalDateTime.now().plusDays(30);
    }

//    public Optional<String> getOriginalUrl(String shortUrl) {
//        return urlDataRepository.findByShortUrl(shortUrl)
//                .map(UrlData::getLongUrl);
//    }
public String getOriginalUrl(String shortUrl) {

    System.out.println("Inside function ");
    String longUrl = getLongUrl1(client, tableId, shortUrl);
    System.out.println("Long url is "+ longUrl);
    return longUrl;

}

    public static String getLongUrl1(BigtableDataClient client, String tableId, String shortUrl) {
        // Extract the ID from the short URL

        String[] parts = shortUrl.split("/");

        // The ID is the last part of the URL
        String id = parts[parts.length - 1];
        System.out.println("Long URL id: " + id);
        System.out.println("table id: " + tableId);



        String longUrl = null;

        // Create a query to fetch the row with the corresponding ID
        Query query = Query.create(tableId).rowKey(id).filter(Filters.FILTERS.limit().cellsPerColumn(1));

        // Execute the query
        for (Row row : client.readRows(query)) {
            System.out.println("\n");

            // Assuming 'long_url' is the column qualifier for the long URL
            if (row != null && row.getCells("url_data", "long_url").size() > 0) {
                longUrl = row.getCells("url_data", "long_url").get(0).getValue().toStringUtf8();
                break; // Exit the loop once the longUrl is found
            }
        }

        if (longUrl == null) {
            System.out.println("No data found for the given short URL: " + shortUrl);
        }

        return longUrl;
    }

    public static void getTable(BigtableDataClient client, String tableId){

        Query query = Query.create(tableId).limit(26);

        for (Row row : client.readRows(query)) {
            printRow(row);
            System.out.println("\n");

        }
    }

    private static void printRow(Row row) {
        if (row == null) {
            return;
        }
        System.out.printf("Reading data for %s%n", row.getKey().toStringUtf8());
        String colFamily = "";
        for (RowCell cell : row.getCells()) {
            if (!cell.getFamily().equals(colFamily)) {
                colFamily = cell.getFamily();
                System.out.printf("Column Family %s%n", colFamily);
            }

            System.out.printf("\t%s: %s%n",
                    cell.getQualifier().toStringUtf8(),
                    cell.getValue().toStringUtf8());



        }
        System.out.println();

    }

}

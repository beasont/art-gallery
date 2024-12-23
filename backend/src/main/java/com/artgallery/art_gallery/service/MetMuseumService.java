package com.artgallery.art_gallery.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class MetMuseumService {

    private static final String BASE_URL = "https://collectionapi.metmuseum.org/public/collection/v1/objects/";

    public Map<String, Object> fetchArtworkDetails(int objectId) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(BASE_URL + objectId, Map.class);

            if (response == null) return null;

            String primaryImage = (String) response.get("primaryImage");
            String primaryImageSmall = (String) response.get("primaryImageSmall");

            // Exclude artworks with no valid images
            if ((primaryImage == null || primaryImage.isEmpty()) &&
                (primaryImageSmall == null || primaryImageSmall.isEmpty())) {
                return null; // No valid image found
            }

            // Use smaller image if primaryImage is missing
            if (primaryImage == null || primaryImage.isEmpty()) {
                response.put("primaryImage", primaryImageSmall);
            }

            return response;

        } catch (RestClientException e) {
            // Log the error and return null
            System.err.println("Error fetching artwork: " + e.getMessage());
            return null;
        }
    }
}

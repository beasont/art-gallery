package com.artgallery.art_gallery.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class ArtController {

    // We'll store the in-memory list as static so other classes can reference
    private static final List<Map<String, String>> IN_MEMORY_ART_LIST = List.of(
        Map.of("id", "1",  "title", "The Fall of the Rebel Angels", "artist", "Gustave Doré",
               "year", "1866",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/0/04/MILTON_%281695%29_p016_PL_1.jpg"),
        Map.of("id", "2",  "title", "Paradise Lost", "artist", "Gustave Doré",
               "year", "1866",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/9/9d/Paradise_Lost_12.jpg"),
        Map.of("id", "3",  "title", "The Son of Man", "artist", "René Magritte",
               "year", "1964",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://files.catbox.moe/208oob.JPG"),
        Map.of("id", "4",  "title", "The Lovers", "artist", "René Magritte",
               "year", "1928",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://miro.medium.com/v2/resize:fit:900/1*qKcjSr2kzRvXilsLqNrV8Q.jpeg"),
        Map.of("id", "5",  "title", "Le Pandemonium", "artist", "John Martin",
               "year", "1841",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/0/08/John_Martin_Le_Pandemonium_Louvre.JPG"),
        Map.of("id", "6",  "title", "The Great Day of His Wrath", "artist", "John Martin",
               "year", "1853",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/8/8d/MARTIN_John_Great_Day_of_His_Wrath.jpg"),
        Map.of("id", "7",  "title", "Pilgrimage", "artist", "Alan Lee",
               "year", "1990",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://arthur.io/img/art/jpg/00017344fc72b64fb/alan-lee/pilgrimage/large/alan-lee--pilgrimage.jpg"),
        Map.of("id", "8",  "title", "The Trolls See the", "artist", "Alan Lee",
               "year", "1985",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://i.pinimg.com/originals/8e/19/e1/8e19e1a70d8ee5f5b4ebad812e2077d2.jpg"),
        Map.of("id", "9",  "title", "The Scream", "artist", "Edvard Munch",
               "year", "1893",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Edvard_Munch%2C_1893%2C_The_Scream%2C_oil%2C_tempera_and_pastel_on_cardboard%2C_91_x_73_cm%2C_National_Gallery_of_Norway.jpg/800px-Edvard_Munch%2C_1893%2C_The_Scream%2C_oil%2C_tempera_and_pastel_on_cardboard%2C_91_x_73_cm%2C_National_Gallery_of_Norway.jpg"),
        Map.of("id", "10", "title", "The Sick Child", "artist", "Edvard Munch",
               "year", "1885",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://media.tate.org.uk/art/images/work/N/N05/N05035_10.jpg"),
        Map.of("id", "11", "title", "Mona Lisa", "artist", "Leonardo da Vinci",
               "year", "1503",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Mona_Lisa%2C_by_Leonardo_da_Vinci%2C_from_C2RMF_retouched.jpg/800px-Mona_Lisa%2C_by_Leonardo_da_Vinci%2C_from_C2RMF_retouched.jpg"),
        Map.of("id", "12", "title", "The Last Supper", "artist", "Leonardo da Vinci",
               "year", "1498",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/0/08/Leonardo_da_Vinci_%281452-1519%29_-_The_Last_Supper_%281495-1498%29.jpg"),
        Map.of("id", "13", "title", "The Garden of Heavenly Delights", "artist", "Hieronymous Bosch",
               "year", "1500",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/3/38/Jheronimus_Bosch_023.jpg"),
        Map.of("id", "14", "title", "The Last Judgment", "artist", "Hieronymous Bosch",
               "year", "1486",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/4/47/Last_judgement_Bosch.jpg"),
        Map.of("id", "15", "title", "Wanderer Above the Sea of Fog", "artist", "Caspar David Friedrich",
               "year", "1818",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/a/af/Caspar_David_Friedrich_-_Wanderer_above_the_Sea_of_Fog.jpeg"),
        Map.of("id", "16", "title", "The Sea of Ice", "artist", "Caspar David Friedrich",
               "year", "1823",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Caspar_David_Friedrich_-_Das_Eismeer_-_Hamburger_Kunsthalle_-_02.jpg/1200px-Caspar_David_Friedrich_-_Das_Eismeer_-_Hamburger_Kunsthalle_-_02.jpg"),
        Map.of("id", "17", "title", "Fallen Angel", "artist", "Alexandre Cabanel",
               "year", "1847",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/f/f7/Alexandre_Cabanel_-_Fallen_Angel.jpg"),
        Map.of("id", "18", "title", "The Birth of Venus", "artist", "Alexandre Cabanel",
               "year", "1863",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Sandro_Botticelli_-_La_nascita_di_Venere_-_Google_Art_Project_-_edited.jpg/1200px-Sandro_Botticelli_-_La_nascita_di_Venere_-_Google_Art_Project_-_edited.jpg"),
        Map.of("id", "19", "title", "The Expedition", "artist", "Wayne Barlowe",
               "year", "1977",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://files.catbox.moe/nrec64.jpg"),
        Map.of("id", "20", "title", "The Daggerwrist", "artist", "Wayne Barlowe",
               "year", "1976",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://media.surrealismtoday.com/wp-content/uploads/2022/10/12155035/daggerwrist.jpg"),
        Map.of("id", "21", "title", "Brain Salad Surgery", "artist", "H.R Giger",
               "year", "1973",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://media1.jpc.de/image/w1155/front/0/4050538180299.jpg"),
        Map.of("id", "22", "title", "Man Proposes, God Disposes", "artist", "Sir Edwin Landseer",
               "year", "1864",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://images.theconversation.com/files/58700/original/rdkrjk2f-1410359010.jpg")
    );

    public static List<Map<String, String>> getInMemoryArtList() {
        return IN_MEMORY_ART_LIST;
    }

    @GetMapping("/artworks")
    public List<Map<String, String>> getArtworks() {
        return IN_MEMORY_ART_LIST;
    }

    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            ResponseEntity<byte[]> response = restTemplate.exchange(
                url, HttpMethod.GET, null, byte[].class);

            headers.setContentType(response.getHeaders().getContentType());

            return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

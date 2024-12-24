package com.artgallery.art_gallery.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
        Map.of("id", "8",  "title", "The Trolls See the Dawn", "artist", "Alan Lee",
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
        Map.of("id", "13", "title", "The Course of Empire: Destruction", "artist", "Thomas Cole",
               "year", "1836",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://www.dailyartmagazine.com/wp-content/uploads/2022/01/Cole_Thomas_The_Course_of_Empire_Destruction_1836-scaled.jpeg"),
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
        Map.of("id", "18", "title", "The Sabbatic Goat, Baphomet", "artist", "Eliphas Levi",
               "year", "1856",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/8/8d/Baphomet_by_%C3%89liphas_L%C3%A9vi.jpg/1200px-Baphomet_by_%C3%89liphas_L%C3%A9vi.jpg"),
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
               "imageUrl", "http://localhost:5443/api/proxy?url=https://images.theconversation.com/files/58700/original/rdkrjk2f-1410359010.jpg"),
               Map.of("id", "23", "title", "The Night Watch", "artist", "Rembrandt",
               "year", "1642",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/The_Night_Watch_-_HD.jpg/1200px-The_Night_Watch_-_HD.jpg"),
        
        Map.of("id", "24", "title", "The Persistence of Memory", "artist", "Salvador Dalí",
               "year", "1931",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/en/d/dd/The_Persistence_of_Memory.jpg"),
        
        Map.of("id", "25", "title", "The Starry Night", "artist", "Vincent van Gogh",
               "year", "1889",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg/1280px-Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg"),
        
        Map.of("id", "26", "title", "Girl with a Pearl Earring", "artist", "Johannes Vermeer",
               "year", "1665",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/1665_Girl_with_a_Pearl_Earring.jpg/800px-1665_Girl_with_a_Pearl_Earring.jpg"),
        
        Map.of("id", "27", "title", "The Birth of Venus", "artist", "Sandro Botticelli",
               "year", "1485",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Sandro_Botticelli_-_La_nascita_di_Venere_-_Google_Art_Project_-_edited.jpg/2560px-Sandro_Botticelli_-_La_nascita_di_Venere_-_Google_Art_Project_-_edited.jpg"),
        
        Map.of("id", "28", "title", "The School of Athens", "artist", "Raphael",
               "year", "1511",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/4/49/%22The_School_of_Athens%22_by_Raffaello_Sanzio_da_Urbino.jpg/1280px-%22The_School_of_Athens%22_by_Raffaello_Sanzio_da_Urbino.jpg"),
        
        Map.of("id", "29", "title", "The Great Wave off Kanagawa", "artist", "Hokusai",
               "year", "1829",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Tsunami_by_hokusai_19th_century.jpg/2560px-Tsunami_by_hokusai_19th_century.jpg"),
        
        Map.of("id", "30", "title", "The Kiss", "artist", "Gustav Klimt",
               "year", "1907",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/The_Kiss_-_Gustav_Klimt_-_Google_Cultural_Institute.jpg/1280px-The_Kiss_-_Gustav_Klimt_-_Google_Cultural_Institute.jpg"),
        
        Map.of("id", "31", "title", "The Garden of Earthly Delights", "artist", "Hieronymus Bosch",
               "year", "1490",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/9/96/The_Garden_of_earthly_delights.jpg/1280px-The_Garden_of_earthly_delights.jpg"),
        
        Map.of("id", "32", "title", "Liberty Leading the People", "artist", "Eugène Delacroix",
               "year", "1830",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://smarthistory.org/wp-content/uploads/2015/11/dellibwholesm.jpg"),
        
        Map.of("id", "33", "title", "The Anatomy Lesson of Dr. Nicolaes Tulp", "artist", "Rembrandt",
               "year", "1632",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Rembrandt_-_The_Anatomy_Lesson_of_Dr_Nicolaes_Tulp.jpg/1280px-Rembrandt_-_The_Anatomy_Lesson_of_Dr_Nicolaes_Tulp.jpg"),
        
        Map.of("id", "34", "title", "The Third of May 1808", "artist", "Francisco Goya",
               "year", "1814",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/El_Tres_de_Mayo%2C_by_Francisco_de_Goya%2C_from_Prado_thin_black_margin.jpg/1280px-El_Tres_de_Mayo%2C_by_Francisco_de_Goya%2C_from_Prado_thin_black_margin.jpg"),
        
        Map.of("id", "35", "title", "The Night Café", "artist", "Vincent van Gogh",
               "year", "1888",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Vincent_Willem_van_Gogh_076.jpg/1280px-Vincent_Willem_van_Gogh_076.jpg"),
        
        Map.of("id", "36", "title", "The Ambassadors", "artist", "Hans Holbein the Younger",
               "year", "1533",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Hans_Holbein_the_Younger_-_The_Ambassadors_-_Google_Art_Project.jpg/1280px-Hans_Holbein_the_Younger_-_The_Ambassadors_-_Google_Art_Project.jpg"),
        
        Map.of("id", "37", "title", "The Arnolfini Portrait", "artist", "Jan van Eyck",
               "year", "1434",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/3/33/Van_Eyck_-_Arnolfini_Portrait.jpg/800px-Van_Eyck_-_Arnolfini_Portrait.jpg"),
        
        Map.of("id", "38", "title", "Les Demoiselles d'Avignon", "artist", "Pablo Picasso",
               "year", "1907",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/en/thumb/4/4c/Les_Demoiselles_d%27Avignon.jpg/1280px-Les_Demoiselles_d%27Avignon.jpg"),
        
        Map.of("id", "39", "title", "The Card Players", "artist", "Paul Cézanne",
               "year", "1892",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/6/69/Les_Joueurs_de_cartes%2C_par_Paul_C%C3%A9zanne.jpg"),
        
        Map.of("id", "40", "title", "Dance at the Moulin de la Galette", "artist", "Pierre-Auguste Renoir",
               "year", "1876",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/4/40/Auguste_Renoir_-_Dance_at_Le_Moulin_de_la_Galette_-_Mus%C3%A9e_d%27Orsay_RF_2739_%28derivative_work_-_AutoContrast_edit_in_LCH_space%29.jpg"),
        
        Map.of("id", "41", "title", "The Sleeping Gypsy", "artist", "Henri Rousseau",
               "year", "1897",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/0/06/La_Boh%C3%A9mienne_endormie.jpg"),
        
        Map.of("id", "42", "title", "The Oath of the Horatii", "artist", "Jacques-Louis David",
               "year", "1784",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/Jacques-Louis_David%2C_Le_Serment_des_Horaces.jpg/1280px-Jacques-Louis_David%2C_Le_Serment_des_Horaces.jpg"),
        
        Map.of("id", "43", "title", "St. Jerome in His Study", "artist", "Albrecht Dürer",
               "year", "1514",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/a/ac/D%C3%BCrer-Hieronymus-im-Geh%C3%A4us.jpg"),
        
        Map.of("id", "44", "title", "The Death of Marat", "artist", "Jacques-Louis David",
               "year", "1793",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/Death_of_Marat_by_David.jpg/800px-Death_of_Marat_by_David.jpg"),
        
        Map.of("id", "45", "title", "Saturn Devouring His Son", "artist", "Francisco Goya",
               "year", "1819",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Francisco_de_Goya%2C_Saturno_devorando_a_su_hijo_%281819-1823%29.jpg/800px-Francisco_de_Goya%2C_Saturno_devorando_a_su_hijo_%281819-1823%29.jpg"),
        
        Map.of("id", "46", "title", "Impression, Sunrise", "artist", "Claude Monet",
               "year", "1872",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/5/59/Monet_-_Impression%2C_Sunrise.jpg/1280px-Monet_-_Impression%2C_Sunrise.jpg"),
        
        Map.of("id", "47", "title", "The Census at Bethlehem", "artist", "Pieter Bruegel the Elder",
               "year", "1566",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/6/64/Pieter_Bruegel_der_%C3%84ltere_-_Volksz%C3%A4hlung_zu_Bethlehem.jpg/1200px-Pieter_Bruegel_der_%C3%84ltere_-_Volksz%C3%A4hlung_zu_Bethlehem.jpg"),
        
        Map.of("id", "48", "title", "Ophelia", "artist", "John Everett Millais",
               "year", "1851",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/John_Everett_Millais_-_Ophelia_-_Google_Art_Project.jpg/1280px-John_Everett_Millais_-_Ophelia_-_Google_Art_Project.jpg"),
        
        Map.of("id", "49", "title", "The Fighting Temeraire", "artist", "J.M.W. Turner",
               "year", "1839",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/The_Fighting_Temeraire%2C_JMW_Turner%2C_National_Gallery.jpg/1280px-The_Fighting_Temeraire%2C_JMW_Turner%2C_National_Gallery.jpg"),
               Map.of("id", "50", "title", "The Garden", "artist", "Joan Miró",
               "year", "1925",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://www.canvasprintsperth.com.au/wp-content/uploads/2024/03/The-Garden-1925-by-Joan-Miro-LR-scaled.jpg"),

       Map.of("id", "50", "title", "Hypnosis", "artist", "Sascha Schneider",
               "year", "1904",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://www.arthistoryproject.com/site/assets/files/37666/sascha-schneider-hypnosis-1904-obelisk-art-history.jpg"),

        Map.of("id", "51", "title", "Napoleon Crossing the Alps", "artist", "Jacques-Louis David",
               "year", "1801",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://smarthistory.org/wp-content/uploads/2022/08/versailles-1st-version-scaled.jpg"),

        Map.of("id", "52", "title", "The Tower of Babel", "artist", "Pieter Bruegel the Elder",
               "year", "1563",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Pieter_Bruegel_the_Elder_-_The_Tower_of_Babel_%28Vienna%29_-_Google_Art_Project_-_edited.jpg/1280px-Pieter_Bruegel_the_Elder_-_The_Tower_of_Babel_%28Vienna%29_-_Google_Art_Project_-_edited.jpg"),

        Map.of("id", "53", "title", "The Kiss of the Muse", "artist", "Paul Cezanne",
               "year", "1900",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://artsdot.com/ADC/Art-ImgScreen-2.nsf/O/A-8XYQ5Q/$FILE/Paul_cezanne-the_kiss_of_the_muse.Jpg"),

        Map.of("id", "54", "title", "Old Masters", "artist", "Zdislaw Beksinski",
               "year", "1977",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://img1.one.bid/img/5099/1332573_1b.jpg"),

        Map.of("id", "55", "title", "Stitches", "artist", "Zdislaw Beksinski",
               "year", "1989",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://img1.onebid.pl/img/4750/1223542_1b.jpg"),
               
        Map.of("id", "56", "title", "Buffalo Bull, Grazing on the Plains", "artist", "George Catlin",
              "year", "1833",
               "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/George_Catlin_-_Buffalo_Bull%2C_Grazing_on_the_Prairie_-_1985.66.404_-_Smithsonian_American_Art_Museum.jpg/1200px-George_Catlin_-_Buffalo_Bull%2C_Grazing_on_the_Prairie_-_1985.66.404_-_Smithsonian_American_Art_Museum.jpg"),
        
        Map.of("id", "57", "title", "Two Laelaps Battling", "artist", "Charles Robert Knight",
              "year", "1897",
              "imageUrl", "http://localhost:5443/api/proxy?url=https://upload.wikimedia.org/wikipedia/commons/8/8f/Laelaps-Charles_Knight-1897.jpg"),
       Map.of("id", "58", "title", "Raft of the Medusa", "artist", "Theodore Gericault",
              "year", "1819",
              "imageUrl", "http://localhost:5443/api/proxy?url=https://cdn.britannica.com/70/43670-050-2E1ED66F/Raft-of-the-Medusa-canvas-Theodore-Gericault-1819.jpg"),
       Map.of("id", "59", "title", "The Nightmare", "artist", "John Henry Fuseli",
              "year", "1781",
              "imageUrl", "http://localhost:5443/api/proxy?url=https://aspasiasbissas.com/wp-content/uploads/2022/11/john_henry_fuseli_-_the_nightmarefxd.jpg")
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

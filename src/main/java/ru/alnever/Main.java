package ru.alnever;


import org.jsoup.Jsoup;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {


        //ParseFromDownloadPages.getWork();

        /*String pathToFileWithListOfAuthors = "C:\\Users\\Admin\\Downloads\\Авторы.txt";
        Set<String> authorsSet = new HashSet<>();
        authorsSet = ParseFromDownloadPages.getAuthorsFromDownloadPages(pathToFileWithListOfAuthors);

        for (String a:authorsSet) {
            ParseFromWeb.getWorkFromWeb(a);
        }*/

        ParseFromDownloadPages.getCollectionsFromDownloadPages();

        //Set<String> worksSetLite = new HashSet<>();
        //worksSetLite = ParseFromWeb.getWorkFromWeb("https://asdfghj.net/authors/486110/profile/works?p=1");



    }
}
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


        /*String pathToFileWithListOfAuthors = "C:\\Users\\Admin\\Downloads\\Авторы.txt";
        Set<String> authorsSet = new HashSet<>();
        authorsSet = ParseFromDownloadPages.getAuthorsFromDownloadPages(pathToFileWithListOfAuthors);*/


        //String path = "https://asdfg.net/authors/486110/profile/works?p=1";
        //ParseFromWeb.getWorksOfAuthorFromWeb(path);

        //Set<String> worksSetLite = new HashSet<>();
        //worksSetLite = ParseFromWeb.getWorkFromWeb("https://asdfg.net/authors/486110/profile/works?p=1");

        ParseFromDownloadPages.getCollectionsFromDownloadPages();

    }
}
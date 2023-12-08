package ru.alnever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;



public class ParseFromDownloadPages {
    public static Set<String> getAuthorsFromDownloadPages(String path) throws IOException {
        // сюда загружать путь к текстовому файлу с перечислением в столбик путей к сохраненным заранее страницам со списком подписок на авторов
        // вида https://asdfghj.net/home/favourites/authors?p=1
        // в итоге получаем список ссылок на скачивание всех работ одного автора

        List<String> authors = new ArrayList<>();
        String fileName;

        //загружаем текстовый файл с перечислением в столбик путей к сохраненным заранее страницам со списком подписок на авторов
        BufferedReader reader = new BufferedReader(new FileReader(path));
        while (reader.ready()) {
            fileName = reader.readLine();
            authors.add(fileName);
        }

        List<String> listAuthors = new ArrayList<>();
        Set<String> authorsSet = new HashSet<>();

        //для каждого пути из текстового файла читаем файл по этому пути
        for (String author:authors) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(author));
            String line;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                listAuthors.add(line);
            }
            bufferedReader.close();
        }

        //для каждого автора выдергиваем ссылку на него в нужном формате
        for (String l:listAuthors) {
            if (l.contains("https://asdfghj.net/authors/")) {//
                l = l.substring(l.indexOf("<"), l.indexOf(">"));
                l = l.replace("<a data-savepage-href=\"/authors/", "https://asdfghj.net/authors/");
                l = l.substring(0, l.indexOf("\""));
                l = l + "/profile/works?p=1";
                authorsSet.add(l);
            }
        }

        System.out.println("Строки списка авторов отформатированы под скачивание списка работ, количество авторов - " + authorsSet.size());

        //записываем в файл на диске ссылки на всех авторов из подписок
        try {
            String pathToFile = "C:\\Users\\Admin\\Downloads\\listOfAuthors.txt";
            Files.createFile(Paths.get(pathToFile));

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathToFile));
            bufferedWriter.write("Строки списка авторов отформатированы под скачивание списка работ, количество авторов - " + authorsSet.size() + System.lineSeparator());
            for (String w:authorsSet) {
                bufferedWriter.write(w + System.lineSeparator());
            }
            bufferedWriter.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        //возвращаем список ссылок на всех авторов
        return authorsSet;

    }

    public static void getListOfWorksFromDownloadPages(String path) throws IOException {
        // сюда загружать путь к текстовому файл с перечислением в столбик путей к сохраненным заранее страницам со списком работ
        // автора вида https://asdfghj.net/authors/486110/profile/works?p=1
        // в итоге получаем список ссылок на скачивание всех работ одного автора

        List<String> paths = new ArrayList<>();
        String fileName;
        String authorName = null;

        //загружаем текстовый файл с перечислением в столбик путей к сохраненным заранее страницам со списком работ автора
        BufferedReader reader = new BufferedReader(new FileReader(path));
        while (reader.ready()) {
            fileName = reader.readLine();
            paths.add(fileName);
        }
        reader.close();

        List<String> listWorks = new ArrayList<>();
        Set<String> worksSet = new HashSet<>();

        // для каждого пути к загруженным заранее страницам со списком работ автора читаем из файла строки
        for (String p : paths) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String line;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                listWorks.add(line);
            }
            bufferedReader.close();
        }

        //из каждого сохраненного в список файла вытаскиваем ссылки на работы и приводим их в нужный вид для ручного скачивания - на полную версию для печати
        for (String l:listWorks) {
            if (l.contains("visit-link\" data-savepage-href=\"/readfic/")) {
                l = l.substring(l.indexOf("<"), l.indexOf(">"));
                l = l.replace("<a class=\"visit-link\" data-savepage-href=\"/readfic/", "");
                l = l.replace("\" href=\"", "?");
                l = l.substring(l.indexOf("?") + 1, l.indexOf("\""));
                l = l + "/all-parts#all-parts-content";
                worksSet.add(l);
            }
            if (l.contains("visit-link\" href=\"https://asdfghj.net/readfic/")) {
                l = l.substring(l.indexOf("<"), l.indexOf(">"));
                l = l.replace("<a class=\"visit-link\" href=\"", "");
                l = l.substring(0, l.indexOf("\""));
                l = l + "/all-parts#all-parts-content";
                worksSet.add(l);
            }
            if (l.contains("<title>")) {
                l = l.substring(l.indexOf(">") + 1, l.indexOf(" – профиль"));
                authorName = l;
                System.out.println("имя автора " + l);
            }
        }
        System.out.println("Строки отсортированы");

        for (String l:worksSet) {
            System.out.println("worksSet " + l);
        }

        System.out.println("Строки отформатированы под скачивание, количество работ автора - " + worksSet.size());
        // выгрузка файла со списками на диск
        String pathFolder = "C:\\Users\\Admin\\Downloads\\" + authorName;
        Files.createDirectories(Paths.get(pathFolder));
        String pathFile = "C:\\Users\\Admin\\Downloads\\" + authorName + "\\" + authorName + ".txt";
        Files.createFile(Paths.get(pathFile));

    }

    public static void getCollectionsFromDownloadPages() throws IOException {
        //получаем список всех своих сборников из заранее загруженной страницы с сайта вида https://asdfghj.net/home/collections
        //String path = "C:\\Users\\Admin\\Downloads\\Сборники\\Сборники.html";
        String path = "C:\\Users\\Admin\\Downloads\\Сборники\\Сборники.htm";

        Set<String> worksSetFromCollections = new HashSet<>();
        Set<String> worksSetPrivateCollections = new HashSet<>();

        File file = new File(path);

        try {
            Document document = Jsoup.parse(file);

            Elements collectionthumb = document.select(".collection-thumb");
            String[] strings = collectionthumb.html().split("\n");
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].contains("<span title=\"Личный сборник\">")) {
                    strings[i] = strings[i].substring(strings[i].indexOf("https://asdfghj.net/collections/"), strings[i].indexOf("\"", strings[i].indexOf("https://asdfghj.net/collections/")));
                    worksSetPrivateCollections.add(strings[i]);
                }
            }

            var list = document.select("a");
            for (var element : list) {
                String work = element.toString();
                if (work.contains("https://asdfghj.net/collections/")) {
                    work = work.substring(work.indexOf("https://asdfghj.net/collections/"), work.lastIndexOf("\""));
                    worksSetFromCollections.add(work);
                }
            }

            for (String element : worksSetPrivateCollections) {
                if (worksSetFromCollections.contains(element)) {
                    worksSetFromCollections.remove(element);
                }
            }

            System.out.println("Найдено сборников: " + worksSetFromCollections.size());
            System.out.println("Найдено личных сборников: " + worksSetPrivateCollections.size());

            //а потом для каждого элемента из worksSetFromCollections создать папку, получить список работ для этой папки из сборника и отправить это в метод getTextFromWeb()

//            try {
//                String pathFolder = "C:\\Users\\Admin\\Downloads\\Сборники\\" + titleElement;
//                Files.createDirectories(Paths.get(pathFolder));
//                String pathFile = "C:\\Users\\Admin\\Downloads\\Сборники\\" + titleElement + "\\" + titleElement + ".txt";
//                Files.createFile(Paths.get(pathFile));
//
//                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFile));
//                bufferedWriter.write(authorName);
//                bufferedWriter.write("Количество работ автора = " + worksSet.size());
//                for (String w:worksSet) {
//                    bufferedWriter.write(w + System.lineSeparator());
//                }
//                bufferedWriter.close();
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

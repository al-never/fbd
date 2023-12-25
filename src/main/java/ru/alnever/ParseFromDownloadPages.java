package ru.alnever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;



public class ParseFromDownloadPages {
    public static Set<String> getAuthorsFromDownloadPages(String path) throws IOException {
        // сюда загружать путь к текстовому файлу с перечислением в столбик путей к сохраненным заранее страницам со списком подписок на авторов
        // вида https://asdfg.net/home/favourites/authors?p=1
        // в итоге получаем список авторов вида https://asdfg.net/authors/486110/profile/works?p=1

        List<String> authors = new ArrayList<>();
        String fileName;

        //загружаем текстовый файл с перечислением в столбик путей к сохраненным заранее страницам со списком подписок на авторов
        BufferedReader reader = new BufferedReader(new FileReader(path));
        while (reader.ready()) {
            fileName = reader.readLine();
            authors.add(fileName);
        }

        Set<String> authorsSet = new HashSet<>();

        //для каждого пути из текстового файла читаем файл по этому пути и для каждого автора выдергиваем ссылку на него в нужном формате
        for (String author : authors) {
            File file = new File(author);
            Document document = Jsoup.parse(file);

            Elements collectionthumb = document.select("div.data-table-row").select("a[href]");
            for (Element el : collectionthumb) {
                String work = el.attr("abs:href");
                if (work.contains("https://asdfg.net/authors/")) {
                    authorsSet.add(work);
                }
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
        // автора вида https://asdfg.net/authors/486110/profile/works?p=1
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

        Set<String> worksSetFirstPage = new HashSet<>();
        Set<String> worksSetFullVersion = new HashSet<>();

        // для каждого пути к загруженным заранее страницам со списком работ автора читаем из файла строки
        for (String p : paths) {
            File file = new File(p);
            try {
                Document document = Jsoup.parse(file);

                var author = document.select("div.user-name").text();
                authorName = author.toString();
                System.out.println("Имя автора = " + authorName);
                var pages = document.select("div.paging-description").text();
                String[] words = pages.split("из ");
                int pagesCount = Integer.parseInt(words[words.length - 1]);
                System.out.println("Количество страниц с работами у автора = " + pagesCount);

                //из каждого сохраненного в список файла вытаскиваем ссылки на работы и приводим их в нужный вид для ручного скачивания - на полную версию для печати
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    String work = link.attr("abs:href");
                    if (work.contains("https://asdfg.net/readfic/")) {
                        worksSetFirstPage.add(work); //этот список нужен для дальнейшей автоматизированной закачки
                        work = work + "/all-parts#all-parts-content";
                        worksSetFullVersion.add(work); //этот список нужен для дальнейшего ручного сохранения версий для печати
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (String l:worksSetFirstPage) {
            System.out.println("worksSetFirstPage " + l);
        }
        for (String l:worksSetFullVersion) {
            System.out.println("worksSetFullVersion " + l);
        }

        System.out.println("Строки отформатированы под скачивание, количество работ автора - " + worksSetFirstPage.size());

         //выгрузка файла со списками на диск
            try {
                String pathFolder = "C:\\Users\\Admin\\Downloads\\" + authorName;
                Files.createDirectories(Paths.get(pathFolder));
                String pathFileLite = "C:\\Users\\Admin\\Downloads\\" + authorName + "\\" + authorName + "-FirstPage.txt";
                Files.createFile(Paths.get(pathFileLite));
                String pathFileFull = "C:\\Users\\Admin\\Downloads\\" + authorName + "\\" + authorName + "-FullVersion.txt";
                Files.createFile(Paths.get(pathFileFull));

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFileLite));
                bufferedWriter.write(authorName + System.lineSeparator());
                bufferedWriter.write("Количество работ автора = " + worksSetFirstPage.size() + System.lineSeparator());

                for (String w : worksSetFirstPage) {
                    bufferedWriter.write(w + System.lineSeparator());
                }
                bufferedWriter.close();

                BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(pathFileFull));
                bufferedWriter2.write(authorName + System.lineSeparator());
                bufferedWriter2.write("Количество работ автора = " + worksSetFullVersion.size() + System.lineSeparator());
                for (String w : worksSetFullVersion) {
                    bufferedWriter2.write(w + System.lineSeparator());
                }
                bufferedWriter2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public static void getCollectionsFromDownloadPages() throws IOException {
        //получаем список всех своих сборников из заранее загруженной страницы с сайта вида https://asdfg.net/home/collections
        //String path = "C:\\Users\\Admin\\Downloads\\Сборники\\Сборники.html";
        String path = "C:\\Users\\Admin\\Downloads\\Сборники\\Сборники.htm";

        HashMap<String, String> worksAndNamesPrivateCollections = new HashMap<>();
        HashMap<String, String> worksAndNamesCollections = new HashMap<>();

        File file = new File(path);

        try {
            Document document = Jsoup.parse(file);

            Elements collectionthumb = document.select(".collection-thumb");
            String[] strings = collectionthumb.html().split("\n");
            for (int i = 0; i < strings.length; i++) {
                //System.out.println(strings[i]);
                if (strings[i].contains("<span title=\"Личный сборник\">")) {
                    String adress = null;
                    String name = null;
                    adress = strings[i].substring(strings[i].indexOf("https://asdfg.net/collections/"), strings[i].indexOf("\"", strings[i].indexOf("https://asdfg.net/collections/")));
                    name = strings[i].substring(strings[i].indexOf("\">") + 2, strings[i].indexOf("</a>"));
                    worksAndNamesPrivateCollections.put(adress, name);
                }
                if (strings[i].contains("<span title=\"Для всех\">")) {
                    String adress = null;
                    String name = null;
                    adress = strings[i].substring(strings[i].indexOf("https://asdfg.net/collections/"), strings[i].indexOf("\"", strings[i].indexOf("https://asdfg.net/collections/")));
                    name = strings[i].substring(strings[i].indexOf("\">") + 2, strings[i].indexOf("</a>"));
                    worksAndNamesCollections.put(adress, name);
                }

            }

            System.out.println("Найдено сборников: " + worksAndNamesCollections.size());
            System.out.println("Найдено личных сборников: " + worksAndNamesPrivateCollections.size());

            //а потом для каждого элемента из worksSetFromCollections создать папку, получить адрес списка работ для этой папки из
            // сборника и отправить это в метод getWorksInCollectionFromWeb

            for (Map.Entry<String, String> entry : worksAndNamesCollections.entrySet()) {
                try {
                    String name = entry.getValue();
                    String adress = entry.getKey();
                    String pathFolder = "C:\\Users\\Admin\\Downloads\\Сборники\\" + name;
                    Files.createDirectories(Paths.get(pathFolder));
                    String pathFile = "C:\\Users\\Admin\\Downloads\\Сборники\\" + name + "\\" + name + ".txt";
                    Files.createFile(Paths.get(pathFile));

                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFile));
                    bufferedWriter.write(name + System.lineSeparator());
                    bufferedWriter.write(adress);
                    bufferedWriter.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

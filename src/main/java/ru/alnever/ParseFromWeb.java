package ru.alnever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseFromWeb {


    public static Set<String> getWorksOfAuthorFromWeb(String uri) throws IOException {
        //на вход получить имя автора и список ссылок на все работы автора по ссылке на первую
        //страницу с работами автора типа "https://asdfg.net/authors/486110/profile/works?p=1";
        //на выходе - созданная папка по имени автора и в ней два текстовых файла со списком ссылок на все работы на полные версии и для загрузки

        Set<String> worksSetLite = new HashSet<>();
        Set<String> worksSetFull = new HashSet<>();

        try {
            var document = Jsoup
                    .connect(uri)
                    .userAgent("Mozilla")
                    .get();

            //получаем имя автора и количество страниц с работами у автора:
            var author = document.select("div.user-name").text();
            String authorName = author.toString();
            //System.out.println("Имя автора = " + authorName);
            var pages = document.select("div.paging-description").text();
            String[] words = pages.split("из ");
            int pagesCount = Integer.parseInt(words[words.length - 1]);
            System.out.println("Количество страниц с работами у автора = " + pagesCount);

            List<String> listPagesOfWorks = new ArrayList<>();
            String str = uri;
            int a = str.indexOf("=");
            str = str.substring(0, a + 1);

            for (int i = 1; i <= pagesCount; i++) {
                String str2 = str + i;
                listPagesOfWorks.add(str2);
            }

            //для каждой страницы с работами у автора получаем список ссылок на сами работы
            for (String u : listPagesOfWorks) {
                try {
                    var document2 = Jsoup
                            .connect(u)
                            .userAgent("Mozilla")
                            .get();

                    Elements links = document2.select("a[href]");
                    for (Element link : links) {
                        String work = link.attr("abs:href");
                        if (work.contains("https://asdfg.net/readfic/")) {
                            worksSetLite.add(work); //этот список нужен для дальнейшей автоматизированной закачки
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (String u : listPagesOfWorks) {
                try {
                    var document2 = Jsoup
                            .connect(u)
                            .userAgent("Mozilla")
                            .get();

                    Elements links = document2.select("a[href]");
                    for (Element link : links) {
                        String work = link.attr("abs:href");
                        if (work.contains("https://asdfg.net/readfic/")) {
                            work = work + "/all-parts#all-parts-content";
                            worksSetFull.add(work); //этот список нужен для дальнейшего ручного сохранения версий для печати
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            //создаем в Загрузках папку по имени автора и внутри нее - список ссылок на полные версии работ и на первые страницы(для закачки)
            try {
                String path = "C:\\Users\\Admin\\Downloads\\" + authorName;
                Files.createDirectories(Paths.get(path));
                String pathFileLite = "C:\\Users\\Admin\\Downloads\\" + authorName + "\\" + authorName + "-Lite.txt";
                Files.createFile(Paths.get(pathFileLite));
                String pathFileFull = "C:\\Users\\Admin\\Downloads\\" + authorName + "\\" + authorName + "-Full.txt";
                Files.createFile(Paths.get(pathFileFull));

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFileLite));
                bufferedWriter.write(authorName + System.lineSeparator());
                bufferedWriter.write("Количество работ автора = " + worksSetFull.size() + System.lineSeparator());

                for (String w : worksSetLite) {
                    bufferedWriter.write(w + System.lineSeparator());
                }
                bufferedWriter.close();

                BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(pathFileFull));
                bufferedWriter2.write(authorName + System.lineSeparator());
                bufferedWriter2.write("Количество работ автора = " + worksSetFull.size() + System.lineSeparator());
                for (String w : worksSetFull) {
                    bufferedWriter2.write(w + System.lineSeparator());
                }
                bufferedWriter2.close();


            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return worksSetLite;
    }

    public static void getWorksInCollectionFromWeb() {
        //получаем на вход путь к папке, в которую будем сохранять коллекцию, и ссылку на саму публичную коллекцию вида https://asdfg.net/collections/4780562
        //на выходе - скачанная в папку по имени сборника коллекция работ

        String uri = "https://asdfg.net/collections/4780562";

        Set<String> worksSetFromCollection = new HashSet<>();

        try {
            Document document = Jsoup
                    .connect(uri)
                    .userAgent("Mozilla")
                    .get();

            //получаем весь документ:
            //System.out.println(document);

            Elements links = document.select("a[href]");
            for (Element link : links) {
                String work = link.attr("abs:href");
                if (work.contains("https://asdfg.net/readfic/")) {
                    worksSetFromCollection.add(work);
                }
            }

            Elements list = document.select("h1.mb-0");
            String collectionName = null;
            if (list.size() == 1) {
                collectionName = list.text();
            } else {
                throw new Exception("Ошибка: найдено более одного имени сборника, исправить код выбора имени");
            }
            System.out.println(collectionName);


            try {
                String pathFolder = "C:\\Users\\Admin\\Downloads\\Сборники\\" + collectionName;
                Files.createDirectories(Paths.get(pathFolder));
                String pathFile = "C:\\Users\\Admin\\Downloads\\Сборники\\" + collectionName + "\\" + collectionName + ".txt";
                Files.createFile(Paths.get(pathFile));

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFile));
                bufferedWriter.write(collectionName);
                bufferedWriter.write("Количество работ в сборнике = " + worksSetFromCollection.size());
                for (String w:worksSetFromCollection) {
                    bufferedWriter.write(w + System.lineSeparator());
                }
                bufferedWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            //!!! + добавить для каждого сборника запуск метода скачать работы getTextFromWeb

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getTextFromWeb() throws IOException {
        //загружаем все тексты автора
        //String uri = "https://asdfg.net/collections/4780562";
        String uri = "https://asdfg.net/readfic/5825121";

        Set<String> worksSetFromCollections = new HashSet<>();

        try {
            var document = Jsoup
                    .connect(uri)
                    .userAgent("Mozilla")
                    .get();

            //получаем весь документ:
            String webpage = document.html();
            //Element el = document.html();
            //System.out.print(document);

            //убрать переносы типа CRLF в тексте
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));
            String s = document.outerHtml().replaceAll("\\r\\n", "<br>");
            System.out.print(s);

            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\Downloads\\Сборники\\123.html"));
                bufferedWriter.write(s);
                bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

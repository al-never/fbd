package ru.alnever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseFromWeb {


    public static Set<String> getWorksOfAuthorFromWeb(String uri) throws IOException {
        //получить имя автора и список ссылок на все работы автора по ссылке на первую
        //страницу с работами автора типа "https://asdfghj.net/authors/486110/profile/works?p=1";

        Set<String> worksSetLite = new HashSet<>();
        Set<String> worksSetFull = new HashSet<>();

        try {
            var document = Jsoup
                    .connect(uri)
                    .userAgent("Mozilla")
                    .get();

            //получаем имя автора и количество страниц с работами у автора:
            var author = document.select("title").text();
            String authorName = author.toString().replace(" – профиль автора фанфиков и ориджиналов – Книга Фанфиков", "");
            //System.out.println("Имя автора = " + authorName);
            var pages = document.select("div.paging-description");
            String[] words = pages.text().split("из ");
            int pagesCount = Integer.parseInt(words[words.length - 1]);
            //System.out.println("Количество страниц с работами у автора = " + pagesCount);

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

                    var works = document2.select("a.visit-link");

                    for (var w : works) {
                        String work = w.toString();
                        if (work.contains("<a class=\"visit-link\" href=\"/readfic/")) {
                            work = work.substring(work.indexOf("<"), work.indexOf(">"));
                            work = work.replace("<a class=\"visit-link\" href=\"/readfic/", "https://asdfghj.net/readfic/");
                            work = work.substring(0, work.indexOf("\""));
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

                    var works = document2.select("a.visit-link");

                    for (var w : works) {
                        String work = w.toString();
                        if (work.contains("<a class=\"visit-link\" href=\"/readfic/")) {
                            work = work.substring(work.indexOf("<"), work.indexOf(">"));
                            work = work.replace("<a class=\"visit-link\" href=\"/readfic/", "https://asdfghj.net/readfic/");
                            work = work.substring(0, work.indexOf("\""));
                            work = work + "/all-parts#all-parts-content";
                            worksSetFull.add(work); //этот список нужен для дальнейшего ручного сохраненияверсий для печати
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


    public static void getTextFromWeb() throws IOException {
        //загружаем все тексты автора
        //String uri = "https://asdfghj.net/collections/24878228";
        String uri = "https://asdfghj.net/readfic/13045083/34234932#part_content";

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
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
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

package ru.alnever;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
        //на выходе - скачанный в папку по имени сборника текстовый файл со ссылками на все работы сборника вида https://asdfg.net/readfic/5825121

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
 
        //String uri = "https://asdfg.net/readfic/5825121/19287575"; //одна часть из работы 
        String uri = "https://asdfg.net/readfic/5825121"; // содержание      


        List<String> partsWork = new ArrayList<>(); //сюда записываются ссылки на главы работы

        try {
            var document = Jsoup
                    .connect(uri)
                    .userAgent("Mozilla")
                    .get();

//            File file = new File(uri3);
//            Document document = Jsoup.parse(file);

            //получаем весь документ:
            //String webpage = document.html();
            //Element el = document.html();
            //System.out.print(document);

            //убрать переносы типа CRLF в тексте
            String nameWork;
            String nameAuthor;
            var author = document.selectFirst("a.creator-username").text();
            nameAuthor = author.toString();
            var work = document.select("title").text();
            nameWork = work.toString().replaceAll("\\r\\n", "").replaceAll(" +", " ");
            System.out.println("Имя автора = " + nameAuthor);
            System.out.println(nameWork);
            document.outputSettings(new Document.OutputSettings().prettyPrint(false));



//            if (uri3.contains("all-parts#all-parts-content")) {
//                try {
//                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\Downloads\\Сборники\\" + nameWork + ", автор " + nameAuthor + ".htm"));
//                    bufferedWriter.write(s);
//                    bufferedWriter.close();
//                    return;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
            Elements link = document.select("a.part-link");            
            boolean flag = link.size() == 0;
            //System.out.println(flag);

            if (flag)  {
                System.out.println("Работает при объеме работы из одной части");
                for( Element element : document.select("div.fb-ads-block") )
                {
                    element.remove();
                }
                for( Element element : document.select("div.mb-15") )
                {
                    element.remove();
                }
                for( Element element : document.select("div.navigation-to-fanfic-parts-container") )
                {
                    element.remove();
                }
                for( Element element : document.select("nav.nav-info") )
                {
                    element.remove();
                }
                for( Element element : document.select("div.help-box") )
                {
                    element.remove();
                }
                for( Element element : document.select("div.blog-area") )
                {
                    element.remove();
                }

                String str = document.outerHtml().replaceAll("\\r\\n", "<br>");
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\Downloads\\Сборники\\" + nameWork + ", автор " + nameAuthor + ".htm"));
                    bufferedWriter.write(str);
                    bufferedWriter.close();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Работает при содержании");
            String str = document.outerHtml().replaceAll("\\r\\n", "<br>");

            for (Element element:link) {
                String url = element.attr("abs:href"); //чтобы получить полные ссылки из неполных
                if (!url.contains("all-parts-content") & !partsWork.contains(url)) {
                    partsWork.add(url);
                }
            }


            var document2 = Jsoup
                    .connect(partsWork.get(0))
                    .timeout(10000)
                    .userAgent("Mozilla")
                    .get();

            //System.out.print(document2);

            document2.outputSettings(new Document.OutputSettings().prettyPrint(false));
            for( Element element : document2.select("div.fb-ads-block") )
            {
                element.remove();
            }
            for( Element element : document2.select("div.mb-15") )
            {
                element.remove();
            }
            for( Element element : document2.select("div.navigation-to-fanfic-parts-container") )
            {
                element.remove();
            }
            for( Element element : document2.select("nav.nav-info") )
            {
                element.remove();
            }
            for( Element element : document2.select("div.help-box") )
            {
                element.remove();
            }
            for( Element element : document2.select("div.blog-area") )
            {
                element.remove();
            }
            String str2 = document2.outerHtml().replaceAll("\\r\\n", "<br>");
            String[] strings = str2.split("\n");

            List<String> allWork = new ArrayList<>();
            for (int i = 0; i < strings.length; i++) {
                allWork.add(strings[i]);
            }            

            System.out.print("Часть 1 работы загружена" + System.lineSeparator());

            int counter = 0;
            for (int i = 1; i < partsWork.size(); i++) {
                System.out.print("Начинаю загружать часть " + (i + 1) + " из " + partsWork.size() + System.lineSeparator());
                try {
                    var document3 = Jsoup
                            .connect(partsWork.get(i))
                            .timeout(10000)
                            .userAgent("Mozilla")
                            .get();

                    int fin = allWork.lastIndexOf("            </article>") - 1;

                    document3.outputSettings(new Document.OutputSettings().prettyPrint(false));
                    for( Element element : document3.select("div.fb-ads-block") )
                    {
                        element.remove();
                    }
                    for( Element element : document3.select("div.mb-15") )
                    {
                        element.remove();
                    }
                    for( Element element : document3.select("div.navigation-to-fanfic-parts-container") )
                    {
                        element.remove();
                    }
                    for( Element element : document3.select("nav.nav-info") )
                    {
                        element.remove();
                    }
                    for( Element element : document3.select("div.help-box") )
                    {
                        element.remove();
                    }
                    for( Element element : document3.select("div.blog-area") )
                    {
                        element.remove();
                    }
                    String str3 = document3.select("article.mb-15").outerHtml().replaceAll("\\r\\n", "<br>");
                    allWork.add(fin, str3);
                    System.out.print("Часть " + (i + 1) + " из " + partsWork.size() + " загружена" + System.lineSeparator());
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Часть " + i + " из " + partsWork.size() + " утеряна!" + System.lineSeparator());
                    counter++;
                    if (counter == 5) {
                        System.out.println("Количество попыток превысило лимит, работа не загружена!" + System.lineSeparator());
                        break;
                    }
                    i--;
                }
                //System.out.println(counter);
            }



            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\Downloads\\Сборники\\" + nameWork + ", автор " + nameAuthor + ".html"));
                for (String line : allWork) {
                    bufferedWriter.write(line);
                }
                bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.snlp;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Clerked {

    HashMap<String, String> info = new HashMap<>();
    HashMap<String, String> entityMap = new HashMap<>();

    public HashMap<String, String> getInfo() { return this.info; }

    public void setInfo(HashMap<String, String> info)
    {
        this.info = info;
    }

    public void addInfo(HashMap<String, String> info) { this.info.putAll(info); }

    public HashMap<String, String> getEntityMap() { return this.entityMap; }

    public void addIntoEntityMap(HashMap<String, String> entityMap) { this.entityMap.putAll(entityMap); }

    public void preprocessInputString(String str,Clerked clerk) throws IOException
    {
        String u = "";
        String pageContent = "";
        //String [] others = new String[];
        ArrayList<String> per = new ArrayList<>();
        HashMap<String, String> newInfo = clerk.getInfo();
        HashMap<String,String> oldInfo = clerk.getInfo();
        HashMap<String,String> newEntityMap = new HashMap<>();


        //str = str.replaceAll("generator"," ");
        str = str.replaceAll("'s"," ");
        str = str.replaceAll("' "," ");
        //s = s.replaceAll("\\."," ");
        str = str.replaceAll("//s+"," ");
        str = str.substring(0,str.length()-1);

        String [] others = str.split(" has been ");

        String [] tempothers = str.split(" ");

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // make an example document
        CoreDocument doc = new CoreDocument(str);
        // annotate the document
        pipeline.annotate(doc);
        // view results
        for (CoreEntityMention em : doc.entityMentions())
        {
            //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
            newEntityMap.put(em.text(),em.entityType());

            if (em.entityType().toLowerCase().equals("person"))
            {
                per.add(em.text());
            }
        }
        clerk.addIntoEntityMap(newEntityMap);

        WikiURLandPageContent wupc = new WikiURLandPageContent();
        if(!per.isEmpty())
        {
            //System.out.println("per.size() ======"+per.size());
            if(per.size() >1)
            {
                //First person check...
                if(oldInfo.get(per.get(0)) == null && newInfo.get(per.get(0)) == null)
                {
                    u = per.get(0) + " ";
                    pageContent = wupc.getPageContent(wupc.getURL(u));
                    if(pageContent != "empty")
                    {
                        newInfo.put(per.get(0).trim(),pageContent);
                        //System.out.println("added =>"+ per.get(0));
                    }
                    else
                    {
                        //System.out.println(" 0 not added");
                    }
                }

                //Second person check ...
                if(oldInfo.get(per.get(1)) == null && newInfo.get(per.get(1)) == null)
                {
                    u = per.get(1) + " ";
                    pageContent = wupc.getPageContent(wupc.getURL(u));
                    if(pageContent != "empty")
                    {
                        newInfo.put(per.get(1).trim(),pageContent);
                        // System.out.println("added =>" +per.get(1));
                    }
                    else
                    {
                        //System.out.println(" 1 not added");
                    }
                }

                for (int i = 0; i < others.length; i++)
                {
                    //System.out.println("others[i] = "+others[i]);
                    if(oldInfo.get(others[i]) == null && newInfo.get(others[i]) == null)
                    {
                        u = others[i] + " ";
                        pageContent = wupc.getPageContent(wupc.getURL(u));

                        if(pageContent != "empty")
                        {
                            newInfo.put(others[i].trim(),pageContent);
                            //System.out.println("added =>" + others[i]);
                        }
                        else
                        {
                            //System.out.println(" 2 not added");
                        }
                    }
                }
            }
            else
            {
                if(oldInfo.get(per.get(0)) == null && newInfo.get(per.get(0)) == null)
                {
                    //System.out.println("per 0 =>"+per.get(0));
                    u = per.get(0) + " ";
                    pageContent = wupc.getPageContent(wupc.getURL(u));
                    if(pageContent != "empty")
                    {
                        newInfo.put(per.get(0).trim(),pageContent);
                        //System.out.println("added =>"+per.get(0));
                    }
                    else
                    {
                        //System.out.println(" 3 not added");
                    }
                }


                for (int i = 0; i < others.length; i++)
                {
                    // System.out.println("others[i] = "+others[i]+"    per.get(0) = "+per.get(0)+ " contains = "+others[i].contains(per.get(0)));
                    if(!others[i].contains(per.get(0)) && oldInfo.get(others[i]) == null && newInfo.get(others[i]) == null)
                    {
                        u = others[i] + " ";
                        pageContent = wupc.getPageContent(wupc.getURL(u));

                        if(pageContent != "empty")
                        {
                            newInfo.put(others[i].trim(),pageContent);
                            //System.out.println("added =>" + others[i]);
                        }
                        else
                        {
                            //System.out.println(" 4 not added");
                        }
                    }
                }
            }
        }
        else
        {
            //System.out.println(" no person added");
            //System.out.println("s ======"+s);
            for (int i = 0; i < others.length; i++)
            {
                if(oldInfo.get(others[i]) == null && newInfo.get(others[i]) == null)
                {
                    u = others[i] + " ";
                    pageContent = wupc.getPageContent(wupc.getURL(u));

                    if(pageContent != "empty")
                    {
                        newInfo.put(others[i].trim(),pageContent);
                        //System.out.println("added");
                    }
                    else
                    {
                        //System.out.println(" 5 not added");
                    }
                }
            }
        }
        //System.out.println("tempothers[tempothers.length]  ======>"+tempothers[tempothers.length-1]);
        if(oldInfo.get(tempothers[tempothers.length-1]) == null && newInfo.get(tempothers[tempothers.length-1]) == null)
        {
            u = tempothers[tempothers.length-1] + " ";
            pageContent = wupc.getPageContent(wupc.getURL(u));

            if(pageContent != "empty")
            {
                newInfo.put(tempothers[tempothers.length-1].trim(),pageContent);
                //System.out.println("added");
            }
            else
            {
                //System.out.println(" 5 not added");
            }
        }
        clerk.addInfo(newInfo);
    }


    public Boolean isFact(String str,Clerked clerk) throws IOException
    {
        HashMap<String,String> entityMap = clerk.getEntityMap();
        HashMap<String,String> infoMap = clerk.getInfo();

        HashMap<String,String> newEntityMap = new HashMap<>();
        String searchKeyword = "nothing";
        String SearchFrom = " ";

        str = str.replaceAll("'s"," ");
        str = str.replaceAll("' "," ");
        //s = s.replaceAll("\\."," ");
        str = str.replaceAll("//s+"," ");
        str = str.substring(0,str.length()-1);

        String [] others = str.split(" has been ");
        String [] tempothers = str.split(" ");

        SearchFrom = tempothers[tempothers.length-1];

        String [] tempothers1 = others[0].toLowerCase().split("stars ");
        searchKeyword = tempothers1[tempothers1.length-1];

       /* if(entityMap.get(searchKeyword) == null || entityMap.get(SearchFrom) == null)
        {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            // make an example document
            CoreDocument doc = new CoreDocument(searchKeyword);
            // annotate the document
            pipeline.annotate(doc);

            for (CoreEntityMention em : doc.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            // make an example document
            CoreDocument doc1 = new CoreDocument(SearchFrom);
            // annotate the document
            pipeline.annotate(doc1);

            for (CoreEntityMention em : doc1.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            clerk.addIntoEntityMap(newEntityMap);
            entityMap = clerk.getEntityMap();
        }
*/

        if(infoMap.get(SearchFrom) != null && infoMap.get(SearchFrom).toLowerCase().contains(searchKeyword.toLowerCase() ))
        {
            return true;
        }
        else
        {
            /*System.out.println("searchKeyword =="+searchKeyword);
        System.out.println("SearchFrom =="+SearchFrom);
        if(infoMap.get(SearchFrom) != null)
        {
            System.out.println("info map has content ---- ");
        }
        else
        {
            System.out.println("no content found ---- ===== ");
        }
             */
        }

        return false;
    }
}

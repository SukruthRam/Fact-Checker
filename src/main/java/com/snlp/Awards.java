package com.snlp;

import java.util.*;
import java.io.*;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import org.jsoup.nodes.*;
import org.jsoup.Jsoup;
import org.json.*;



public class Awards {

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


    public int testVal =0;

    public int testTrueCount =0;

    public void preprocessInputString(String s,Awards award) throws IOException
    {
        String u = "";
        String pageContent = "";
        ArrayList<String> per = new ArrayList<>();
        HashMap<String, String> newInfo = award.getInfo();
        HashMap<String,String> oldInfo = award.getInfo();
        HashMap<String,String> newEntityMap = new HashMap<>();


        s = s.replaceAll("award"," ");
        s = s.replaceAll("'s"," ");
        s = s.replaceAll("'"," ");
        //s = s.replaceAll("\\."," ");
        s = s.replaceAll("//s+"," ");
        s = s.substring(0,s.length()-1);

        String [] others = s.split(" is ");


        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // make an example document
        CoreDocument doc = new CoreDocument(s);
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
        award.addIntoEntityMap(newEntityMap);

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
                         //System.out.println("not added");
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
                        //System.out.println("not added");
                    }
                }
            }
            else
            {
                if(oldInfo.get(per.get(0)) == null && newInfo.get(per.get(0)) == null)
                {
                    u = per.get(0) + " ";
                    pageContent = wupc.getPageContent(wupc.getURL(u));
                    if(pageContent != "empty")
                    {
                        newInfo.put(per.get(0).trim(),pageContent);
                        //System.out.println("added =>"+per.get(0));
                    }
                    else
                    {
                        //System.out.println("not added");
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
                            //System.out.println("not added");
                        }
                    }
                }
            }
        }
        else
        {
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
                        //System.out.println("not added");
                    }
                }
            }
        }

        award.addInfo(newInfo);
    }

    public Boolean isFact(String str,Awards award) throws IOException
    {
        HashMap<String,String> entityMap = award.getEntityMap();
        HashMap<String,String> infoMap = award.getInfo();

        //int i=0;
        //System.out.println("inm = "+i+" =============="+infoMap);
        //i++;
        HashMap<String,String> newEntityMap = new HashMap<>();
        String awardedTo = "";
        String awardName = "";

        if(str.contains("award is"))
        {
            str = str.replaceAll("'s"," ");
            str = str.replaceAll("'"," ");
            //str = str.replaceAll("\\."," ");
            str = str.replaceAll("//s+"," ");
            str = str.substring(0,str.length()-1);

            String[] awardIsArray = str.split("award is ");

            awardedTo = awardIsArray[0].trim();
            awardName = awardIsArray[1].trim();

            //System.out.println(awardedTo+"_award_is_"+awardName);

        }
        else
        {
            String[] awardIsArray = str.split("award.");

            if(awardIsArray.length > 0)
            {
                str = awardIsArray[0];
                str = str.replaceAll("'s"," ");
                str = str.replaceAll("'"," ");
                //str = str.replaceAll("\\."," ");
                str = str.replaceAll("//s+"," ");

                awardIsArray = str.split("is");

                awardedTo = awardIsArray[1].trim();
                awardName = awardIsArray[0].trim();

                //System.out.println("awardedTo =="+awardedTo);
                //System.out.println("awardName =="+awardName);

            }
        }

        if(entityMap.get(awardedTo) == null || entityMap.get(awardName) == null)
        {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            // make an example document
            CoreDocument doc = new CoreDocument(awardedTo);
            // annotate the document
            pipeline.annotate(doc);

            for (CoreEntityMention em : doc.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            // make an example document
            CoreDocument doc1 = new CoreDocument(awardName);
            // annotate the document
            pipeline.annotate(doc1);

            for (CoreEntityMention em : doc1.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            award.addIntoEntityMap(newEntityMap);
            entityMap = award.getEntityMap();
        }

        if(entityMap.get(awardedTo) != null && entityMap.get(awardName) != null)
        {
                /*System.out.println("11awardedTo =="+awardedTo);
                System.out.println("111awardName =="+awardName);

                 */
            if(entityMap.get(awardedTo).equals("PERSON") && entityMap.get(awardName).equals("PERSON"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("COUNTRY") || entityMap.get(awardName).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(awardName).equals("ORGANIZATION"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("CITY") || entityMap.get(awardName).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("STATE_OR_PROVINCE") || entityMap.get(awardName).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("NUMBER") || entityMap.get(awardName).equals("NUMBER"))
            {
                return false;
            }
        }
        else if(entityMap.get(awardedTo) != null)
        {
            if(entityMap.get(awardedTo).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("ORGANIZATION"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(awardedTo).equals("NUMBER"))
            {
                return false;
            }
        }
        else if(entityMap.get(awardName) != null)
        {
            if(entityMap.get(awardName).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(awardName).equals("ORGANIZATION"))
            {
                return false;
            }
            else if(entityMap.get(awardName).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(awardName).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(awardName).equals("NUMBER"))
            {
                return false;
            }
        }

        if(infoMap.get(awardedTo) != null && infoMap.get(awardedTo).contains(awardName))
        {
            return true;
        }
        else if(awardName.contains("Nobel"))
        {
            awardName = awardName.replace(" in "," for ");
            if(infoMap.get(awardedTo) != null && infoMap.get(awardedTo).contains(awardName))
            {
                return true;
            }
                /*System.out.println("awardedTo =="+awardedTo);
                System.out.println("awardName =="+awardName);
                System.out.println("infoMap.get(awardedTo) =="+infoMap.get(awardedTo) != null);

                 */
        }


        return false;
    }
}

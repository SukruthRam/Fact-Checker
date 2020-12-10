package com.snlp;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class Subordinate {

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

    public void preprocessInputString(String str,Subordinate sub) throws IOException
    {
        String u = "";
        String pageContent = "";
        //String [] others = new String[];
        ArrayList<String> per = new ArrayList<>();
        HashMap<String, String> newInfo = sub.getInfo();
        HashMap<String,String> oldInfo = sub.getInfo();
        HashMap<String,String> newEntityMap = new HashMap<>();


        str = str.replaceAll("subordinate"," ");
        str = str.replaceAll("'s"," ");
        str = str.replaceAll("'"," ");
        //s = s.replaceAll("\\."," ");
        str = str.replaceAll("//s+"," ");
        str = str.substring(0,str.length()-1);

        String [] others = str.split(" is ");



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
        sub.addIntoEntityMap(newEntityMap);

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
                        //System.out.println("not added");
                    }
                }
            }
        }

        sub.addInfo(newInfo);
    }


    public Boolean isFact(String str,Subordinate sub) throws IOException
    {
        HashMap<String,String> entityMap = sub.getEntityMap();
        HashMap<String,String> infoMap = sub.getInfo();

        HashMap<String,String> newEntityMap = new HashMap<>();
        String subordinateTo = "";
        String subordinateName = "";

        if(str.contains("subordinate is"))
        {
            str = str.replaceAll("'s"," ");
            str = str.replaceAll("'"," ");
            //str = str.replaceAll("\\."," ");
            str = str.replaceAll("//s+"," ");
            str = str.substring(0,str.length()-1);

            String[] subordinateArray = str.split("subordinate is ");

            subordinateTo = subordinateArray[0].trim();
            subordinateName = subordinateArray[1].trim();
        }
        else
        {
            String[] subordinateArray = str.split("subordinate.");

            if(subordinateArray.length > 0)
            {
                str = subordinateArray[0];
                str = str.replaceAll("'s"," ");
                str = str.replaceAll("'"," ");
                //str = str.replaceAll("\\."," ");
                str = str.replaceAll("//s+"," ");

                subordinateArray = str.split(" is ");

                subordinateTo = subordinateArray[0].trim();
                subordinateName = subordinateArray[1].trim();
            }
        }
/*
        if(entityMap.get(subordinateTo) == null || entityMap.get(subordinateName) == null)
        {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            // make an example document
            CoreDocument doc = new CoreDocument(subordinateTo);
            // annotate the document
            pipeline.annotate(doc);

            for (CoreEntityMention em : doc.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            // make an example document
            CoreDocument doc1 = new CoreDocument(subordinateName);
            // annotate the document
            pipeline.annotate(doc1);

            for (CoreEntityMention em : doc1.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            sub.addIntoEntityMap(newEntityMap);
            entityMap = sub.getEntityMap();
        }
        */

        if(infoMap.get(subordinateTo) != null && infoMap.get(subordinateTo).toLowerCase().contains(subordinateName.toLowerCase() ))
        {
            return true;
        }
        else
        {
            /*System.out.println("subordinateTo =="+subordinateTo);
            System.out.println("subordinateName =="+subordinateName);
            System.out.println("infoMap.get(awardedTo) =="+infoMap.get(subordinateTo) != null);

             */
        }

        return false;
    }
}

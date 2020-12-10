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

public class Role {

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

    public void preprocessInputString(String str,Role role) throws IOException
    {
        String u = "";
        String pageContent = "";
        //String [] others = new String[];
        ArrayList<String> per = new ArrayList<>();
        HashMap<String, String> newInfo = role.getInfo();
        HashMap<String,String> oldInfo = role.getInfo();
        HashMap<String,String> newEntityMap = new HashMap<>();


        String[] roleArray = str.split("role.");

        if(roleArray.length > 0)
        {
            str = roleArray[0];
            str = str.replaceAll("'s"," ");
            str = str.replaceAll("'"," ");
            //str = str.replaceAll("\\."," ");
            str = str.replaceAll("//s+"," ");

            //roleTo = roleArray[1].trim();
            //roleName = roleArray[0].trim();

            //System.out.println("awardedTo =="+awardedTo);
            //System.out.println("awardName =="+awardName);

        }
        String []  others = str.split(" is ");



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
        role.addIntoEntityMap(newEntityMap);

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

        role.addInfo(newInfo);
    }


    public Boolean isFact(String str,Role role) throws IOException
    {
        HashMap<String,String> entityMap = role.getEntityMap();
        HashMap<String,String> infoMap = role.getInfo();

        HashMap<String,String> newEntityMap = new HashMap<>();
        String roleTo = "";
        String roleName = "";

        String[] roleArray = str.split("role.");

        if(roleArray.length > 0)
        {
            str = roleArray[0];
            str = str.replaceAll("'s"," ");
            str = str.replaceAll("'"," ");
            //str = str.replaceAll("\\."," ");
            str = str.replaceAll("//s+"," ");

            roleArray = str.split(" is ");

            roleTo = roleArray[1].trim();
            roleName = roleArray[0].trim();

            //System.out.println("awardedTo =="+awardedTo);
            //System.out.println("awardName =="+awardName);

        }

       /* if(entityMap.get(roleTo) == null || entityMap.get(roleName) == null)
        {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            // make an example document
            CoreDocument doc = new CoreDocument(roleTo);
            // annotate the document
            pipeline.annotate(doc);

            for (CoreEntityMention em : doc.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            // make an example document
            CoreDocument doc1 = new CoreDocument(roleName);
            // annotate the document
            pipeline.annotate(doc1);

            for (CoreEntityMention em : doc1.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            role.addIntoEntityMap(newEntityMap);
            entityMap = role.getEntityMap();
        }
        */

        if(infoMap.get(roleTo) != null && (infoMap.get(roleTo).toLowerCase().contains("prime minister of "+roleName.toLowerCase()) || infoMap.get(roleTo).toLowerCase().contains("prime minister of the "+roleName.toLowerCase())))
        {
            return true;
        }
        else
        {
            /*System.out.println("awardedTo =="+roleTo);
            System.out.println("awardName =="+roleName);
            System.out.println("infoMap.get(awardedTo) =="+infoMap.get(roleTo) != null);*/
        }

        return false;
    }
}

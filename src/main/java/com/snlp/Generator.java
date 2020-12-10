package com.snlp;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Generator {

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

    public void preprocessInputString(String str,Generator gen) throws IOException
    {
        String u = "";
        String pageContent = "";
        //String [] others = new String[];
        ArrayList<String> per = new ArrayList<>();
        HashMap<String, String> newInfo = gen.getInfo();
        HashMap<String,String> oldInfo = gen.getInfo();
        HashMap<String,String> newEntityMap = new HashMap<>();


        str = str.replaceAll("generator"," ");
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
        gen.addIntoEntityMap(newEntityMap);

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

        gen.addInfo(newInfo);
    }


    public Boolean isFact(String str,Generator gen) throws IOException
    {
        HashMap<String,String> entityMap = gen.getEntityMap();
        HashMap<String,String> infoMap = gen.getInfo();

        HashMap<String,String> newEntityMap = new HashMap<>();
        String genTo = "";
        String genName = "";

        if(str.contains("generator is"))
        {
            str = str.replaceAll("'s"," ");
            str = str.replaceAll("'"," ");
            //str = str.replaceAll("\\."," ");
            str = str.replaceAll("//s+"," ");
            str = str.substring(0,str.length()-1);

            String[] tempArray = str.split("generator is ");

            genTo = tempArray[1].trim();
            genName = tempArray[0].trim();
        }
        else
        {
            String[] tempArray = str.split("generator.");

            if(tempArray.length > 0)
            {
                str = tempArray[0];
                str = str.replaceAll("'s"," ");
                str = str.replaceAll("'"," ");
                //str = str.replaceAll("\\."," ");
                str = str.replaceAll("//s+"," ");

                tempArray = str.split(" is ");

                genTo = tempArray[1].trim();
                genName = tempArray[0].trim();
            }
        }

        if(entityMap.get(genTo) == null || entityMap.get(genName) == null)
        {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            // make an example document
            CoreDocument doc = new CoreDocument(genTo);
            // annotate the document
            pipeline.annotate(doc);

            for (CoreEntityMention em : doc.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            // make an example document
            CoreDocument doc1 = new CoreDocument(genName);
            // annotate the document
            pipeline.annotate(doc1);

            for (CoreEntityMention em : doc1.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            gen.addIntoEntityMap(newEntityMap);
            entityMap = gen.getEntityMap();
        }

        if(entityMap.get(genTo) != null && entityMap.get(genName) != null)
        {
            //System.out.println("1 genTo  =="+genTo + "   ,entityMap.get(genTo)"+entityMap.get(genTo));
            //System.out.println("1 genName =="+genName+ "   ,entityMap.get(genName)"+entityMap.get(genName));
            if(entityMap.get(genTo).equals("COUNTRY") || entityMap.get(genName).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(genTo).equals("CITY") || entityMap.get(genName).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(genTo).equals("STATE_OR_PROVINCE") || entityMap.get(genName).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(genTo).equals("NUMBER") || entityMap.get(genName).equals("NUMBER"))
            {
                return false;
            }
        }
        else if(entityMap.get(genTo) != null)
        {
            //System.out.println("2 genTo  =="+genTo);
            //System.out.println("2 entityMap.get(genTo) =="+entityMap.get(genTo));

            if(entityMap.get(genTo).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(genTo).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(genTo).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(genTo).equals("NUMBER"))
            {
                return false;
            }
        }
        else if(entityMap.get(genName) != null)
        {
            //System.out.println("3 genName  =="+genName);
            //System.out.println("3 entityMap.get(genName) =="+entityMap.get(genName));

            if(entityMap.get(genName).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(genName).equals("ORGANIZATION"))
            {
                return false;
            }
            else if(entityMap.get(genName).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(genName).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(genName).equals("NUMBER"))
            {
                return false;
            }
        }

        if(infoMap.get(genTo) != null && infoMap.get(genTo).toLowerCase().contains(genName.toLowerCase() ))
        {
            return true;
        }
        else
        {
            /*System.out.println("gen to =="+genTo);
        System.out.println("gen Name =="+genName);
        if(infoMap.get(genTo) != null)
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

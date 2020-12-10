package com.snlp;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Honour {

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

    public void preprocessInputString(String str,Honour honour) throws IOException
    {
        String u = "";
        String pageContent = "";
        //String [] others = new String[];
        ArrayList<String> per = new ArrayList<>();
        HashMap<String, String> newInfo = honour.getInfo();
        HashMap<String,String> oldInfo = honour.getInfo();
        HashMap<String,String> newEntityMap = new HashMap<>();


        str = str.replaceAll("honour"," ");
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
        honour.addIntoEntityMap(newEntityMap);

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

        honour.addInfo(newInfo);
    }


    public Boolean isFact(String str,Honour honour) throws IOException
    {
        HashMap<String,String> entityMap = honour.getEntityMap();
        HashMap<String,String> infoMap = honour.getInfo();

        HashMap<String,String> newEntityMap = new HashMap<>();
        String honourTo = "";
        String honourName = "";

        if(str.contains("honour is"))
        {
            str = str.replaceAll("'s"," ");
            str = str.replaceAll("'"," ");
            //str = str.replaceAll("\\."," ");
            str = str.replaceAll("//s+"," ");
            str = str.substring(0,str.length()-1);

            String[] tempArray = str.split("honour is ");

            honourTo = tempArray[1].trim();
            honourName = tempArray[0].trim();
        }
        else
        {
            String[] subordinateArray = str.split("honour.");

            if(subordinateArray.length > 0)
            {
                str = subordinateArray[0];
                str = str.replaceAll("'s"," ");
                str = str.replaceAll("'"," ");
                //str = str.replaceAll("\\."," ");
                str = str.replaceAll("//s+"," ");

                subordinateArray = str.split(" is ");

                honourTo = subordinateArray[1].trim();
                honourName = subordinateArray[0].trim();
            }
        }


        if(entityMap.get(honourTo) == null || entityMap.get(honourName) == null)
        {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            // make an example document
            CoreDocument doc = new CoreDocument(honourTo);
            // annotate the document
            pipeline.annotate(doc);

            ArrayList<String> per = new ArrayList<>();
            for (CoreEntityMention em : doc.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
                if (em.entityType().toLowerCase().equals("person"))
                {
                    per.add(em.text());
                }
            }

            if(per.size() >1)
            {
                boolean newreturnValue = false;

                for (String person:per)
                {
                    if(infoMap.get(person) != null && infoMap.get(person).toLowerCase().contains(honourName.toLowerCase() ))
                    {
                        newreturnValue = true;
                    }
                    else if(honourName.contains("Nobel"))
                    {

                        honourName = honourName.replace(" in "," for ");
                        if(infoMap.get(person) != null && infoMap.get(person).contains(honourName))
                        {
                            newreturnValue = true;
                        }
                    }
                }

                if(newreturnValue)
                {
                    return newreturnValue;
                }
            }
            // make an example document
            CoreDocument doc1 = new CoreDocument(honourName);
            // annotate the document
            pipeline.annotate(doc1);

            for (CoreEntityMention em : doc1.entityMentions())
            {
                //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
                newEntityMap.put(em.text(),em.entityType());
            }

            honour.addIntoEntityMap(newEntityMap);
            entityMap = honour.getEntityMap();
        }

        if(entityMap.get(honourTo) != null && entityMap.get(honourName) != null)
        {
                //System.out.println("1 honourTo  =="+honourTo + "   ,entityMap.get(honourTo)"+entityMap.get(honourTo));
                //System.out.println("1 honourName =="+honourName+ "   ,entityMap.get(honourName)"+entityMap.get(honourName));


            if(entityMap.get(honourTo).equals("PERSON") && entityMap.get(honourName).equals("PERSON"))
            {
                return false;
            }
            else if(entityMap.get(honourTo).equals("COUNTRY") || entityMap.get(honourName).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(honourName).equals("ORGANIZATION"))
            {
                return false;
            }
            else if(entityMap.get(honourTo).equals("CITY") || entityMap.get(honourName).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(honourTo).equals("STATE_OR_PROVINCE") || entityMap.get(honourName).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(honourTo).equals("NUMBER") || entityMap.get(honourName).equals("NUMBER"))
            {
                return false;
            }
        }
        else if(entityMap.get(honourTo) != null)
        {
            //System.out.println("2 honourTo  =="+honourTo);
            //System.out.println("2 entityMap.get(honourTo) =="+entityMap.get(honourTo));

            if(entityMap.get(honourTo).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(honourTo).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(honourTo).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(honourTo).equals("NUMBER"))
            {
                return false;
            }
        }
        else if(entityMap.get(honourName) != null)
        {
            //System.out.println("3 honourName  =="+honourName);
            //System.out.println("3 entityMap.get(honourName) =="+entityMap.get(honourName));

            if(entityMap.get(honourName).equals("COUNTRY"))
            {
                return false;
            }
            else if(entityMap.get(honourName).equals("ORGANIZATION"))
            {
                return false;
            }
            else if(entityMap.get(honourName).equals("CITY"))
            {
                return false;
            }
            else if(entityMap.get(honourName).equals("STATE_OR_PROVINCE"))
            {
                return false;
            }
            else if(entityMap.get(honourName).equals("NUMBER"))
            {
                return false;
            }
        }


        if(infoMap.get(honourTo) != null && infoMap.get(honourTo).toLowerCase().contains(honourName.toLowerCase() ))
        {
            return true;
        }
        else if(honourName.contains("Nobel"))
        {

            honourName = honourName.replace(" in "," for ");
            if(infoMap.get(honourTo) != null && infoMap.get(honourTo).contains(honourName))
            {
                return true;
            }
            /*System.out.println("honour to =="+honourTo);
            System.out.println("honourName =="+honourName);
            System.out.println("infoMap.get(honourTo) =="+infoMap.get(honourTo) != null);*/

        }
        /*System.out.println("honour to =="+honourTo);
        System.out.println("honourName =="+honourName);
        if(infoMap.get(honourTo) != null)
        {
            System.out.println("info map has content ---- ");
        }
        else
        {
            System.out.println("no content found ---- ===== ");
        }
*/


        return false;
    }
}

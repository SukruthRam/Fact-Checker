package com.snlp;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import  java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Place
{
    HashMap<String, String> info = new HashMap<>();

    public HashMap<String, String> getInfo()
    {
        return info;
    }

    public void setInfo(HashMap<String, String> info)
    {
        this.info = info;
    }

    public boolean getInfo(String s) throws IOException
    {


        boolean exists = checkFor(s);



        return exists;

    }

    private String getPlaceInfo(String s)
    {
        String retStr = "";

        if(s.contains("birth"))
        {

            retStr = "birth_place";


        }
        else if(s.contains("death"))
        {
            retStr = "death_place";

        }
        else if(s.contains("nascence"))
        {

            retStr = "birth_place";
        }
        else if(s.contains("last"))
        {

            retStr = "death_place";
        }
        else if(s.contains("innovation"))
        {
            retStr = "location";

        }
        else if(s.contains("foundation"))
        {
            retStr = "found";


        }
        else if(s.contains("office"))
        {
            retStr = "office";

        }
        else
        {


        }


        return retStr ;
    }


    private boolean checkFor(String s)
    {
        boolean isExists = true;
        String place = getPlaceInfo(s);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // make an example document
        CoreDocument doc = new CoreDocument(s);
        // annotate the document
        pipeline.annotate(doc);
        // view results
        ////System.out.println("---");
        //   Sentence sent = new Sentence(s);
        //  List<String> nerTags = sent.nerTags();
        String u = "";
        ////System.out.println("entities found");
        ArrayList<String> per = new ArrayList<>();
        ArrayList<String> org = new ArrayList<>();
        for (CoreEntityMention em : doc.entityMentions())
        {
            // //System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
            if (em.entityType().toLowerCase().equals("person"))
            {
                per.add(em.text());
            }

            if (em.entityType().toLowerCase().equals("organization"))
            {

                org.add(em.text());
            }
        }





        // //System.out.println(per.size());
        ////System.out.println("Text Serach " + u);

        String search = "";
        String tokensAndNERTags = doc.tokens().stream().map(token -> "(" + token.word() + "," + token.ner() + ")").collect(
                Collectors.joining(" "));
        if(per.size() == 1)
        {
            for (int i = 0; i < per.size(); i++)
            {
                u = u + per.get(i) + " ";
            }
            ////System.out.println("Info " + u);
            if (u.length() > 1)
            {
                u = u.replace(" ", "%20");
                u = u.substring(0, u.length() - 3);
            }

            if(s.contains("place is"))
            {
                ////System.out.println(s);
                String retStr[] = changeStr(s);

                search = retStr[1];

            }
            else if(s.contains("place."))
            {
                //       //System.out.println("NOT  " +s);
                String retStr[] = changeStrLast(s);

                search = retStr[0];
            }


            if(info.containsKey(u))
            {
                String content = info.get(u);

                search = search.replaceAll("[.]", "");
                //System.out.println(search);
                if(content.contains(search))
                {
                    isExists = true;
                    return isExists;
                }
                else
                {
                    isExists = false;
                    return false;

                }



            }
            ////System.out.println("---");
           // //System.out.println("tokens and ner tags");

           // //System.out.println(tokensAndNERTags);


            //String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=" +u;


            return (getContent(u, place ,s, search));
        }
        else if(per.size() == 0 || per.size() == 2)
        {

            ////System.out.println(per.size() +" Person not found "+s);



            String search3 = "";

            if(s.contains("place is"))
            {
                ////System.out.println(s);
                String retStr[] = changeStr(s);
                u = retStr[0];
                search3 = retStr[1];

            }
            else if(s.contains("place."))
            {
                //       //System.out.println("NOT  " +s);
                String retStr[] = changeStrLast(s);
                u = retStr[1];
                search3 = retStr[0];
            }

            if (u.length() > 1)
            {
                u = u.replace(" ", "%20");
                ////System.out.println(s);
                u = u.substring(0, u.length() - 3);
            }


                if(info.containsKey(u))
                {
            String content = info.get(u);
            search3 = search3.replaceAll("[.]", "");
            //System.out.println(search);
            if(content.contains(search3))
            {
                isExists = true;
                return isExists;
            }
            else
            {
                isExists = false;
                return false;

            }

        }



            ////System.out.println(u);

            return(getContent(u, place, s , search3));





        }

        return isExists;
    }

    private boolean getContent(String u, String place, String s, String sear)
    {
        boolean isExits = true;
        ////System.out.println("U is "+ u);
        ////System.out.println("S is "+ s);
        u = u.trim();
        String url = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + u + "&rvsection=0";
        ////System.out.println(url);

        HttpClient httpclient = HttpClients.createDefault();
        ////System.out.println(url);
        HttpPost httpPost = new HttpPost(url);

        HttpResponse response = null;
        try
        {
            response = httpclient.execute(httpPost);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        BasicStatusLine n = (BasicStatusLine) response.getStatusLine();
        String jsonString = null;
        try
        {
            jsonString = EntityUtils.toString(response.getEntity());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject(jsonString);
        if (jsonString.contains("query") && obj.getJSONObject("query") != null)
        {
            JSONObject pageNamejjj = obj.getJSONObject("query");
            //String pageid = pageNamejjj.getString("pageid");

            Map<String, Object> ne = new HashMap<>();
            ne = pageNamejjj.toMap();
            Map<String, Object> ne1 = new HashMap<>();
            ne1 = (Map<String, Object>) ne.get("pages");

            Set<Object> x = Collections.singleton(ne1.keySet());


            Object f = x.iterator().next();
            String o = f.toString();
            String k = o.substring(1, o.length() - 1);
            Map<String, Object> ne2 = new HashMap<>();
            ne2 = (Map<String, Object>) ne1.get(o.substring(1, o.length() - 1));

            ArrayList<Object> rev = (ArrayList<Object>) ne2.get("revisions");
            Map<String, Object> ne3 = new HashMap<>();
            try
            {
                if (rev.get(0) != null)
                {
                    ne3 = (Map<String, Object>) rev.get(0);
                    Object h = ne3.get("*");


                    if (h != null)
                    {
                        String g = h.toString();
                        //  //System.out.println(g);
                        info.put(u,g);


                        String result = sear.replaceAll("[.]", "");
                        if(result.equals("New York City"))
                        {
                            result = "New York";
                        }
                        ////System.out.println(result);
                        result = result.trim();

                        //g = parse(g);
                        int a = g.indexOf("Infobox");
                        if(a > 0)
                        {
                            g = g.substring(a, g.length() - 1);
                            String out[] = g.split("\\n");
                            g = ifKeyword(out, place);
                            g = g.toLowerCase();
                            result = result.trim().toLowerCase();
                            if(g.contains(result))
                            {
                                isExits = true;
                            }
                            else {
                                isExits = false;
                            }
                        }
                        else{
                            String serach4 = "";
                            if(s.contains("place is"))
                            {
                                ////System.out.println(s);
                                String retStr[] = changeStr(s);
                                u = retStr[0];
                                serach4 = retStr[1];

                            }
                            else if(s.contains("place."))
                            {
                                //       //System.out.println("NOT  " +s);
                                String retStr[] = changeStrLast(s);
                                u = retStr[1];
                                serach4 = retStr[0];
                            }

                            if (u.length() > 1)
                            {
                                u = u.replace(" ", "%20");
                                ////System.out.println(s);
                                u = u.substring(0, u.length() - 3);
                            }

                            u = u.trim();
                            String newurl = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + u + "&rvsection=0";


                            ////System.out.println(url);
                            httpPost = new HttpPost(newurl);

                            response = null;
                            try
                            {
                                response = httpclient.execute(httpPost);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            jsonString = null;
                            try
                            {
                                jsonString = EntityUtils.toString(response.getEntity());
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            obj = new JSONObject(jsonString);
                            if (obj.getJSONObject("query") != null)
                            {
                                pageNamejjj = obj.getJSONObject("query");
                                //String pageid = pageNamejjj.getString("pageid");

                                ne = new HashMap<>();
                                ne = pageNamejjj.toMap();
                                ne1 = new HashMap<>();
                                ne1 = (Map<String, Object>) ne.get("pages");

                                x = Collections.singleton(ne1.keySet());


                                f = x.iterator().next();
                                o = f.toString();
                                k = o.substring(1, o.length() - 1);
                                ne2 = new HashMap<>();
                                ne2 = (Map<String, Object>) ne1.get(o.substring(1, o.length() - 1));

                                rev = (ArrayList<Object>) ne2.get("revisions");
                                ne3 = new HashMap<>();
                                if (rev.get(0) != null)
                                {
                                    ne3 = (Map<String, Object>) rev.get(0);
                                    h = ne3.get("*");
                                    if (h != null)
                                    {
                                        g = h.toString();
                                        //  //System.out.println(g);
                                        info.put(u,g);
                                        // g = parse(g);
                                        a = g.indexOf("Infobox");
                                        if(a > 0)
                                        {
                                            g = g.substring(a, g.length() - 1);
                                            String out[] = g.split("\\n");
                                            g = ifKeyword(out, place);
                                            if(g.contains(result))
                                            {
                                                isExits = true;
                                            }
                                            else {
                                                isExits = false;
                                            }
                                        }

                                    }
                                }
                            }
                        }

                    }





                }
            }
            catch (NullPointerException e)
            {
                System.out.println("Exception " +" "+u);
            }
        }
        return isExits;
    }

    private String ifKeyword(String[] out, String place)
    {
        String rtr = "";
        for(int i = 0; i< out.length; i++)
        {
            if(out[i].contains(place))
            {
                rtr = rtr + out[i];

            }
        }


        return rtr;
    }

    private String parse(String g)
    {
        int a = g.indexOf("Infobox");
        if(a < 0)
        {
            g = g.substring(a, g.length() - 1);
            String out[] = g.split("\\n");
        }


        return g;
    }

    private String[] changeStrLast(String s)
    {
        String initial = "place.";
        String retStr = "";
        if(s.contains("birth"))
        {
            s = s.replace("birth "+initial, "");
            s = s.replace(" is ", "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[1] = name[1].replace("'s", "");
            name[1] = name[1].replace("'", "");
            ////System.out.println(name[1]);
            return name;

        }
        else if(s.contains("death"))
        {
            s = s.replace("death "+initial, "");
            s = s.replace(" is ", "GAP");
            String name[] = s.split("GAP");

            ////System.out.println(s);
            name[1] = name[1].replace("'s", "");
            name[1] = name[1].replace("'", "");
            ////System.out.println(name[1]);
            return name;
        }
        else if(s.contains("nascence"))
        {
            s = s.replace("nascence "+initial, "");
            s = s.replace(" is ", "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[1] = name[1].replace("'s", "");
            name[1] = name[1].replace("'", "");
            ////System.out.println(name[1]);
            return name;
        }
        else if(s.contains("last"))
        {
            s = s.replace("last "+initial, "");
            s = s.replace(" is ", "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[1] = name[1].replace("'s", "");
            name[1] = name[1].replace("'", "");
            ////System.out.println(name[1]);
            return name;
        }
        else if(s.contains("innovation"))
        {
            s = s.replace("innovation "+initial, "");
            s = s.replace(" is ", "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[1] = name[1].replace("'s", "");
            name[1] = name[1].replace("'", "");
            ////System.out.println(name[1]);
            return name;
        }
        else if(s.contains("foundation"))
        {
            s = s.replace("foundation "+initial, "");
            s = s.replace(" is ", "GAP");
            String name[] = s.split("GAP");

            ////System.out.println(s);
            name[1] = name[1].replace("'s", "");
            name[1] = name[1].replace("'", "");
            ////System.out.println(name[1]);
            return name;

        }
        else if(s.contains("office"))
        {
            s = s.replace("office "+initial, "");
            s = s.replace(" is ", "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[1] = name[1].replace("'s", "");
            name[1] = name[1].replace("'", "");
            ////System.out.println(name[1]);
            return name;
        }
        else
        {
            String name[] = new String[0];
            ////System.out.println(s);
            return name;
        }


    }

    private String[] changeStr(String s)
    {
        String initial = "place is";
        String retStr = "";
        if(s.contains("birth"))
        {
            s = s.replace("birth "+initial, "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[0] = name[0].replace("'s", "");
            name[0] = name[0].replace("'", "");
            ////System.out.println(name[0]);
            return name;

        }
        else if(s.contains("death"))
        {
            s = s.replace("death "+initial, "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[0] = name[0].replace("'s", "");
            name[0] = name[0].replace("'", "");
            ////System.out.println(name[0]);
            return name;
        }
        else if(s.contains("nascence"))
        {
            s = s.replace("nascence "+initial, "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[0] = name[0].replace("'s", "");
            name[0] = name[0].replace("'", "");
            ////System.out.println(name[0]);
            return name;
        }
        else if(s.contains("last"))
        {
            s = s.replace("last "+initial, "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[0] = name[0].replace("'s", "");
            name[0] = name[0].replace("'", "");
            ////System.out.println(name[0]);
            return name;
        }
        else if(s.contains("innovation"))
        {
            s = s.replace("innovation "+initial, "GAP");
            String name[] = s.split("GAP");
            /////System.out.println(s);
            name[0] = name[0].replace("'s", "");
            name[0] = name[0].replace("'", "");
            ////System.out.println(name[0]);
            return name;
        }
        else if(s.contains("foundation"))
        {
            s = s.replace("foundation "+initial, "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[0] = name[0].replace("'s", "");
            name[0] = name[0].replace("'", "");
            ////System.out.println(name[0]);
            return name;

        }
        else if(s.contains("office"))
        {
            s = s.replace("office "+initial, "GAP");
            String name[] = s.split("GAP");
            ////System.out.println(s);
            name[0] = name[0].replace("'s", "");
            name[0] = name[0].replace("'", "");
            ////System.out.println(name[0]);
            return name;
        }
        else
        {
            String name[] = new String[0];
            // //System.out.println(s);
            return name;
        }


    }

}

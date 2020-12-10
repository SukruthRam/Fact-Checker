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

public class Author
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


        boolean exists = checkForAuthor(s);



        return exists;

    }

    private boolean checkForAuthor(String s)
    {
        String search3 = "";
        String u = "";
        boolean isExists;
        if(s.contains("author is"))
        {
            ////System.out.println(s);
            String retStr[] = changeStr(s);
            u = retStr[0];
            search3 = retStr[1];

        }
        else if(s.contains("author."))
        {
            //       //System.out.println("NOT  " +s);
            String retStr[] = changeStrLast(s);
            u = retStr[1];
            search3 = retStr[0];
        }

        if (u.length() > 1)
        {
            u = u.replace(" ", "%20");
            //System.out.println(s);
            u = u.substring(0, u.length() - 3);
        }
        //System.out.println(u);

        if(info.containsKey(u))
        {
            String content = info.get(u);
            search3 = search3.replaceAll("[.]", "");
            //System.out.println(search);
            if (content.contains(search3))
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
        String place = "author";
        return(getContent(u, place, s , search3));

    }





    private boolean getContent(String u, String place, String s, String sear)
    {
        boolean isExits = true;
        //System.out.println("U is "+ u);
        //System.out.println("S is "+ s);
        u = u.trim();
        String url = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + u + "&rvsection=0";
        //System.out.println(url);

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
        try
        {
            if (obj.getJSONObject("query") != null)
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

                        //System.out.println(result);
                        result = result.trim();

                        //g = parse(g);
                        int a = g.toLowerCase().indexOf("infobox");
                        if(a > 0)
                        {
                            g = g.substring(a, g.length() - 1);
                            String out[] = g.split("\\n");
                            g = ifKeyword(out, place);
                            g = g.toLowerCase();
                            result = result.trim().toLowerCase();
                            g = g.replaceAll("[.]", "");
                            if(g.contains(result))
                            {
                                isExits = true;
                            }
                            else {
                                isExits = false;
                            }
                        }
                        else
                        {
                            u = u + "%20(novel)";



                            String newurl = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + u + "&rvsection=0";
                            //System.out.println(newurl);

                            httpclient = HttpClients.createDefault();
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
                            n = (BasicStatusLine) response.getStatusLine();
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


                                        result = sear.replaceAll("[.]", "");

                                        //System.out.println(result);
                                        result = result.trim();

                                        //g = parse(g);
                                        a = g.toLowerCase().indexOf("infobox");
                                        if(a > 0)
                                        {
                                            g = g.substring(a, g.length() - 1);
                                            String out[] = g.split("\\n");
                                            g = ifKeyword(out, place);
                                            g = g.toLowerCase();
                                            result = result.trim().toLowerCase();
                                            g = g.replaceAll("[.]", "");
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
        }
        catch (Exception e)
        {
            System.out.println("Exception " +" "+u);
            return false;
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
        String initial = "author.";

        s = s.replace(initial, "");
        s = s.replace(" is ", "GAP");
        String name[] = s.split("GAP");
        ////System.out.println(s);
        name[1] = name[1].replace("'s", "");
        name[1] = name[1].replace("'", "");
        ////System.out.println(name[1]);
        return name;



    }

    private String[] changeStr(String s)
    {
        String initial = "author is";
        String retStr = "";

        s = s.replace(initial, "GAP");
        String name[] = s.split("GAP");
        ////System.out.println(s);
        name[0] = name[0].replace("'s", "");
        name[0] = name[0].replace("'", "");
        ////System.out.println(name[0]);
        return name;


    }

}

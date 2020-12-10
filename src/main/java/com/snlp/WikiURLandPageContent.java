package com.snlp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class WikiURLandPageContent {


    public String getURL(String u)
    {
        if (u.length() > 1) {
            u = u.replace(" ", "%20");
            u = u.substring(0, u.length() - 3);
        }

        //System.out.println("Text Serach " + u);

        String url = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + u + "&rvsection=0";
        //System.out.println(url);

        return url;
    }

    public String getPageContent(String url) throws IOException
    {
        String returnValue = "empty";
        HttpClient httpclient = HttpClients.createDefault();
        //System.out.println(url);
        HttpPost httpPost = new HttpPost(url);

        HttpResponse response = httpclient.execute(httpPost);
        BasicStatusLine n = (BasicStatusLine) response.getStatusLine();
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONObject obj = new JSONObject(jsonString);
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

            if (ne2.get("revisions") != null)
            {
                ArrayList<Object> rev = (ArrayList<Object>) ne2.get("revisions");
                Map<String, Object> ne3 = new HashMap<>();
                //      System.out.println("rev.size() == " + rev.size());
                if (!rev.isEmpty() && rev.get(0) != null)
                {
                    ne3 = (Map<String, Object>) rev.get(0);
                    Object h = ne3.get("*");

                    if (h != null)
                    {
                        returnValue = h.toString();
                        //System.out.println(returnValue);
                    }
                    else
                    {
                        System.out.println("inside 4");
                        System.out.println("*****");

                    }

                }
                else
                {
                    System.out.println("inside 3");
                    System.out.println("*****");


                }
            }
            else
            {
                 System.out.println("inside 2");
                System.out.println("*****");


            }
        }
        else
        {
            System.out.println("inside 1");
            System.out.println("*****");


        }
        return returnValue;
    }
}

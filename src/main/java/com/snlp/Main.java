package com.snlp;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import org.jsoup.nodes.*;
import org.jsoup.Jsoup;
import org.json.*;

public class Main
{
    private static final String GET_URL = "https://en.wikipedia.org/w/index.php?search=Michael%20Jackson";
    private static final String USER_AGENT = "Mozilla/5.0";
    public static Awards awd = new Awards();
    public static Role role = new Role();
    public static Subordinate sub = new Subordinate();
    public static Honour honour = new Honour();
    public static Generator gen = new Generator();
    public static Clerked clerk = new Clerked();
    public static BetterHalf bh = new BetterHalf();
    public static Subsidiary subsi = new Subsidiary();

    public static Place p = new Place();
    public static Author a = new Author();
    public static Spouse spouseObj = new Spouse();
    public static Team t = new Team();
    public static Stars starsObj = new Stars();
    //public static Stars satrs = new Stars();
    public static  ArrayList<String> trainingFactIdList = new ArrayList<>();
    public static  ArrayList<String> trainingFactsList = new ArrayList<>();


    public static void main(String args[]) throws IOException
    {

        int awardCheck =0;
        int awardCheckError =0;

        int placeCheck =0;
        int placeCheckError =0;

        int roleCheck =0;
        int roleCheckError =0;

        int subCheck =0;
        int subCheckError =0;

        int honourCheck =0;
        int honourCheckError =0;

        int genCheck =0;
        int genCheckError =0;

        int clerkCheck =0;
        int clerkCheckError =0;
        int bhCheck =0;
        int bhCheckError =0;
        int subsiCheck =0;
        int subsiCheckError =0;
        int starsCheck =0;
        int starsCheckError =0;

        int spouseCheck =0;
        int spouseCheckError =0;
        int squadCheck =0;
        int squadCheckError =0;
        int teamCheck =0;
        int teamCheckError =0;
        int authorCheck =0;
        int authorCheckError =0;

        int others = 0;

        ArrayList<Place> placeList = new ArrayList<>();
        String data = "";
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();

        if(System.getProperty("os.name").startsWith("Windows")){
            path = path + "\\SNLP2019_training.tsv";

        }else
        {
            path = path + "//SNLP2019_training.tsv";

        }

        File file = new File(path);

        Writer writer1 = null;
        String opath = currentRelativePath.toAbsolutePath().toString();
        if(System.getProperty("os.name").startsWith("Windows")) {
            opath = opath + "\\output.ttl";
        }else {
            opath = opath + "//output.ttl";

        }

        writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(opath), StandardCharsets.UTF_8));
        //writer1.write("Skill\tE-mail\n");

        int count = 0;
        HashMap<String, String> map1 = new HashMap<>();
        HashMap<String, String> map2 = new HashMap<>();

        ArrayList<String> input = new ArrayList<>();

        ArrayList<String> input_NEEDTOREMOVE = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = "";

        ArrayList<String> id = new ArrayList<>();
        while ((s = br.readLine()) != null)
        {

            String inp[] = s.split("\\t+");
            input.add(inp[1]);
            id.add(inp[0]);

            input_NEEDTOREMOVE.add(inp[2]);
            trainingFactIdList.add(inp[0]);
            trainingFactsList.add(inp[1]);
        }

        boolean factValue = false;
        for (int y = 1; y < input.size(); y++)
        {
            //Sentence sent = new Sentence(X[i]);
            // List<String> nerTags = sent.nerTags();  // [PERSON, O, O, O, O, O, O, O]
            ArrayList<String> test = new ArrayList<>();
            //String URL = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=Colin%20Firth";
            //for(int j=0; j<sent.length(); j++)
            //{
            //  test.add(sent.posTag(j));
            //}

            if (input.get(y).contains("place") || input.get(y).contains("office"))
            {

                //System.out.println("Place -->" +input.get(y));
                //print(input.get(y));


                factValue = p.getInfo(input.get(y));


                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    placeCheck++;
                }
                else
                {
                    placeCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }
            }
            else if (input.get(y).contains("author"))
            {
                //System.out.println("Author -->" +input.get(y));

                factValue = a.getInfo(input.get(y));

                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    authorCheck++;
                }
                else
                {
                    authorCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }


            }
            else if (input.get(y).contains("spouse"))
            {

                //System.out.println("spouse -->" +input.get(y));


                factValue = spouseObj.getInfo(input.get(y));


                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    spouseCheck++;
                }
                else
                {
                    spouseCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }

            }
            else if (input.get(y).contains("team"))
            {
                //System.out.println("Team -->" +input.get(y));

                 factValue = t.getInfo(input.get(y));

                String abasd = "0.0";


                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    teamCheck++;
                }
                else
                {
                    teamCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }
            }
            else if (input.get(y).contains("squad"))
            {
                //System.out.println("Squad -->" +input.get(y));
                //print(input.get(y));
                String in = input.get(y);
                in = input.get(y).replace("squad", "team");
                 factValue = t.getInfo(in);

                String abasd = "0.0";


                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    squadCheck++;
                }
                else
                {
                    squadCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }
            }
            else if (input.get(y).contains("stars"))
            {
                // System.out.println("Stars -->" +input.get(y));
                //print(input.get(y));
                 factValue = starsObj.getInfo(input.get(y));


                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    starsCheck++;
                }
                else
                {
                    starsCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }
            }
            else if (input.get(y).contains("award"))
            {
                //System.out.println("<---------                       Award                          ------->");

                awd.preprocessInputString(input.get(y),awd);
                factValue = awd.isFact(input.get(y),awd);


                String abasd = "0.0";
                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    awardCheck++;
                }
                else
                {
                    awardCheckError++;
                    //System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }

            }
            else if (input.get(y).contains("role"))
            {
                //System.out.println("Role -->" +input.get(y) + input_NEEDTOREMOVE.get(y));

                role.preprocessInputString(input.get(y),role);
                factValue = role.isFact(input.get(y),role);


                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    roleCheck++;
                }
                else
                {
                    roleCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }

            }
            else if (input.get(y).contains("subordinate"))
            {
                //System.out.println("Subordinate -->" +input.get(y));

                sub.preprocessInputString(input.get(y),sub);
                factValue = sub.isFact(input.get(y),sub);


                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    subCheck++;
                }
                else
                {
                    subCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }

            }
            else if (input.get(y).contains("honour"))
            {
                //System.out.println("Honour -->" +input.get(y));

                honour.preprocessInputString(input.get(y),honour);

                factValue = honour.isFact(input.get(y),honour);
                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    honourCheck++;
                }
                else
                {
                    honourCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }


            }
            else if (input.get(y).contains("generator"))
            {
                //System.out.println("Generator -->" +input.get(y));

                gen.preprocessInputString(input.get(y),gen);

                factValue = gen.isFact(input.get(y),gen);
                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    genCheck++;
                }
                else
                {
                    genCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }

            }
            else if (input.get(y).contains("has been"))
            {
                //System.out.println("Been -->" +input.get(y));

                clerk.preprocessInputString(input.get(y),clerk);
                 factValue = clerk.isFact(input.get(y),clerk);
                String abasd = "0.0";


                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    clerkCheck++;
                }
                else
                {
                    clerkCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }


            }
            else if (input.get(y).contains("better half"))
            {
                //System.out.println("Better half -->" +input.get(y));

                bh.preprocessInputString(input.get(y),bh);
                 factValue = bh.isFact(input.get(y),bh);
                String abasd = "0.0";


                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    bhCheck++;
                }
                else
                {
                    bhCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }

            }
            else if (input.get(y).contains("subsidiary"))
            {
                //System.out.println("subsidiary -->" +input.get(y) + " "+input_NEEDTOREMOVE.get(y));

                subsi.preprocessInputString(input.get(y),subsi);
                factValue = subsi.isFact(input.get(y),subsi);
                String abasd = "0.0";

                if(factValue)
                {
                    abasd = "1.0";
                }
                if(abasd.equals(input_NEEDTOREMOVE.get(y)))
                {
                    subsiCheck++;
                }
                else
                {
                    subsiCheckError++;
                    System.out.println("error in  => " +input.get(y) + "   ----------------------"+input_NEEDTOREMOVE.get(y));
                }


            }

            if(factValue)
            {
                //writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");

                writer1.write("<http://swc2017.aksw.org/task2/dataset/"+id.get(y) +"><http://swc2017.aksw.org/hasTruthValue>\"1.0\"^^<http://www.w3.org/2001/XMLSchema#double> ."+ "\n");
            }
            else
            {
                writer1.write("<http://swc2017.aksw.org/task2/dataset/"+id.get(y) +"><http://swc2017.aksw.org/hasTruthValue>\"0.0\"^^<http://www.w3.org/2001/XMLSchema#double> ."+ "\n");

            }


        }


        System.out.println("                    "+input.size());
        System.out.println("positive award Check         = "+awardCheck+"          ,negative awardCheck         = "+awardCheckError);
        System.out.println("positive role Check          = "+roleCheck+"          ,negative roleCheck          = "+roleCheckError);
        System.out.println("positive subordinate Check  = "+subCheck+"          ,negative subordinate Check  = "+subCheckError);
        System.out.println("positive honour Check       = "+honourCheck+"          ,negative honour Check       = "+honourCheckError);
        System.out.println("positive generator Check    = "+genCheck+"          ,negative generator Check    = "+genCheckError);
        System.out.println("positive clerk Check        = "+clerkCheck+"          ,negative clerk Check    = "+clerkCheckError);
        System.out.println("positive better HAlf Check  = "+bhCheck+"          ,negative bh Check    = "+bhCheckError);
        System.out.println("positive subsidery Check  = "+subsiCheck+"          ,negative subsidery Check    = "+subsiCheckError);

        System.out.println("positive stars Check  = "+starsCheck+"          ,negative stars Check    = "+starsCheckError);
        System.out.println("positive place Check         = "+placeCheck+"          ,negative place Check         = "+placeCheckError);
        System.out.println("positive squad Check         = "+squadCheck+"          ,negative award Check         = "+squadCheckError);
        System.out.println("positive team Check         = "+teamCheck+"          ,negative award Check         = "+teamCheckError);
        System.out.println("positive author Check         = "+authorCheck+"          ,negative award Check         = "+authorCheckError);
        System.out.println("positive spouse Check         = "+spouseCheck+"          ,negative award Check         = "+spouseCheckError);
        int pCheck = awardCheck+roleCheck+subCheck+honourCheck+genCheck+clerkCheck+bhCheck+subsiCheck+starsCheck+placeCheck+squadCheck+teamCheck+authorCheck+spouseCheck;
        int nCheck =awardCheckError+roleCheckError+subCheckError+honourCheckError+genCheckError+clerkCheckError+bhCheckError+subsiCheckError+starsCheckError+placeCheckError+squadCheckError+teamCheckError+authorCheckError+spouseCheckError;

        System.out.println("total positive Check  = "+pCheck+"          ,total negative Check    = "+nCheck);
        System.out.println("================================================================================================");



        checkFact();


        writer1.close();


    }

    private static void checkFact() throws IOException
    {
        //System.out.println("training done"+ awd.testVal);
        Path currentRelativePath = Paths.get("");
        Writer writer1 = null;
        String opath = currentRelativePath.toAbsolutePath().toString();
        if(System.getProperty("os.name").startsWith("Windows")) {
            opath = opath + "\\result.ttl";
        }
        else
            {
                opath = opath + "//result.ttl";
        }

        writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(opath), StandardCharsets.UTF_8));
        //writer1.write("Skill\tE-mail\n");


        String data = "";
        //Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        if(System.getProperty("os.name").startsWith("Windows")) {
            path = path + "\\SNLP2019_test.tsv";
        }
        else
        {
            path = path + "//SNLP2019_test.tsv";
        }

        File file = new File(path);


        int placeVal = 0;
        int squadVal = 0;
        int teamVal = 0;
        int spouseVal = 0;
        int awardVal = 0;
        int starVal = 0;
        int subsidaryVal = 0;
        int roleVal = 0;
        int clerkVal = 0;
        int subordinateVal = 0;
        int honourVal = 0;
        int betterVal = 0;
        int authorVal = 0;
        int generatorVal = 0;


        HashMap<String, String> map1 = new HashMap<>();
        HashMap<String, String> map2 = new HashMap<>();

        ArrayList<String> input = new ArrayList<>();
        ArrayList<String> id = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = "";

        while ((s = br.readLine()) != null)
        {
            String [] inputstring = s.split("\\t+");
            input.add(inputstring[1]);
            id.add(inputstring[0]);
            //System.out.println("inputstring = " +inputstring[1]);
            if(trainingFactIdList.contains(inputstring[0]))
            {
                //System.out.println(" fatc id =>"+inputstring[0]);
            }
            if(trainingFactsList.contains(inputstring[1]))
            {
                //System.out.println(" fatc  =>"+inputstring[1]);
            }
        }
        boolean factValue = false;
        for (int y = 1; y < input.size(); y++)
        {

            if (input.get(y).contains("place"))
            {
                placeVal++;
                //System.out.println("Place -->" +input.get(y));
                //print(input.get(y));

                factValue = p.getInfo(input.get(y));

            }
            else if (input.get(y).contains("author"))
            {
                authorVal++;
                //System.out.println("Author -->" +input.get(y));

                factValue = a.getInfo(input.get(y));

                /*if (ifExists1)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }*/


            }
            else if (input.get(y).contains("spouse"))
            {
                spouseVal++;
                //System.out.println("spouse -->" +input.get(y));

                factValue = spouseObj.getInfo(input.get(y));
/*
                if (ifExists1)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }
  */
            }
            else if (input.get(y).contains("team"))
            {
                teamVal++;
                //System.out.println("Team -->" +input.get(y));

                factValue = t.getInfo(input.get(y));

    /*            if (ifExists1)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }
      */
            }
            else if (input.get(y).contains("squad"))
            {
                squadVal++;
                //System.out.println("Squad -->" +input.get(y));
                //print(input.get(y));
                String in = input.get(y);
                in = input.get(y).replace("squad", "team");
                factValue = t.getInfo(in);

        /*        if (ifExists1)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }
          */
            }
            else if (input.get(y).contains("stars"))
            {
                //System.out.println("Stars -->" +input.get(y));
                starVal++;
                //print(input.get(y));
                factValue = starsObj.getInfo(input.get(y));


            /*    if (ifExists1)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }
*/
            }
            else if (input.get(y).contains("award"))
            {
                awardVal++;
                awd.preprocessInputString(input.get(y),awd);
                factValue = awd.isFact(input.get(y),awd);
                //System.out.println(input.get(y)+"    ------ "+factValue);

                /*if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }*/

            }
            else if (input.get(y).contains("role"))
            {
                roleVal++;
                role.preprocessInputString(input.get(y),role);
                factValue = role.isFact(input.get(y),role);
                //System.out.println(input.get(y)+"    ------ "+factValue);
               /* if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }
*/
            }
            else if (input.get(y).contains("subordinate"))
            {
                subordinateVal++;
                sub.preprocessInputString(input.get(y), sub);
                factValue = sub.isFact(input.get(y), sub);
                //System.out.println(input.get(y)+"    ------ "+factValue);
               /* if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }
                */


            }
            else if (input.get(y).contains("honour"))
            {
                honourVal++;
                honour.preprocessInputString(input.get(y), honour);
                factValue = honour.isFact(input.get(y), honour);
                //System.out.println(input.get(y)+"    ------ "+factValue);

               /* if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }

                */
            }
            else if (input.get(y).contains("generator"))
            {
                generatorVal++;
                gen.preprocessInputString(input.get(y), gen);
                factValue = gen.isFact(input.get(y), gen);
                //System.out.println(input.get(y)+"    ------ "+factValue);
               /* if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }

                */
            }
            else if (input.get(y).contains(" has been"))
            {
                clerkVal++;
                clerk.preprocessInputString(input.get(y), clerk);
                factValue = clerk.isFact(input.get(y), clerk);
                //System.out.println(input.get(y)+"    ------ "+factValue);
               /* if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }

                */
            }
            else if (input.get(y).contains("better half"))
            {
                betterVal++;
                bh.preprocessInputString(input.get(y), bh);
                factValue = bh.isFact(input.get(y), bh);
                //System.out.println(input.get(y) + "    ------ " + factValue);
                /*if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }

                 */
            }
            else if (input.get(y).contains("subsidiary"))
            {
                subsidaryVal++;
                subsi.preprocessInputString(input.get(y), subsi);
                factValue = subsi.isFact(input.get(y), subsi);
                //System.out.println(input.get(y) + "    ------ " + factValue);
              /*  if (factValue)
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");
                }
                else
                {
                    writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 0.0+ "\n");
                }

               */
            }


            if (factValue)
            {
                //writer1.write(id.get(y) + "\t" + input.get(y) + "\t" + 1.0 + "\n");

                writer1.write("<http://swc2017.aksw.org/task2/dataset/"+id.get(y) +"><http://swc2017.aksw.org/hasTruthValue>\"1.0\"^^<http://www.w3.org/2001/XMLSchema#double> ."+ "\n");
            }
            else
            {
                writer1.write("<http://swc2017.aksw.org/task2/dataset/"+id.get(y) +"><http://swc2017.aksw.org/hasTruthValue>\"0.0\"^^<http://www.w3.org/2001/XMLSchema#double> ."+ "\n");

            }
        }



        int total = placeVal+squadVal+teamVal+awardVal+starVal+spouseVal+subsidaryVal+roleVal+clerkVal+subordinateVal+honourVal+betterVal+authorVal+generatorVal;

        System.out.println("placeVal =>"+placeVal);
        System.out.println("squadVal =>"+squadVal);
        System.out.println("teamVal =>"+teamVal);
        System.out.println("awardVal =>"+awardVal);
        System.out.println("starVal =>"+starVal);
        System.out.println("spouseVal =>"+spouseVal);
        System.out.println("subsidaryVal =>"+subsidaryVal);
        System.out.println("roleVal =>"+roleVal);
        System.out.println("clerkVal =>"+clerkVal);
        System.out.println("subordinateVal =>"+subordinateVal);
        System.out.println("honourVal =>"+honourVal);
        System.out.println("betterVal =>"+betterVal);
        System.out.println("authorVal =>"+authorVal);
        System.out.println("generatorVal =>"+generatorVal);


        System.out.println("total =>"+total);


        writer1.close();
    }


    private static boolean getDetails(String s) throws IOException
    {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // make an example document
        CoreDocument doc = new CoreDocument(s);
        // annotate the document
        pipeline.annotate(doc);
        // view results
        System.out.println("---");
        String u = "";
        System.out.println("entities found");
        ArrayList<String> per = new ArrayList<>();
        for (CoreEntityMention em : doc.entityMentions())
        {
            System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
            if (em.entityType().toLowerCase().equals("person"))
            {
                per.add(em.text());
            }

        }

        for (int i = 0; i < per.size(); i++)
        {
            u = u + per.get(i) + " ";
        }
        System.out.println("Info " + u);
        if (u.length() > 1)
        {
            u = u.replace(" ", "%20");
            u = u.substring(0, u.length() - 3);
        }


        System.out.println(per.size());
        System.out.println("Text Serach " + u);


            System.out.println("---");
            System.out.println("tokens and ner tags");
            String tokensAndNERTags = doc.tokens().stream().map(token -> "(" + token.word() + "," + token.ner() + ")").collect(
                    Collectors.joining(" "));
            System.out.println(tokensAndNERTags);


            //String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=" +u;

            String url = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + u + "&rvsection=0";
            System.out.println(url);

            HttpClient httpclient = HttpClients.createDefault();
            System.out.println(url);
            HttpPost httpPost = new HttpPost(url);

            HttpResponse response = httpclient.execute(httpPost);
            BasicStatusLine n = (BasicStatusLine) response.getStatusLine();
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject obj = new JSONObject(jsonString);
            if(obj.getJSONObject("query") != null)
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
                if(rev.get(0) != null)
                {
                    ne3 = (Map<String, Object>) rev.get(0);
                    Object h = ne3.get("*");


                    if (h != null)
                    {
                        String g = h.toString();
                        System.out.println(g);

                    }

                }
            }






            return true;

    }

    private static void print(String s )
    {
        HashMap<String , String> placeMap = new HashMap<>();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // make an example document
        CoreDocument doc = new CoreDocument(s);
        // annotate the document
        pipeline.annotate(doc);
        // view results
        System.out.println("---");
        String u = "";
        System.out.println("entities found");
        ArrayList<String> per = new ArrayList<>();
        for (CoreEntityMention em : doc.entityMentions())
        {
            System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType());
            if (em.entityType().toLowerCase().equals("person"))
            {
                per.add(em.text());
            }

        }
    }


    private static void sendGET() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        }
        else {
            System.out.println("GET request not worked");
        }

    }
}

package com.example.newsappdemo;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class QueryUtils {

    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }



     public static ArrayList<News> fetchNewsData(String requestUrl)
    {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        ArrayList<News> news = extractNews(jsonResponse);

        return news;
    }


    //Parsing the Json Response is done here
    private static ArrayList<News> extractNews(String jsonResponse)
    {
        ArrayList<News> news = new ArrayList<>();
        if(TextUtils.isEmpty(jsonResponse))
            return null;
        //Extracting Json
        try {
            JSONObject allInfo = new JSONObject(jsonResponse);
            JSONObject root = allInfo.getJSONObject("response");
            JSONArray resultsArray = root.getJSONArray("results");
            for(int i = 0;i<resultsArray.length();i++)
            {
                JSONObject innerObject = resultsArray.getJSONObject(i);
                String title = innerObject.getString("webTitle");
                String url = innerObject.getString("webUrl");
                String date = innerObject.getString("webPublicationDate");
                String author = "";
                String section = innerObject.getString("sectionName");
                news.add(new News(title,author,url,date,section));
            }

        }catch (JSONException e){
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);

        }
        // Return the list of news
        return news;
    }



    private static URL createUrl(String s)
    {
        URL url = null;
        try {
            url = new URL(s);
        }catch (Exception e){
            Log.e(LOG_TAG,"Error with creating Url:"+e);
            return null;
        }
        return  url;
    }


    private static String makeHttpRequest(URL url) throws IOException
    {
        String jsonResponse = "";
        if(url == null)
            return jsonResponse;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
            {
                Log.e(LOG_TAG,"Error Response Code:"+urlConnection.getResponseCode());
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


//    static String createStringUrl() {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("http")
//                .encodedAuthority("content.guardianapis.com")
//                .appendPath("search")
//                .appendQueryParameter("order-by", "newest")
//                .appendQueryParameter("show-references", "author")
//                .appendQueryParameter("show-tags", "contributor")
//                .appendQueryParameter("q", "Android")
//                .appendQueryParameter("api-key", "test");
//        String url = builder.build().toString();
//        return url;
//    }
//
//    static URL createUrl() {
//        String stringUrl = createStringUrl();
//        try {
//            return new URL(stringUrl);
//        } catch (MalformedURLException e) {
//            Log.e("Queryutils", "Error creating URL: ", e);
//            return null;
//        }
//    }
//
//    private static String formatDate(String rawDate) {
//        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
//        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
//        try {
//            Date parsedJsonDate = jsonFormatter.parse(rawDate);
//            String finalDatePattern = "MMM d, yyy";
//            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
//            return finalDateFormatter.format(parsedJsonDate);
//        } catch (ParseException e) {
//            Log.e("QueryUtils", "Error parsing JSON date: ", e);
//            return "";
//        }
//    }
//
//    static String makeHttpRequest(URL url) throws IOException {
//        String jsonResponse = "";
//
//        if (url == null){
//            return jsonResponse;
//        }
//        HttpURLConnection urlConnection = null;
//        InputStream inputStream = null;
//
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.setReadTimeout(10000 /* milliseconds */);
//            urlConnection.setConnectTimeout(15000 /* milliseconds */);
//            urlConnection.connect();
//            if (urlConnection.getResponseCode() == 200){
//                inputStream = urlConnection.getInputStream();
//                jsonResponse = readFromStream(inputStream);
//            } else {
//                Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
//            }
//        } catch (IOException e) {
//            Log.e("Queryutils", "Error making HTTP request: ", e);
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (inputStream != null) {
//                inputStream.close();
//            }
//        }
//        return jsonResponse;
//    }
//
//    private static String readFromStream(InputStream inputStream) throws IOException {
//        StringBuilder output = new StringBuilder();
//        if (inputStream != null) {
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
//            BufferedReader reader = new BufferedReader(inputStreamReader);
//            String line = reader.readLine();
//            while (line != null) {
//                output.append(line);
//                line = reader.readLine();
//            }
//        }
//        return output.toString();
//    }
//
//    static List<News> parseJson(String response) {
//        ArrayList<News> listOfNews = new ArrayList<>();
//        try {
//            JSONObject jsonResponse = new JSONObject(response);
//            JSONObject jsonResults = jsonResponse.getJSONObject("response");
//            JSONArray resultsArray = jsonResults.getJSONArray("results");
//
//            for (int i = 0; i < resultsArray.length(); i++) {
//                JSONObject oneResult = resultsArray.getJSONObject(i);
//                String webTitle = oneResult.getString("webTitle");
//                String url = oneResult.getString("webUrl");
//                String date = oneResult.getString("webPublicationDate");
//                date = formatDate(date);
//                String section = oneResult.getString("sectionName");
//                JSONArray tagsArray = oneResult.getJSONArray("tags");
//                String author = "";
//
//                if (tagsArray.length() == 0) {
//                    author = null;
//                } else {
//                    for (int j = 0; j < tagsArray.length(); j++) {
//                        JSONObject firstObject = tagsArray.getJSONObject(j);
//                        author += firstObject.getString("webTitle") + ". ";
//                    }
//                }
//                listOfNews.add(new News(webTitle, author, url, date, section));
//            }
//        } catch (JSONException e) {
//            Log.e("Queryutils", "Error parsing JSON response", e);
//        }
//        return listOfNews;
//    }
}

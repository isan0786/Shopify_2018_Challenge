
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Ishan
 * emailId - ishaananand696@gmail.com
 *
 */
public class Solution {

    
    public static void main(String[] args) {

        String temporary = "";
        String emptyContainer = "";
        int tCounter = 0;
        int counter = 0;
        double cartOrProductSaver = 0;
        double totalAmount = 0;

        double totalDiscountPrice = 0;
        long id = 0;
        int pages = 1;
        String save = "";
        String saver = "";
        int counters = 0;
        boolean go = true;
        long totalItems = 0;
        String discountType = "";
        double discountValue = 0;
        String jsonObjects = "";
        String userInput = "";
        String temp = "";
        Scanner input = new Scanner(System.in);

        userInput = input.nextLine();

       
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(userInput);
            JSONObject jf = (JSONObject) obj;

            discountValue = (double) jf.get("discount_value");

            id = (long) jf.get("id");

            discountType = (String) jf.get("discount_type");

            if (jf.containsKey("collection")) {
                save = (String) jf.get("collection");

            }
            if (jf.containsKey("cart_value")) {
                cartOrProductSaver = (double) jf.get("cart_value");
            }
            if (jf.containsKey("product_value")) {
                cartOrProductSaver = (double) jf.get("product_value");
            }

            while (go) {

                URL url = new URL("http://backend-challenge-fall-2018.herokuapp.com/carts.json?id=" + id + "&page=" + pages);

                BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));

                while ((emptyContainer = br.readLine()) != null) {

                    jsonObjects += emptyContainer;

                }

                br.close();

                Object objs = parser.parse(jsonObjects); 
                JSONObject js = (JSONObject) objs;
                

                JSONObject paginationObject = (JSONObject) js.get("pagination");

                if (tCounter == 0) {
                    totalItems = (long) (paginationObject.get("total"));

                    tCounter++;
                }

                JSONArray arr = (JSONArray) js.get("products");

                Iterator k = arr.iterator();
                Iterator s = arr.iterator();
                while (k.hasNext()) {

                    temporary = k.next().toString();
                    Object objt = parser.parse(temporary);
                    JSONObject jt = (JSONObject) objt;
                    totalAmount += (double) jt.get("price");

                    counters++;

                }

                if (jf.containsKey("collection") && (discountType.equals("product"))) {

                    save = (String) jf.get("collection");
                    while (s.hasNext()) {

                        temporary = s.next().toString();

                        Object objt = parser.parse(temporary);
                        JSONObject jt = (JSONObject) objt;

                        if (jf.containsKey("collection")) {
                            saver = (String) jt.get("collection");

                            if ((saver != null && save != null) && saver.equals(save)) {

                                if (((double) jt.get("price") > discountValue) || ((double) jt.get("price") == discountValue)) {

                                    totalDiscountPrice += (double) jt.get("price") - discountValue;
                                    counter++;

                                } else {
                                  
                                    counter++;

                                }
                            } else {
                                totalDiscountPrice += (double) jt.get("price");
                                counter++;

                            }

                        } else {
                            //add into total
                            totalDiscountPrice += (double) jt.get("price");
                            counter++;
                        }

                    }
                }

                if (jf.containsKey("cart_value") && (discountType.equals("cart"))) {

                    cartOrProductSaver = (double) jf.get("cart_value");

                    if ((totalAmount > cartOrProductSaver) || (totalAmount == cartOrProductSaver)) {

                        totalDiscountPrice = totalAmount - discountValue;

                        counter += counters;

                    } else {
                        totalDiscountPrice = totalAmount;
                        counter += counters;
                    }
                }

                //work on this one now
                if (jf.containsKey("product_value") && (discountType.equals("product"))) {

                    cartOrProductSaver = (double) jf.get("product_value");
                    while (s.hasNext()) {

                        temporary = s.next().toString();

                        Object objt = parser.parse(temporary);
                        JSONObject jt = (JSONObject) objt;

                        if (((double) jt.get("price") > cartOrProductSaver) || ((double) jt.get("price") == cartOrProductSaver)) {

                            if ((double) jt.get("price") - discountValue < 0) {
                                counter++;
                            } else {
                                totalDiscountPrice += (double) jt.get("price") - discountValue;
                                counter++;
                            }
                        } else {
                            totalDiscountPrice += (double) jt.get("price");
                            counter++;
                        }

                    }

                }

                if ((totalItems - counter) > 0) {
                    pages++;
                } else {

                    JSONObject lastObj = new JSONObject();

                    lastObj.put("total_after_discount", new Double(totalDiscountPrice));

                    lastObj.put("total_amount", new Double(totalAmount));

                    //  temp = lastObj.toString();
                    temp = "{\"total_amount\":" + String.valueOf(totalAmount) + ",\"total_after_discount\":" + String.valueOf(totalDiscountPrice) + "}";
                   
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonParser jp = new JsonParser();
                    JsonElement je = jp.parse(temp);
                 
                    System.out.println(gson.toJson(je));

                    go = false;
                    return; // return string(json object), method will return string
                }

                jsonObjects = "";
            }
        } catch (ParseException ex) {
            System.out.println(ex+"    Please Check your input");
        } catch (MalformedURLException ex) {
            System.out.println(ex+"    Please Check your input");
        } catch (IOException ex) {
            System.out.println(ex+"    Please Check your input");
        }catch (Exception ex) {
             System.out.println(ex+"   Please Check your input");
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weathersearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class server extends Thread {

    private final String places[] = {"taipei-city", "keelung-city", "new-taipei-city",
        "taoyuan-city", "hsinchu-city", "miaoli-county", "taichung-city", "nantou-county",
        "changhua-county", "yunlin-county", "chiayi-city", "tainan-city", "kaohsiung-city",
        "pingtung-county", "taitung-county", "hualien-county", "yilan-county", "penghu-county",
        "kinmen-county", "lienchiang-county"};
    private final String cityCodes[] = {"315078", "312605", "2515397",
        "3369297", "313567", "3369299", "315040", "3369301",
        "3369300", "3369302", "312591", "314999", "313812",
        "3369304", "3369305", "3369306", "3369296", "3369307",
        "2332525", "3369309"};
    public String weather = "";
    public String date = "";
    public String rain = "-";
    public String[] Day = new String[3];
    public String[] Day1 = new String[6];
    public String[] Day2 = new String[6];
    public String[] Day3 = new String[6];
    public String[] Day4 = new String[6];
    public String[] Day5 = new String[6];
    public String[] Day6 = new String[6];
    public String[] Day7 = new String[6];
    Map<String, String> map = new HashMap<String, String>();

    class Connection extends Thread {

        Socket socket;
        ObjectInputStream input;
        ObjectOutputStream output;

        //create input and output stream
        public Connection(Socket s) {
            socket = s;
            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException x) {
                x.printStackTrace();
            }
        }

        //method to search weather and send back
        public void search(int received) throws IOException, URISyntaxException {
            //encode user input place
            //search weather according to input place
            loadURL("https://www.accuweather.com/en/tw/" + places[received] + "/" + cityCodes[received] + "/daily-weather-forecast/" + cityCodes[received]);
            analyzeWeather();
            String per = Day1[5];
            if(Day1[5].length()==3)per = Day1[5].substring(0,2);
            if(Day1[5].length()==2)per = Day1[5].substring(0,1);
            if (Integer.parseInt(per) >= 30) {
                rain = "Carry rain gear";
            }
            //send result to client
            String col = "Day\tdate\tTemperature           Precipitation\tPharse\n";
            String d1 = String.format("%-3s\t%-5s\t%-3s-%-3s℃\t%-6s\t%-40s\n", Day1[0], Day1[1], Day1[3], Day1[2], Day1[5], Day1[4]);
            String d2 = String.format("%-3s\t%-5s\t%-3s-%-3s℃\t%-6s\t%-40s\n", Day2[0], Day2[1], Day2[3], Day2[2], Day2[5], Day2[4]);
            String d3 = String.format("%-3s\t%-5s\t%-3s-%-3s℃\t%-6s\t%-40s\n", Day3[0], Day3[1], Day3[3], Day3[2], Day3[5], Day3[4]);
            String d4 = String.format("%-3s\t%-5s\t%-3s-%-3s℃\t%-6s\t%-40s\n", Day4[0], Day4[1], Day4[3], Day4[2], Day4[5], Day4[4]);
            String d5 = String.format("%-3s\t%-5s\t%-3s-%-3s℃\t%-6s\t%-40s\n", Day5[0], Day5[1], Day5[3], Day5[2], Day5[5], Day5[4]);
            String d6 = String.format("%-3s\t%-5s\t%-3s-%-3s℃\t%-6s\t%-40s\n", Day6[0], Day6[1], Day6[3], Day6[2], Day6[5], Day6[4]);
            String d7 = String.format("%-3s\t%-5s\t%-3s-%-3s℃\t%-6s\t%-40s\n", Day7[0], Day7[1], Day7[3], Day7[2], Day7[5], Day7[4]);
            output.writeObject(places[received] + ":\n" + "-----------------------------------------------------------------------------------------------------------------------------------------------------------------\n"
                    + col + d1 + d2 + d3 + d4 + d5 + d6 + d7);
        }

        //method to search weather and send back
        public void search2(int received) throws IOException, URISyntaxException {
            //encode user input place
            //search weather according to input place
            String wear = "";
            loadURL2("https://www.accuweather.com/en/tw/" + places[received] + "/" + cityCodes[received] + "/current-weather/" + cityCodes[received]);
            analyzeWeather2();
            map.put("1", "Thick down jacket");
            map.put("2", "Thin down jacket");
            map.put("3", "Thick coat");
            map.put("4", "Thin coat");
            map.put("5", "Thick sweater or vest");
            map.put("6", "sweater");
            map.put("7", "Cotton top");

            int tem = Integer.parseInt(Day[2]);
            if (tem >= 25) {
                wear = map.get("7");
            } else if (tem <= 10) {
                wear = map.get("1") + "+" + map.get("5") + "+" + map.get("6") + "+" + map.get("7");
            } else if (tem == 24) {
                wear = map.get("6");
            } else if (tem == 23) {
                wear = map.get("4");
            } else if (tem == 22) {
                wear = map.get("4") + "+" + map.get("7");
            } else if (tem == 21) {
                wear = map.get("4") + "+" + map.get("6");
            } else if (tem == 20) {
                wear = map.get("3") + "+" + map.get("7");
            } else if (tem == 19) {
                wear = map.get("3") + "+" + map.get("6");
            } else if (tem == 18) {
                wear = map.get("3") + "+" + map.get("6") + "+" + map.get("7");
            } else if (tem == 17) {
                wear = map.get("3") + "+" + map.get("5");
            } else if (tem == 16) {
                wear = map.get("3") + "+" + map.get("5") + "+" + map.get("7");
            } else if (tem == 15) {
                wear = map.get("3") + "+" + map.get("5") + "+" + map.get("6");
            } else if (tem == 14) {
                wear = map.get("2") + "+" + map.get("5") + "+" + map.get("6");
            } else if (tem == 13) {
                wear = map.get("1") + "+" + map.get("5");
            } else if (tem == 12) {
                wear = map.get("1") + "+" + map.get("5") + "+" + map.get("7");
            } else if (tem == 11) {
                wear = map.get("1") + "+" + map.get("5") + "+" + map.get("6");
            }
            //send result to client
            output.writeObject("Time: " + Day[0] + "\nTemperature: " + Day[1]
                    + "℃\nReal Feel: " + Day[2] + "℃\nSuggestion: \n" + rain + "\n" + wear);
        }

        @Override
        public void run() {
            try {
                output.flush();
                while (true) {
                    //receive data from client
                    int received = (int) input.readObject();
                    System.out.println("receiving " + received);
                    //search weather and send back
                    search(received);
                    search2(received);
                }
            } catch (ClassNotFoundException x) {
                x.printStackTrace();
            } catch (IOException x) {
                System.out.println(x);
                close();
            } catch (URISyntaxException ex) {
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //method to close connection
        public void close() {
            try {
                System.out.println("close connection...");
                input.close();
                output.close();
                socket.close();
                removeConnection(this);
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
    }

    //socket parameter
    int port;
    int maxConnections;
    ServerSocket serverSocket;
    List<Connection> connections;

    //constructor to new server socket
    public server(int p, int c) {
        port = p;
        maxConnections = c;
        connections = Collections.synchronizedList(new LinkedList());
        try {
            serverSocket = new ServerSocket(port, maxConnections);
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    //remove connection
    void removeConnection(Connection connection) {
        connections.remove(connection);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // connection client socket
                System.out.println("wait for connections...");
                Socket connSocket = serverSocket.accept();
                System.out.println("maxConnections=" + maxConnections);
                if (connections.size() < maxConnections) {
                    Connection connection = new Connection(connSocket);
                    connections.add(connection);
                    connection.start();
                    System.out.println("create connection socket successfully");
                }
            } catch (IOException x) {
                x.printStackTrace();
            }
        }

    }

    public void loadURL2(String urls) throws IOException, URISyntaxException {
        String data = "";
        try {
            //url connect
            URL url = new URL(urls);
            URLConnection urlConnection = url.openConnection();
            System.out.println(urlConnection.getContentEncoding());
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            //scan data
            while ((line = in.readLine()) != null) {
                data += line;
            }
            int beg = data.indexOf("<p class=\"module-header sub date\">");
            int end = data.indexOf("<p class=\"realFeel\">");
            date = data.substring(beg, end);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void analyzeWeather2() throws IOException, URISyntaxException {
        int time = date.indexOf("module-header sub date");
        int timeend = date.indexOf("<div class=\"temp-icon-wrapper\">");
        int temp = date.indexOf("value");
        int tempend = date.indexOf("hi-lo-label");
        int feel = date.indexOf("RealFeel");
        Day[0] = date.substring(time + 24, timeend - 5);
        Day[1] = date.substring(temp + 11, temp + 13);
        Day[2] = date.substring(feel + 18, feel + 20);
        if (Day[1].endsWith("&")) {
            Day[1] = Day[1].substring(0, 1);
        }
        if (Day[2].endsWith("&")) {
            Day[2] = Day[2].substring(0, 1);
        }
    }

    //method to get weather data from website
    public void loadURL(String urls) throws IOException, URISyntaxException {
        String data = "";
        try {
            //url connect
            URL url = new URL(urls);
            URLConnection urlConnection = url.openConnection();
            System.out.println(urlConnection.getContentEncoding());
            HttpURLConnection connection = null;
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            //scan data
            while ((line = in.readLine()) != null) {
                data += line;
            }
            int beg = data.indexOf("class=\"forecast-list-card forecast-card  today\"");
            int end = data.indexOf("<p class=\"module-title\">");
            weather = data.substring(beg, end);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method to get weather data from website
    public void analyzeWeather() throws IOException, URISyntaxException {
        int d1 = weather.indexOf("day=1\">");
        int d2 = weather.indexOf("day=2\">");
        int d3 = weather.indexOf("day=3\">");
        int d4 = weather.indexOf("day=4\">");
        int d5 = weather.indexOf("day=5\">");
        int d6 = weather.indexOf("day=6\">");
        int d7 = weather.indexOf("day=7\">");
        String day1 = weather.substring(d1, d2);
        String day2 = weather.substring(d2, d3);
        String day3 = weather.substring(d3, d4);
        String day4 = weather.substring(d4, d5);
        String day5 = weather.substring(d5, d6);
        String day6 = weather.substring(d6, d7);
        String day7 = weather.substring(d7);
        //day1
        int dow = day1.indexOf("<p class=\"dow\">");
        int sub = day1.indexOf("<p class=\"sub\">");
        int icon = day1.indexOf("</p>	</div>	<img class=\"weather-icon icon");
        int high = day1.indexOf("<span class=\"high\">");
        int low = day1.indexOf("<span class=\"low\">");
        int phrase = day1.indexOf("<span class=\"phrase\">");
        int precip = day1.indexOf("<div class=\"info precip\">");
        int prec = day1.indexOf("%</p>");
        Day1[0] = day1.substring(dow + 18, sub - 8);
        Day1[1] = day1.substring(sub + 18, icon - 2);
        Day1[2] = day1.substring(high + 20, high + 22);
        Day1[3] = day1.substring(low + 21, low + 23);
        Day1[4] = day1.substring(phrase + 23, precip - 10);
        Day1[5] = day1.substring(prec - 2, prec + 1);
        if (Day1[5].startsWith(">")) {
            Day1[5] = Day1[5].substring(1);
        }
        if (Day1[3].contains("&")) {
            Day1[3] = Day1[3].substring(0, 1);
        }
        //day2
        dow = day2.indexOf("<p class=\"dow\">");
        sub = day2.indexOf("<p class=\"sub\">");
        icon = day2.indexOf("</p>	</div>	<img class=\"weather-icon icon");
        high = day2.indexOf("<span class=\"high\">");
        low = day2.indexOf("<span class=\"low\">");
        phrase = day2.indexOf("<span class=\"phrase\">");
        precip = day2.indexOf("<div class=\"info precip\">");
        prec = day2.indexOf("%</p>");
        Day2[0] = day2.substring(dow + 18, sub - 8);
        Day2[1] = day2.substring(sub + 18, icon - 2);
        Day2[2] = day2.substring(high + 20, high + 22);
        Day2[3] = day2.substring(low + 21, low + 23);
        Day2[4] = day2.substring(phrase + 23, precip - 10);
        Day2[5] = day2.substring(prec - 2, prec + 1);
        if (Day2[5].startsWith(">")) {
            Day2[5] = Day2[5].substring(1);
        }
        if (Day2[3].contains("&")) {
            Day2[3] = Day2[3].substring(0, 1);
        }
        //day3
        dow = day3.indexOf("<p class=\"dow\">");
        sub = day3.indexOf("<p class=\"sub\">");
        icon = day3.indexOf("</p>	</div>	<img class=\"weather-icon icon");
        high = day3.indexOf("<span class=\"high\">");
        low = day3.indexOf("<span class=\"low\">");
        phrase = day3.indexOf("<span class=\"phrase\">");
        precip = day3.indexOf("<div class=\"info precip\">");
        prec = day3.indexOf("%</p>");
        Day3[0] = day3.substring(dow + 18, sub - 8);
        Day3[1] = day3.substring(sub + 18, icon - 2);
        Day3[2] = day3.substring(high + 20, high + 22);
        Day3[3] = day3.substring(low + 21, low + 23);
        Day3[4] = day3.substring(phrase + 23, precip - 10);
        Day3[5] = day3.substring(prec - 2, prec + 1);
        if (Day3[5].startsWith(">")) {
            Day3[5] = Day3[5].substring(1);
        }
        if (Day3[3].contains("&")) {
            Day3[3] = Day3[3].substring(0, 1);
        }
        //day4
        dow = day4.indexOf("<p class=\"dow\">");
        sub = day4.indexOf("<p class=\"sub\">");
        icon = day4.indexOf("</p>	</div>	<img class=\"weather-icon icon");
        high = day4.indexOf("<span class=\"high\">");
        low = day4.indexOf("<span class=\"low\">");
        phrase = day4.indexOf("<span class=\"phrase\">");
        precip = day4.indexOf("<div class=\"info precip\">");
        prec = day4.indexOf("%</p>");
        Day4[0] = day4.substring(dow + 18, sub - 8);
        Day4[1] = day4.substring(sub + 18, icon - 2);
        Day4[2] = day4.substring(high + 20, high + 22);
        Day4[3] = day4.substring(low + 21, low + 23);
        Day4[4] = day4.substring(phrase + 23, precip - 10);
        Day4[5] = day4.substring(prec - 2, prec + 1);
        if (Day4[5].startsWith(">")) {
            Day4[5] = Day4[5].substring(1);
        }
        if (Day4[3].contains("&")) {
            Day4[3] = Day4[3].substring(0, 1);
        }
        //day5
        dow = day5.indexOf("<p class=\"dow\">");
        sub = day5.indexOf("<p class=\"sub\">");
        icon = day5.indexOf("</p>	</div>	<img class=\"weather-icon icon");
        high = day5.indexOf("<span class=\"high\">");
        low = day5.indexOf("<span class=\"low\">");
        phrase = day5.indexOf("<span class=\"phrase\">");
        precip = day5.indexOf("<div class=\"info precip\">");
        prec = day5.indexOf("%</p>");
        Day5[0] = day5.substring(dow + 18, sub - 8);
        Day5[1] = day5.substring(sub + 18, icon - 2);
        Day5[2] = day5.substring(high + 20, high + 22);
        Day5[3] = day5.substring(low + 21, low + 23);
        Day5[4] = day5.substring(phrase + 23, precip - 10);
        Day5[5] = day5.substring(prec - 2, prec + 1);
        if (Day5[5].startsWith(">")) {
            Day5[5] = Day5[5].substring(1);
        }
        if (Day5[3].contains("&")) {
            Day5[3] = Day5[3].substring(0, 1);
        }
        //day6
        dow = day6.indexOf("<p class=\"dow\">");
        sub = day6.indexOf("<p class=\"sub\">");
        icon = day6.indexOf("</p>	</div>	<img class=\"weather-icon icon");
        high = day6.indexOf("<span class=\"high\">");
        low = day6.indexOf("<span class=\"low\">");
        phrase = day6.indexOf("<span class=\"phrase\">");
        precip = day6.indexOf("<div class=\"info precip\">");
        prec = day6.indexOf("%</p>");
        Day6[0] = day6.substring(dow + 18, sub - 8);
        Day6[1] = day6.substring(sub + 18, icon - 2);
        Day6[2] = day6.substring(high + 20, high + 22);
        Day6[3] = day6.substring(low + 21, low + 23);
        Day6[4] = day6.substring(phrase + 23, precip - 10);
        Day6[5] = day6.substring(prec - 2, prec + 1);
        if (Day6[5].startsWith(">")) {
            Day6[5] = Day6[5].substring(1);
        }
        if (Day6[3].contains("&")) {
            Day6[3] = Day6[3].substring(0, 1);
        }
        //day7
        dow = day7.indexOf("<p class=\"dow\">");
        sub = day7.indexOf("<p class=\"sub\">");
        icon = day7.indexOf("</p>	</div>	<img class=\"weather-icon icon");
        high = day7.indexOf("<span class=\"high\">");
        low = day7.indexOf("<span class=\"low\">");
        phrase = day7.indexOf("<span class=\"phrase\">");
        precip = day7.indexOf("<div class=\"info precip\">");
        prec = day7.indexOf("%</p>");
        Day7[0] = day7.substring(dow + 18, sub - 8);
        Day7[1] = day7.substring(sub + 18, icon - 2);
        Day7[2] = day7.substring(high + 20, high + 22);
        Day7[3] = day7.substring(low + 21, low + 23);
        Day7[4] = day7.substring(phrase + 23, precip - 10);
        Day7[5] = day7.substring(prec - 2, prec + 1);
        if (Day7[5].startsWith(">")) {
            Day7[5] = Day7[5].substring(1);
        }
        if (Day7[3].contains("&")) {
            Day7[3] = Day7[3].substring(0, 1);
        }
    }

    public static void main(String args[]) {
        //set port and max connection num
        server server = new server(5001, 100);
        //start thread
        server.start();
    }
}


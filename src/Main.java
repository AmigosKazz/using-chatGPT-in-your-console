import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            while(true){
                System.out.print("You: ");
                String message = scanner.nextLine();
                if(message.equals("exit")){
                    System.out.println("Bot: Bye!");
                    break;
                }

                String response = chatGPT(message);
                System.out.println("Bot: " + response);
            }
        }
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        // The API key should be stored securely and not hardcoded into the application.
        // For example, it could be stored in an environment variable.
        String apiKey = System.getenv("OPENAI_API_KEY");
        String model = "gpt-3.5-turbo";

        try {
            //Create the http post request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            //Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Check the HTTP response code
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //Get the response
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String contentSubstring = "\"content\":\"";
                int contentIndex = response.indexOf(contentSubstring);

                System.out.println("API Response: " + response.toString());
                if (contentIndex != -1){
                    return response.substring(contentIndex + contentSubstring.length()).split("\"")[0].substring(4);
                } else {
                    return "Sorry, I don't understand.";
                }
            } else {
                System.out.println("HTTP error occurred: " + responseCode);
                return "Sorry, I can't respond right now.";
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
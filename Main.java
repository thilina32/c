import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        try {
            Path currentFolder = Paths.get(".");
            Path targetFile = null;

            // ෆෝල්ඩර් එකේ තියන පින්තූරයක් හරි text ෆයිල් එකක් හරි හොයනවා
            try (var paths = Files.list(currentFolder)) {
                targetFile = paths.filter(p -> {
                    String name = p.toString().toLowerCase();
                    return name.endsWith(".txt") || name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
                }).findFirst().orElse(null);
            }

            // 1. ෆයිල් එකක් නැත්තම් Error එකක් දෙනවා
            if (targetFile == null) {
                System.out.println("Error: No valid file (.txt, .png, .jpg, .jpeg) found in the current directory!");
                return;
            }

            String ext = targetFile.toString().toLowerCase();
            String mimeType = ext.endsWith(".txt") ? "text/plain" : (ext.endsWith(".png") ? "image/png" : "image/jpeg");

            // Node.js Backend එකට ෆයිල් එක යවනවා 
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://javascanner.vercel.app/process"))
                    .header("Content-Type", mimeType)
                    .POST(HttpRequest.BodyPublishers.ofFile(targetFile))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // සාර්ථක වුණොත් App.java විදියට සේව් කරලා 1 ප්‍රින්ට් කරනවා
            if (response.statusCode() == 200 && !response.body().equals("Error")) {
                Files.writeString(Paths.get("App.java"), response.body());
                System.out.println("1");
            } else {
                // 2. Backend එකෙන් අවුලක් ආවොත් Error එක පෙන්නනවා
                System.out.println("Error: Backend request failed! Status Code: " + response.statusCode());
                System.out.println("Response details: " + response.body());
            }
        } catch (Exception e) {
            // 3. වෙන මොකක් හරි Exception එකක් (උදා: Node.js server එක off වෙලා වගේ) ආවොත් Error එක පෙන්නනවා
            System.out.println("Error: An unexpected exception occurred!");
            System.out.println("Details: " + e.getMessage());
        }
    }
}

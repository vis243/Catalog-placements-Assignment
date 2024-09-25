import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;

// Class to represent a point (x, y)
class Point {
    int x;
    long y;

    Point(int x, long y) {
        this.x = x;
        this.y = y;
    }
}

public class ShamirSecretSharing {

    // Function to parse the JSON file and return the necessary points and k value
    public static Map<String, Object> parseInput(String filePath) {
        List<Point> points = new ArrayList<>();
        int k = 0;

        try {
            // Reading and parsing the JSON file
            String rawData = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonData = new JSONObject(rawData);

            int n = jsonData.getJSONObject("keys").getInt("n");
            k = jsonData.getJSONObject("keys").getInt("k");

            // Reading the points
            for (int i = 1; i <= n; i++) {
                if (jsonData.has(String.valueOf(i))) {
                    JSONObject pointData = jsonData.getJSONObject(String.valueOf(i));
                    if (pointData.has("base") && pointData.has("value")) {
                        int base = pointData.getInt("base");
                        String valueStr = pointData.getString("value");
                        long y = Long.parseLong(valueStr, base); // Decode the y value based on the base
                        points.add(new Point(i, y)); // Use the key as x
                    } else {
                        System.err.println("Error parsing data for index " + i + ". Missing base or value property.");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("points", points);
        result.put("k", k);

        return result;
    }

    // Function to compute Lagrange interpolation and find the constant term (c)
    public static double lagrangeInterpolation(List<Point> points, int k) {
        double constantTerm = 0;

        for (int i = 0; i < k; i++) {
            int xi = points.get(i).x;
            long yi = points.get(i).y;
            double li = 1;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    int xj = points.get(j).x;
                    li *= (-xj) / (double) (xi - xj); // Calculating the Lagrange basis polynomial
                }
            }
            constantTerm += yi * li; // Adding the contribution of each term
        }

        return constantTerm;
    }

    // Main function to handle the entire flow
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide the input file path as an argument.");
            return;
        }

        // Parse input and get points and k value
        Map<String, Object> parsedData = parseInput(args[0]);
        List<Point> points = (List<Point>) parsedData.get("points");
        int k = (int) parsedData.get("k");

        // Perform Lagrange interpolation and find the constant term (c)
        double constantTerm = lagrangeInterpolation(points, k);
        System.out.println("The constant term (c) is: " + constantTerm);
    }
}

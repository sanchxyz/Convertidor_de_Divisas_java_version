
// Importar las bibliotecas necesarias
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

// Clase principal que contiene el convertidor de divisas
public class CurrencyConverter {

    // Clase interna que mapea la respuesta de la API
    static class ExchangeRateResponse {
        String result; // Resultado de la API ("success" o "error")
        String base_code; // Código de la moneda base
        String target_code; // Código de la moneda objetivo
        double conversion_rate; // Tasa de conversión obtenida
    }

    // Método privado para obtener el tipo de cambio entre dos monedas
    private static double getExchangeRate(String fromCurrency, String toCurrency) {
        // API key y URL para obtener los datos de conversión
        String apiKey = "8cf46ee2da7e84a3bb68e9e4";
        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/pair/" + fromCurrency + "/" + toCurrency;

        try {
            // Conectar con la URL de la API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Solicitud GET para obtener datos

            // Obtener el código de respuesta HTTP
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer la respuesta de la API
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;

                // Almacenar la respuesta línea por línea
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // Cerrar el lector y desconectar
                in.close();
                connection.disconnect();

                // Convertir la respuesta JSON a un objeto Java usando Gson
                Gson gson = new Gson();
                ExchangeRateResponse exchangeRateResponse = gson.fromJson(content.toString(),
                        ExchangeRateResponse.class);

                // Verificar si el resultado fue exitoso
                if ("success".equals(exchangeRateResponse.result)) {
                    return exchangeRateResponse.conversion_rate; // Retornar la tasa de conversión
                } else {
                    // Mostrar error si la respuesta no fue exitosa
                    System.out.println("Error: " + exchangeRateResponse.result);
                }
            } else {
                // Mostrar error si el código de respuesta HTTP no es 200
                System.out.println("Error: no se pudo obtener una respuesta válida de la API. Código de respuesta: "
                        + responseCode);
            }
        } catch (Exception e) {
            // Manejo de excepciones generales con un mensaje de error
            System.out.println("Error al obtener el tipo de cambio: " + e.getMessage());
        }

        return 0.0; // Retorna 0.0 si no se puede obtener el tipo de cambio
    }

    // Método para convertir una cantidad de una moneda a otra
    public static double convertCurrency(String fromCurrency, String toCurrency, double amount) {
        // Obtener la tasa de cambio usando el método getExchangeRate
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        return amount * exchangeRate; // Multiplicar la cantidad por la tasa de conversión
    }

    // Método principal que ejecuta el programa
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Escáner para entrada del usuario

        System.out.println("Conversor de Divisas");
        System.out.println("====================");

        // Solicitar al usuario la moneda de origen
        System.out.print("Introduce la moneda de origen (por ejemplo, USD): ");
        String fromCurrency = scanner.nextLine().toUpperCase(); // Convertir a mayúsculas

        // Solicitar al usuario la moneda de destino
        System.out.print("Introduce la moneda de destino (por ejemplo, EUR): ");
        String toCurrency = scanner.nextLine().toUpperCase(); // Convertir a mayúsculas

        // Solicitar al usuario la cantidad a convertir
        System.out.print("Introduce la cantidad a convertir: ");
        double amount = scanner.nextDouble();

        // Validar que la cantidad ingresada sea mayor que cero
        if (amount <= 0) {
            System.out.println("Por favor ingresa un valor mayor que cero.");
            return;
        }

        // Realizar la conversión de la moneda
        double convertedAmount = convertCurrency(fromCurrency, toCurrency, amount);

        // Mostrar el resultado de la conversión
        System.out.println("Cantidad convertida: " + convertedAmount + " " + toCurrency);

        // Cerrar el escáner
        scanner.close();
    }
}

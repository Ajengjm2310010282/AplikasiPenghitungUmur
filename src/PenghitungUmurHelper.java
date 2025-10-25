
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import org.json.JSONArray;
import org.json.JSONObject;

public class PenghitungUmurHelper {

    public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
        Period selisih = Period.between(lahir, sekarang);
        return selisih.getYears() + " tahun, " + selisih.getMonths() + " bulan, " + selisih.getDays() + " hari";
    }

    public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate sekarang) {
        LocalDate ulangTahun = lahir.withYear(sekarang.getYear());
        if (!ulangTahun.isAfter(sekarang)) {
            ulangTahun = ulangTahun.plusYears(1);
        }
        return ulangTahun;
    }

    public String getDayOfWeekInIndonesian(LocalDate tanggal) {
        switch (tanggal.getDayOfWeek()) {
            case MONDAY:
                return "Senin";
            case TUESDAY:
                return "Selasa";
            case WEDNESDAY:
                return "Rabu";
            case THURSDAY:
                return "Kamis";
            case FRIDAY:
                return "Jumat";
            case SATURDAY:
                return "Sabtu";
            case SUNDAY:
                return "Minggu";
            default:
                return "";
        }
    }

    public static String getPeristiwaPenting(int bulan, int tanggal) {
        StringBuilder hasil = new StringBuilder();
        try {
            String urlString = String.format("https://byabbe.se/on-this-day/%d/%d/events.json", bulan, tanggal);
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Gagal terhubung ke server (HTTP " + conn.getResponseCode() + ")");
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONObject jsonObj = new JSONObject(response.toString());
            JSONArray events = jsonObj.getJSONArray("events");

            if (events.length() == 0) {
                return "Tidak ada peristiwa penting yang tercatat pada tanggal ini.";
            }

            hasil.append(" PERISTIWA PENTING PADA  ")
                    .append(String.format("%02d", tanggal)).append("/")
                    .append(String.format("%02d", bulan)).append("\n\n");

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String tahun = event.optString("year", "????");
                String deskripsi = event.optString("description", "(tidak ada deskripsi)");
                hasil.append("• ").append(tahun).append(": ").append(deskripsi).append("\n");
            }

            hasil.append("\nTotal peristiwa: ").append(events.length());
            return translateToIndonesian(hasil.toString());

        } catch (Exception e) {
            return " Gagal mengambil data peristiwa.\nPeriksa koneksi internet.\nError: " + e.getMessage();
        }
    }

    private static String translateToIndonesian(String teksInggris) {
        try {
            String apiURL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=id&dt=t&q="
                    + java.net.URLEncoder.encode(teksInggris, "UTF-8");

            HttpURLConnection conn = (HttpURLConnection) new URL(apiURL).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return teksInggris;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONArray json = new JSONArray(response.toString());
            JSONArray sentences = json.getJSONArray(0);

            StringBuilder hasil = new StringBuilder();
            for (int i = 0; i < sentences.length(); i++) {
                hasil.append(sentences.getJSONArray(i).getString(0));
            }

            return hasil.toString().replace("\\n", "\n");

        } catch (Exception e) {
            return teksInggris + "\n(Gagal melakukan terjemahan: " + e.getMessage() + ")";
        }
    }
}

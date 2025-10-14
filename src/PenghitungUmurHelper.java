
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
            conn.setConnectTimeout(1000); 
            conn.setReadTimeout(1000);

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

            if (events.isEmpty()) {
                return "Tidak ada peristiwa penting yang tercatat pada tanggal ini.";
            }

            hasil.append(" PERISTIWA PENTING TANGGAL ")
                    .append(String.format("%02d/%02d", tanggal, bulan))
                    .append("\n\n");

            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String tahun = event.optString("year", "????");
                String deskripsi = event.optString("description", "(tidak ada deskripsi)");

                hasil.append("• ").append(tahun)
                        .append(" — ").append(deskripsi)
                        .append("\n");
            }

            hasil.append("\nTotal peristiwa: ").append(events.length());
        } catch (Exception e) {
            hasil.append("Tidak dapat melihat peristiwa yang penting.\n")
                    .append("Harap pastikan koneksi internet anda aktif.\n")
                    .append(" error: ").append(e.getMessage());
        }

        return hasil.toString();
    }

}

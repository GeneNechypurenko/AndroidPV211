package itstep.learning.androidpv211;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import itstep.learning.androidpv211.nbu.NbuRateAdapter;
import itstep.learning.androidpv211.orm.NbuRate;

public class RatesActivity extends AppCompatActivity {
    private static final String nbuRatesUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private ExecutorService pool;
    private final List<NbuRate> nbuRates = new ArrayList<>();
    private NbuRateAdapter nbuRateAdapter;
    private TextView tvExchangeDate;
    private Button btnPickDate;
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rates);

        tvExchangeDate = findViewById(R.id.nbu_rate_exchangedate);
        btnPickDate  = findViewById(R.id.rates_btn_pick_date);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeBars = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, Math.max(systemBars.bottom, imeBars.bottom));
            return insets;
        });

        pool = Executors.newFixedThreadPool(3);
        CompletableFuture
                .supplyAsync(this::loadRates, pool)
                .thenAccept(this::parseNbuResponse)
                .thenRun(() -> runOnUiThread(() -> {
                    if (!nbuRates.isEmpty()) {
                        tvExchangeDate.setText(String.valueOf(displayDateFormat.format(nbuRates.get(0).getExchangeDate())));
                    }
                    showNbuRates();
                    btnPickDate.setOnClickListener(v -> showDatePicker());
                }));

        RecyclerView rvContainer = findViewById(R.id.rates_rv_container);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvContainer.setLayoutManager(layoutManager);
        nbuRateAdapter = new NbuRateAdapter(nbuRates);
        rvContainer.setAdapter(nbuRateAdapter);

        SearchView svFilter = findViewById(R.id.rates_sv_filter);
        svFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return onFilterChange(s);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return onFilterChange(s);
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String dateStr = apiDateFormat.format(calendar.getTime());
                    tvExchangeDate.setText(displayDateFormat.format(calendar.getTime()));

                    CompletableFuture
                            .supplyAsync(() -> loadRatesByDate(dateStr), pool)
                            .thenAccept(response -> {
                                nbuRates.clear();
                                parseNbuResponse(response);
                            })
                            .thenRun(() -> runOnUiThread(() -> {
                                if (!nbuRates.isEmpty()) {
                                    tvExchangeDate.setText(String.valueOf(displayDateFormat.format(nbuRates.get(0).getExchangeDate())));
                                }
                                nbuRateAdapter.setNbuRates(nbuRates);
                                showNbuRates();
                            }));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private String loadRatesByDate(String yyyymmdd) {
        try {
            String urlStr = nbuRatesUrl + "&date=" + yyyymmdd;
            URL url = new URL(urlStr);
            InputStream urlStream = url.openStream();
            ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int length;
            while ((length = urlStream.read(buffer)) > 0) {
                byteBuilder.write(buffer, 0, length);
            }
            String data = byteBuilder.toString(StandardCharsets.UTF_8.name());
            urlStream.close();
            return data;
        } catch (Exception ex) {
            Log.e("loadRatesByDate", "Exception: " + ex.getMessage());
        }
        return null;
    }

    private boolean onFilterChange(String s) {
        Log.d("onFilterChange", s);
        String query = s.toUpperCase();
        nbuRateAdapter.setNbuRates(
                nbuRates.stream()
                        .filter(r -> r.getCc().toUpperCase().contains(query)
                                || r.getTxt().toUpperCase().contains(query))
                        .collect(Collectors.toList())
        );
        return true;
    }

    private void showNbuRates() {
        runOnUiThread(() -> {
            nbuRateAdapter.notifyItemRangeChanged(0, nbuRates.size());
        });
    }

    private void parseNbuResponse(String body) {
        try {
            JSONArray arr = new JSONArray(body);
            for (int i = 0; i < arr.length(); i++) {
                nbuRates.add(NbuRate.fromJsonObject(arr.getJSONObject(i)));
            }
        } catch (JSONException ex) {
            Log.d("parseNbuResponse", "JSONException: " + ex.getMessage());
        }
    }

    private String loadRates() {
        try {
            URL url = new URL(nbuRatesUrl);
            InputStream urlStream = url.openStream();
            ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int length;
            while ((length = urlStream.read(buffer)) > 0) {
                byteBuilder.write(buffer, 0, length);
            }
            String charsetName = StandardCharsets.UTF_8.name();
            String data = byteBuilder.toString(charsetName);
            urlStream.close();
            return data;
        } catch (MalformedURLException ex) {
            Log.d("loadRates", "MalformedURLException " + ex.getMessage());
        } catch (IOException ex) {
            Log.d("loadRates", "IOException" + ex.getMessage());
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        pool.shutdownNow();
        super.onDestroy();
    }
}
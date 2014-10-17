package com.lmax.ticketing.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;

import com.lmax.disruptor.collections.Histogram;

public class UpdatePriceClient implements Runnable
{
    private final long accountId;
    private final long iterations;
    private final long concertId;
    private final long sectionId;

    public UpdatePriceClient(long accountId, long iterations, long concertId, long sectionId) throws URISyntaxException
    {
        this.accountId = accountId;
        this.iterations = iterations;
        this.concertId = concertId;
        this.sectionId = sectionId;
    }

    @Override
    public void run()
    {
        try
        {
            URL requestUri = new URL("http://localhost:7070/update");

            JSONObject json = new JSONObject();

            for (int x = 0; x < iterations; x++)
            {
                long requestId = System.currentTimeMillis();
                json.put("requestId", requestId);

                json.put("concertId", 1);
                json.put("sectionId", (x % 8));

                double price = 50 + Math.random() * 10;
                json.put("price", String.format(Locale.US,"%2.2f", price));

                HttpURLConnection cn = (HttpURLConnection) requestUri.openConnection();
                cn.setRequestMethod("POST");

                cn.setDoOutput(true);

                OutputStream out = cn.getOutputStream();
                Writer writer = new OutputStreamWriter(out);

                JSONValue.writeJSONString(json, writer);
                writer.flush();
                out.close();

                int code = cn.getResponseCode();
                if (200 != code)
                {
                    System.err.println("Invalid response code" + code);
                    return;
                }
                Thread.sleep(500);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException
    {
        System.out.println("Warm-up");
        UpdatePriceClient client = new UpdatePriceClient(100, 1000, 1, 1);
        client.run();
    }


    public static String print(Histogram h)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Histogram{");

        sb.append("min=").append(h.getMin()).append(", ");
        sb.append("max=").append(h.getMax()).append(", ");
        sb.append("mean=").append(h.getMean()).append(", ");
        sb.append("99%=").append(h.getTwoNinesUpperBound()).append(", ");
        sb.append("99.99%=").append(h.getFourNinesUpperBound()).append("}");

        return sb.toString();
    }

    private static long[] createBounds(int i, int j)
    {
        long[] bounds = new long[(j - i) + 1];
        for (int x = 0; i <= j; i++, x++)
        {
            bounds[x] = i;
        }

        return bounds;
    }
}

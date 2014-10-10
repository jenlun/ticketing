package com.lmax.ticketing.web;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.io.UdpEventHandler;
import com.lmax.ticketing.web.json.TicketPurchaseFromJson;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

@SuppressWarnings("serial")
public class RequestServlet extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(RequestServlet.class.getName());
    private Disruptor<Message> disruptor;

    @SuppressWarnings("unchecked")
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        
        String host = config.getInitParameter("host");
        int port = Integer.parseInt(config.getInitParameter("port"));
        
        LOGGER.info("Connect to " + host + ":" + port);

        disruptor = new Disruptor<Message>(Message.FACTORY, 1024, newSingleThreadExecutor());
        UdpEventHandler handler = new UdpEventHandler(host, port);
        disruptor.handleEventsWith(handler);
        disruptor.start();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            JSONObject request = (JSONObject) parser.parse(req.getReader());
            disruptor.publishEvent(new TicketPurchaseFromJson(request));
        }
        catch (Exception e)
        {
            throw new ServletException("Invalid input", e);
        }
    }
}

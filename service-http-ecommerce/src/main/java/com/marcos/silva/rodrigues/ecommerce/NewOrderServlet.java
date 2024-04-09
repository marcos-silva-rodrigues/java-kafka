package com.marcos.silva.rodrigues.ecommerce;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.Source;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

  private final KafkaDispatcher orderDispatcher = new KafkaDispatcher<Order>();
  private final KafkaDispatcher emailDispatcher = new KafkaDispatcher<String>();


  @Override
  public void destroy() {
    super.destroy();
    orderDispatcher.close();
    emailDispatcher.close();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
      var email = req.getParameter("email");
      var amount = new BigDecimal(req.getParameter("amount"));
      var orderId = UUID.randomUUID().toString();

      var order = new Order(orderId, amount, email);

      orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);
      ;

      var emailCode = "Thanks you for uor order! We are processing your order";

      emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
      resp.getWriter().println("New order sent successfully");
      resp.setStatus(HttpServletResponse.SC_OK);
    } catch (ExecutionException | InterruptedException e) {
      throw new ServletException(e);
    }
  }


}

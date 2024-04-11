package com.marcos.silva.rodrigues.ecommerce;

import com.marcos.silva.rodrigues.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

  private final KafkaDispatcher orderDispatcher = new KafkaDispatcher<Order>();


  @Override
  public void destroy() {
    super.destroy();
    orderDispatcher.close();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    try {
      var email = req.getParameter("email");
      var amount = new BigDecimal(req.getParameter("amount"));
      var orderId = req.getParameter("uuid");

      var order = new Order(orderId, amount, email);

      try (
              var database = new OrdersDatabase();

              ) {
        if (database.saveNew(order)) {
          orderDispatcher.send(
                  "ECOMMERCE_NEW_ORDER",
                  email,
                  new CorrelationId(NewOrderServlet.class.getSimpleName()),
                  order);

          resp.getWriter().println("New order sent successfully");
          resp.setStatus(HttpServletResponse.SC_OK);
        } else {
          resp.getWriter().println("old order received");
          resp.setStatus(HttpServletResponse.SC_OK);
        }
      }




    } catch (ExecutionException | InterruptedException | SQLException e) {
      throw new ServletException(e);
    }
  }


}

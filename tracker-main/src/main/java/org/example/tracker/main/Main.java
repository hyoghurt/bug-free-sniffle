package org.example.tracker.main;

import org.example.tracker.controller.PingController;

public class Main {

  public static void main(String[] args) {
    PingController controller = new PingController();
    System.out.println(controller.ping());
  }
}

package com.company.vertical.infra.gorestclient.auth;

import java.util.List;

public interface TokenProvider {

  String current();

  String next();

  List<String> tokens();
}
